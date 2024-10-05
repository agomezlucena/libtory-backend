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
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static io.github.agomezlucena.libtory.shared.FakerIsbn.IsbnType.ISBN_13;

public class DataFakerExtension implements ParameterResolver {
    private final Faker faker = new Faker();
    private final Set<String> generatedIsbns = new ConcurrentSkipListSet<>();

    @Override
    public boolean supportsParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        var parameter = parameterContext.getParameter();
        return parameter.getType().equals(Faker.class) ||
                parameter.isAnnotationPresent(FakerIsbn.class) ||
                parameter.isAnnotationPresent(FakerBookTitle.class);
    }

    @Override
    public Object resolveParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        var parameter = parameterContext.getParameter();
        if (parameter.isAnnotationPresent(FakerIsbn.class)) {
            return isbn(parameter.getAnnotation(FakerIsbn.class));
        }
        if (parameter.isAnnotationPresent(FakerBookTitle.class)) {
            return faker.book().title();
        }

        return faker;
    }

    private String isbn(FakerIsbn isbn) {
        var avoidedIsbn = Optional.ofNullable(isbn.avoidIsbn())
                .map(it -> it.split(";"))
                .map(Set::of)
                .orElseGet(Collections::emptySet);

        String generatedIsbn = (isbn.dontRepeat()) ?
                generateNotRepeated(isbn, avoidedIsbn) :
                generateAvoiding(isbn, avoidedIsbn);

        generatedIsbns.add(generatedIsbn);
        return generatedIsbn;
    }

    private String generateAvoiding(FakerIsbn isbn, Set<String> avoidedIsbn) {
        String generatedIsbn;
        do {
            generatedIsbn = generateIsbn(isbn);
        } while (avoidedIsbn.contains(generatedIsbn));
        return generatedIsbn;
    }

    private String generateNotRepeated(FakerIsbn isbn, Set<String> avoidedIsbn) {
        String generatedIsbn;
        do {
            generatedIsbn = generateIsbn(isbn);
        } while (avoidedIsbn.contains(generatedIsbn) || generatedIsbns.contains(generatedIsbn));
        return generatedIsbn;
    }

    private String generateIsbn(FakerIsbn isbn) {
        return (ISBN_13.equals(isbn.value())) ?
                faker.code().isbn13(isbn.withHyphens()) :
                faker.code().isbn10(isbn.withHyphens());
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FakerBookTitle {

    }
}
