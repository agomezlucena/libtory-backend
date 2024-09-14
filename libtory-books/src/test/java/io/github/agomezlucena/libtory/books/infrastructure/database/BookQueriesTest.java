package io.github.agomezlucena.libtory.books.infrastructure.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Book queries should")
class BookQueriesTest {
    private final BookQueries bookQueries = new BookQueries();
    private final Properties externalizedProperties = new Properties();

    BookQueriesTest() throws IOException {
        externalizedProperties.loadFromXML(
                BookQueriesTest.class.getClassLoader().getResourceAsStream("book-sql/queries.xml")
        );
    }

    @Test
    @DisplayName("create without any problem")
    void shouldCreateWithoutAnyProblem() {
        assertDoesNotThrow(BookQueries::new);
    }

    @ParameterizedTest
    @DisplayName("should return a not null string for every given key")
    @EnumSource(BookQueries.BookQueryName.class)
    void shouldReturnANotNullStringForEveryGivenKey(final BookQueries.BookQueryName queryName) {
        assertNotNull(bookQueries.getQuery(queryName));
    }

    @ParameterizedTest
    @DisplayName("should return a not blank string for every given key")
    @EnumSource(BookQueries.BookQueryName.class)
    void shouldReturnANotNullStringForEveryGivenKeyWithBlank(final BookQueries.BookQueryName queryName) {
        assertFalse(bookQueries.getQuery(queryName).isBlank(),"query for name: "+queryName+" is blank");
    }

    @ParameterizedTest
    @DisplayName("should return the query from book-sql/queries.xml for the passed query name")
    @EnumSource(BookQueries.BookQueryName.class)
    void shouldReturnTheQueryFromBookSqlQueryName(final BookQueries.BookQueryName queryName) {
        var expectedValue = externalizedProperties.getProperty(queryName.queryName);
        assertEquals(expectedValue,bookQueries.getQuery(queryName));
    }
}