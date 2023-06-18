package net.jqwik.micronaut;

import io.micronaut.test.annotation.MockBean;
import jakarta.inject.Inject;

import net.jqwik.api.Example;
import net.jqwik.micronaut.beans.math.MathService;
import net.jqwik.micronaut.beans.math.MathServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@JqwikMicronautTest
class MathFieldMockServiceTest {
    @MockBean(MathServiceImpl.class)
    final MathService mock = mock(MathService.class);

    @Inject
    private MathService mathService;

    @Example
    void testComputeNumToSquare() {
        when(mathService.compute(10))
                .then(invocation -> Long.valueOf(Math.round(Math.pow(10, 2))).intValue());

        final Integer result = mathService.compute(10);

        assertThat(result).isEqualTo(100);
        verify(mathService).compute(10);
    }
}