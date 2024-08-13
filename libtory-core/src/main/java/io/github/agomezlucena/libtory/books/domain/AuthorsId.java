package io.github.agomezlucena.libtory.books.domain;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This record represent a set of authors ids that have a book.
 * @param ids a set of author ids.
 * @author Alejandro Gómez Lucena.
 */
record AuthorsId(Set<UUID> ids) {
    private static final AuthorsId EMPTY_AUTHORS = new AuthorsId(Collections.emptySet());

    /**
     * create a new instance with the given authors ids.
     * @param authorsId authors id
     * @return an instance with the passed by parameters ids, in case that you pass a null or empty value
     * will return an empty shared instance.
     */
    public static @NonNull AuthorsId from (@Nullable UUID... authorsId){
        return Optional.ofNullable(authorsId)
                .filter(it -> it.length > 0)
                .map(Set::of)
                .map(AuthorsId::new)
                .orElse(EMPTY_AUTHORS);
    }

    /**
     * will create a new AuthorId object with the union of the given ids and the id that holds this object.
     * @param ids given authors id to add
     * @return <p>if ids is not null and is not empty return the expected value.</p>
     * <p>if ids are null or empty return the called object.</p>
     * <p>if called object is empty return a new AuthorId with the input</p>
     */
    public @NonNull AuthorsId addAuthors(@Nullable UUID... ids){
        if(ids == null || ids.length == 0) return this;
        if(this.ids.isEmpty()) return from(ids);

        return new AuthorsId(
                Stream.concat(
                    this.ids.stream(),
                    Arrays.stream(ids)
                ).collect(Collectors.toUnmodifiableSet())
        );
    }

    /**
     * will create a new object without the id passed by parameter
     * @param ids author id to remove
     * @return will return a set without the passed ids.
     * <p>if called object has not id hold will return themself.</p>
     * <p>if ids are null or empty will return the called object.</p>
     * <p>if ids are the same that holds the called object will return the shared empty instance.</p>
     */
    public @NonNull AuthorsId remove(@Nullable UUID... ids){
        if(this.ids.isEmpty() || ids == null || ids.length == 0) return this;

        var authorsToRemove = Set.of(ids);
        if(this.ids.equals(authorsToRemove)) return EMPTY_AUTHORS;


        return new AuthorsId(
                this.ids.stream()
                        .filter(id -> !authorsToRemove.contains(id))
                        .collect(Collectors.toSet())
        );
    }
}
