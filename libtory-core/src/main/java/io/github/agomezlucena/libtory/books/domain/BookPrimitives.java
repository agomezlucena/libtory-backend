package io.github.agomezlucena.libtory.books.domain;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public record BookPrimitives (
        String isbn,
        String title,
        Set<UUID> authors
) {
    public BookPrimitives(String isbn, String title) {
        this(isbn, title, Collections.emptySet());
    }
}
