package io.github.agomezlucena.libtory.books.domain;

public class InvalidIsbn extends RuntimeException {
    public InvalidIsbn(final String isbn) {
        super(String.format("ISBN: %s is invalid",isbn));
    }
}
