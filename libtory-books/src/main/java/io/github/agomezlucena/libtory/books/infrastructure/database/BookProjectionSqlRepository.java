package io.github.agomezlucena.libtory.books.infrastructure.database;

import io.github.agomezlucena.libtory.books.domain.Author;
import io.github.agomezlucena.libtory.books.domain.BookProjection;
import io.github.agomezlucena.libtory.books.domain.BookProjectionRepository;
import io.github.agomezlucena.libtory.books.domain.Isbn;
import io.github.agomezlucena.libtory.shared.queries.PagedQuery;
import io.github.agomezlucena.libtory.shared.queries.PagedResult;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BookProjectionSqlRepository implements BookProjectionRepository {
    private static final String BOOK_ISBN_QUERY_PARAM = "book_isbn";
    private static final String BOOK_TITLE_QUERY_PARAM = "book_title";
    private static final String AUTHOR_ID_QUERY_PARAMS = "author_id";

    private final BookQueries bookQueries;
    private final NamedParameterJdbcOperations jdbcOperations;

    public BookProjectionSqlRepository(
            final BookQueries bookQueries,
            final NamedParameterJdbcOperations jdbcOperations
    ) {
        this.bookQueries = bookQueries;
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public PagedResult<BookProjection> findAllProjections(PagedQuery query) {
        var sql = getProjectionQueryByOrdering(query);
        var parameters = getParametersForQuery(query);
        var totalBooks = countAllBooks();
        var items = jdbcOperations.query(sql, parameters, getProjections());
        return new PagedResult<>(
                items,
                Optional.ofNullable(items).map(Collection::size).orElse(0),
                totalBooks,
                query.sortingField(),
                Optional.ofNullable(query.sortingField())
                        .map(it -> query.sortingDirection())
                        .orElse(null)
        );
    }

    private String getProjectionQueryByOrdering(PagedQuery query) {
        if (query.sortingField() != null) {
            return String.format(
                    bookQueries.getQuery(BookQueries.BookQueryName.QUERY_BOOK_PROJECTIONS_SORTING),
                    query.sortingField(),
                    query.sortingDirection()
            );
        }
        return bookQueries.getQuery(BookQueries.BookQueryName.QUERY_BOOK_PROJECTIONS_WITHOUT_SORTING);
    }

    private SqlParameterSource getParametersForQuery(PagedQuery query) {
        return new MapSqlParameterSource("size", query.size())
                .addValue("offset", query.page() * query.size());
    }

    private int countAllBooks() {
        var result = jdbcOperations.queryForObject(
                bookQueries.getQuery(BookQueries.BookQueryName.COUNT_ALL_BOOKS),
                new MapSqlParameterSource(),
                (rs, ignored) -> rs.getInt(1)
        );
        return (result == null) ? 0 : result;
    }

    private ResultSetExtractor<List<BookProjection>> getProjections() {
        return rs -> {
            var entityMap = getProjectionsFromResultSet(rs);
            return List.copyOf(entityMap.values());
        };
    }

    @Override
    public Optional<BookProjection> findProjectionByIsbn(Isbn isbn) {
        var sql = bookQueries.getQuery(BookQueries.BookQueryName.QUERY_BOOK);
        var isbnLiteral = isbn.isbnLiteral();
        return jdbcOperations.query(
                sql,
                new MapSqlParameterSource(BOOK_ISBN_QUERY_PARAM, isbnLiteral),
                getProjectionByIsbn(isbnLiteral)
        );
    }


    private ResultSetExtractor<Optional<BookProjection>> getProjectionByIsbn(String queriedIsbn) {
        return rs -> {
            var entityMap = getProjectionsFromResultSet(rs);
            return Optional.ofNullable(entityMap.get(queriedIsbn));
        };
    }

    private Map<String, BookProjection> getProjectionsFromResultSet(ResultSet resultSet) throws SQLException {
        var entityMap = new HashMap<String, BookProjection>();
        while (resultSet.next()) {
            var isbn = resultSet.getString(BOOK_ISBN_QUERY_PARAM);
            var title = resultSet.getString(BOOK_TITLE_QUERY_PARAM);
            var authorId = resultSet.getObject(AUTHOR_ID_QUERY_PARAMS, UUID.class);
            var authorName = resultSet.getString("author_name");
            var cachedEntity = entityMap.get(isbn);

            if (cachedEntity != null) {
                cachedEntity.authors().add(new Author(authorId, authorName));
                continue;
            }

            cachedEntity = new BookProjection(isbn, title, new HashSet<>());
            if (authorId != null) {
                cachedEntity.authors().add(new Author(authorId, authorName));
            }

            entityMap.put(isbn, cachedEntity);
        }
        return entityMap;
    }
}
