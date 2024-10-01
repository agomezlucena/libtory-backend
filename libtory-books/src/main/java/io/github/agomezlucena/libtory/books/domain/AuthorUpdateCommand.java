package io.github.agomezlucena.libtory.books.domain;

import java.util.UUID;

/**
 * Represent the available action that a user can make with the authors of a book
 */
public sealed interface AuthorUpdateCommand {
    /**
     * Will create a command that will add authors to the book.
     * @param authorChecker will check that the given authors exists.
     * @param repository persistence representation that contains the book aggregates
     * @param authorIds an array of uuid that represents the authors to add to the book
     * @return an AuthorCommand that allows to remove authors.
     */
    static AuthorUpdateCommand addAuthors(
            AuthorChecker authorChecker,
            BookRepository repository,
            UUID...authorIds
    ){
        return new AddAuthors(authorChecker,repository,authorIds);
    }

    /**
     * Will create a command that will remove authors from the book.
     * @param repository persistence representation that contains the book aggregates.
     * @param authorIds an array of uuid that represents the authors to remove from the book.
     * @return an AuthorCommand that allows to remove authors.
     */
    static AuthorUpdateCommand deleteAuthors(BookRepository repository, UUID... authorIds){
        return new DeleteAuthor(repository,authorIds);
    }

    record AddAuthors(AuthorChecker authorChecker, BookRepository repository, UUID...authorIds)
        implements AuthorUpdateCommand {

    }

    record DeleteAuthor(BookRepository repository, UUID... authorsIds) implements AuthorUpdateCommand {

    }
}
