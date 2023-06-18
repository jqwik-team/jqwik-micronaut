package net.jqwik.micronaut;

import io.micronaut.context.annotation.Requires;

import net.jqwik.api.Example;

import static org.assertj.core.api.Assertions.fail;

@JqwikMicronautTest
@Requires(property = "does.not.exist")
class RequiresTest {
    @Example
    void testNotExecuted() {
        fail("Should never be executed");
    }
}