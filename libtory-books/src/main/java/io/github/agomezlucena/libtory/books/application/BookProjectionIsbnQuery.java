package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.books.domain.BookProjection;
import io.github.agomezlucena.libtory.books.domain.Isbn;
import io.github.agomezlucena.libtory.shared.queries.SingletonQuery;

public record BookProjectionIsbnQuery(String isbn) implements SingletonQuery<BookProjection, Isbn> {
    @Override
    public Isbn getDiscriminatorValue() {
        return Isbn.fromString(isbn);
    }
}
