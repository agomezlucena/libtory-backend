package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.books.domain.BookProjection;
import io.github.agomezlucena.libtory.books.domain.BookProjectionRepository;
import io.github.agomezlucena.libtory.shared.cqrs.QueryHandler;

import java.util.Optional;

public class QueryBookByIsbnUseCase implements QueryHandler<BookProjectionIsbnQuery, Optional<BookProjection>> {
    private final BookProjectionRepository repository;

    public QueryBookByIsbnUseCase(BookProjectionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<BookProjection> handle(BookProjectionIsbnQuery query) {
        return repository.findProjectionByIsbn(query.getDiscriminatorValue());
    }
}
