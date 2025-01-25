package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.books.domain.BookProjection;
import io.github.agomezlucena.libtory.books.domain.BookProjectionRepository;
import io.github.agomezlucena.libtory.shared.queries.*;

public class QueryBooksPaginatedUseCase
        implements QueryHandler<BookProjectionPaginatedQuery, PaginatedResult<BookProjection>> {

    private final BookProjectionRepository repository;

    public QueryBooksPaginatedUseCase(BookProjectionRepository repository) {
        this.repository = repository;
    }

    @Override
    public PaginatedResult<BookProjection> handle(BookProjectionPaginatedQuery query) {
        return repository.findAllProjections(query);
    }
}
