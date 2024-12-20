package io.github.agomezlucena.libtory.books.application;

import io.github.agomezlucena.libtory.books.domain.BookProjectionRepository;
import io.github.agomezlucena.libtory.books.domain.Isbn;
import io.github.agomezlucena.libtory.books.testutils.BooksTestUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class QueryBookByIsbnUseCaseITTest {
    private BookProjectionRepository bookProjectionRepository;
    private QueryBookByIsbnUseCase queryBookByIsbnUseCase;

    @BeforeEach
    void setUp(@Autowired BookProjectionRepository repository){
        bookProjectionRepository = spy(repository);
        queryBookByIsbnUseCase = new QueryBookByIsbnUseCase(bookProjectionRepository);
    }


    @BeforeAll
    public static void setUpTestData(@Autowired NamedParameterJdbcTemplate jdbcOperations) throws InterruptedException {
        BooksTestUtils.createTestData(jdbcOperations);
    }

    @AfterAll
    public static void tearDownTestData(@Autowired NamedParameterJdbcTemplate jdbcOperations) {
        BooksTestUtils.cleanupDatabase(jdbcOperations);
    }

    @Test
    public void shouldFindAnExistingBookByIsbn(){
        var expectedValue = BooksTestUtils.createTheIliad();
        var givenIsbnQuery = new BookProjectionIsbnQuery(expectedValue.isbn());
        var obtainedValue = queryBookByIsbnUseCase.handle(givenIsbnQuery);

        assertEquals(Optional.of(expectedValue), obtainedValue);

        verify(bookProjectionRepository).findProjectionByIsbn(Isbn.fromString(expectedValue.isbn()));
    }
}
