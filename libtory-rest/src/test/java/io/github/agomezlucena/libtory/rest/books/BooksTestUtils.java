package io.github.agomezlucena.libtory.rest.books;

import io.github.agomezlucena.libtory.books.domain.Author;
import io.github.agomezlucena.libtory.books.domain.BookProjection;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public interface BooksTestUtils {
    static BookProjection createTheIliad() {
        return new BookProjection(
                "9781914602108",
                "The Iliad",
                List.of(
                        new Author(
                                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                                "Homer"
                        )
                )
        );
    }

    static BookProjection createTheGreatGatsby() {
        return new BookProjection(
                "9780785839996",
                "The Great Gatsby",
                List.of(
                        new Author(
                                UUID.fromString("123e4567-e89b-12d3-a456-426614174001"),
                                "F. Scott Fitzgerald"
                        )
                )
        );
    }

    static BookProjection createThePragmaticProgrammer() {
        return new BookProjection(
                "9780201616224",
                "The Pragmatic Programmer: From Journeyman to Master",
                List.of(
                        new Author(
                                UUID.fromString("123e4567-e89b-12d3-a456-426614174002"),
                                "Andrew Hunt"
                        ),
                        new Author(
                                UUID.fromString("123e4567-e89b-12d3-a456-426614174003"),
                                "David Thomas"
                        )
                )
        );
    }

    static void cleanupDatabase(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        var deleteBookSql = """
                delete from books.books
                where isbn not in ('9781914602108','9780785839996','9780201616224')
                """;

        var deleteAuthorRelationShip = """
                delete from books.book_authors
                where book_isbn not in ('9781914602108','9780785839996','9780201616224')
                """;

        namedParameterJdbcOperations.getJdbcOperations().execute(deleteAuthorRelationShip);
        namedParameterJdbcOperations.getJdbcOperations().execute(deleteBookSql);
    }

    static void deleteAllRegisters(NamedParameterJdbcOperations operations) throws InterruptedException {
        var deleteBookAuthors = "delete from books.book_authors";
        var deleteBooks = "delete from books.books";
        var deleteFromAuthors = "delete from books.authors";
        operations.update(deleteBookAuthors, Collections.emptyMap());
        var deleteBooksTask = Thread.ofVirtual().start(() -> operations.update(deleteBooks, Collections.emptyMap()));
        var deleteAuthorsTask = Thread.ofVirtual().start(() -> operations.update(deleteFromAuthors, Collections.emptyMap()));
        deleteBooksTask.join();
        deleteAuthorsTask.join();
    }

    static void createTestData(NamedParameterJdbcOperations operations) throws InterruptedException {
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
