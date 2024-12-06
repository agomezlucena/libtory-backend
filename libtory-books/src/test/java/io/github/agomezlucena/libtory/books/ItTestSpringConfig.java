package io.github.agomezlucena.libtory.books;

import io.github.agomezlucena.libtory.books.application.QueryBooksPaginatedUseCase;
import io.github.agomezlucena.libtory.books.application.UpdateBookUseCase;
import io.github.agomezlucena.libtory.books.domain.AuthorChecker;
import io.github.agomezlucena.libtory.books.domain.BookProjectionRepository;
import io.github.agomezlucena.libtory.books.domain.BookRepository;
import io.github.agomezlucena.libtory.books.infrastructure.database.AuthorSqlChecker;
import io.github.agomezlucena.libtory.books.infrastructure.database.BookProjectionMybatisRepository;
import io.github.agomezlucena.libtory.books.infrastructure.database.BookQueries;
import io.github.agomezlucena.libtory.books.infrastructure.database.BookSqlRepository;
import io.github.agomezlucena.libtory.books.infrastructure.database.mappers.BookProjectionMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.io.IOException;

@SpringBootApplication
public class ItTestSpringConfig {
    public static void main(String[] args) {
        SpringApplication.run(ItTestSpringConfig.class, args);
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    }

    @Bean
    BookQueries bookQueries() throws IOException {
        return new BookQueries();
    }

    @Bean
    BookSqlRepository bookSqlRepository(
            BookQueries queries,
            NamedParameterJdbcOperations jdbcOperations
    ) {
        return new BookSqlRepository(queries, jdbcOperations);
    }

    @Bean
    BookProjectionMybatisRepository bookProjectionSqlRepository(BookProjectionMapper mapper) {
        return new BookProjectionMybatisRepository(mapper);
    }

    @Bean
    AuthorChecker authorChecker(
            BookQueries queries,
            NamedParameterJdbcOperations jdbcOperations
    ){
        return new AuthorSqlChecker(queries,jdbcOperations);
    }

    @Bean
    QueryBooksPaginatedUseCase queryBooksPaginatedUseCase(BookProjectionRepository mapper){
        return new QueryBooksPaginatedUseCase(mapper);
    }

    @Bean
    UpdateBookUseCase updateBookUseCase(BookRepository repository, AuthorChecker authorChecker) {
        return new UpdateBookUseCase(repository, authorChecker);
    }

    @DynamicPropertySource
    static void registerDatasource(DynamicPropertyRegistry registry, PostgreSQLContainer<?> container) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Bean
    CommandLineRunner runner(){
        return args -> {
            org.apache.ibatis.logging.LogFactory.useSlf4jLogging();
        };
    }
}
