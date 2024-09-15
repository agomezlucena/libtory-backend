package io.github.agomezlucena.libtory.books.domain;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Set;
import java.util.UUID;

public record BookPrimitives (
        String isbn,
        String title,
        Set<UUID> authors
) {

    public BookPrimitives(String isbn, String title, UUID... authors){
        this(isbn,title, Stream.of(authors).collect(Collectors.toCollection(HashSet::new)));
    }

    public void addAuthor(UUID author){
        if (author == null || authors.contains(author)) return;
        authors.add(author);
    }

    public UUID[] authorsAsArray(){
        return authors.toArray(UUID[]::new);
    }

    public Book toBook(){
        return new Book(
                Isbn.fromString(isbn),
                Title.fromText(title),
                new AuthorsId(authors)
        );
    }
}
