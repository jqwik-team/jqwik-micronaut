package net.jqwik.micronaut;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.test.annotation.MockBean;
import jakarta.inject.Inject;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;
import net.jqwik.micronaut.beans.math.MathService;
import net.jqwik.micronaut.beans.math.MathServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@JqwikMicronautTest
@Requires(property = "mockito.test.enabled", defaultValue = StringUtils.FALSE, value = StringUtils.TRUE)
class MathMockServiceTest {
    @Inject
    private MathService mathService;

    @Inject
    private MathService[] mathServices;

    @MockBean(MathServiceImpl.class)
    MathService mathService() {
        return mock(MathService.class);
    }

    @Property(tries = 1)
    void testComputeNumToSquare(@ForAll("10") final Integer num) {
        when(mathService.compute(10))
                .then(invocation -> Long.valueOf(Math.round(Math.pow(num, 2))).intValue());

        final Integer result = mathService.compute(10);

        assertThat(result).isEqualTo(num * num);
        assertThat(mathServices.length).isEqualTo(1);
        verify(mathService).compute(10);
    }

    @Provide("10")
    private Arbitrary<Integer> numbers() {
        return Arbitraries.of(10);
    }
}