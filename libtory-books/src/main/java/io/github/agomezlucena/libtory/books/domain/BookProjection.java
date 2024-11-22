package io.github.agomezlucena.libtory.books.domain;

import java.util.Set;

public record BookProjection(
        String isbn,
        String title,
        Set<Author> authors
) {
}
