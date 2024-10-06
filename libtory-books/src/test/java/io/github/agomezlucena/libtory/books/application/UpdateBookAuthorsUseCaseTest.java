package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.books.domain.AuthorChecker;
import io.github.agomezlucena.libtory.books.domain.BookPrimitives;
import io.github.agomezlucena.libtory.books.domain.BookRepository;
import io.github.agomezlucena.libtory.books.domain.Isbn;
import io.github.agomezlucena.libtory.shared.DataFakerExtension;
import io.github.agomezlucena.libtory.shared.FakerIsbn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static io.github.agomezlucena.libtory.books.application.UpdateAuthorsCommand.UpdateType.ADDITION;
import static io.github.agomezlucena.libtory.books.application.UpdateAuthorsCommand.UpdateType.DELETION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(DataFakerExtension.class)
class UpdateBookAuthorsUseCaseTest {
    private AuthorChecker checker;
    private BookRepository repository;
    private UpdateBookAuthorsUseCase testSubject;

    @BeforeEach
    void setUp() {
        checker = mock();
        repository = mock();
        testSubject = new UpdateBookAuthorsUseCase(checker, repository);
    }

    @Test
    @DisplayName("should throw if the given UpdateAuthorsCommand is null")
    void shouldThrowIfGivenUpdateAuthorsCommandIsNull() {
        var expectedMessage = "you can't update without data";
        var exception = assertThrows(InvalidUpdateAuthorCommand.class, () -> testSubject.handleCommand(null));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("should add the authors to the book when those authors exists and save it")
    void shouldAddTheAuthorsToTheBookWhenThoseAuthorsExistsAndSaveIt(@FakerIsbn String isbn) {
        var givenCommand = new UpdateAuthorsCommand(ADDITION, isbn, UUID.randomUUID());
        var returnedBook = new BookPrimitives(isbn, "test").toBook();

        when(checker.authorsExists(givenCommand.authorsIds())).thenReturn(true);
        when(repository.findByIsbn(Isbn.fromString(isbn))).thenReturn(Optional.of(returnedBook));

        testSubject.handleCommand(givenCommand);

        assertEquals(returnedBook.getAuthorsIds(), Set.of(givenCommand.authorsIds()));

        verify(checker).authorsExists(givenCommand.authorsIds());
        verify(repository).findByIsbn(Isbn.fromString(isbn));
        verify(repository).save(returnedBook);
    }

    @Test
    @DisplayName("should remove the authors from the book and save it")
    void shouldRemoveTheFromTheBookAndSaveItEvenIfNotExistsThoseAuthors(@FakerIsbn String isbn) {
        var givenCommand = new UpdateAuthorsCommand(DELETION, isbn, UUID.randomUUID());
        var returnedBook = new BookPrimitives(isbn, "test", givenCommand.authorsIds()).toBook();

        when(repository.findByIsbn(Isbn.fromString(isbn))).thenReturn(Optional.of(returnedBook));

        testSubject.handleCommand(givenCommand);

        assertTrue(returnedBook.getAuthorsIds().isEmpty());
        verify(repository).findByIsbn(Isbn.fromString(isbn));
        verify(repository).save(returnedBook);
    }

    @Test
    @DisplayName("should not save changes if after an addition the book authors does not change")
    void shouldNotSaveChangesIfAfterAnAdditionTheBookAuthorsDoesNotChange(@FakerIsbn String isbn) {
        var givenAuthors = new UUID[]{UUID.randomUUID(), UUID.randomUUID()};
        var givenAdditionCommand = new UpdateAuthorsCommand(ADDITION, isbn, givenAuthors);
        var returnedBook = new BookPrimitives(isbn, "test", givenAuthors).toBook();

        when(checker.authorsExists(givenAuthors)).thenReturn(true);
        when(repository.findByIsbn(Isbn.fromString(isbn))).thenReturn(Optional.of(returnedBook));

        testSubject.handleCommand(givenAdditionCommand);

        verify(repository).findByIsbn(Isbn.fromString(isbn));
        verify(checker).authorsExists(givenAuthors);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("should not save changes if after a deletion the book authors does not change")
    void shouldNotSaveChangesIfAfterADeletionTheBookAuthorsDoesNotChange(@FakerIsbn String isbn) {
        var returnedBook = new BookPrimitives(isbn, "test").toBook();
        var givenDeletionCommand = new UpdateAuthorsCommand(DELETION, isbn, UUID.randomUUID());

        when(repository.findByIsbn(Isbn.fromString(isbn))).thenReturn(Optional.of(returnedBook));

        testSubject.handleCommand(givenDeletionCommand);

        verify(repository).findByIsbn(Isbn.fromString(isbn));
        verifyNoInteractions(checker);
        verifyNoMoreInteractions(repository);
    }
}