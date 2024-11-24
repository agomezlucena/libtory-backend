package io.github.agomezlucena.libtory.books.domain;

import java.util.ArrayList;
import java.util.List;

public record BookProjection(
        String isbn,
        String title,
        List<Author> authors
){
    public BookProjection(String isbn, String title) {
        this(isbn, title, new ArrayList<>());
    }
}
