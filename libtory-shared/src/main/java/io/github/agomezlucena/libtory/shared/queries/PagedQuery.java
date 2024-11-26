package io.github.agomezlucena.libtory.shared.queries;

public interface PagedQuery<T> extends Query<T>{
    int getPage();
    int getSize();
    String getSortingField();
    String getSortingDirection();
}
