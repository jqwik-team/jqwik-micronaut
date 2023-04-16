package net.jqwik.micronaut;

import jakarta.inject.Inject;
import net.jqwik.api.Property;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;
import net.jqwik.micronaut.beans.math.MathService;
import org.assertj.core.api.Assertions;

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