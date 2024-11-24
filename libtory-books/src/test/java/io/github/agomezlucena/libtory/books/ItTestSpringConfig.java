package io.github.agomezlucena.libtory.books;

import io.github.agomezlucena.libtory.books.domain.AuthorChecker;
import io.github.agomezlucena.libtory.books.infrastructure.database.AuthorSqlChecker;
import io.github.agomezlucena.libtory.books.infrastructure.database.BookProjectionMyBatisRepository;
import io.github.agomezlucena.libtory.books.infrastructure.database.BookQueries;
import io.github.agomezlucena.libtory.books.infrastructure.database.BookSqlRepository;
import io.github.agomezlucena.libtory.books.infrastructure.database.mappers.BookProjectionMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
    @Qualifier("bookJdbcOperations")
    NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        var jdbcOperations = new NamedParameterJdbcTemplate(dataSource);
        jdbcOperations.getJdbcOperations().execute("set search_path to books");
        return jdbcOperations;
    }

    @Bean
    BookSqlRepository bookSqlRepository(
            BookQueries queries,
            @Qualifier("bookJdbcOperations") NamedParameterJdbcOperations jdbcOperations
    ) {
        return new BookSqlRepository(queries, jdbcOperations);
    }

    @Bean
    BookProjectionMyBatisRepository bookProjectionSqlRepository(BookProjectionMapper mapper) {
        return new BookProjectionMyBatisRepository(mapper);
    }

    @Bean
    AuthorChecker authorChecker(
            BookQueries queries,
            @Qualifier("bookJdbcOperations")NamedParameterJdbcOperations jdbcOperations
    ){
        return new AuthorSqlChecker(queries,jdbcOperations);
    }

    @DynamicPropertySource
    static void registerDatasource(DynamicPropertyRegistry registry, PostgreSQLContainer<?> container) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }
}
