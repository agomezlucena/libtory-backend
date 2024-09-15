package io.github.agomezlucena.libtory.books.infrastructure.database;

import io.github.agomezlucena.libtory.books.infrastructure.database.BookQueries.BookQueryName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("an author sql checker should")
class AuthorSqlCheckerTest {
    private BookQueries queries;
    private NamedParameterJdbcOperations jdbcOperations;
    private AuthorSqlChecker checker;

    @BeforeEach
    void setUp() throws IOException {
        queries = new BookQueries();
        jdbcOperations = mock();
        checker = new AuthorSqlChecker(queries, jdbcOperations);
    }

    @Test
    @DisplayName("call to the expected query for checking that authors exists")
    void shouldCallToTheExpectedQueryForCheckingThatAuthorsExists() {
        var rng = RandomGenerator.getDefault();
        var givenAuthorsIds = Stream.generate(UUID::randomUUID)
                .limit(rng.nextLong(1, 10L))
                .toArray(UUID[]::new);

        var captor = ArgumentCaptor.forClass(SqlParameterSource.class);

        when(jdbcOperations.queryForObject(
                        eq(queries.getQuery(BookQueryName.CHECK_AUTHOR_EXISTENCE)),
                        captor.capture(),
                        eq(Boolean.class)
                )
        ).thenReturn(true);

        var result = checker.authorsExists(givenAuthorsIds);
        assertTrue(result);
        var parameters = captor.getValue();
        assertEquals(givenAuthorsIds.length,parameters.getValue("expectedAmount"));
        assertThat((Set<UUID>)parameters.getValue("expectedAuthors"))
                .containsOnly(givenAuthorsIds);
    }
}