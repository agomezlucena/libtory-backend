package io.github.agomezlucena.libtory.books.infrastructure.database;

import java.io.IOException;
import java.util.Properties;

public class BookQueries {
    private final Properties queryProperties;

    public BookQueries() throws IOException {
        this.queryProperties = new Properties();
        this.queryProperties.loadFromXML(
                getClass()
                        .getClassLoader()
                        .getResourceAsStream("book-sql/queries.xml")
        );
    }

    public String getQuery(BookQueryName queryName) {
        return queryProperties.getProperty(queryName.queryName).trim();
    }

    public enum BookQueryName {
        CHECK_AUTHOR_EXISTENCE("check-author-existence"),
        GET_BOOK_INFORMATION("get-book-information"),
        SAVE_BOOK_INFORMATION("save-book-information"),
        SAVE_AUTHOR_INFORMATION("save-author-information"),
        RELATE_BOOK_WITH_AUTHOR("relate-book-with-author"),
        DELETE_BOOK("delete-book"),
        DELETE_BOOK_RELATIONSHIP_WITH_AUTHORS("delete-book-relationship-with-authors"),
        DERELATE_BOOK_WITH_NOT_GIVEN_AUTHORS("derelate-book-with-not-given-authors");

        final String queryName;

        BookQueryName(String queryName) {
            this.queryName = queryName;
        }
    }
}
