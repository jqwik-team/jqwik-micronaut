package net.jqwik.micronaut;

import jakarta.inject.Named;
import net.jqwik.api.Property;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;
import net.jqwik.micronaut.beans.named.MyInterface;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest
class ConstructorNamedInjectionTest {
    private final MyInterface myInterface;

    ConstructorNamedInjectionTest(@Named("B") final MyInterface myInterface) {
        this.myInterface = myInterface;
    }

    @Property(tries = 1)
    void testConstructorInjected() {
        assertThat(myInterface.test()).isEqualTo("B");
    }
}