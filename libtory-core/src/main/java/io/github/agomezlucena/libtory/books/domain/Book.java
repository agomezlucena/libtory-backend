package io.github.agomezlucena.libtory.books.domain;

import java.util.Set;
import java.util.UUID;

import static io.github.agomezlucena.libtory.books.domain.AuthorUpdateCommand.AddAuthors;
import static io.github.agomezlucena.libtory.books.domain.AuthorUpdateCommand.DeleteAuthor;

public class Book {
    private final Isbn isbn;
    private Title title;
    private AuthorsId authorsId;

    public static Book createBook(BookPrimitives primitives) {
        return new Book(
                Isbn.fromString(primitives.isbn()),
                Title.fromText(primitives.title()),
                AuthorsId.from(primitives.authors().toArray(new UUID[0]))
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

    public void updateAuthors(AuthorUpdateCommand command) {
        switch (command) {
            case AddAuthors(var checker, var repo, var ids) -> addAuthors(repo, checker, ids);
            case DeleteAuthor(var repo, var ids) -> deleteAuthors(repo,ids);
        }
    }

    private void addAuthors(BookRepository repository, AuthorChecker checker, UUID... authorIds) {
        if (!checker.authorsExists(authorIds)) throw new InvalidAuthor();
        this.authorsId = this.authorsId.addAuthors(authorIds);
        repository.save(this);
    }

    private void deleteAuthors(BookRepository repository, UUID...authorIds){
        var currentAuthors = this.authorsId;
        var newAuthors = currentAuthors.remove(authorIds);
        if(currentAuthors.equals(newAuthors)){
            return;
        }

        this.authorsId = newAuthors;
        repository.save(this);
    }
}
