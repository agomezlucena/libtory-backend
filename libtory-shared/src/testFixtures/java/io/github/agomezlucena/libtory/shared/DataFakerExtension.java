package io.github.agomezlucena.libtory.shared;

import net.datafaker.Faker;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.github.agomezlucena.libtory.shared.DataFakerExtension.FakerIsbn.IsbnType.ISBN_13;

public class DataFakerExtension implements ParameterResolver {
    private final Faker faker = new Faker();

    @Override
    public boolean supportsParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        var parameter = parameterContext.getParameter();
        return  parameter.getType().equals(Faker.class) ||
                parameter.isAnnotationPresent(FakerIsbn.class) ||
                parameter.isAnnotationPresent(FakerBookTitle.class);
    }

    @Override
    public Object resolveParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        var parameter = parameterContext.getParameter();
        if(parameter.isAnnotationPresent(FakerIsbn.class)) {
            return isbn(parameter.getAnnotation(FakerIsbn.class));
        }
        if(parameter.isAnnotationPresent(FakerBookTitle.class)) {
            return faker.book().title();
        }

        return faker;
    }

    private String isbn(FakerIsbn isbn) {
        if (ISBN_13.equals(isbn.value())){
            return faker.code().isbn13(isbn.withHyphens());
        }
        return faker.code().isbn10(isbn.withHyphens());
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FakerIsbn{
        enum IsbnType {
            ISBN_13,
            ISBN_10
        }
        IsbnType value() default ISBN_13;
        boolean withHyphens() default false;
    }
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FakerBookTitle{

    }
}
