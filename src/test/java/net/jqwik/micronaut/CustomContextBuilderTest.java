package net.jqwik.micronaut;

import net.jqwik.api.Property;
import net.jqwik.micronaut.annotation.CustomBuilderMicronautTest;

import static org.assertj.core.api.Assertions.assertThat;

@CustomBuilderMicronautTest
class CustomContextBuilderTest {
    @io.micronaut.context.annotation.Property(name = "custom.builder.prop")
    private String val;

    @Property(tries = 1)
    void testCustomBuilderIsUsed() {
        assertThat(val).isEqualTo("value");
    }
}