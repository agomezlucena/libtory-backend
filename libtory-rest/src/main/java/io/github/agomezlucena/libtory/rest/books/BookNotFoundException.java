package io.github.agomezlucena.libtory.rest.books;

public class BookNotFoundException extends RuntimeException {
    public static BookNotFoundException bookNotFoundByIsbn (){
        return new BookNotFoundException("the given isbn is not associated to any book in our library.");
    }
    private BookNotFoundException(String message) {
        super(message);
    }
}
