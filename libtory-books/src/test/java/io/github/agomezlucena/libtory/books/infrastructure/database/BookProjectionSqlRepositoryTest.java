package io.github.agomezlucena.libtory.books.infrastructure.database;

import io.github.agomezlucena.libtory.books.domain.BookProjection;
import io.github.agomezlucena.libtory.books.domain.Isbn;
import io.github.agomezlucena.libtory.shared.DataFakerExtension;
import io.github.agomezlucena.libtory.shared.FakerIsbn;
import io.github.agomezlucena.libtory.shared.queries.PagedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static io.github.agomezlucena.libtory.shared.DataFakerExtension.FakerBookTitle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(DataFakerExtension.class)
@DisplayName("a book projection sql repository should")
class BookProjectionSqlRepositoryTest {
    private static final String BOOK_ISBN_QUERY_PARAM = "book_isbn";

    private BookQueries queries;
    private NamedParameterJdbcOperations jdbcOperations;
    private BookProjectionSqlRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        queries = new BookQueries();
        jdbcOperations = mock();
        repository = new BookProjectionSqlRepository(queries, jdbcOperations);
    }

    @Test
    @DisplayName("call to count query when search for all books")
    void shouldCallToCountQueryWhenSearchForAlBooks() {
        var expectedQuery = queries.getQuery(BookQueries.BookQueryName.COUNT_ALL_BOOKS);
        var parameters = ArgumentCaptor.forClass(SqlParameterSource.class);
        when(jdbcOperations.queryForObject(eq(expectedQuery), parameters.capture(), any(RowMapper.class)))
                .thenReturn(1);

        var result = repository.findAllProjections(
                new PagedQuery(null, null, null, null)
        );

        assertNotNull(result);
        assertEquals(1, result.totalAmount());

        var givenParameters = parameters.getValue();
        verify(jdbcOperations).queryForObject(eq(expectedQuery), any(SqlParameterSource.class), any(RowMapper.class));
        assertThat(givenParameters.getParameterNames()).isEmpty();
    }

    @Test
    @DisplayName("call to the unordered query when sorting field is not defined")
    void shouldCallToTheUnorderedQueryWhenSortingFieldIsNotDefined(@FakerIsbn String isbn, @FakerBookTitle String title) {
        var expectedBookCountQuery = queries.getQuery(BookQueries.BookQueryName.COUNT_ALL_BOOKS);
        var expectedBookExtractionQuery = queries.getQuery(BookQueries.BookQueryName.QUERY_BOOK_PROJECTIONS_WITHOUT_SORTING);
        var returnedValue = List.of(new BookProjection(isbn, title, Collections.emptySet()));
        var parametersCaptor = ArgumentCaptor.forClass(SqlParameterSource.class);

        var givenPagedQuery = new PagedQuery(null, null, null, null);

        when(jdbcOperations.query(eq(expectedBookExtractionQuery), parametersCaptor.capture(), any(ResultSetExtractor.class)))
                .thenReturn(returnedValue);

        var result = repository.findAllProjections(givenPagedQuery);

        assertNotNull(result);
        assertSame(returnedValue, result.items());
        assertEquals(returnedValue.size(), result.size());

        verify(jdbcOperations).queryForObject(eq(expectedBookCountQuery), any(SqlParameterSource.class), any(RowMapper.class));
        verify(jdbcOperations).query(eq(expectedBookExtractionQuery), any(SqlParameterSource.class), any(ResultSetExtractor.class));
        verifyNoMoreInteractions(jdbcOperations);

        var parameters = parametersCaptor.getValue();
        assertEquals(givenPagedQuery.size(), parameters.getValue("size"));
        assertEquals(
                givenPagedQuery.size() * givenPagedQuery.page(),
                parameters.getValue("offset")
        );
    }

    @Test
    @DisplayName("call to the ordered query when sorting field is defined")
    void shouldCallToTheOrderedQueryWhenSortingFieldIsDefined(@FakerIsbn String isbn, @FakerBookTitle String title) {
        var expectedBookCountQuery = queries.getQuery(BookQueries.BookQueryName.COUNT_ALL_BOOKS);
        var expectedBookExtractionQuery = String.format(
                queries.getQuery(BookQueries.BookQueryName.QUERY_BOOK_PROJECTIONS_SORTING),
                "title",
                "ASC"
        );
        var returnedValue = List.of(new BookProjection(isbn, title, Collections.emptySet()));
        var parametersCaptor = ArgumentCaptor.forClass(SqlParameterSource.class);

        var givenPagedQuery = new PagedQuery(null, null, "title", null);


        when(jdbcOperations.query(eq(expectedBookExtractionQuery), parametersCaptor.capture(), any(ResultSetExtractor.class)))
                .thenReturn(returnedValue);

        var result = repository.findAllProjections(givenPagedQuery);

        assertNotNull(result);
        assertSame(returnedValue, result.items());
        assertEquals(returnedValue.size(), result.size());

        verify(jdbcOperations).queryForObject(eq(expectedBookCountQuery), any(SqlParameterSource.class), any(RowMapper.class));
        verify(jdbcOperations).query(eq(expectedBookExtractionQuery), any(SqlParameterSource.class), any(ResultSetExtractor.class));
        verifyNoMoreInteractions(jdbcOperations);

        var parameters = parametersCaptor.getValue();
        assertEquals(givenPagedQuery.size(), parameters.getValue("size"));
        assertEquals(
                givenPagedQuery.size() * givenPagedQuery.page(),
                parameters.getValue("offset")
        );
    }

    @Test
    @DisplayName("call to the expected query when you look a projection by id")
    void shouldCallToTheExpectedQueryWhenYouLookForABookProjectionByIsbn(
            @FakerIsbn String isbn,
            @FakerBookTitle String title
    ) {
        var expectedQuery = queries.getQuery(BookQueries.BookQueryName.QUERY_BOOK);
        var parametersCaptor = ArgumentCaptor.forClass(SqlParameterSource.class);
        var returnedBookProjection = Optional.of(new BookProjection(isbn, title, new HashSet<>()));

        when(jdbcOperations.query(eq(expectedQuery), parametersCaptor.capture(), any(ResultSetExtractor.class)))
                .thenReturn(returnedBookProjection);

        var result = repository.findProjectionByIsbn(Isbn.fromString(isbn));

        assertNotNull(result);
        assertSame(returnedBookProjection, result);

        verify(jdbcOperations).query(eq(expectedQuery), parametersCaptor.capture(), any(ResultSetExtractor.class));
        verifyNoMoreInteractions(jdbcOperations);
        var parameters = parametersCaptor.getValue();
        assertEquals(isbn, parameters.getValue(BOOK_ISBN_QUERY_PARAM));
    }
}