package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.shared.cqrs.PaginatedResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.List;

import static io.github.agomezlucena.libtory.books.testutils.BooksTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class QueryBooksPaginatedUseCaseITTest {
    @Autowired
    private QueryBooksPaginatedUseCase testSubject;

    @BeforeAll
    public static void prepareData(@Autowired NamedParameterJdbcOperations operations) throws InterruptedException {
        createTestData(operations);
    }
    @AfterAll
    public static void cleanData(@Autowired NamedParameterJdbcOperations operations) throws InterruptedException{
        deleteAllRegisters(operations);
    }

    @Test
    @DisplayName("should call to the book repository when is called")
    void shouldCallToTheBookRepositoryWhenIsCalled() {
        var expectedResult = new PaginatedResult<>(List.of(createTheIliad()),1,3,null,null);
        var givenPagedQuery = new BookProjectionPaginatedQuery(0,1,null,null);

        var result = testSubject.handle(givenPagedQuery);

        assertEquals(expectedResult,result);
    }
}