package io.github.agomezlucena.libtory.shared.queries;

import java.util.Optional;
import java.util.Set;

public abstract class AbstractPagedQuery<T> implements PagedQuery<T> {
    private static final int MINIMUM_SIZE = 1;
    private static final int MINIMUM_PAGE = 0;
    private static final String INVALID_SORTING_FIELD_TEMPLATE = "field %s is not valid for this query";
    private static final Set<String> VALID_SORTING_DIRECTIONS = Set.of("ASC", "DESC");

    private final Integer page;
    private final Integer size;
    private final String sortingField;
    private final String sortingDirection;

    public AbstractPagedQuery(Integer page, Integer size, String sortingField, String sortingDirection) {
        if (pageHaveNegativeValue(page)) {
            throw new InvalidQuery("invalid paginated query page should be at least 0");
        }

        if (sizeIsLessThanMinimum(size)) {
            throw new InvalidQuery("invalid paginated query size should be at least 1");
        }

        if (sortingFieldIsNullAndSortingDirectionNot(sortingField, sortingDirection)) {
            throw new InvalidQuery(
                    "invalid paginated query sorting field is required when sorting direction is defined"
            );
        }

        if (sortingFieldIsDefinedAndSortingDirectionIsInvalid(sortingField, sortingDirection)) {
            throw new InvalidQuery("invalid paginated query the given sorting direction is invalid");
        }

        if (sortingFieldIsInvalid(sortingField)) {
            throw new InvalidQuery(String.format(INVALID_SORTING_FIELD_TEMPLATE, sortingField));
        }

        this.page = Optional.ofNullable(page).orElse(0);
        this.size = Optional.ofNullable(size).orElse(10);
        this.sortingField = sortingField;
        this.sortingDirection = Optional.ofNullable(sortingDirection)
                .orElse("ASC");

    }

    protected abstract boolean sortingFieldIsInvalid(String sortingField);

    private boolean pageHaveNegativeValue(Integer page) {
        return page != null && page < MINIMUM_PAGE;
    }

    private boolean sizeIsLessThanMinimum(Integer size) {
        return size != null && size < MINIMUM_SIZE;
    }

    private boolean sortingFieldIsNullAndSortingDirectionNot(String sortingField, String sortingDirection) {
        return sortingField == null && sortingDirection != null;
    }

    private boolean sortingFieldIsDefinedAndSortingDirectionIsInvalid(String sortingField, String sortingDirection) {
        return sortingField != null &&
                (sortingDirection != null && !VALID_SORTING_DIRECTIONS.contains(sortingDirection));
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getSortingField() {
        return sortingField;
    }

    @Override
    public String getSortingDirection() {
        return sortingDirection;
    }
}
