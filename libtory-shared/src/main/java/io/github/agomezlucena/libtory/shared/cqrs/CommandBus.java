package io.github.agomezlucena.libtory.shared.cqrs;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class CommandBus {
    private static final Map<Class<?>, CommandHandler<?>> handlers = new ConcurrentHashMap<>();

    public static CommandBus getNewCommandBus(){
        return new CommandBus();
    }

    public <T> void sendCommand (T command){
        var handler = Optional.ofNullable(handlers.get(command.getClass()))
                .map(it -> (CommandHandler<T>) it)
                .orElseThrow(()-> new IllegalArgumentException(
                        "not handler found for command: "+
                                command.getClass().getSimpleName())
                );

        handler.handleCommand(command);
    }

    public CommandBus addHandler(Class<?> commandClassToHandle, CommandHandler<?> handler){
        if (!handler.canHandle(commandClassToHandle)){
            throw new IllegalArgumentException(
                    String.format("handler: %s can not handle command: %s",
                    handler.getClass().getSimpleName(),
                    commandClassToHandle.getSimpleName())
            );
        }
        handlers.put(commandClassToHandle, handler);
        return this;
    }
}
