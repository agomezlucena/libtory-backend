package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.shared.DataFakerExtension;
import io.github.agomezlucena.libtory.shared.FakerIsbn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DataFakerExtension.class)
class UpdateAuthorsCommandTest {
    @Test
    @DisplayName("shouldn't allow to create a instance if UpdateType is null")
    void shouldNotAllowToCreateInstanceIfUpdateTypeIsNull() {
        var expectedMessage = "is mandatory to provide an update type";
        var obtainedError = assertThrows(
                InvalidUpdateAuthorCommand.class,
                () -> new UpdateAuthorsCommand(null, null)
        );
        assertEquals(expectedMessage, obtainedError.getMessage());
    }

    @Test
    @DisplayName("shouldn't allow to create a instance if isbn is invalid")
    void shouldNotAllowToCreateInstanceIfIsbnIsInvalid() {
        var expectedMessage = "the provided isbn is not a valid ISBN 13";
        var obtainedError = assertThrows(
                InvalidUpdateAuthorCommand.class,
                () -> new UpdateAuthorsCommand(UpdateAuthorsCommand.UpdateType.ADDITION, null)
        );
        assertEquals(expectedMessage, obtainedError.getMessage());
    }

    @Test
    @DisplayName("shouldn't allow to create a instance if authors are not present")
    void shouldNotAllowToCreateInstanceIfAuthorsAreNotPresent(@FakerIsbn String isbn) {
        var expectedMessage = "is mandatory to provide authors for addition or deletion";
        var obtainedError = assertThrows(
                InvalidUpdateAuthorCommand.class,
                () -> new UpdateAuthorsCommand(UpdateAuthorsCommand.UpdateType.ADDITION, isbn)
        );
        assertEquals(expectedMessage, obtainedError.getMessage());
    }

    @Test
    @DisplayName("should allow access to the data that holds")
    void shouldAllowAccessToTheDataThatHolds(@FakerIsbn String givenIsbn) {
        var giveUpdateType = UpdateAuthorsCommand.UpdateType.DELETION;
        var givenAuthorId = UUID.randomUUID();

        var result = assertDoesNotThrow(() -> new UpdateAuthorsCommand(giveUpdateType, givenIsbn, givenAuthorId));

        assertEquals(givenIsbn, result.getIsbn().isbnLiteral());
        assertEquals(giveUpdateType, result.updateType());
        assertThat(result.authorsIds()).containsExactly(givenAuthorId);
    }


    @Test
    @DisplayName("should allow to add various authors")
    void shouldAllowToAddVariousAuthors(@FakerIsbn String givenIsbn) {
        var giveUpdateType = UpdateAuthorsCommand.UpdateType.DELETION;
        var givenFirstAuthorId = UUID.randomUUID();
        var givenSecondAuthorId = UUID.randomUUID();

        var result = assertDoesNotThrow(() -> new UpdateAuthorsCommand(
                        giveUpdateType,
                        givenIsbn,
                        givenFirstAuthorId,
                        givenSecondAuthorId
                )
        );

        assertEquals(givenIsbn, result.getIsbn().isbnLiteral());
        assertEquals(giveUpdateType, result.updateType());
        assertThat(result.authorsIds()).containsExactlyInAnyOrder(givenFirstAuthorId, givenSecondAuthorId);
    }
}