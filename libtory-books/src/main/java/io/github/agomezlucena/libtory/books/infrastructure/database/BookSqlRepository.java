package io.github.agomezlucena.libtory.books.infrastructure.database;

import io.github.agomezlucena.libtory.books.domain.Book;
import io.github.agomezlucena.libtory.books.domain.BookPrimitives;
import io.github.agomezlucena.libtory.books.domain.BookRepository;
import io.github.agomezlucena.libtory.books.domain.Isbn;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static io.github.agomezlucena.libtory.books.infrastructure.database.BookQueries.BookQueryName;

public class  BookSqlRepository implements BookRepository {
    private static final String BOOK_ISBN_QUERY_PARAM = "book_isbn";
    private static final String BOOK_TITLE_QUERY_PARAM = "book_title";
    private static final String AUTHOR_ID_QUERY_PARAMS = "author_id";

    private final BookQueries bookQueries;
    private final NamedParameterJdbcOperations jdbcOperations;

    public BookSqlRepository(
            final BookQueries bookQueries,
            final NamedParameterJdbcOperations jdbcOperations
    ) {
        this.bookQueries = bookQueries;
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public Optional<Book> findByIsbn(Isbn isbn) {
        return jdbcOperations.query(
                bookQueries.getQuery(BookQueryName.GET_BOOK_INFORMATION),
                new MapSqlParameterSource(BOOK_ISBN_QUERY_PARAM, isbn.isbnLiteral()),
                extractBook(isbn.isbnLiteral())
        );
    }

    private ResultSetExtractor<Optional<Book>> extractBook(String queryIsbn) {
        return rs -> {
            var resultMap = new HashMap<String, BookPrimitives>();
            while (rs.next()) {
                var isbn = rs.getString(BOOK_ISBN_QUERY_PARAM);
                var title = rs.getString(BOOK_TITLE_QUERY_PARAM);
                var authorId = rs.getObject(AUTHOR_ID_QUERY_PARAMS, UUID.class);
                Optional.ofNullable(resultMap.get(isbn))
                        .ifPresentOrElse(
                                it -> it.addAuthor(authorId),
                                () -> {
                                    var mutableBook = new BookPrimitives(isbn, title);
                                    mutableBook.addAuthor(authorId);
                                    resultMap.put(isbn, mutableBook);
                                }
                        );
            }

            return Optional.ofNullable(resultMap.get(queryIsbn))
                    .map(BookPrimitives::toBook);
        };
    }

    @Override
    @Transactional
    public void delete(Book book) {
        var deleteRelationshipQuery = bookQueries.getQuery(BookQueryName.DELETE_BOOK_RELATIONSHIP_WITH_AUTHORS);
        var deleteBook = bookQueries.getQuery(BookQueryName.DELETE_BOOK);
        var params = new MapSqlParameterSource(BOOK_ISBN_QUERY_PARAM, book.getIsbn());
        jdbcOperations.update(deleteRelationshipQuery, params);
        jdbcOperations.update(deleteBook, params);
    }

    @Override
    @Transactional
    public void save(Book book) {
        updateBook(book);
        derelateNotContainedAuthors(book);
        relateContainedAuthors(book);
    }


    private void updateBook(Book book) {
        var cleansedIsbn = book.getIsbn();

        var insertBookQuery = bookQueries.getQuery(BookQueryName.SAVE_BOOK_INFORMATION);

        var queryParams = new MapSqlParameterSource()
                .addValue(BOOK_ISBN_QUERY_PARAM, cleansedIsbn)
                .addValue(BOOK_TITLE_QUERY_PARAM, book.getTitle());

        jdbcOperations.update(insertBookQuery, queryParams);
    }

    private void derelateNotContainedAuthors(Book book) {
        if(!book.hasAuthors()){
            jdbcOperations.update(
                    bookQueries.getQuery(BookQueryName.DELETE_BOOK_RELATIONSHIP_WITH_AUTHORS),
                    new MapSqlParameterSource(BOOK_ISBN_QUERY_PARAM, book.getIsbn())
            );
            return;
        }

        jdbcOperations.update(
                bookQueries.getQuery(BookQueryName.DERELATE_BOOK_WITH_NOT_GIVEN_AUTHORS),
                new MapSqlParameterSource(BOOK_ISBN_QUERY_PARAM, book.getIsbn())
                        .addValue(AUTHOR_ID_QUERY_PARAMS, book.getAuthorsIds())
        );
    }

    private void relateContainedAuthors(Book book) {
        if(!book.hasAuthors()) return;

        var authorsAsParameters = book.getAuthorsIds().stream()
                .map(authorId -> new MapSqlParameterSource(BOOK_ISBN_QUERY_PARAM, book.getIsbn())
                        .addValue(AUTHOR_ID_QUERY_PARAMS, authorId))
                .toArray(SqlParameterSource[]::new);

        jdbcOperations.batchUpdate(
                bookQueries.getQuery(BookQueryName.RELATE_BOOK_WITH_AUTHOR),
                authorsAsParameters
        );
    }

}
