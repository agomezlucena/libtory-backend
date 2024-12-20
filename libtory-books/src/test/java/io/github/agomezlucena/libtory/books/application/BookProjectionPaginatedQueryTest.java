package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.shared.queries.InvalidQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookProjectionPaginatedQueryTest {

    @ParameterizedTest(name = "{0}: is an allowed value")
    @DisplayName("should only allow to create sorted queries on one of the following fields")
    @ValueSource(strings = {"title", "isbn", "author_name", "null"})
    void shouldOnlyAllowToCreateSortedQueriesOnOneOfTheFollowingFields(String sortingField) {
        var givenSortingField = ("null".equals(sortingField)) ? null : sortingField;
        assertDoesNotThrow(
                () -> new BookProjectionPaginatedQuery(
                        0,
                        10,
                        givenSortingField,
                        null
                )
        );
    }

    @Test
    @DisplayName("should throw on any different value")
    void shouldThrowOnAnyDifferentValue() {
        assertThrows(
                InvalidQuery.class,
                () -> new BookProjectionPaginatedQuery(
                        0,
                        10,
                        "not_valid_field",
                        null
                )
        );
    }
}