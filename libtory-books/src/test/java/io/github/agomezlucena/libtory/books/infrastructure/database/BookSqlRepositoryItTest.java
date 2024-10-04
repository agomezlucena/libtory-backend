package io.github.agomezlucena.libtory.books.infrastructure.database;

import io.github.agomezlucena.libtory.books.domain.AuthorChecker;
import io.github.agomezlucena.libtory.books.domain.Book;
import io.github.agomezlucena.libtory.books.domain.BookPrimitives;
import io.github.agomezlucena.libtory.shared.DataFakerExtension;
import io.github.agomezlucena.libtory.shared.DataFakerExtension.FakerBookTitle;
import io.github.agomezlucena.libtory.shared.DataFakerExtension.FakerIsbn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("BookSqlRepository should do in database")
@ExtendWith(DataFakerExtension.class)
@SpringBootTest
class BookSqlRepositoryItTest {
    @Autowired
    private BookSqlRepository bookSqlRepository;
    @Autowired
    private NamedParameterJdbcOperations namedParameterJdbcOperations;
    @Autowired
    private AuthorChecker authorChecker;

    @BeforeEach
    public void setUpBooksAndAuthorsInDatabase() {
        var insertBooksSql = """
                INSERT INTO books.books (isbn, title)
                VALUES
                ('9781914602108', 'The Iliad'),
                ('9780785839996', 'The Great Gatsby')
                ON CONFLICT (isbn) do update
                    set title = excluded.title
                """;

        var insertAuthorsSql = """
                INSERT INTO books.authors (author_id, author_name)
                VALUES
                ('123e4567-e89b-12d3-a456-426614174000', 'Homer'),
                ('123e4567-e89b-12d3-a456-426614174001', 'F. Scott Fitzgerald')
                on conflict do nothing
                """;

        var insertBookAuthorsSql = """
                INSERT INTO books.book_authors (book_isbn, author_id)
                VALUES
                ('9781914602108', '123e4567-e89b-12d3-a456-426614174000'),
                ('9780785839996', '123e4567-e89b-12d3-a456-426614174001')
                on conflict do nothing
                """;


        namedParameterJdbcOperations.getJdbcOperations().execute(insertAuthorsSql);
        namedParameterJdbcOperations.getJdbcOperations().execute(insertBooksSql);
        namedParameterJdbcOperations.getJdbcOperations().execute(insertBookAuthorsSql);
    }

    @BeforeEach
    public void deletedNonFixedTestData() {
        var deleteBookSql = """
                delete from books.books
                where isbn not in ('9781914602108','9780785839996')
                """;

        var deleteAuthorRelationShip = """
                delete from books.book_authors
                where book_isbn not in ('9781914602108','9780785839996')
                """;

        namedParameterJdbcOperations.getJdbcOperations().execute(deleteAuthorRelationShip);
        namedParameterJdbcOperations.getJdbcOperations().execute(deleteBookSql);
    }

    @Test
    @DisplayName("when saving a new book will create a new record in books table")
    void createNewRecordInBooksTableIfBookDoesNotExist(
            @FakerIsbn(avoidIsbn = "9781914602108;9780785839996") String isbn,
            @FakerBookTitle String title
    ) {
        var givenBookPrimitives = new BookPrimitives(isbn, title);
        var givenBook = Book.createBook(givenBookPrimitives, authorChecker);

        bookSqlRepository.save(givenBook);
        var verificationQuery = "select count(*) = 1 from books where isbn = :isbn";

        assertTrue(
                Optional.ofNullable(
                                namedParameterJdbcOperations.queryForObject(
                                        verificationQuery,
                                        new MapSqlParameterSource("isbn", isbn),
                                        Boolean.class
                                )
                        )
                        .orElse(false)
        );
    }

    @Test
    @DisplayName("when saving a existing book will update the existing record at book table")
    void updateRecordInBooksTableIfAlreadyExists(
            @FakerIsbn(avoidIsbn = "9781914602108;9780785839996") String isbn,
            @FakerBookTitle String title
    ) {
        var givenBookPrimitives = new BookPrimitives(isbn, title);
        var givenBook = Book.createBook(givenBookPrimitives, authorChecker);

        bookSqlRepository.save(givenBook);
        givenBook.setTitle("another test title");
        bookSqlRepository.save(givenBook);

        var verificationQuery = "select count(*) = 1 from books where isbn = :isbn and title = :title";
        var result = Optional.ofNullable(
                namedParameterJdbcOperations.queryForObject(
                        verificationQuery,
                        Map.of("isbn", isbn, "title", givenBook.getTitle()),
                        Boolean.class
                ))
                .orElse(false);
        assertTrue(result);
    }

    @Test
    @DisplayName(
                 """
                 when saving a new book with existing authors will create a new record for each author id
                 in book_authors
                 """
    )
    void createANewRecordInBookAuthorsForEachAuthorIdThatTheGivenBookHasWhenIsNewlyCreated(
            @FakerIsbn(avoidIsbn = "9781914602108;9780785839996") String isbn,
            @FakerBookTitle String title
    ){
        var givenBookPrimitives = new BookPrimitives(
                isbn,
                title,
                UUID.fromString("123e4567-e89b-12d3-a456-426614174001")
        );

        var givenBook = Book.createBook(givenBookPrimitives, authorChecker);
        bookSqlRepository.save(givenBook);
        var verificationQuery = "select count(*) = 1 from book_authors where book_isbn = :isbn";
        var result = Optional.ofNullable(
                        namedParameterJdbcOperations.queryForObject(
                                verificationQuery,
                                Map.of("isbn", isbn, "title", givenBook.getTitle()),
                                Boolean.class
                        ))
                .orElse(false);
        assertTrue(result);
    }

    @Test
    @DisplayName(
            """
            when saving an existing book with authors in database will remove the records in book_authors that
            aren't present in the given book
            """
    )
    void mustDeleteNotPresentAuthorInDatabaseWhenIsNotPresentWhenSaving(){
        var preconditionQuery = """
                insert into book_authors (book_isbn,author_id)
                values ('9781914602108','123e4567-e89b-12d3-a456-426614174001')
                """;
        namedParameterJdbcOperations.update(preconditionQuery, Collections.emptyMap());

        var primitives = new BookPrimitives(
                "9781914602108",
                "The Iliad",
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        );

        var givenBook = Book.createBook(primitives, authorChecker);
        bookSqlRepository.save(givenBook);

        var verificationQuery = "select count(*) = 1 from book_authors where book_isbn = '9781914602108'";

        var result = Optional.ofNullable(
                        namedParameterJdbcOperations.queryForObject(
                                verificationQuery,
                                Collections.emptyMap(),
                                Boolean.class
                        ))
                .orElse(false);
        assertTrue(result);
    }

    @Test
    @DisplayName(
            """
            when saving an existing book that have authors in db but the given book does not have any.
            will remove all records for book in book_authors table
            """
    )
    void mustDeleteAllRelatedBookAuthorForAExistingBook(){

        var primitives = new BookPrimitives(
                "9781914602108",
                "The Iliad"
        );

        var givenBook = Book.createBook(primitives, authorChecker);
        bookSqlRepository.save(givenBook);

        var verificationQuery = "select count(*) = 0 from book_authors where book_isbn = '9781914602108'";

        var result = Optional.ofNullable(
                        namedParameterJdbcOperations.queryForObject(
                                verificationQuery,
                                Collections.emptyMap(),
                                Boolean.class
                        ))
                .orElse(false);
        assertTrue(result);
    }
}
