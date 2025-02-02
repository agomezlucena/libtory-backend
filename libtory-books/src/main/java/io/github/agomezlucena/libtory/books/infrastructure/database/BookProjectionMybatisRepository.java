package io.github.agomezlucena.libtory.books.infrastructure.database;

import io.github.agomezlucena.libtory.books.domain.BookProjection;
import io.github.agomezlucena.libtory.books.domain.BookProjectionRepository;
import io.github.agomezlucena.libtory.books.domain.Isbn;
import io.github.agomezlucena.libtory.books.infrastructure.database.mappers.BookProjectionMapper;
import io.github.agomezlucena.libtory.shared.cqrs.PagedQuery;
import io.github.agomezlucena.libtory.shared.cqrs.PaginatedResult;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class BookProjectionMybatisRepository implements BookProjectionRepository {
    private final BookProjectionMapper mapper;
    private final ExecutorService executorService;

    public BookProjectionMybatisRepository(
            BookProjectionMapper mapper,
            ExecutorService executorService
    ) {
        this.mapper = mapper;
        this.executorService = executorService;
    }

    @Override
    public PaginatedResult<BookProjection> findAllProjections(PagedQuery<BookProjection> query) {
        var itemsFuture = CompletableFuture.supplyAsync(()->mapper.getAllBooks(query),executorService);
        var totalBooksFuture = CompletableFuture.supplyAsync(mapper::countAllBooks,executorService);
        CompletableFuture.allOf(itemsFuture,totalBooksFuture).join();
        var items = itemsFuture.join();
        var totalBooks = totalBooksFuture.join();

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
