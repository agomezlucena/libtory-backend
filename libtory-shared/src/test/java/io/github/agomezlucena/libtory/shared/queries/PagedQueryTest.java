package io.github.agomezlucena.libtory.shared.queries;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class PagedQueryTest {
    @Test
    @DisplayName("should set page to zero when value is null")
    void shouldSetPageToZeroWhenValueIsNull() {
        var testSubject = new PagedQuery(null,null,null,null);
        assertEquals(0,testSubject.page());
    }

    @Test
    @DisplayName("should set the value of size to ten when size value is null")
    void shouldSetSizeValueToTenWhenSizeValueIsNull() {
        var testSubject = new PagedQuery(null,null,null,null);
        assertEquals(10,testSubject.size());
    }

    @Test
    @DisplayName("should throw an InvalidQuery when page has a value lower than 0")
    void shouldThrowAnInvalidQueryWhenPageHasAValueLowerThan0() {
        var expectedMessage = "invalid paginated query page should be at least 0";
        var obtainedException = assertThrows(
                InvalidQuery.class,
                () -> new PagedQuery(-1,null,null,null)
        );
        assertEquals(expectedMessage, obtainedException.getMessage());
    }

    @Test
    @DisplayName("should throw an InvalidQuery when size is lower than 1")
    void shouldThrowAnInvalidQueryWhenSizeIsLowerThan1() {
        var expectedMessage = "invalid paginated query size should be at least 1";
        var obtainedException = assertThrows(
                InvalidQuery.class,
                () -> new PagedQuery(0,0,null,null)
        );
        assertEquals(expectedMessage, obtainedException.getMessage());
    }

    @Test
    @DisplayName("should throw an InvalidQuery when sortingField is not defined and sorting direction is defined")
    void shouldThrowAnInvalidQueryWhenSortingFieldIsNotDefinedAndSortingDirectionIsDefined() {
        var expectedMessage = "invalid paginated query sorting field is required when sorting direction is defined";
        var obtainedException = assertThrows(
                InvalidQuery.class,
                () -> new PagedQuery(0,1,null,"ASC")
        );
        assertEquals(expectedMessage, obtainedException.getMessage());
    }

    @Test
    @DisplayName("should throw an InvalidQuery when sorting direction is not ASC or DESC or null")
    void shouldThrowAnInvalidQueryWhenSortingDirectionIsNotASC() {
        var expectedMessage = "invalid paginated query the given sorting direction is invalid";
        var obtainedException = assertThrows(
                InvalidQuery.class,
                () -> new PagedQuery(0,1,"blob","bleb")
        );
        assertEquals(expectedMessage, obtainedException.getMessage());
    }

    @Test
    @DisplayName("should set sorting direction to ASC when sorting field is defined and direction is null")
    void shouldSetSortingDirectionToAscWhenSortingFieldIsDefinedAndDirectionIsNull() {
        var expectedDirection = "ASC";
        var testSubject = new PagedQuery(null, null, "title",null);
        assertEquals(expectedDirection,testSubject.sortingDirection());
    }

    @Test
    @DisplayName("should throw an InvalidQuery when the sorting field does not match with the validation predicate")
    void shouldThrowAnInvalidQueryWhenTheSortingFieldDoesNotMatchWIthTheValidationPredicate(){
        var expectedMessage= "field title is not valid for this query";
        Predicate<String> givenPredicate = (ignored) -> false;
        var testSubject = new PagedQuery(null, null, "title",null);
        var obtainedException = assertThrows(
                InvalidQuery.class,
                ()-> testSubject.validateSortingField(givenPredicate)
        );
        assertEquals(expectedMessage, obtainedException.getMessage());

    }
}