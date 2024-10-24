package io.github.agomezlucena.libtory.shared.queries;

import java.util.List;

public record PagedResult <T> (List<T> items, int size, int totalAmount, String sortingField, String sortingDirection) {
}
