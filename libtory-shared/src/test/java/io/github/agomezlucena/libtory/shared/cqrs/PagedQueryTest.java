package io.github.agomezlucena.libtory.shared.cqrs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PagedQueryTest {
    @Test
    @DisplayName("should set page to zero when value is null")
    void shouldSetGetPageToZeroWhenValueIsNull() {
        var testSubject = new TestPagedQuery(null, null, null, null);
        assertEquals(0, testSubject.getPage());
    }

    @Test
    @DisplayName("should set the value of size to ten when size value is null")
    void shouldSetSizeValueToTenWhenGetSizeValueIsNull() {
        var testSubject = new TestPagedQuery(null, null, null, null);
        assertEquals(10, testSubject.getSize());
    }

    @Test
    @DisplayName("should throw an InvalidQuery when page has a value lower than 0")
    void shouldThrowAnInvalidQueryWhenGetPageHasAValueLowerThan0() {
        var expectedMessage = "invalid paginated query page should be at least 0";
        var obtainedException = assertThrows(
                InvalidQuery.class,
                () -> new TestPagedQuery(-1, null, null, null)
        );
        assertEquals(expectedMessage, obtainedException.getMessage());
    }

    @Test
    @DisplayName("should throw an InvalidQuery when size is lower than 1")
    void shouldThrowAnInvalidQueryWhenGetSizeIsLowerThan1() {
        var expectedMessage = "invalid paginated query size should be at least 1";
        var obtainedException = assertThrows(
                InvalidQuery.class,
                () -> new TestPagedQuery(0, 0, null, null)
        );
        assertEquals(expectedMessage, obtainedException.getMessage());
    }

    @Test
    @DisplayName("should throw an InvalidQuery when sortingField is not defined and sorting direction is defined")
    void shouldThrowAnInvalidQueryWhenSortingFieldIsNotDefinedAndGetGetSortingDirectionIsDefined() {
        var expectedMessage = "invalid paginated query sorting field is required when sorting direction is defined";
        var obtainedException = assertThrows(
                InvalidQuery.class,
                () -> new TestPagedQuery(0, 1, null, "ASC")
        );
        assertEquals(expectedMessage, obtainedException.getMessage());
    }

    @Test
    @DisplayName("should throw an InvalidQuery when sorting direction is not ASC or DESC or null")
    void shouldThrowAnInvalidQueryWhenGetSortingDirectionIsNotASC() {
        var expectedMessage = "invalid paginated query the given sorting direction is invalid";
        var obtainedException = assertThrows(
                InvalidQuery.class,
                () -> new TestPagedQuery(0, 1, "test", "bleb")
        );
        assertEquals(expectedMessage, obtainedException.getMessage());
    }

    @Test
    @DisplayName("should set sorting direction to ASC when sorting field is defined and direction is null")
    void shouldSetSortingDirectionToAscWhenGetGetSortingFieldIsDefinedAndDirectionIsNull() {
        var expectedDirection = "ASC";
        var testSubject = new TestPagedQuery(null, null, "title", null);
        assertEquals(expectedDirection, testSubject.getSortingDirection());
    }

    @Test
    @DisplayName("should throw an InvalidQuery when the sorting field does not match with the validation predicate")
    void shouldThrowAnInvalidQueryWhenTheGetSortingFieldDoesNotMatchWIthTheValidationPredicate() {
        var expectedMessage = "field invalidField is not valid for this query";
        var obtainedException = assertThrows(
                InvalidQuery.class,
                () -> new TestPagedQuery(null, null, "invalidField", null)
        );
        assertEquals(expectedMessage, obtainedException.getMessage());

    }

    public static class TestPagedQuery extends AbstractPagedQuery<Object> {

        public TestPagedQuery(Integer page, Integer size, String sortingField, String sortingDirection) {
            super(page, size, sortingField, sortingDirection);
        }

        @Override
        public boolean sortingFieldIsInvalid(String sortingField) {
            return Objects.nonNull(sortingField) && !sortingField.equals("title");
        }
    }
}