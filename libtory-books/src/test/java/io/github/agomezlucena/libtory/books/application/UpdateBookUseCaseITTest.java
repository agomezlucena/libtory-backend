package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.books.domain.BookPrimitives;
import io.github.agomezlucena.libtory.shared.DataFakerExtension;
import io.github.agomezlucena.libtory.shared.DataFakerExtension.FakerBookTitle;
import io.github.agomezlucena.libtory.shared.FakerIsbn;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.Map;
import java.util.UUID;

import static io.github.agomezlucena.libtory.books.testutils.BooksTestUtils.createTestData;
import static io.github.agomezlucena.libtory.books.testutils.BooksTestUtils.deleteAllRegisters;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(DataFakerExtension.class)
@SpringBootTest
class UpdateBookUseCaseITTest {
    @Autowired
    private UpdateBookUseCase testSubject;
    @Autowired
    private NamedParameterJdbcOperations operations;


    @BeforeAll
    public static void setUpTestData(@Autowired NamedParameterJdbcOperations operations) throws InterruptedException {
        createTestData(operations);
    }

    @AfterAll
    public static void tearDownTestData(
            @Autowired NamedParameterJdbcOperations operations
    ) throws InterruptedException {
        deleteAllRegisters(operations);
    }

    @Test
    @DisplayName("should create a record in books when the given primitive is well formed and don't have authors")
    void shouldCreateARecordInBooksWhenTheGivenPrimitiveIsWellFormed(
            @FakerIsbn(avoidIsbn = "9781914602108;9780785839996;9780201616224", dontRepeat = true) String isbn,
            @FakerBookTitle String title
    ) {
        var givenPrimitives = new BookPrimitives(isbn, title);
        testSubject.handleCommand(givenPrimitives);
        var result = operations.queryForObject("""
                        select (
                            (select count(*) from books.books where isbn = :isbn) +
                            (select count(*) from books.book_authors where book_isbn = :isbn)
                        ) = 1
                        """,
                Map.of("isbn", isbn),
                Boolean.class
        );

        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    @DisplayName("should create a record in books and book_authors when the new book has existing authors ids")
    void shouldCreateARecordInBooksAndBookAuthorsWhenTheNewBookHasExistingAuthorsIds(
            @FakerIsbn(avoidIsbn = "9781914602108;9780785839996;9780201616224", dontRepeat = true) String isbn,
            @FakerBookTitle String title
    ){
        var givenPrimitive = new BookPrimitives(
                isbn,
                title,
                UUID.fromString("123e4567-e89b-12d3-a456-426614174003")
        );

        testSubject.handleCommand(givenPrimitive);

        var result = operations.queryForObject("""
                        select (
                            (select count(*) from books.books where isbn = :isbn) +
                            (select count(*) from books.book_authors where book_isbn = :isbn)
                        ) = 2
                        """,
                Map.of("isbn", isbn),
                Boolean.class
        );

        assertNotNull(result);
        assertTrue(result);
    }
}