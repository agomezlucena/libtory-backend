package io.github.agomezlucena.libtory.rest.books;

import io.github.agomezlucena.libtory.rest.api.BooksApi;
import io.github.agomezlucena.libtory.rest.model.LibtoryEntity;
import io.github.agomezlucena.libtory.rest.model.PagedResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookRestController implements BooksApi {
    private final BookService bookService;

    public BookRestController(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public ResponseEntity<Void> addAuthorsToBook(String isbn, LibtoryEntity libtoryEntity) {
        return BooksApi.super.addAuthorsToBook(isbn, libtoryEntity);
    }

    @Override
    public ResponseEntity<Void> addBook(String isbn, LibtoryEntity libtoryEntity) {
        bookService.saveBook(isbn,libtoryEntity);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<LibtoryEntity> findBookByIsbn(String isbn) {
        return bookService.getByIsbn(isbn)
                .map(ResponseEntity::ok)
                .orElseThrow(BookNotFoundException::bookNotFoundByIsbn);
    }

    @Override
    public ResponseEntity<PagedResult> getAllBookPaginated(
            Integer size,
            Integer page,
            String sortingField,
            String sortingDirection
    ) {
        PagedResult result = bookService.getAllBooks(size,page,sortingDirection,sortingField);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Void> removeAuthorsFromBook(String isbn, LibtoryEntity libtoryEntity) {
        return BooksApi.super.removeAuthorsFromBook(isbn, libtoryEntity);
    }

    @Override
    public ResponseEntity<Void> removeBook(String isbn) {
        bookService.deleteByIsbn(isbn);
        return ResponseEntity.noContent().build();
    }

}
