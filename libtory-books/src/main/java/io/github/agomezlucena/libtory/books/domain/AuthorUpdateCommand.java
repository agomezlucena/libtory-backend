package io.github.agomezlucena.libtory.books.domain;

import java.util.UUID;

public sealed interface AuthorUpdateCommand {
    static AuthorUpdateCommand addAuthors(
            AuthorChecker authorChecker,
            BookRepository repository,
            UUID...authorIds
    ){
        return new AddAuthors(authorChecker,repository,authorIds);
    }

    static AuthorUpdateCommand deleteAuthors(BookRepository repository, UUID... authorIds){
        return new DeleteAuthor(repository,authorIds);
    }

    record AddAuthors(AuthorChecker authorChecker, BookRepository repository, UUID...authorIds)
        implements AuthorUpdateCommand {

    }

    record DeleteAuthor(BookRepository repository, UUID... authorsIds) implements AuthorUpdateCommand {

    }
}
