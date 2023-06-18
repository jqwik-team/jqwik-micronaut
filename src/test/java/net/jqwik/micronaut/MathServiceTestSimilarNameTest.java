package net.jqwik.micronaut;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.test.annotation.MockBean;
import jakarta.inject.Inject;

import net.jqwik.api.Property;
import net.jqwik.micronaut.beans.math.MathService;
import net.jqwik.micronaut.beans.math.MathServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@JqwikMicronautTest
@Requires(property = "mockito.test.enabled", defaultValue = StringUtils.FALSE, value = StringUtils.TRUE)
class MathServiceTestSimilarNameTest {
    @Inject
    private MathService mathService;

    @Property(tries = 1)
    void testThatSimilarlyNamedTestSuitesDontLeakMocks() {
        int num = 10;
        when(mathService.compute(num)).then(invocation -> num * 2); // non mock impl is * 4

        final int result = mathService.compute(num);

        assertThat(result).isEqualTo(20);
    }

    @MockBean(MathServiceImpl.class)
    MathService mathService() {
        return mock(MathService.class);
    }
}