package io.github.agomezlucena.libtory.shared.queries;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class QueryBus {
    private final Map<Class<? extends Query<?>>,QueryHandler<?,?>> handlers = new ConcurrentHashMap<>();

    public QueryBus addHandler(Class<? extends Query<?>> clazz, QueryHandler<?,?> mockedHandler) {
        this.handlers.put(clazz, mockedHandler);
        return this;
    }

    public <T,R> R handle(Query<T> query) {
       return Optional.ofNullable(handlers.get(query.getClass()))
               .map(handler -> (QueryHandler<Query<T>,R>) handler)
               .map(handler -> handler.handle(query))
               .orElseThrow();
    }
}
