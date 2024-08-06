package io.github.agomezlucena.libtory.books.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("ISBN should")
class IsbnTest {

    @ParameterizedTest(name = "rule: {0}")
    @MethodSource("validInputs")
    @DisplayName("allow create a valid ISBN")
    void shouldAllowCreateAValidISBN(
            @SuppressWarnings("unused") String ruleName,
            String givenIsbnLiteral
    ) {
        Isbn obtainedValue = Isbn.fromString(givenIsbnLiteral);
        assertNotNull(obtainedValue);
        assertEquals(givenIsbnLiteral, obtainedValue.isbnLiteral());
    }

    @ParameterizedTest(name = "rule: {0}")
    @MethodSource("invalidInputs")
    @DisplayName("not allow create a invalid ISBN")
    void shouldNotAllowCreateAInvalidISBN(
            @SuppressWarnings("unused") String ruleName,
            String givenIsbnLiteral
    ) {
        String expectedMessage = String.format("ISBN: %s is invalid",givenIsbnLiteral);
        InvalidIsbn obtainedValue = assertThrows(InvalidIsbn.class,()->Isbn.fromString(givenIsbnLiteral));
        assertEquals(expectedMessage, obtainedValue.getMessage());
    }

    private static Stream<Arguments> validInputs() {
        return Stream.of(
                arguments("must allow 13 digit strings", "9780596520687"),
                arguments("must allow ISBN with hyphens", "978-0-596-52068-7"),
                arguments("must allow those 13 digits string that pass the checksum", "9780134685991"),
                arguments("must allow those ISBN with hyphens that pass the checksum", "979-8-886-45174-0")
        );
    }

    private static Stream<Arguments> invalidInputs() {
        return Stream.of(
                arguments("must not admit null values",null),
                arguments("must not admit empty values",""),
                arguments("must not admit blank values","                               "),
                arguments("must not admit letters","abc-a-23-asdfasd-1"),
                arguments("must not admit isbn with invalid checksum digit","978-0-13-468599-2"),
                arguments("must not admit isbn string of 13 digits cheksum digit","9780134685993"),
                arguments("must not admit special characters","978-@-&<->_[]{}!-#")
        );
    }
}