package io.github.agomezlucena.libtory.books.domain;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record AuthorsId(Set<UUID> ids) {
    public static AuthorsId from (UUID... authorsId){
        return new AuthorsId(Set.of(authorsId));
    }

    public AuthorsId addAuthors(UUID... ids){
        return new AuthorsId(Stream.concat(
                this.ids.stream(),
                Arrays.stream(ids)
        ).collect(Collectors.toUnmodifiableSet()));
    }
}
