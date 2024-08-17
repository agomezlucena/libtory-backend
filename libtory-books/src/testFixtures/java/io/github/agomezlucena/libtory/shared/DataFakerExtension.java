package io.github.agomezlucena.libtory.shared;

import net.datafaker.Faker;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class DataFakerExtension implements ParameterResolver {
    private final Faker faker = new Faker();

    @Override
    public boolean supportsParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(Faker.class);
    }

    @Override
    public Object resolveParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return faker;
    }
}
