package io.github.agomezlucena.libtory.books.testutils;

import io.github.agomezlucena.libtory.books.domain.Author;
import io.github.agomezlucena.libtory.books.domain.BookProjection;

import java.util.List;
import java.util.UUID;

public interface BookProjectionFactory {
    static BookProjection createTheIliad() {
        return new BookProjection(
                "9781914602108",
                "The Iliad",
                List.of(
                        new Author(
                                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                                "Homer"
                        )
                )
        );
    }

    static BookProjection createTheGreatGatsby() {
        return new BookProjection(
                "9780785839996",
                "The Great Gatsby",
                List.of(
                        new Author(
                                UUID.fromString("123e4567-e89b-12d3-a456-426614174001"),
                                "F. Scott Fitzgerald"
                        )
                )
        );
    }

    static BookProjection createThePragmaticProgrammer() {
        return new BookProjection(
                "9780201616224",
                "The Pragmatic Programmer: From Journeyman to Master",
                List.of(
                        new Author(
                                UUID.fromString("123e4567-e89b-12d3-a456-426614174002"),
                                "Andrew Hunt"
                        ),
                        new Author(
                                UUID.fromString("123e4567-e89b-12d3-a456-426614174003"),
                                "David Thomas"
                        )
                )
        );
    }
}
