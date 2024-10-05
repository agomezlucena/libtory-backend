package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.books.domain.*;
import io.github.agomezlucena.libtory.shared.cqrs.CommandHandler;

/// This use case allows you to create or update an existing book
/// @author Alejandro Gómez Lucena
/// @see CommandHandler
public class UpdateBookUseCase implements CommandHandler<BookPrimitives> {
    private final BookRepository repository;
    private final AuthorChecker checker;

    public UpdateBookUseCase(BookRepository repository, AuthorChecker checker) {
        this.repository = repository;
        this.checker = checker;
    }

    /// will create or update an existing book with the information of the given book primitive object.
    ///
    /// @param command this object contains the information of the book to create or update
    /// @throws InvalidAuthor when one of the present authors id not exists.
    /// @throws InvalidIsbn   when the given isbn is not a valid ISBN 13
    /// @throws InvalidTitle  when the given title is invalid
    /// @see Book
    /// @see BookPrimitives
    @Override
    public void handleCommand(BookPrimitives command) {
        repository.save(Book.createBook(command, checker));
    }

    /// will check if the given class is a supported class for this handler
    /// @return true -> BookPrimitives.class
    @Override
    public boolean canHandle(Class<?> commandClass) {
        return BookPrimitives.class.equals(commandClass);
    }
}
