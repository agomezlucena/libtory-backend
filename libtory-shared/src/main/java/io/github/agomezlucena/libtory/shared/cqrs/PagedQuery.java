package io.github.agomezlucena.libtory.shared.cqrs;

public interface PagedQuery<T> extends Query<T>{
    int getPage();
    int getSize();
    String getSortingField();
    String getSortingDirection();
}
