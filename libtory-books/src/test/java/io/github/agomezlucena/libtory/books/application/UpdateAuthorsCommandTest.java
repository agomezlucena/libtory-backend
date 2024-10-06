package io.github.agomezlucena.libtory.books.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateAuthorsCommandTest {
    @Test
    @DisplayName("shouldn't allow to create a instance if UpdateType is null")
    void shouldNotAllowToCreateInstanceIfUpdateTypeIsNull() {
        var expectedMessage = "is mandatory to provide an update type";
        var obtainedError = assertThrows(
                InvalidUpdateAuthorCommand.class,
                ()->new UpdateAuthorsCommand(null,null)
        );
        assertEquals(expectedMessage, obtainedError.getMessage());
    }
    @Test
    @DisplayName("shouldn't allow to create a instance if isbn is invalid")
    void shouldNotAllowToCreateInstanceIfIsbnIsInvalid() {
        var expectedMessage = "the provided isbn is not a valid ISBN 13";
        var obtainedError = assertThrows(
                InvalidUpdateAuthorCommand.class,
                ()->new UpdateAuthorsCommand(UpdateAuthorsCommand.UpdateType.ADDITION,null)
        );
        assertEquals(expectedMessage, obtainedError.getMessage());
    }
    @Test
    @DisplayName("shouldn't allow to create a instance if authors are not present")
    void shouldNotAllowToCreateInstanceIfAuthorsAreNotPresent() {
        var expectedMessage = "is mandatory to provide an update type";
        var obtainedError = assertThrows(
                InvalidUpdateAuthorCommand.class,
                ()->new UpdateAuthorsCommand(null,null)
        );
        assertEquals(expectedMessage, obtainedError.getMessage());
    }

}