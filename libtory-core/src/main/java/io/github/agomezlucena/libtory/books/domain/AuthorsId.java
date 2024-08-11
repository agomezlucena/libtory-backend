package io.github.agomezlucena.libtory.books.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

record AuthorsId(Set<UUID> ids) {
    private static final AuthorsId EMPTY_AUTHORS = new AuthorsId(Collections.emptySet());

    public static AuthorsId from (UUID... authorsId){
        if(authorsId == null || authorsId.length == 0){
            return EMPTY_AUTHORS;
        }
        return new AuthorsId(Set.of(authorsId));
    }

    public AuthorsId addAuthors(UUID... ids){
        if(ids == null) return this;
        return new AuthorsId(Stream.concat(
                this.ids.stream(),
                Arrays.stream(ids)
        ).collect(Collectors.toUnmodifiableSet()));
    }

    public AuthorsId remove(UUID... ids){
        if(ids == null) return this;

        var authorsToRemove = Set.of(ids);

        if(this.ids.equals(authorsToRemove)){
            return EMPTY_AUTHORS;
        }

        return new AuthorsId(
                this.ids.stream().filter(id -> !authorsToRemove.contains(id)).collect(Collectors.toSet())
        );
    }
}
