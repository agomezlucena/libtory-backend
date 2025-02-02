package io.github.agomezlucena.libtory.rest.books;

import io.github.agomezlucena.libtory.rest.model.LibtoryError;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static io.github.agomezlucena.libtory.rest.model.LibtoryError.CodeEnum.CLIENT_ERROR;
import static io.github.agomezlucena.libtory.rest.model.LibtoryError.CodeEnum.ENTITY_NOT_FOUND_ERROR;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class QueryingBooksWorkflowItTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenUserLookForABookAndSendAnInvalidIsbnWillReturnA400Error() {
        given()
                .pathParam("isbn", "a")
                .get("/books/{isbn}")
                .then()
                .assertThat()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .expect(result -> {
                    var expectedMessage = "ISBN: a is invalid";
                    var response = result.getResponse();
                    var error = objectMapper.readValue(response.getContentAsString(), LibtoryError.class);
                    assertNotNull(error.getTransactionId());
                    assertEquals(CLIENT_ERROR,error.getCode());
                    assertEquals(expectedMessage,error.getMessage());
                })
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
                .expect(result -> {
                    var expectedMessage = "the given isbn is not associated to any book in our library.";
                    var response = result.getResponse();
                    var error = objectMapper.readValue(response.getContentAsString(), LibtoryError.class);
                    assertNotNull(error.getTransactionId());
                    assertEquals(ENTITY_NOT_FOUND_ERROR,error.getCode());
                    assertEquals(expectedMessage,error.getMessage());
                })
                .log()
                .ifValidationFails(LogDetail.ALL);
    }

}