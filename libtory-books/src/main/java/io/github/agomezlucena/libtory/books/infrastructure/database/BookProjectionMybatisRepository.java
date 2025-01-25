package io.github.agomezlucena.libtory.books.infrastructure.database;

import io.github.agomezlucena.libtory.books.domain.BookProjection;
import io.github.agomezlucena.libtory.books.domain.BookProjectionRepository;
import io.github.agomezlucena.libtory.books.domain.Isbn;
import io.github.agomezlucena.libtory.books.infrastructure.database.mappers.BookProjectionMapper;
import io.github.agomezlucena.libtory.shared.queries.PagedQuery;
import io.github.agomezlucena.libtory.shared.queries.PaginatedResult;

import java.util.Collection;
import java.util.Optional;

public class BookProjectionMybatisRepository implements BookProjectionRepository {

    private final BookProjectionMapper mapper;

    public BookProjectionMybatisRepository(BookProjectionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public PaginatedResult<BookProjection> findAllProjections(PagedQuery<BookProjection> query) {
        var items = mapper.getAllBooks(query);
        var totalBooks = mapper.countAllBooks();
        return new PaginatedResult<>(
                items,
                Optional.ofNullable(items).map(Collection::size).orElse(0),
                totalBooks,
                query.getSortingField(),
                Optional.ofNullable(query.getSortingField())
                        .map(it -> query.getSortingDirection())
                        .orElse(null)
        );
    }

    @Override
    public Optional<BookProjection> findProjectionByIsbn(Isbn isbn) {
        return Optional.ofNullable(mapper.findByIsbn(isbn.isbnLiteral()));
    }
}
