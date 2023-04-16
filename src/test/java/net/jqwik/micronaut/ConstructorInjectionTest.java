package net.jqwik.micronaut;

import io.micronaut.context.annotation.Value;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeProperty;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;
import net.jqwik.micronaut.beans.AppBean;
import org.assertj.core.api.SoftAssertions;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest
@io.micronaut.context.annotation.Property(name = "class.property", value = "hello")
class ConstructorInjectionTest {
    private final AppBean appBean;
    private final String testProperty;
    private final String classProperty;

    ConstructorInjectionTest(final AppBean appBean,
                             @Value("${test.anotherProperty}") final String testProperty,
                             @io.micronaut.context.annotation.Property(name = "class.property") final String classProperty) {
        this.appBean = appBean;
        this.testProperty = testProperty;
        this.classProperty = classProperty;
    }

    @BeforeProperty
    void injectStatic(final AppBean appBean) {
        assertThat(appBean).isNotNull();
    }

    @Property(tries = 1)
    void testConstructorInjected() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(appBean).isNotNull();
            softly.assertThat(testProperty).isEqualTo("Hello");
            softly.assertThat(classProperty).isEqualTo("hello");
        });
    }
}