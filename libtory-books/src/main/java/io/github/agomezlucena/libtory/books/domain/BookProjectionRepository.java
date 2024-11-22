package io.github.agomezlucena.libtory.books.domain;

import io.github.agomezlucena.libtory.shared.queries.PagedQuery;
import io.github.agomezlucena.libtory.shared.queries.PagedResult;

import java.util.Optional;

public interface BookProjectionRepository {
    PagedResult<BookProjection> findAllProjections(PagedQuery query);
    Optional<BookProjection> findProjectionByIsbn(Isbn isbn);
}
