package io.github.agomezlucena.libtory.books.infrastructure.database;

import io.github.agomezlucena.libtory.books.domain.BookProjection;
import io.github.agomezlucena.libtory.books.domain.BookProjectionRepository;
import io.github.agomezlucena.libtory.books.domain.Isbn;
import io.github.agomezlucena.libtory.books.infrastructure.database.mappers.BookProjectionMapper;
import io.github.agomezlucena.libtory.shared.queries.PagedQuery;
import io.github.agomezlucena.libtory.shared.queries.PagedResult;

import java.util.Collection;
import java.util.Optional;

public class BookProjectionMyBatisRepository implements BookProjectionRepository {

    private final BookProjectionMapper mapper;

    public BookProjectionMyBatisRepository(final BookProjectionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public PagedResult<BookProjection> findAllProjections(PagedQuery query) {
        var items = mapper.getAllBooks(query);
        var totalBooks = mapper.countAllBooks();
        return new PagedResult<>(
                items,
                Optional.ofNullable(items).map(Collection::size).orElse(0),
                totalBooks,
                query.sortingField(),
                Optional.ofNullable(query.sortingField())
                        .map(it -> query.sortingDirection())
                        .orElse(null)
        );
    }

    @Override
    public Optional<BookProjection> findProjectionByIsbn(Isbn isbn) {
        return Optional.ofNullable(mapper.findByIsbn(isbn.isbnLiteral()));
    }

}
