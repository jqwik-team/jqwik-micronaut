package net.jqwik.micronaut;

import jakarta.inject.Inject;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.micronaut.beans.math.MathService;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest
class MathServiceTest {
    @Inject
    private MathService mathService;

    @Property(tries = 1)
    void testComputeNumToSquare(@ForAll("10") final Integer num) {
        final Integer result = mathService.compute(num);
        assertThat(result).isEqualTo(num * 4);
    }

    @Provide("10")
    private Arbitrary<Integer> numbers() {
        return Arbitraries.of(10);
    }
}