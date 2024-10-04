package io.github.agomezlucena.libtory.books.domain;

import java.util.Optional;

public interface BookRepository {
    Optional<Book> findByIsbn(Isbn isbn);
    void delete(Book book);
    void save(Book book);
}
