package io.github.agomezlucena.libtory.book;

import io.github.agomezlucena.libtory.books.application.BookProjectionIsbnQuery;
import io.github.agomezlucena.libtory.books.application.BookProjectionPaginatedQuery;
import io.github.agomezlucena.libtory.books.domain.Author;
import io.github.agomezlucena.libtory.books.domain.BookPrimitives;
import io.github.agomezlucena.libtory.books.domain.BookProjection;
import io.github.agomezlucena.libtory.books.domain.Isbn;
import io.github.agomezlucena.libtory.rest.model.LibtoryEntity;
import io.github.agomezlucena.libtory.rest.model.PagedResult;
import io.github.agomezlucena.libtory.rest.model.Property;
import io.github.agomezlucena.libtory.shared.cqrs.CommandBus;
import io.github.agomezlucena.libtory.shared.cqrs.PaginatedResult;
import io.github.agomezlucena.libtory.shared.cqrs.QueryBus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.github.agomezlucena.libtory.rest.model.LibtoryEntity.TypeEnum.AUTHOR;
import static io.github.agomezlucena.libtory.rest.model.LibtoryEntity.TypeEnum.BOOK;

@Service
public class BookService {
    private final CommandBus commandBus;
    private final QueryBus queryBus;

    public BookService(
            @Qualifier("booksCommandBus") CommandBus commandBus,
            @Qualifier("booksQueryBus") QueryBus queryBus
    ) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }


    public PagedResult getAllBooks(Integer size, Integer page, String sortingDirection, String sortingField) {
        var query = new BookProjectionPaginatedQuery(size, page, sortingDirection, sortingField);
        PaginatedResult<BookProjection> result = queryBus.handle(query);
        return new PagedResult(result.map(this::fromBook).items(), result.size(), result.totalAmount());
    }

    public void saveBook(String isbn, LibtoryEntity book) {
        if (book.getType() != BOOK) {
            throw new IllegalArgumentException("the given entity isn't a book");
        }
        commandBus.sendCommand(bookPrimitivesFromEntity(isbn, book));
    }

    public Optional<LibtoryEntity> getByIsbn(String isbn) {
        var isbnQuery = new BookProjectionIsbnQuery(isbn);
        Optional<BookProjection> result = queryBus.handle(isbnQuery);
        return result.map(this::fromBook);
    }

    public void deleteByIsbn(String isbn) {
        commandBus.sendCommand(Isbn.fromString(isbn));
    }

    private BookPrimitives bookPrimitivesFromEntity(String isbn, LibtoryEntity entity) {
        var properties = getProperties(entity);
        return new BookPrimitives(
                isbn,
                (String) properties.get("title"),
                entity.getRelatedEntities()
                        .stream()
                        .filter(it -> it.getType().equals(AUTHOR))
                        .map(LibtoryEntity::getId)
                        .map(UUID::fromString)
                        .toArray(UUID[]::new)
        );
    }

    private Map<String, Object> getProperties(LibtoryEntity entity) {
        return entity.getProperties()
                .stream()
                .collect(Collectors.toMap(Property::getField, Property::getValue));
    }

    private LibtoryEntity fromBook(BookProjection bookProjection) {
        var result = new LibtoryEntity();

        result.setType(BOOK);
        result.setId(bookProjection.isbn());
        result.url(URI.create("/books/" + bookProjection.isbn()));
        result.addPropertiesItem(new Property().field("title").value(bookProjection.title()));

        bookProjection
                .authors()
                .stream()
                .map(this::fromAuthor)
                .forEach(result::addRelatedEntitiesItem);

        return result;
    }

    private LibtoryEntity fromAuthor(Author author) {
        if (author.authorId() == null) {
            return null;
        }
        return new LibtoryEntity()
                .id(author.authorId().toString())
                .type(AUTHOR)
                .addPropertiesItem(
                        new Property()
                                .field("name")
                                .value(author.name())
                );
    }
}
