package io.github.agomezlucena.libtory.book;

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
    public ResponseEntity<PagedResult> booksGet(
            Integer size,
            Integer page,
            String sortingField,
            String sortingDirection
    ) {
        PagedResult result = bookService.getAllBooks(size,page,sortingDirection,sortingField);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<LibtoryEntity> booksIsbnGet(String isbn) {
        return bookService.getByIsbn(isbn)
                .map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> booksIsbnPut(String isbn, LibtoryEntity libtoryEntity) {
        bookService.saveBook(isbn,libtoryEntity);
        return ResponseEntity.ok().build();
    }
}
