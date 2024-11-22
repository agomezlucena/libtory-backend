package io.github.agomezlucena.libtory.books.infrastructure.database;

import io.github.agomezlucena.libtory.books.domain.Author;
import io.github.agomezlucena.libtory.books.domain.BookProjection;
import io.github.agomezlucena.libtory.shared.DataFakerExtension;
import io.github.agomezlucena.libtory.shared.queries.PagedQuery;
import io.github.agomezlucena.libtory.shared.queries.PagedResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("BookProjectionSqlRepository should do in database")
@ExtendWith(DataFakerExtension.class)
@SpringBootTest
public class BookProjectionSqlRepositoryItTest {
    @Autowired
    private NamedParameterJdbcOperations namedParameterJdbcOperations;
    @Autowired
    private BookProjectionSqlRepository bookProjectionSqlRepository;

    @BeforeEach
    public void setUpBooksAndAuthorsInDatabase() {
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


        namedParameterJdbcOperations.getJdbcOperations().execute(insertAuthorsSql);
        namedParameterJdbcOperations.getJdbcOperations().execute(insertBooksSql);
        namedParameterJdbcOperations.getJdbcOperations().execute(insertBookAuthorsSql);
    }

    @Test
    @DisplayName("query all the books and return the expected books")
    void queryAllBooksAndReturnExpectedBooks() {
        var expectedBooks = new PagedResult<>(
            List.of(
                new BookProjection(
                        "9781914602108",
                        "The Iliad",
                        Set.of(
                                new Author(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),"Homer")
                        )
                )
            ),
                1,
                3,
                null,
                null
        );

        var obtainedValue = bookProjectionSqlRepository.findAllProjections(new PagedQuery(0,1,null,null));
        assertEquals(expectedBooks,obtainedValue);
    }

    @Test
    @DisplayName("should return the expected value ordered by the selected field")
    void shouldReturnTheExpectedValueOrderedByTheSelectedField(){
        var expectedBooks = new PagedResult<>(
                List.of(
                        new BookProjection(
                                "9780785839996",
                                "The Great Gatsby",
                                Set.of(
                                        new Author(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"),"F. Scott Fitzgerald")
                                )
                        )
                ),
                1,
                3,
                "title",
                "ASC"
        );

        var givenPageRequest = new PagedQuery(0,1,"title","ASC");
        var obtainedValue = bookProjectionSqlRepository.findAllProjections(givenPageRequest);
        assertEquals(expectedBooks,obtainedValue);
    }
}
