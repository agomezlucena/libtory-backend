package io.github.agomezlucena.libtory.books.domain;

public class InvalidAuthor extends RuntimeException{
    InvalidAuthor() {
        super("At least one of the given authors is not registered in our system.");
    }
}
