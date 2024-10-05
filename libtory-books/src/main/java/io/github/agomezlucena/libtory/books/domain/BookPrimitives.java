package io.github.agomezlucena.libtory.books.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// This object represent the literals of a book
///
/// @author Alejandro Gómez Lucena
public record BookPrimitives(
        String isbn,
        String title,
        Set<UUID> authors
) {

    public BookPrimitives(String isbn, String title, UUID... authors) {
        this(isbn, title, Stream.of(authors).collect(Collectors.toCollection(HashSet::new)));
    }

    /// allows you to add an author
    public void addAuthor(UUID author) {
        if (author == null || authors.contains(author)) return;
        authors.add(author);
    }

    /// map the authors to an array
    public UUID[] authorsAsArray() {
        return authors.toArray(UUID[]::new);
    }

    /// will create a book with the information retained
    public Book toBook() {
        return new Book(
                Isbn.fromString(isbn),
                Title.fromText(title),
                new AuthorsId(authors)
        );
    }
}
