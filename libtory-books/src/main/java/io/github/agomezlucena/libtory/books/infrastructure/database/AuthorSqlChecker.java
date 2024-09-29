package io.github.agomezlucena.libtory.books.infrastructure.database;

import io.github.agomezlucena.libtory.books.domain.AuthorChecker;
import io.github.agomezlucena.libtory.books.infrastructure.database.BookQueries.BookQueryName;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class AuthorSqlChecker implements AuthorChecker {
    private final BookQueries queries;
    private final NamedParameterJdbcOperations jdbcOperations;

    public AuthorSqlChecker(
            final BookQueries queries,
            final NamedParameterJdbcOperations jdbcOperations
    ) {
        this.queries = queries;
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public boolean authorsExists(UUID... authorIds) {
        var ids = Set.of(authorIds);
        return Optional.ofNullable(jdbcOperations.queryForObject(
                queries.getQuery(BookQueryName.CHECK_AUTHOR_EXISTENCE),
                new MapSqlParameterSource("expectedAmount",ids.size())
                        .addValue("expectedAuthors",ids),
                Boolean.class
        )).orElse(false);
    }
}
