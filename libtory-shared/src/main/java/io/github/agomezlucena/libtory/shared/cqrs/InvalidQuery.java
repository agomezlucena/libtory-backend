package io.github.agomezlucena.libtory.shared.cqrs;

public class InvalidQuery extends RuntimeException {
    public InvalidQuery(String message) {
        super(message);
    }
}
