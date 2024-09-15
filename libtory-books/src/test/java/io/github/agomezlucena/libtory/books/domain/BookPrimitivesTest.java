package io.github.agomezlucena.libtory.books.domain;

import io.github.agomezlucena.libtory.shared.DataFakerExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static io.github.agomezlucena.libtory.shared.DataFakerExtension.FakerIsbn;
import static io.github.agomezlucena.libtory.shared.DataFakerExtension.FakerBookTitle;

@ExtendWith(DataFakerExtension.class)
@DisplayName("BookPrimitives should")
class BookPrimitivesTest {
    @Test
    @DisplayName("allow creation without authors id")
    void shouldAllowCreationWithoutAuthorId(@FakerIsbn String givenIsbn, @FakerBookTitle String givenTitle) {
        var obtainedValue = assertDoesNotThrow(()-> new BookPrimitives(givenIsbn, givenTitle));
        assertEquals(givenIsbn, obtainedValue.isbn());
        assertEquals(givenTitle, obtainedValue.title());
        assertNotNull(obtainedValue.authors());
        assertInstanceOf(HashSet.class, obtainedValue.authors());
        assertTrue(obtainedValue.authors().isEmpty());
    }

    @Test
    @DisplayName("allow to add new author id if dont have any author")
    void shouldAllowToAddNewAuthorIdIfDontHaveAnyAuthor(
            @FakerIsbn String givenIsbn,
            @FakerBookTitle String givenTitle
    ){
        var givenAuthorId = UUID.randomUUID();
        var obtainedValue = new BookPrimitives(givenIsbn, givenTitle);
        assertDoesNotThrow(()->obtainedValue.addAuthor(givenAuthorId));

        var authorAsArray = obtainedValue.authorsAsArray();

        assertThat(authorAsArray)
                .isNotEmpty()
                .containsOnly(givenAuthorId);
    }

    @Test
    @DisplayName("allow to add a new author id if already have some")
    void shouldAllowToAddNewOneIfAlreadyHasAuthors(
            @FakerIsbn String givenIsbn,
            @FakerBookTitle String givenTitle
    ){
        var givenFirstAuthorId = UUID.randomUUID();
        var givenSecondAuthorId = UUID.randomUUID();
        var givenThirdAuthorId = UUID.randomUUID();
        var obtainedValue = new BookPrimitives(givenIsbn, givenTitle,givenFirstAuthorId,givenSecondAuthorId);

        assertDoesNotThrow(()->obtainedValue.addAuthor(givenThirdAuthorId));
        var authorAsArray = obtainedValue.authorsAsArray();
        assertThat(authorAsArray)
                .isNotEmpty()
                .containsOnly(givenFirstAuthorId, givenSecondAuthorId,givenThirdAuthorId);
    }

    @Test
    @DisplayName("throw the expected domain exception if is not valid")
    void shouldThrowTheExpectedDomainExceptionIfIsNotValid(
            @FakerIsbn String givenValidIsbn,
            @FakerBookTitle String givenValidTitle
    ){
        var firstTestSubject = new BookPrimitives("", givenValidTitle);
        var secondTestSubject = new BookPrimitives(givenValidIsbn, "");
        assertThrows(InvalidIsbn.class,firstTestSubject::toBook);
        assertThrows(InvalidTitle.class,secondTestSubject::toBook);
    }
}