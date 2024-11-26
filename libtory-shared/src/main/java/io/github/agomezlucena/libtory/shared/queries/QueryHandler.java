package io.github.agomezlucena.libtory.shared.queries;

public interface QueryHandler <T extends Query<?>,R>{
    R handle(T query);
}
