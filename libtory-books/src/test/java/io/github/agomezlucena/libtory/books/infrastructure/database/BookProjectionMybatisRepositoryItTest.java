package io.github.agomezlucena.libtory.books.infrastructure.database;

import io.github.agomezlucena.libtory.books.application.BookProjectionPaginatedQuery;
import io.github.agomezlucena.libtory.books.domain.Isbn;
import io.github.agomezlucena.libtory.shared.DataFakerExtension;
import io.github.agomezlucena.libtory.shared.FakerIsbn;
import io.github.agomezlucena.libtory.shared.queries.PaginatedResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.List;
import java.util.Optional;

import static io.github.agomezlucena.libtory.books.testutils.BooksTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("BookProjectionSqlRepository should do in database")
@ExtendWith(DataFakerExtension.class)
@SpringBootTest
public class BookProjectionMybatisRepositoryItTest {
    @Autowired
    private BookProjectionMybatisRepository bookProjectionMyBatisRepository;

    @BeforeAll
    public static void setUpData(@Autowired NamedParameterJdbcOperations operations) throws InterruptedException {
        createTestData(operations);
    }

    @AfterAll
    public static void cleanData(@Autowired NamedParameterJdbcOperations operations) throws InterruptedException {
        deleteAllRegisters(operations);
    }

    @Test
    @DisplayName("query all the books and return the expected books")
    void queryAllBooksAndReturnExpectedBooks() {
        var expectedBooks = new PaginatedResult<>(List.of(createTheIliad()), 1, 3, null, null);

        var givenQuery = new BookProjectionPaginatedQuery(0, 1, null, null);
        var obtainedValue = bookProjectionMyBatisRepository.findAllProjections(givenQuery);
        assertEquals(expectedBooks, obtainedValue);
    }

    @Test
    @DisplayName("should return the expected value ordered by the selected field")
    void shouldReturnTheExpectedValueOrderedByTheSelectedField() {
        var expectedBooks = new PaginatedResult<>(List.of(createTheGreatGatsby()), 1, 3, "title", "ASC");

        var givenPageRequest = new BookProjectionPaginatedQuery(0, 1, "title", "ASC");
        var obtainedValue = bookProjectionMyBatisRepository.findAllProjections(givenPageRequest);
        assertEquals(expectedBooks, obtainedValue);
    }

    @Test
    @DisplayName("should return an optional with the expected value when is found")
    void shouldReturnTheExpectedValueWhenIsFound() {
        var expectedValue = Optional.of(createThePragmaticProgrammer());

        var givenIsbn = Isbn.fromString("9780201616224");
        var obtainedValue = bookProjectionMyBatisRepository.findProjectionByIsbn(givenIsbn);
        assertEquals(expectedValue, obtainedValue);
    }

    @Test
    @DisplayName("should return a empty optional when no value is found")
    void shouldReturnAEmptyOptionalWhenNoValueIsFound(
            @FakerIsbn(avoidIsbn = "9780201616224,9780785839996,9781914602108") String generatedIsbn
    ) {
        var givenIsbn = Isbn.fromString(generatedIsbn);
        var obtainedValue = bookProjectionMyBatisRepository.findProjectionByIsbn(givenIsbn);
        assertTrue(obtainedValue.isEmpty());
    }


}
