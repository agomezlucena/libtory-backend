package io.github.agomezlucena.libtory.shared.cqrs;

public interface QueryHandler <T extends Query<?>,R>{
    R handle(T query);
}
