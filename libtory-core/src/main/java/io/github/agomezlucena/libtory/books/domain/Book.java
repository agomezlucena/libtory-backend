package io.github.agomezlucena.libtory.books.domain;

import java.util.Set;
import java.util.UUID;

public class Book {
    private final Isbn isbn;
    private Title title;
    private Set<UUID> authorsId;

    public static Book createBook(String isbn, String title, UUID...authorsId) {
        return new Book(
                Isbn.fromString(isbn),
                Title.fromText(title),
                authorsId
        );
    }

    private Book(Isbn isbn, Title title,UUID...authorsId) {
        this.isbn = isbn;
        this.title = title;
        this.authorsId = Set.of(authorsId);
    }

    public String getIsbn() {
        return isbn.isbnLiteral();
    }

    public String getTitle() {
        return title.title();
    }

    public void setTitle(String title) {
        this.title = Title.fromText(title);
    }

    public Set<UUID> getAuthorsIds() {
        return authorsId;
    }
}
