package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.books.domain.Isbn;

import java.util.UUID;

public record UpdateAuthorsCommand(
        UpdateType updateType,
        String isbn,
        UUID... authorsIds
) {
    enum UpdateType {
        ADDITION,
        DELETION
    }

    public UpdateAuthorsCommand {
        if (updateType == null)
            throw new InvalidUpdateAuthorCommand("is mandatory to provide an update type");
        if (!Isbn.isValidISBN(isbn))
            throw new InvalidUpdateAuthorCommand("the provided isbn is not a valid ISBN 13");
        if (authorsIds == null || authorsIds.length == 0)
            throw new InvalidUpdateAuthorCommand("is mandatory to provide authors for addition or deletion");
    }

    public Isbn getIsbn() {
        return Isbn.fromString(isbn);
    }
}
