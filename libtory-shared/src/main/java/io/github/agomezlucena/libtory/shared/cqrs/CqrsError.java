package io.github.agomezlucena.libtory.shared.cqrs;

public class CqrsError extends RuntimeException {
    public CqrsError(String message) {
        super(message);
    }
}
