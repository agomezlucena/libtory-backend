package io.github.agomezlucena.libtory.books.application;

public class InvalidUpdateAuthorCommand extends RuntimeException {
    public InvalidUpdateAuthorCommand(String message) {
        super(message);
    }
}
