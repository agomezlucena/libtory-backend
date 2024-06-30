package io.github.agomezlucena.libtory.books.domain;

public class Book {
    private final Isbn isbn;
    private String title;

    public static Book createBook(String givenIsbn, String title) {
        if(title == null || title.isBlank()) throw new InvalidTitle();
        return new Book(
                Isbn.fromString(givenIsbn),
                title
        );
    }

    private Book(Isbn isbn, String title) {
        this.isbn = isbn;
        this.title = title;
    }

    public String getIsbn() {
        return isbn.getValue();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(title == null || title.isBlank()) throw new InvalidTitle();
        this.title = title;
    }
}
