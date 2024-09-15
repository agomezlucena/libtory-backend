package io.github.agomezlucena.libtory.books.infrastructure.database;

import io.github.agomezlucena.libtory.books.domain.AuthorChecker;
import io.github.agomezlucena.libtory.books.domain.Book;
import io.github.agomezlucena.libtory.books.domain.BookPrimitives;
import io.github.agomezlucena.libtory.books.domain.Isbn;
import io.github.agomezlucena.libtory.shared.DataFakerExtension;
import io.github.agomezlucena.libtory.shared.DataFakerExtension.FakerIsbn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static io.github.agomezlucena.libtory.books.infrastructure.database.BookQueries.BookQueryName;


@ExtendWith(DataFakerExtension.class)
@DisplayName("a book sql repository should")
class BookSqlRepositoryTest {
    private BookQueries queries;
    private NamedParameterJdbcOperations jdbcOperations;
    private BookSqlRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        queries = new BookQueries();
        jdbcOperations = mock();
        repository = new BookSqlRepository(queries, jdbcOperations);
    }

    @Test
    @DisplayName("save a book without authors if that book does not exists")
    void shouldSaveABookIfThatBookDoesNotExist(@FakerIsbn String isbn) {
        var saveQueryExistenceParameterCaptor = ArgumentCaptor.forClass(SqlParameterSource.class);
        var bookSaveQuery = queries.getQuery(BookQueryName.SAVE_BOOK_INFORMATION);
        var givenBook = createBookWithIsbnAndAuthors(isbn);

        when(jdbcOperations.update(eq(bookSaveQuery),saveQueryExistenceParameterCaptor.capture())).thenReturn(1);

        repository.save(givenBook);

        var insertQueryParams = saveQueryExistenceParameterCaptor.getValue();

        assertEquals(givenBook.getIsbn(), insertQueryParams.getValue("book_isbn"));
        assertEquals(givenBook.getTitle(),insertQueryParams.getValue("book_title"));

        verify(jdbcOperations).update(bookSaveQuery,insertQueryParams);
        verifyNoMoreInteractions(jdbcOperations);
    }

    @Test
    @DisplayName("save a new book with authors if not exists")
    void shouldSaveANewBookWithAuthorsIfNotExists(@FakerIsbn String givenIsbn){
        var givenAuthorId = UUID.randomUUID();
        var givenBook = createBookWithIsbnAndAuthors(givenIsbn,givenAuthorId);
        var bookQuery = queries.getQuery(BookQueryName.SAVE_BOOK_INFORMATION);
        var derelateQuery = queries.getQuery(BookQueryName.DERELATE_BOOK_WITH_NOT_GIVEN_AUTHORS);
        var relateQuery = queries.getQuery(BookQueryName.RELATE_BOOK_WITH_AUTHOR);

        var updateQueryArgumentCaptor = ArgumentCaptor.forClass(SqlParameterSource.class);
        var batchUpdateQueryArgumentCaptor = ArgumentCaptor.forClass(SqlParameterSource[].class);
        when(jdbcOperations.update(eq(bookQuery),updateQueryArgumentCaptor.capture())).thenReturn(1);
        when(jdbcOperations.update(eq(derelateQuery),updateQueryArgumentCaptor.capture())).thenReturn(0);
        when(jdbcOperations.batchUpdate(eq(relateQuery),batchUpdateQueryArgumentCaptor.capture()))
                .thenReturn(new int[]{1});

        repository.save(givenBook);

        var updateQueriesArguments = updateQueryArgumentCaptor.getAllValues();
        assertNotNull(updateQueriesArguments);
        assertEquals(2, updateQueriesArguments.size());
        assertEquals(givenBook.getIsbn(), updateQueriesArguments.getFirst().getValue("book_isbn"));
        assertEquals(givenBook.getTitle(),updateQueriesArguments.getFirst().getValue("book_title"));
        assertEquals(givenBook.getIsbn(),updateQueriesArguments.get(1).getValue("book_isbn"));
        assertEquals(Set.of(givenAuthorId),updateQueriesArguments.get(1).getValue("author_id"));

        var batchUpdateQueriesArguments = batchUpdateQueryArgumentCaptor.getAllValues();
        assertNotNull(batchUpdateQueriesArguments);
        assertEquals(1, batchUpdateQueriesArguments.size());

        var batchUpdateParameters = batchUpdateQueriesArguments.getFirst();
        assertEquals(1,batchUpdateParameters.length);
        assertEquals(givenBook.getIsbn(),batchUpdateParameters[0].getValue("book_isbn"));
        assertEquals(givenAuthorId,batchUpdateParameters[0].getValue("author_id"));

        verify(jdbcOperations,times(2)).update(anyString(),notNull(SqlParameterSource.class));
        verify(jdbcOperations).batchUpdate(eq(relateQuery),notNull(SqlParameterSource[].class));
    }

    @Test
    @DisplayName("remove the given book")
    void shouldRemoveGivenBook(@FakerIsbn String isbn){
        var givenBook = createBookWithIsbnAndAuthors(isbn);
        var captor = ArgumentCaptor.forClass(SqlParameterSource.class);
        var deleteBookRelationshipWithAuthorsQuery = queries.getQuery(BookQueryName.DELETE_BOOK_RELATIONSHIP_WITH_AUTHORS);
        var deleteBookQuery = queries.getQuery(BookQueryName.DELETE_BOOK);

        when(jdbcOperations.update(eq(deleteBookRelationshipWithAuthorsQuery),captor.capture())).thenReturn(1);
        when(jdbcOperations.update(eq(deleteBookQuery),captor.capture())).thenReturn(1);

        repository.delete(givenBook);

        verify(jdbcOperations,times(2)).update(anyString(),notNull(SqlParameterSource.class));
        var givenParameters = captor.getAllValues();
        assertNotNull(givenParameters);
        assertEquals(2,givenParameters.size());

        var firstQueryParam = givenParameters.getFirst();
        var secondQueryParam = givenParameters.get(1);

        assertEquals(givenBook.getIsbn(),firstQueryParam.getValue("book_isbn"));
        assertEquals(givenBook.getIsbn(),secondQueryParam.getValue("book_isbn"));
    }

    @Test
    @DisplayName("call to the valid query when you are looking and locking for a book by isbn")
    void shouldCallToTheValidQueryWhenYouAreLookingAndLockingForABookByIsbn(@FakerIsbn String givenIsbn){
        var expectedQuery = queries.getQuery(BookQueryName.GET_BOOK_INFORMATION);
        var expectedOutput = Optional.of(createBookWithIsbnAndAuthors(givenIsbn));
        var givenCaptor = ArgumentCaptor.forClass(SqlParameterSource.class);

        when(jdbcOperations.query(
                eq(expectedQuery),
                givenCaptor.capture(),
                (ResultSetExtractor<Optional<Book>>) notNull( ))
        ).thenReturn(expectedOutput);

        var result = repository.findByIsbnLocking(Isbn.fromString(givenIsbn));
        assertNotNull(result);
        assertEquals(expectedOutput,result);
        var params = givenCaptor.getValue();
        assertNotNull(params);
        assertEquals(givenIsbn,params.getValue("book_isbn"));
    }

    private Book createBookWithIsbnAndAuthors(String isbn, UUID...authors){
        var mockedAuthorChecker = mock(AuthorChecker.class);
        when(mockedAuthorChecker.authorsExists(any())).thenReturn(true);
        return Book.createBook(
                new BookPrimitives(isbn,"test title",authors),
                mockedAuthorChecker
        );
    }
}