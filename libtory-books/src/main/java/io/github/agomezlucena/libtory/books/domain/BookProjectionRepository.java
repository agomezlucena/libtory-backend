package io.github.agomezlucena.libtory.books.domain;

import io.github.agomezlucena.libtory.shared.cqrs.PagedQuery;
import io.github.agomezlucena.libtory.shared.cqrs.PaginatedResult;

import java.util.Optional;

public interface BookProjectionRepository {
    PaginatedResult<BookProjection> findAllProjections(PagedQuery<BookProjection> query);
    Optional<BookProjection> findProjectionByIsbn(Isbn isbn);
}
