package net.jqwik.micronaut;

import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.inject.Inject;

import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest
class PropertyClassTest {
    @Inject
    private Config config;

    @Property(tries = 1)
    @io.micronaut.context.annotation.Property(name = "demo.foo", value = "FOO")
    void testFoo() {
        assertThat(config.getFoo()).isEqualTo("FOO");
    }

    @ConfigurationProperties("demo")
    static class Config {
        String foo = "It does not matter if the property value is defined here or not.";

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }
}