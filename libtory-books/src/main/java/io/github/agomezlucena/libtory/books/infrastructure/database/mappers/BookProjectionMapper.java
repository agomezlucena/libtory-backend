package io.github.agomezlucena.libtory.books.infrastructure.database.mappers;

import io.github.agomezlucena.libtory.books.domain.BookProjection;
import io.github.agomezlucena.libtory.shared.queries.PagedQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookProjectionMapper {
    List<BookProjection> getAllBooks(PagedQuery<BookProjection> pagedQuery);
    int countAllBooks();
    BookProjection findByIsbn(@Param("isbn") String isbn);
}
