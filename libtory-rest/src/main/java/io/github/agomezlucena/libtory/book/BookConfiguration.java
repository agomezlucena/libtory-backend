package io.github.agomezlucena.libtory.book;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties(prefix = "books")
public class BookConfiguration {
    private String jdbcUrl;
    private String jdbcUsername;
    private String jdbcPassword;
    private String jdbcDriver;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getJdbcUsername() {
        return jdbcUsername;
    }

    public void setJdbcUsername(String jdbcUsername) {
        this.jdbcUsername = jdbcUsername;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    @Bean
    @Qualifier("booksDataSource")
    public DataSource booksDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setCurrentSchema("books");
        return DataSourceBuilder.derivedFrom(dataSource)
                .url(jdbcUrl)
                .driverClassName(jdbcDriver)
                .username(jdbcUsername)
                .password(jdbcPassword)
                .build();
    }

    @Bean
    @Qualifier("booksNamedParameterOperations")
    public NamedParameterJdbcOperations booksNamedParameterOperations(
            @Qualifier("booksDataSource") DataSource dataSource
    ){
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
