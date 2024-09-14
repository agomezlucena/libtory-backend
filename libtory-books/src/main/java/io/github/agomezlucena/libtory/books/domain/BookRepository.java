package io.github.agomezlucena.libtory.books.domain;

public interface BookRepository {
    void delete(Book book);
    void save(Book book);
}
