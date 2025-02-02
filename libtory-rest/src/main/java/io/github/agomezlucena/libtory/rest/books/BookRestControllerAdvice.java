package io.github.agomezlucena.libtory.rest.books;

import io.github.agomezlucena.libtory.books.domain.InvalidAuthor;
import io.github.agomezlucena.libtory.books.domain.InvalidIsbn;
import io.github.agomezlucena.libtory.books.domain.InvalidTitle;
import io.github.agomezlucena.libtory.rest.model.LibtoryError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.*;

import static io.github.agomezlucena.libtory.rest.model.LibtoryError.CodeEnum.*;

@RestControllerAdvice
public class BookRestControllerAdvice {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler({InvalidIsbn.class,InvalidTitle.class,InvalidAuthor.class})
    public ResponseEntity<LibtoryError> handleInvalidIsbn(NativeWebRequest request, Throwable exception) {
        var error = new LibtoryError()
                .transactionId(getTransactionId(request))
                .code(CLIENT_ERROR)
                .message(exception.getMessage())
                .localizedMessage(getLocalizedMessage(request.getLocale(), exception.getClass()));

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<LibtoryError> handleInvalidTitle(NativeWebRequest request, Throwable exception) {
        var error = new LibtoryError()
                .transactionId(getTransactionId(request))
                .code(ENTITY_NOT_FOUND_ERROR)
                .message(exception.getMessage())
                .localizedMessage(getLocalizedMessage(request.getLocale(), exception.getClass()));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<LibtoryError> handleException(NativeWebRequest request, Exception exception) {
        var error = new LibtoryError()
                .transactionId(getTransactionId(request))
                .code(SERVER_ERROR)
                .message("an unknown error occurred");

        log.error("there was an unknown error caused by: {}",exception.getMessage());
        log.debug("exception body",exception);
        return ResponseEntity.badRequest().body(error);
    }

    private String getLocalizedMessage(Locale locale, Class<? extends Throwable> exceptionClass) {
        try {
            return ResourceBundle.getBundle("book-errors-messages", locale)
                    .getString(exceptionClass.getName());
        } catch (MissingResourceException e) {
            return null;
        }
    }

    private UUID getTransactionId(NativeWebRequest request) {
        try {
            return Optional.ofNullable(request.getHeader("X-Transaction-Id"))
                    .map(UUID::fromString)
                    .orElse(UUID.randomUUID());
        } catch (IllegalArgumentException e) {
            return UUID.randomUUID();
        }
    }
}
