package io.github.agomezlucena.libtory.books.domain;

import io.github.agomezlucena.libtory.shared.DataFakerExtension;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("Book should")
@ExtendWith(DataFakerExtension.class)
class BookTest {
    private final AuthorChecker defaultAuthorChecker = mock(AuthorChecker.class);

    @BeforeEach
    void setUpDefaultMocks(){
        reset(defaultAuthorChecker);
        when(defaultAuthorChecker.authorsExists(notNull(UUID[].class))).thenReturn(true);
    }

    @Test
    @DisplayName("allow to create a book with an valid isbn and give access to it")
    void shouldAllowToCreateABookWithValidIsbnAndGiveAccessToIt(Faker faker) {
        var givenIsbn = "978-0-596-52068-7";
        var primitives = new BookPrimitives(givenIsbn, faker.book().title());
        var obtainedBook = Book.createBook(primitives,this.defaultAuthorChecker);

        assertNotNull(obtainedBook);
        assertEquals(givenIsbn, obtainedBook.getIsbn());
    }

    @Test
    @DisplayName("allow to access to the title")
    void shouldAllowToAccessToTheTitle(Faker faker) {
        var givenTitle = faker.book().title();
        var givenPrimitive = new BookPrimitives(faker.code().isbn13(), givenTitle);
        var obtainedBook = Book.createBook(givenPrimitive,this.defaultAuthorChecker);

        assertNotNull(obtainedBook);
        assertEquals(givenTitle, obtainedBook.getTitle());
    }

    @Test
    @DisplayName("allow to modify the title")
    void shouldAllowToModifyTheTitle(Faker faker) {
        var originalTitle = faker.book().title();
        var secondTitle = faker.book().title();
        var primitives = new BookPrimitives(faker.code().isbn13(), originalTitle);
        var testSubject = Book.createBook(primitives,this.defaultAuthorChecker);

        testSubject.setTitle(secondTitle);

        var obtainedValue = testSubject.getTitle();
        assertNotEquals(originalTitle, obtainedValue);
        assertEquals(secondTitle, obtainedValue);
    }

    @ParameterizedTest(name = "a string is considered empty if: {0}")
    @MethodSource("emptyTitles")
    @DisplayName("not allow to create a book without a title")
    void shouldNotAllowToCreateABookWithoutATitle(
            @SuppressWarnings("unused") String emptyRule,
            String title,
            Faker faker
    ) {
        var givenIsbn = faker.code().isbn13();
        var expectedMessage = "given title is invalid";
        var givenPrimitives = new BookPrimitives(givenIsbn, title);
        var result = assertThrows(
                InvalidTitle.class,
                () -> Book.createBook(givenPrimitives,this.defaultAuthorChecker)
        ).getMessage();

        assertEquals(expectedMessage, result);
    }

    @ParameterizedTest(name = "a string is considered empty if: {0}")
    @MethodSource("emptyTitles")
    @DisplayName("not allow to modify a book title with an empty one")
    void shouldNotAllowToModifyABookTitleWithAnEmptyOne(
            @SuppressWarnings("unused") String emptyRule,
            String title,
            Faker faker
    ) {
        var primitives = new BookPrimitives(faker.code().isbn13(), faker.book().title());
        var testSubject = Book.createBook(primitives,this.defaultAuthorChecker);
        assertThrows(InvalidTitle.class, () -> testSubject.setTitle(title));
    }

    @Test
    @DisplayName("allow to create a book with authors and give access to them")
    void shouldAllowToCreateABookWithAuthorsAndGiveAccessToThem(Faker faker) {
        final var givenAuthorsIds = new UUID[]{UUID.randomUUID(), UUID.randomUUID()};
        final var expectedValue = Set.of(givenAuthorsIds);
        final var primitives = new BookPrimitives(
                faker.code().isbn13(),
                faker.book().title(),
                Set.of(givenAuthorsIds)
        );

        final var testSubject = Book.createBook(primitives,this.defaultAuthorChecker);
        assertEquals(expectedValue, testSubject.getAuthorsIds());
    }

    @Test
    @DisplayName("not allow to create a book with authors if any of the given authors don't exists")
    void shouldNotAllowToCreateABookWithAuthorsIfAnyOfTheGivenAuthorsDoesNotExists(Faker faker){
        final var mockedChecker = mock(AuthorChecker.class);

        final var givenPrimitives = new BookPrimitives(
                faker.code().isbn13(),
                faker.book().title(),
                UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID()
        );

        assertThrows(InvalidAuthor.class,()->Book.createBook(givenPrimitives,mockedChecker));                                                                                                        assertThrows(InvalidAuthor.class,()->Book.createBook(givenPrimitives,mockedChecker));

        //crappy hack mockito is not counting well the interactions with the mock for vargs.
        verify(mockedChecker,times(2)).authorsExists(any(UUID[].class));
    }

