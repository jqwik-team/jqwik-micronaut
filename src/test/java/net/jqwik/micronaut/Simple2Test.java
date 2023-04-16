package net.jqwik.micronaut;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Inject;
import net.jqwik.api.Property;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;
import net.jqwik.micronaut.beans.SimpleService;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest
@Requires(property = "mockito.test.enabled", defaultValue = StringUtils.FALSE, value = StringUtils.TRUE)
class Simple2Test extends SimpleBaseTest {
    @Inject
    private SimpleService simpleService;

    @Property(tries = 1)
    void testComputeNumToSquare() {
        assertThat(simpleService).isNotNull();
    }
}