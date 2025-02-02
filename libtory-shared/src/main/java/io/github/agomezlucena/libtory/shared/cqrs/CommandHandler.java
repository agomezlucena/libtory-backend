package io.github.agomezlucena.libtory.shared.cqrs;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface CommandHandler<T> {

    void handleCommand(T command);

    default boolean canHandle(Class<?> commandClass) {
        return Stream.of(getClass().getGenericInterfaces())
                .mapMulti(this::extractFirstGenericElement)
                .anyMatch(it -> it.equals(commandClass));
    }

    private void extractFirstGenericElement(Type type, Consumer<Type> consumer) {
        if(!(type instanceof ParameterizedType parameterizedType)){
            return;
        }

        if(! CommandHandler.class.equals(parameterizedType.getRawType())){
            return;
        }

        consumer.accept(parameterizedType.getActualTypeArguments()[0]);
    }
}
