package io.github.agomezlucena.libtory.books.infrastructure.database;

import io.github.agomezlucena.libtory.books.domain.Book;
import io.github.agomezlucena.libtory.books.domain.BookRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BookSqlRepository implements BookRepository {
    private static final String BOOK_ISBN_QUERY_PARAM = "book_isbn";
    private static final String BOOK_TITLE_QUERY_PARAM = "book_title";
    private static final String AUTHOR_ID_QUERY_PARAMS = "author_id";

    private final BookQueries bookQueries;
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public BookSqlRepository(
            final BookQueries bookQueries,
            @Qualifier("booksNamedParameterOperations")
            final NamedParameterJdbcOperations namedParameterJdbcOperations
    ) {
        this.bookQueries = bookQueries;
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    @Transactional
    public void save(Book book) {
        updateBook(book);
        if(!book.hasAuthors()) return;
        derelateNotContainedAuthors(book);
        relateContainedAuthors(book);
    }

    private void updateBook(Book book){
        var cleansedIsbn = book.getIsbn();

        var insertBookQuery = bookQueries.getQuery(BookQueries.BookQueryName.SAVE_BOOK_INFORMATION);

        var queryParams = new MapSqlParameterSource()
                .addValue(BOOK_ISBN_QUERY_PARAM, cleansedIsbn)
                .addValue(BOOK_TITLE_QUERY_PARAM, book.getTitle());

        namedParameterJdbcOperations.update(insertBookQuery, queryParams);
    }

    private void derelateNotContainedAuthors(Book book){
        namedParameterJdbcOperations.update(
                bookQueries.getQuery(BookQueries.BookQueryName.DERELATE_BOOK_WITH_NOT_GIVEN_AUTHORS),
                new MapSqlParameterSource(BOOK_ISBN_QUERY_PARAM, book.getIsbn())
                        .addValue(AUTHOR_ID_QUERY_PARAMS,book.getAuthorsIds())
        );
    }

    private void relateContainedAuthors(Book book) {
        var authorsAsParameters = book.getAuthorsIds().stream()
                .map(authorId -> new MapSqlParameterSource(BOOK_ISBN_QUERY_PARAM, book.getIsbn())
                        .addValue(AUTHOR_ID_QUERY_PARAMS, authorId))
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcOperations.batchUpdate(
                bookQueries.getQuery(BookQueries.BookQueryName.RELATE_BOOK_WITH_AUTHOR),
                authorsAsParameters
        );
    }

}
