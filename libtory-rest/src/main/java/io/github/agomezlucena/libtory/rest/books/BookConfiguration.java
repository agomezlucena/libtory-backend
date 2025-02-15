package io.github.agomezlucena.libtory.rest.books;

import io.github.agomezlucena.libtory.books.application.*;
import io.github.agomezlucena.libtory.books.domain.*;
import io.github.agomezlucena.libtory.books.infrastructure.database.AuthorSqlChecker;
import io.github.agomezlucena.libtory.books.infrastructure.database.BookProjectionMybatisRepository;
import io.github.agomezlucena.libtory.books.infrastructure.database.BookQueries;
import io.github.agomezlucena.libtory.books.infrastructure.database.BookSqlRepository;
import io.github.agomezlucena.libtory.books.infrastructure.database.mappers.BookProjectionMapper;
import io.github.agomezlucena.libtory.shared.cqrs.CommandBus;
import io.github.agomezlucena.libtory.shared.cqrs.QueryBus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class BookConfiguration {

    @Bean
    @Qualifier("booksIoExecutorService")
    ExecutorService booksIoExecutorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    @Qualifier("booksNamedParameterOperations")
    public NamedParameterJdbcOperations booksNamedParameterOperations(DataSource dataSource){
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    BookQueries bookQueries() throws IOException {
        return new BookQueries();
    }

    @Bean
    BookRepository bookRepository(
            @Qualifier("booksNamedParameterOperations") NamedParameterJdbcOperations operations,
            BookQueries bookQueries
    ){
        return new BookSqlRepository(bookQueries, operations);
    }

    @Bean
    BookProjectionRepository bookProjectionRepository(
            BookProjectionMapper bookProjectionMapper,
            @Qualifier("booksIoExecutorService") ExecutorService executorService
    ){
        return new BookProjectionMybatisRepository(bookProjectionMapper,executorService);
    }

    @Bean
    AuthorChecker authorChecker(BookQueries queries,NamedParameterJdbcOperations operations) {
        return new AuthorSqlChecker(queries, operations);
    }

    @Bean
    UpdateBookUseCase updateBookUseCase(
            BookRepository bookRepository,
            AuthorChecker authorChecker
    ){
        return new UpdateBookUseCase(bookRepository,authorChecker);
    }

    @Bean
    UpdateBookAuthorsUseCase updateAuthorsCommand(
            BookRepository bookRepository,
            AuthorChecker authorChecker
    ){
        return new UpdateBookAuthorsUseCase(authorChecker, bookRepository);
    }

    @Bean
    DeleteBookByIsbnUseCase deleteBookByIsbnUseCase(BookRepository bookRepository){
        return new DeleteBookByIsbnUseCase(bookRepository);
    }

    @Bean
    QueryBookByIsbnUseCase queryBookByIsbnUseCase(BookProjectionRepository bookProjectionRepository){
        return new QueryBookByIsbnUseCase(bookProjectionRepository);
    }

    @Bean
    QueryBooksPaginatedUseCase queryBooksPaginatedUseCase(BookProjectionRepository bookProjectionRepository){
        return new QueryBooksPaginatedUseCase(bookProjectionRepository);
    }

    @Bean
    @Qualifier("booksCommandBus")
    public CommandBus booksCommandBus(
            UpdateBookUseCase updateBookUseCase,
            UpdateBookAuthorsUseCase authorsUseCase,
            DeleteBookByIsbnUseCase deleteBookByIsbnUseCase
    ) {
        return CommandBus.getNewCommandBus()
                .addHandler(BookPrimitives.class,updateBookUseCase)
                .addHandler(UpdateAuthorsCommand.class,authorsUseCase)
                .addHandler(Isbn.class,deleteBookByIsbnUseCase);

    }

    @Bean
    @Qualifier("booksQueryBus")
    public QueryBus booksQueryBus(
            QueryBookByIsbnUseCase queryBookByIsbnUseCase,
            QueryBooksPaginatedUseCase queryBooksPaginatedUseCase
    ) {
        return new QueryBus()
                .addHandler(BookProjectionIsbnQuery.class,queryBookByIsbnUseCase)
                .addHandler(BookProjectionPaginatedQuery.class,queryBooksPaginatedUseCase);
    }
}
