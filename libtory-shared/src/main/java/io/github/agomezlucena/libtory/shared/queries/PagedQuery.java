package io.github.agomezlucena.libtory.shared.queries;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public record PagedQuery(Integer page, Integer size, String sortingField, String sortingDirection) {
    private static final int MINIMUM_SIZE = 1;
    private static final int MINIMUM_PAGE = 0;
    private static final String INVALID_SORTING_FIELD_TEMPLATE = "field %s is not valid for this query";
    private static final Set<String> VALID_SORTING_DIRECTIONS = Set.of("ASC", "DESC");

    public PagedQuery(Integer page, Integer size, String sortingField, String sortingDirection) {
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

        this.page = Optional.ofNullable(page).orElse(0);
        this.size = Optional.ofNullable(size).orElse(10);
        this.sortingField = sortingField;
        this.sortingDirection = Optional.ofNullable(sortingDirection)
                .orElse("ASC");
    }

    public void validateSortingField(Predicate<String> sortingFieldChecker) {
        if (!sortingFieldChecker.test(sortingField)) {
            throw new InvalidQuery(String.format(INVALID_SORTING_FIELD_TEMPLATE, sortingField));
        }
    }

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
}
