package net.jqwik.micronaut;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import net.jqwik.api.Property;
import net.jqwik.micronaut.beans.FactoryBean;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest
class FactoryTest {
    @Inject
    private FactoryBean factoryBean;

    @Singleton
    FactoryBean factoryBean() {
        return new FactoryBean();
    }

    @Property(tries = 1)
    void testFactoryMethod() {
        assertThat(factoryBean).isNotNull();
    }
}