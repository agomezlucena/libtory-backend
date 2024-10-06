package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.books.domain.AuthorChecker;
import io.github.agomezlucena.libtory.books.domain.AuthorUpdateCommand;
import io.github.agomezlucena.libtory.books.domain.BookRepository;
import io.github.agomezlucena.libtory.shared.cqrs.CommandHandler;
import org.jspecify.annotations.NonNull;

/// This class represent the use case of when we only wants to add or remove an author
/// to a book
/// @author Alejandro Gómez Lucena.
public class UpdateBookAuthorsUseCase implements CommandHandler<UpdateAuthorsCommand> {
    private final AuthorChecker authorChecker;
    private final BookRepository bookRepository;

    public UpdateBookAuthorsUseCase(AuthorChecker authorChecker, BookRepository bookRepository) {
        this.authorChecker = authorChecker;
        this.bookRepository = bookRepository;
    }

    /// will manage the given author update command and will modify the entity based on the information of the command
    /// @param command a command that indicates to add or delete the given authors from a book.
    /// @see AuthorUpdateCommand
    /// @see io.github.agomezlucena.libtory.books.domain.Book Book
    @Override
    public void handleCommand(@NonNull UpdateAuthorsCommand command) {
        if (command == null) throw new InvalidUpdateAuthorCommand("you can't update without data");
        bookRepository.findByIsbn(command.getIsbn())
                .ifPresent(book -> book.updateAuthors(prepareCommand(command)));
    }

    private AuthorUpdateCommand prepareCommand(UpdateAuthorsCommand command) {
        return switch (command.updateType()) {
            case ADDITION -> AuthorUpdateCommand.addAuthors(authorChecker, bookRepository, command.authorsIds());
            case DELETION -> AuthorUpdateCommand.deleteAuthors(bookRepository, command.authorsIds());
        };
    }
}
