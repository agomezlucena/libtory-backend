package io.github.agomezlucena.libtory.books.domain;

public class InvalidTitle extends RuntimeException {
    public InvalidTitle() {
        super("given title is invalid");
    }
}
