package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.books.domain.BookProjection;
import io.github.agomezlucena.libtory.books.domain.BookProjectionRepository;
import io.github.agomezlucena.libtory.shared.queries.*;

public class QueryBooksPaginatedUseCase implements QueryHandler<PagedQuery<BookProjection>, PagedResult<BookProjection>> {

    private final BookProjectionRepository repository;

    public QueryBooksPaginatedUseCase(BookProjectionRepository repository) {
        this.repository = repository;
    }

    @Override
    public PagedResult<BookProjection> handle(PagedQuery<BookProjection> query) {
        return repository.findAllProjections(query);
    }
}
