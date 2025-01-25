package io.github.agomezlucena.libtory.shared.queries;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record PaginatedResult<T> (
        List<T> items,
        int size,
        int totalAmount,
        String sortingField,
        String sortingDirection
) {
    public <U> PaginatedResult<U> map(Function<? super T, U> mapper) {
        return new PaginatedResult<>(
                this.items.stream().map(mapper).toList(),
                size,
                totalAmount,
                sortingField,
                sortingDirection
        );
    }
}
