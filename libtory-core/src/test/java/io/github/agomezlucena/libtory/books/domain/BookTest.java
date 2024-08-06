package io.github.agomezlucena.libtory.books.domain;

import io.github.agomezlucena.libtory.shared.DataFakerExtension;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Book should")
@ExtendWith(DataFakerExtension.class)
class BookTest {

    @Test
    @DisplayName("allow to create a book with an valid isbn and give access to it")
    void shouldAllowToCreateABookWithValidIsbnAndGiveAccessToIt(Faker faker) {
        var givenIsbn = "978-0-596-52068-7";

        var obtainedBook = Book.createBook(givenIsbn, faker.book().title());

        assertNotNull(obtainedBook);
        assertEquals(givenIsbn,obtainedBook.getIsbn());
    }

    @Test
    @DisplayName("allow to access to the title")
    void shouldAllowToAccessToTheTitle(Faker faker) {
        var givenTitle = faker.book().title();

        var obtainedBook = Book.createBook(faker.code().isbn13(), givenTitle);

        assertNotNull(obtainedBook);
        assertEquals(givenTitle,obtainedBook.getTitle());
    }

    @Test
    @DisplayName("allow to modify the title")
    void shouldAllowToModifyTheTitle(Faker faker) {
        var originalTitle = faker.book().title();
        var secondTitle = faker.book().title();
        var testSubject = Book.createBook(faker.code().isbn13(), originalTitle);

        testSubject.setTitle(secondTitle);

        var obtainedValue = testSubject.getTitle();
        assertNotEquals(originalTitle,obtainedValue);
        assertEquals(secondTitle,obtainedValue);
    }

    @ParameterizedTest(name = "a string is considered empty if: {0}")
    @MethodSource("emptyTitles")
    @DisplayName("not allow to create a book without a title")
    void shouldNotAllowToCreateABookWithoutATitle(
            @SuppressWarnings("unused") String emptyRule,
            String title,
            Faker faker
    ){
        var givenIsbn = faker.code().isbn13();
        var expectedMessage = "given title is invalid";
        var result = assertThrows(InvalidTitle.class,()->Book.createBook(givenIsbn,title)).getMessage();
        assertEquals(expectedMessage,result);
    }

    @ParameterizedTest(name= "a string is considered empty if: {0}")
    @MethodSource("emptyTitles")
    @DisplayName("not allow to modify a book title with an empty one")
    void shouldNotAllowToModifyABookTitleWithAnEmptyOne(
            @SuppressWarnings("unused") String emptyRule,
            String title,
            Faker faker
    ){
        var testSubject = Book.createBook(faker.code().isbn13(),faker.book().title());
        assertThrows(InvalidTitle.class,()->testSubject.setTitle(title));
    }

    private static Stream<Arguments> emptyTitles(){
        return Stream.of(
                arguments("is null",null),
                arguments("is empty",""),
                arguments("contains only empty characters","                         ")
        );
    }
}