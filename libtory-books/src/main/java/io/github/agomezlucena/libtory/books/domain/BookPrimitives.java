package io.github.agomezlucena.libtory.books.domain;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public record BookPrimitives (
        String isbn,
        String title,
        Set<UUID> authors
) {

    public BookPrimitives(String isbn, String title, UUID... authors){
        this(isbn,title, Optional.ofNullable(authors).map(Set::of).orElseGet(Collections::emptySet));
    }

    public UUID[] authorsAsArray(){
        return authors.toArray(UUID[]::new);
    }
}
