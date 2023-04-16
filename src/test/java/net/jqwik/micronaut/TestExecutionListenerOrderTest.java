package net.jqwik.micronaut;

import jakarta.inject.Inject;
import net.jqwik.api.Property;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;
import net.jqwik.micronaut.listeners.FirstExecutionListener;
import net.jqwik.micronaut.listeners.SecondExecutionListener;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest(environments = TestExecutionListenerOrderTest.ENVIRONMENT)
public class TestExecutionListenerOrderTest {
    public static final String ENVIRONMENT = "TEST_EXECUTION_LISTENER_ORDER_TEST";

    @Inject
    private FirstExecutionListener firstExecutionListener;

    @Inject
    private SecondExecutionListener secondExecutionListener;

    @Property(tries = 1)
    void test() {
        assertThat(firstExecutionListener).isNotNull();
        assertThat(secondExecutionListener).isNotNull();
    }
}

