package net.jqwik.micronaut;

import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;

import net.jqwik.api.Property;
import net.jqwik.micronaut.beans.math.MathService;

class MathInheritedTest extends BaseTest {
    @Inject
    private MathService mathService;

    @Property(tries = 1)
    void testComputeNumToSquare() {
        final Integer result = mathService.compute(2);
        Assertions.assertThat(result).isEqualTo(8);
    }
}

@JqwikMicronautTest
abstract class BaseTest {
}