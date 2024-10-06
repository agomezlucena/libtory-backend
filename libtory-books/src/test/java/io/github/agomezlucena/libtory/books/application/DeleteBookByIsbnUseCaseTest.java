package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.books.domain.BookRepository;
import io.github.agomezlucena.libtory.books.domain.Isbn;
import io.github.agomezlucena.libtory.shared.DataFakerExtension;
import io.github.agomezlucena.libtory.shared.FakerIsbn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(DataFakerExtension.class)
class DeleteBookByIsbnUseCaseTest {
    private BookRepository bookRepository;
    private DeleteBookByIsbnUseCase testSubject;

    @BeforeEach
    public void setUp() {
        bookRepository = mock();
        testSubject = new DeleteBookByIsbnUseCase(bookRepository);
    }

    @Test
    @DisplayName("should allow to accept isbn as command")
    void shouldAllowToAcceptIsbnAsCommand() {
        assertTrue(testSubject.canHandle(Isbn.class));
        assertFalse(testSubject.canHandle(Integer.class));
    }

    @Test
    @DisplayName("should call to the repository for deleting the book with the given isbn")
    void shouldCallToRepositoryForDeletingIsbn(@FakerIsbn String givenIsbn) {
        var isbn = Isbn.fromString(givenIsbn);
        testSubject.handleCommand(isbn);
        verify(bookRepository).delete(isbn);
    }
}