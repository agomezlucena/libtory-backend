package io.github.agomezlucena.libtory.books.infrastructure.database;

import io.github.agomezlucena.libtory.books.domain.BookProjection;
import io.github.agomezlucena.libtory.shared.queries.AbstractPagedQuery;

import java.util.Objects;
import java.util.Set;

public class BookProjectionPaginatedQuery extends AbstractPagedQuery<BookProjection> {
    private static final Set<String> validSortingFields = Set.of("isbn","title","author_name");

    public BookProjectionPaginatedQuery(Integer page, Integer size, String sortingField, String sortingDirection) {
        super(page, size, sortingField, sortingDirection);
    }

    @Override
    protected boolean sortingFieldIsInvalid(String sortingField) {
        return Objects.nonNull(sortingField) && !validSortingFields.contains(sortingField.toLowerCase());
    }
}
