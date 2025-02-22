package io.github.agomezlucena.libtory.rest.books;

import io.github.agomezlucena.libtory.rest.model.LibtoryEntity;
import io.github.agomezlucena.libtory.rest.model.LibtoryError;
import io.github.agomezlucena.libtory.rest.model.LibtoryError.CodeEnum;
import io.github.agomezlucena.libtory.rest.model.Property;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.List;

import static io.github.agomezlucena.libtory.rest.books.BooksTestUtils.cleanupDatabase;
import static io.github.agomezlucena.libtory.rest.books.BooksTestUtils.createTestData;
import static io.github.agomezlucena.libtory.rest.model.LibtoryEntity.TypeEnum.AUTHOR;
import static io.github.agomezlucena.libtory.rest.model.LibtoryEntity.TypeEnum.BOOK;
import static io.github.agomezlucena.libtory.rest.model.LibtoryError.CodeEnum.CLIENT_ERROR;
import static io.github.agomezlucena.libtory.rest.model.LibtoryError.CodeEnum.ENTITY_NOT_FOUND_ERROR;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class QueryingBooksWorkflowItTest {
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUpResources(@Autowired NamedParameterJdbcOperations jdbcOperations) throws InterruptedException {
        createTestData(jdbcOperations);
    }

    @AfterAll
    static void tearDownResources(@Autowired NamedParameterJdbcOperations jdbcOperations) {
        cleanupDatabase(jdbcOperations);
    }

    @Test
    void whenUserLookForABookAndSendAnInvalidIsbnWillReturnA400Error() {
        given()
                .pathParam("isbn", "a")
                .get("/books/{isbn}")
                .then()
                .assertThat()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .expect(libtoryErrorWithMessageAndCode("ISBN: a is invalid", CLIENT_ERROR))
                .log()
                .ifValidationFails(LogDetail.ALL);
    }

    @Test
    void whenUserLookForABookAndSentAIsbnThatDoesNotExistWillReturnA404Error() {
        var givenIsbn = "9781234567897";
        given()
                .pathParam("isbn", givenIsbn)
                .get("/books/{isbn}")
                .then()
                .assertThat()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .expect(
                        libtoryErrorWithMessageAndCode(
                                "the given isbn is not associated to any book in our library.",
                                ENTITY_NOT_FOUND_ERROR
                        )
                )
                .log()
                .ifValidationFails(LogDetail.ALL);
    }

    @Test
    void whenUserLookForABookByIsbnAndExistsWillReturnA200WithTheExpectedBookInformation() {
        var givenIsbn = "9781914602108";
        given()
                .pathParam("isbn", givenIsbn)
                .get("/books/{isbn}")
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .expect(result -> {
                    var response = result.getResponse();
                    var libtoryEntity = objectMapper.readValue(response.getContentAsString(), LibtoryEntity.class);
                    assertNotNull(libtoryEntity);
                    assertEquals(BOOK, libtoryEntity.getType());
                    assertEquals(givenIsbn, libtoryEntity.getId());
                    assertEquals(URI.create("/books/9781914602108"), libtoryEntity.getUrl());
                    assertThatListContainsExpectedValues(
                            new Property()
                                    .field("title")
                                    .value("The Iliad")
                            ,
                            libtoryEntity.getProperties()
                    );
                    assertThatListContainsExpectedValues(
                            new LibtoryEntity()
                                    .id("123e4567-e89b-12d3-a456-426614174000")
                                    .type(AUTHOR)
                                    .addPropertiesItem(
                                            new Property()
                                                    .field("name")
                                                    .value("Homer")
                                    )
                            ,
                            libtoryEntity.getRelatedEntities()
                    );
                })
                .log()
                .ifValidationFails(LogDetail.ALL);
    }

    private ResultMatcher libtoryErrorWithMessageAndCode(String message, CodeEnum errorCode) {
        return result -> {
            var response = result.getResponse();
            var error = objectMapper.readValue(response.getContentAsString(), LibtoryError.class);
            assertNotNull(error.getTransactionId());
            assertEquals(errorCode, error.getCode());
            assertEquals(message, error.getMessage());
        };
    }

    private <T> void assertThatListContainsExpectedValues(
            T expectedValue,
            List<T> actualValue
    ) {
        assertThatListContainsExpectedValues(List.of(expectedValue), actualValue);
    }

    private <T> void assertThatListContainsExpectedValues(
            List<T> expectedValue,
            List<T> actualValue
    ) {
        assertThat(actualValue)
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(expectedValue);
    }
}