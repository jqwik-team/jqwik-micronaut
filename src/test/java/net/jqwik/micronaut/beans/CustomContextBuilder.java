package net.jqwik.micronaut.beans;

import java.util.Collections;

import io.micronaut.context.DefaultApplicationContextBuilder;
import io.micronaut.core.annotation.Introspected;

@Introspected
public class CustomContextBuilder extends DefaultApplicationContextBuilder {
    CustomContextBuilder() {
        properties(Collections.singletonMap("custom.builder.prop", "value"));
    }
}
