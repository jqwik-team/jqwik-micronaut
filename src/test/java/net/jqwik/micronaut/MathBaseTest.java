package net.jqwik.micronaut;

import io.micronaut.test.annotation.MockBean;
import net.jqwik.micronaut.beans.math.MathService;
import net.jqwik.micronaut.beans.math.MathServiceImpl;

import static org.mockito.Mockito.mock;

abstract class MathBaseTest {
    @MockBean(MathServiceImpl.class)
    MathService mathService() {
        return mock(MathService.class);
    }
}
