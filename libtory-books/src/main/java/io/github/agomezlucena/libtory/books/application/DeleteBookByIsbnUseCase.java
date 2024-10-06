package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.books.domain.BookRepository;
import io.github.agomezlucena.libtory.books.domain.Isbn;
import io.github.agomezlucena.libtory.shared.cqrs.CommandHandler;
import org.jspecify.annotations.NonNull;

/// this class represent the use case of removing a book
///
/// @author Alejandro Gómez Lucena
public class DeleteBookByIsbnUseCase implements CommandHandler<Isbn> {
    private final BookRepository bookRepository;

    public DeleteBookByIsbnUseCase(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /// will remove the book and the associations with another entities to that book in persistence
    ///
    /// @param bookIsbn a well-formed isbn 13
    @Override
    public void handleCommand(@NonNull Isbn bookIsbn) {
        bookRepository.delete(bookIsbn);
    }
}
