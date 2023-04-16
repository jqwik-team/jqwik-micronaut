package net.jqwik.micronaut;

import io.micronaut.test.annotation.MockBean;
import jakarta.inject.Inject;
import net.jqwik.api.Property;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;
import net.jqwik.micronaut.beans.math.MathService;
import net.jqwik.micronaut.beans.math.MathServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest
class MathInnerService2Test {
    @Inject
    private MathService mathService;

    @Inject
    private MathService[] services;

    /**
     * Tests that it is possible to have 2 mock beans
     */
    @Property(tries = 1)
    void testInnerMockAgain() {
        final int result = mathService.compute(10);

        assertThat(services.length).isEqualTo(1);
        assertThat(result).isEqualTo(50);
        assertThat(mathService).isInstanceOf(MyService.class);
    }

    @MockBean(MathServiceImpl.class)
    static class MyService implements MathService {
        @Override
        public Integer compute(final Integer num) {
            return 50;
        }
    }
}