package io.github.agomezlucena.libtory.books.domain;

import java.util.Objects;
import java.util.UUID;

public record Author(UUID authorId, String name) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author author)) return false;
        return Objects.equals(authorId, author.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authorId);
    }
}
