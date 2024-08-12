package io.github.agomezlucena.libtory.books.domain;

import java.util.Set;
import java.util.UUID;

import static io.github.agomezlucena.libtory.books.domain.AuthorUpdateCommand.AddAuthors;
import static io.github.agomezlucena.libtory.books.domain.AuthorUpdateCommand.DeleteAuthor;

/**
 * Book class represent a book this is the aggregate root for
 * Book management bounded context.
 * @author Alejandro Gómez Lucena
 */
public class Book {
    private final Isbn isbn;
    private Title title;
    private AuthorsId authorsId;

    /**
     * create a book from inputs ports with the information of the given primitives, and will check
     * the existence of the authors passed in the primitives.
     * @param primitives an object with the information of the book as primitives.
     * @param checker an object that check that authors in the primitives objects exists in our systems.
     * @return a book with well-formed isbn, not empty title, and a set of authors if any was passed.
     * @throws InvalidIsbn if passed ISBN is not well-formed.
     * @throws InvalidTitle if the passed title is null or blank.
     * @throws InvalidAuthor if any of the passed authors is not registered in our systems.
     */
    public static Book createBook(BookPrimitives primitives, AuthorChecker checker) {
        final var authorIds = primitives.authorsAsArray();

        if(authorIds.length > 0 && !checker.authorsExists(authorIds)) throw new InvalidAuthor();

        return new Book(
                Isbn.fromString(primitives.isbn()),
                Title.fromText(primitives.title()),
                AuthorsId.from(authorIds)
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

    /**
     * update authors adding or deleting the given authors based in the command past by parameter.
     * @param command and adding author command or deleting author command.
     * @throws InvalidAuthor if you try to add a not registered author in our systems.
     */
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
