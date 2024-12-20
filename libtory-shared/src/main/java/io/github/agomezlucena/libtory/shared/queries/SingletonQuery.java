package io.github.agomezlucena.libtory.shared.queries;

public interface SingletonQuery <T,V> extends Query<T>{
    V getDiscriminatorValue();
}
