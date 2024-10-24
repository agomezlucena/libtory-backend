package io.github.agomezlucena.libtory.shared.queries;

public class InvalidQuery extends RuntimeException {
    public InvalidQuery(String message) {
        super(message);
    }
}
