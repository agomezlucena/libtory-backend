package io.github.agomezlucena.libtory.books.domain;

import io.github.agomezlucena.libtory.shared.queries.PagedQuery;
import io.github.agomezlucena.libtory.shared.queries.PaginatedResult;

import java.util.Optional;

public interface BookProjectionRepository {
    PaginatedResult<BookProjection> findAllProjections(PagedQuery<BookProjection> query);
    Optional<BookProjection> findProjectionByIsbn(Isbn isbn);
}
