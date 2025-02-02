package io.github.agomezlucena.libtory.shared.cqrs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QueryBus {
    private final Map<Class<? extends Query<?>>, QueryHandler<?, ?>> handlers = new ConcurrentHashMap<>();

    public QueryBus addHandler(Class<? extends Query<?>> clazz, QueryHandler<?, ?> mockedHandler) {
        this.handlers.put(clazz, mockedHandler);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T, R> R handle(Query<T> query) {
        var handler = (QueryHandler<Query<T>, R>) handlers.get(query.getClass());
        if (handler == null) {
            throw new CqrsError("the query is not supported in this bus");
        }

        return handler.handle(query);
    }
}
