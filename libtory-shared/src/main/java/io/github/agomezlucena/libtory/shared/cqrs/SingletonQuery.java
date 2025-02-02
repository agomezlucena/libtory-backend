package io.github.agomezlucena.libtory.shared.cqrs;

public interface SingletonQuery <T,V> extends Query<T>{
    V getDiscriminatorValue();
}
