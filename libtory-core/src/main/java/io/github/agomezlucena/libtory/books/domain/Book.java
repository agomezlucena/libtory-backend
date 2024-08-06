package io.github.agomezlucena.libtory.books.domain;

public class Book {
    private final Isbn isbn;
    private Title title;

    public static Book createBook(String givenIsbn, String title) {
        return new Book(
                Isbn.fromString(givenIsbn),
                Title.fromText(title)
        );
    }

    private Book(Isbn isbn, Title title) {
        this.isbn = isbn;
        this.title = title;
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
}
