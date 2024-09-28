package io.github.agomezlucena.libtory.shared.cqrs;

public interface CommandHandler <T>{
    void handleCommand(T command);
    boolean canHandle(Class<?> command);
}
