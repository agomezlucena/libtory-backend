package io.github.agomezlucena.libtory.books.domain;

import java.util.Optional;

public interface BookRepository {
    Optional<Book> findByIsbn(Isbn isbn);
    void delete(Isbn isbn);
    void save(Book book);
}
