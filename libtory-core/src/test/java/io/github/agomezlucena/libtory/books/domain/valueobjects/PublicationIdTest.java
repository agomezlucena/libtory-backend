package io.github.agomezlucena.libtory.books.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PublicationIdTest {

    @Test
    @DisplayName("should allow the creation of book id from a ISBN when matches with a 13 digits string")
    void shouldAllowTheCreationOfBookIdFromISBNWhenMatchesWithA13DigitsString() {
        String givenId ="979-8-886-45174-0";
        PublicationId obtainedValue = PublicationId.fromIsbnString(givenId);
        assertNotNull(obtainedValue);
        assertEquals(givenId, obtainedValue.getId());
    }
}