    @Test
    @DisplayName("not call to the checker if dont have any author")
    void shouldNotCallToTheCheckerIfDontHaveAnyAuthor(Faker faker){
        final var mockedChecker = mock(AuthorChecker.class);
        final var givenPrimitives = new BookPrimitives(
                faker.code().isbn13(),
                faker.book().title()
        );

        when(mockedChecker.authorsExists(any())).thenReturn(true);

        Book.createBook(givenPrimitives,mockedChecker);

        verify(mockedChecker,times(0)).authorsExists(any());
    }

    @Test
    @DisplayName("allow to add a new author and save the book if author exists")
    void shouldAllowToAddANewAuthorAndSaveTheBookIfAuthorExists(Faker faker) {
        final var givenAuthorId = UUID.randomUUID();
        final var expectedValue = Set.of(givenAuthorId);
        final var mockedRepository = mock(BookRepository.class);
        final var mockedChecker = mock(AuthorChecker.class);

        final var authorUpdateCommand = AuthorUpdateCommand.addAuthors(mockedChecker, mockedRepository, givenAuthorId);
        final var primitives = new BookPrimitives(faker.code().isbn13(), faker.book().title());

        final var testSubject = Book.createBook(primitives,this.defaultAuthorChecker);

        when(mockedChecker.authorsExists(givenAuthorId)).thenReturn(true);

        testSubject.updateAuthors(authorUpdateCommand);

        assertEquals(expectedValue, testSubject.getAuthorsIds());
        verify(mockedRepository).save(testSubject);
    }

    @Test
    @DisplayName("fail if the author is not registered")
    void shouldFailIfTheAuthorIsNotRegistered(Faker faker) {
        final var givenAuthorId = UUID.randomUUID();
        final var mockedRepository = mock(BookRepository.class);
        final var mockedChecker = mock(AuthorChecker.class);

        final var authorUpdateCommand = AuthorUpdateCommand.addAuthors(mockedChecker, mockedRepository, givenAuthorId);
        final var primitives = new BookPrimitives(faker.code().isbn13(), faker.book().title());

        final var testSubject = Book.createBook(primitives,this.defaultAuthorChecker);

        assertThrows(InvalidAuthor.class, () -> testSubject.updateAuthors(authorUpdateCommand));

        verify(mockedRepository, times(0)).save(testSubject);
    }

    @Test
    @DisplayName("remove an author if exists as author from the book")
    void shouldRemoveAnAuthorIfExistsAsAuthorFromTheBook(Faker faker) {
        final var givenRemovedAuthorId = UUID.randomUUID();
        final var mockedRepository = mock(BookRepository.class);
        final var otherAuthorId = UUID.randomUUID();
        final var authorsBeforeRemoval = Set.of(givenRemovedAuthorId, otherAuthorId);
        final var authorAfterRemoval = Set.of(otherAuthorId);

        final var authorUpdateCommand = AuthorUpdateCommand.deleteAuthors(mockedRepository, givenRemovedAuthorId);
        final var primitives = new BookPrimitives(
                faker.code().isbn13(),
                faker.book().title(),
                Set.of(givenRemovedAuthorId, otherAuthorId)
        );

        final var testSubject = Book.createBook(primitives, this.defaultAuthorChecker);

        assertEquals(authorsBeforeRemoval, testSubject.getAuthorsIds());

        testSubject.updateAuthors(authorUpdateCommand);

        assertEquals(authorAfterRemoval, testSubject.getAuthorsIds());
        verify(mockedRepository).save(testSubject);
    }

    @Test
    @DisplayName("not call to the repository if there is not changes after removal")
    void shouldNotCallToTheRepositoryIfThereIsNotChangesAfterRemoval(Faker faker) {
        final var givenRemovedAuthorId = UUID.randomUUID();
        final var mockedRepository = mock(BookRepository.class);
        final var authorUpdateCommand = AuthorUpdateCommand.deleteAuthors(mockedRepository, givenRemovedAuthorId);
        final var primitives = new BookPrimitives(faker.code().isbn13(), faker.book().title());
        final var testSubject = Book.createBook(primitives,this.defaultAuthorChecker);

        testSubject.updateAuthors(authorUpdateCommand);

        verify(mockedRepository, times(0)).save(testSubject);
    }

    private static Stream<Arguments> emptyTitles() {
        return Stream.of(
                arguments("is null", null),
                arguments("is empty", ""),
                arguments("contains only empty characters", "                         ")
        );
    }
}