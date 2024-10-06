package io.github.agomezlucena.libtory.shared.cqrs;

import java.lang.reflect.ParameterizedType;
import java.util.stream.Stream;

public interface CommandHandler <T>{
    void handleCommand(T command);
    default boolean canHandle(Class<?> command){
        return Stream.of(getClass().getGenericInterfaces())
                .filter(ParameterizedType.class::isInstance)
                .filter(it -> CommandHandler.class.equals(((ParameterizedType) it).getRawType()))
                .map(it -> ((ParameterizedType) it).getActualTypeArguments()[0])
                .anyMatch(it -> it.equals(command));
    }
}
