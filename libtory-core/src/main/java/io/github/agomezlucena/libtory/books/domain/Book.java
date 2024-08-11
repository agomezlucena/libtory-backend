package io.github.agomezlucena.libtory.books.domain;

import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public class Book {
    private final Isbn isbn;
    private Title title;
    private AuthorsId authorsId;

    public static Book createBook(String isbn, String title, UUID...authorsId) {
        return new Book(
                Isbn.fromString(isbn),
                Title.fromText(title),
                AuthorsId.from(authorsId)
        );
    }

    private Book(Isbn isbn, Title title, AuthorsId authorsId) {
        this.isbn = isbn;
        this.title = title;
        this.authorsId = authorsId;
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
        return authorsId.ids();
    }

    public BiConsumer<BookRepository, AuthorChecker> addAuthors(UUID...authorIds) {
        return (repository,checker) -> {
            if(!checker.authorsExists(authorIds)) throw new InvalidAuthor();
            this.authorsId = this.authorsId.addAuthors(authorIds);
            repository.save(this);
        };
    }
}
