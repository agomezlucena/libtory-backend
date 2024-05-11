package io.github.agomezlucena.libtory.books.domain.valueobjects;

public abstract class PublicationId {
    private final String id;

    protected PublicationId(String id) {
        this.id = id;
    }

    public static PublicationId fromIsbnString(String isbn) {
        return Isbn.fromString(isbn);
    }

    public String getId() {
        return id;
    }
}
