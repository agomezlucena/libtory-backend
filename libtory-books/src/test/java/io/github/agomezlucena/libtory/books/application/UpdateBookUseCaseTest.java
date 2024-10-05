package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.books.domain.*;
import io.github.agomezlucena.libtory.shared.DataFakerExtension;
import io.github.agomezlucena.libtory.shared.DataFakerExtension.FakerBookTitle;
import io.github.agomezlucena.libtory.shared.FakerIsbn;
import io.github.agomezlucena.libtory.shared.cqrs.CommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(DataFakerExtension.class)
@DisplayName("update book use case should")
class UpdateBookUseCaseTest {
    private BookRepository repository;
    private AuthorChecker authorChecker;
    private UpdateBookUseCase testSubject;

    @BeforeEach
    void shouldCreateTheUseCaseWithoutProblem() {
        repository = mock();
        authorChecker = mock();
        testSubject = new UpdateBookUseCase(repository, authorChecker);
    }

    @Test
    @DisplayName("be instance of CommandHandler")
    void shouldBeInstanceOfCommandHandler() {
        assertInstanceOf(CommandHandler.class, testSubject);
    }

    @Test
    @DisplayName("support BookPrimitives as command only")
    void shouldSupportBookPrimitivesAsCommandOnly() {
        assertTrue(testSubject.canHandle(BookPrimitives.class));
        assertFalse(testSubject.canHandle(Integer.class));
    }

    @Test
    @DisplayName("save the given book without authors in database when is ok")
    void shouldSaveTheGivenBookWithoutAuthorInDatabaseWhenIsOk(
            @FakerIsbn String givenIsbn,
            @FakerBookTitle String givenTitle
    ) {
        var givenPrimitive = new BookPrimitives(givenIsbn, givenTitle);
        testSubject.handleCommand(givenPrimitive);
        verify(repository).save(givenPrimitive.toBook());
        verifyNoInteractions(authorChecker);
    }

    @Test
    @DisplayName("save the given book with author in database whe is ok")
    void shouldSaveTheGivenBookWithDatabaseWhenIsOk(
            @FakerIsbn String givenIsbn,
            @FakerBookTitle String givenTitle
    ) {
        var givenPrimitive = new BookPrimitives(givenIsbn, givenTitle, UUID.randomUUID());

        when(authorChecker.authorsExists(any(UUID[].class))).thenReturn(true);
        testSubject.handleCommand(givenPrimitive);
        verify(authorChecker).authorsExists(givenPrimitive.authorsAsArray());
        verify(repository).save(givenPrimitive.toBook());
    }

    @Test
    @DisplayName("throw an InvalidAuthor exception when the given author does not exists")
    void shouldThrowAnInvalidAuthorExceptionWhenTheGivenAuthorDoesNotExist(
            @FakerIsbn String givenIsbn,
            @FakerBookTitle String givenTitle
    ) {
        var givenPrimitive = new BookPrimitives(givenIsbn, givenTitle, UUID.randomUUID());
        when(authorChecker.authorsExists(any(UUID[].class))).thenReturn(false);
        assertThrows(InvalidAuthor.class, () -> testSubject.handleCommand(givenPrimitive));
        verify(authorChecker).authorsExists(givenPrimitive.authorsAsArray());
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("throw an InvalidIsbn exception when the given isbn is not a well formed ISBN 13")
    void shouldThrowAnInvalidIsbnExceptionWhenTheGivenIsbnIsNotAWellFormedISBN13(@FakerBookTitle String givenTitle) {
        var givenPrimitives = new BookPrimitives("",givenTitle);
        assertThrows(InvalidIsbn.class, () -> testSubject.handleCommand(givenPrimitives));
        verifyNoInteractions(authorChecker,repository);
    }

    @Test
    @DisplayName("throw an InvalidTitle exception when the given title is not well formed")
    void shouldThrowAnInvalidIsbnExceptionWhenTheGivenTitleIsNotWellFormed(@FakerIsbn String givenIsbn) {
        var givenPrimitives = new BookPrimitives(givenIsbn,null);
        assertThrows(InvalidTitle.class, () -> testSubject.handleCommand(givenPrimitives));
        verifyNoInteractions(authorChecker,repository);
    }

}