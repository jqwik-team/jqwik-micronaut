package net.jqwik.micronaut.beans;

import io.micronaut.context.DefaultApplicationContextBuilder;
import io.micronaut.core.annotation.Introspected;

import java.util.Map;

@Introspected
public class CustomContextBuilder extends DefaultApplicationContextBuilder {
    CustomContextBuilder() {
        properties(Map.of("custom.builder.prop", "value"));
    }
}
