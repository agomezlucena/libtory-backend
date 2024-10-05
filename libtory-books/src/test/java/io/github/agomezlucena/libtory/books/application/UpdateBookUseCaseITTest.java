package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.books.domain.AuthorChecker;
import io.github.agomezlucena.libtory.books.domain.BookPrimitives;
import io.github.agomezlucena.libtory.books.domain.BookRepository;
import io.github.agomezlucena.libtory.shared.DataFakerExtension;
import io.github.agomezlucena.libtory.shared.DataFakerExtension.FakerBookTitle;
import io.github.agomezlucena.libtory.shared.FakerIsbn;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(DataFakerExtension.class)
@SpringBootTest
class UpdateBookUseCaseITTest {
    private UpdateBookUseCase testSubject;

    @Autowired
    private NamedParameterJdbcOperations operations;

    @BeforeEach
    void setUpTestData() throws InterruptedException {
        deleteAllRegisters(operations);
        createTestData(operations);
    }

    @AfterEach
    void cleanData() throws InterruptedException {
        deleteAllRegisters(operations);
    }

    @BeforeEach
    void createTestSubject(
            @Autowired BookRepository repository,
            @Autowired AuthorChecker authorChecker
    ) {
        testSubject = new UpdateBookUseCase(repository, authorChecker);
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
                            (select count(*) from books where isbn = :isbn) +
                            (select count(*) from book_authors where book_isbn = :isbn)
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
                            (select count(*) from books where isbn = :isbn) +
                            (select count(*) from book_authors where book_isbn = :isbn)
                        ) = 2
                        """,
                Map.of("isbn", isbn),
                Boolean.class
        );

        assertNotNull(result);
        assertTrue(result);
    }


    private static void deleteAllRegisters(NamedParameterJdbcOperations operations) throws InterruptedException {
        var deleteBookAuthors = "delete from books.book_authors";
        var deleteBooks = "delete from books.books";
        var deleteFromAuthors = "delete from books.authors";
        operations.update(deleteBookAuthors, Collections.emptyMap());
        var deleteBooksTask = Thread.ofVirtual().start(() -> operations.update(deleteBooks, Collections.emptyMap()));
        var deleteAuthorsTask = Thread.ofVirtual().start(() -> operations.update(deleteFromAuthors, Collections.emptyMap()));
        deleteBooksTask.join();
        deleteAuthorsTask.join();
    }

    private static void createTestData(NamedParameterJdbcOperations operations) throws InterruptedException {
        var insertBooksSql = """
                INSERT INTO books.books (isbn, title)
                VALUES
                ('9781914602108', 'The Iliad'),
                ('9780785839996', 'The Great Gatsby'),
                ('9780201616224','The Pragmatic Programmer: From Journeyman to Master')
                ON CONFLICT (isbn) do update
                    set title = excluded.title
                """;

        var insertAuthorsSql = """
                INSERT INTO books.authors (author_id, author_name)
                VALUES
                ('123e4567-e89b-12d3-a456-426614174000', 'Homer'),
                ('123e4567-e89b-12d3-a456-426614174001', 'F. Scott Fitzgerald'),
                ('123e4567-e89b-12d3-a456-426614174002', 'Andrew Hunt'),
                ('123e4567-e89b-12d3-a456-426614174003', 'David Thomas')
                on conflict do nothing
                """;

        var insertBookAuthorsSql = """
                INSERT INTO books.book_authors (book_isbn, author_id)
                VALUES
                ('9781914602108', '123e4567-e89b-12d3-a456-426614174000'),
                ('9780785839996', '123e4567-e89b-12d3-a456-426614174001'),
                ('9780201616224','123e4567-e89b-12d3-a456-426614174002'),
                ('9780201616224','123e4567-e89b-12d3-a456-426614174003')
                on conflict do nothing
                """;


        var insertBookTask = Thread.ofVirtual().start(() -> operations.update(insertBooksSql, Collections.emptyMap()));
        var insertAuthorTask = Thread.ofVirtual().start(() -> operations.update(insertAuthorsSql, Collections.emptyMap()));
        insertBookTask.join();
        insertAuthorTask.join();
        operations.update(insertBookAuthorsSql, Collections.emptyMap());
    }
}