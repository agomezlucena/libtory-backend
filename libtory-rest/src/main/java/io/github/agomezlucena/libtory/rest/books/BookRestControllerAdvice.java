package io.github.agomezlucena.libtory.rest.books;

import io.github.agomezlucena.libtory.books.domain.InvalidAuthor;
import io.github.agomezlucena.libtory.books.domain.InvalidIsbn;
import io.github.agomezlucena.libtory.books.domain.InvalidTitle;
import io.github.agomezlucena.libtory.rest.model.LibtoryError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.*;

@RestControllerAdvice
public class BookRestControllerAdvice {
    @ExceptionHandler(InvalidIsbn.class)
    public ResponseEntity<LibtoryError> handleInvalidIsbn(NativeWebRequest request, InvalidIsbn exception) {
        var error = new LibtoryError()
                .transactionId(getTransactionId(request))
                .code(LibtoryError.CodeEnum.CLIENT_ERROR)
                .message(exception.getMessage())
                .localizedMessage(getLocalizedMessage(request.getLocale(), InvalidIsbn.class));
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(InvalidTitle.class)
    public ResponseEntity<LibtoryError> handleInvalidTitle(NativeWebRequest request, InvalidTitle exception) {
        var error = new LibtoryError()
                .transactionId(getTransactionId(request))
                .code(LibtoryError.CodeEnum.CLIENT_ERROR)
                .message(exception.getMessage())
                .localizedMessage(getLocalizedMessage(request.getLocale(), InvalidTitle.class));

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(InvalidAuthor.class)
    public ResponseEntity<LibtoryError> handleInvalidAuthor(NativeWebRequest request, InvalidAuthor exception) {
        LibtoryError error = new LibtoryError()
                .transactionId(getTransactionId(request))
                .code(LibtoryError.CodeEnum.CLIENT_ERROR)
                .message("one of the given authors doesn't exists")
                .localizedMessage(getLocalizedMessage(request.getLocale(), InvalidTitle.class));

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<LibtoryError> handleException(NativeWebRequest request, Exception exception) {
        var error = new LibtoryError()
                .transactionId(getTransactionId(request))
                .code(LibtoryError.CodeEnum.SERVER_ERROR)
                .message("an unknown error occurred");
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
