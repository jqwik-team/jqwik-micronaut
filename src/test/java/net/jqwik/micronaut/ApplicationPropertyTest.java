package net.jqwik.micronaut;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import net.jqwik.api.Property;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.util.Maps;

import java.util.Map;
import java.util.Optional;

@JqwikMicronautTest
@io.micronaut.context.annotation.Property(name = "test.class.property", value = "Hello world!")
class ApplicationPropertyTest implements TestPropertyProvider {
    @Inject
    private EmbeddedApplication<?> application;

    @Value("${main.property}")
    private String mainApplicationProperty;

    @Value("${test.property}")
    private String testApplicationProperty;

    @Value("${test.class.property}")
    private String classProperty;

    @Value("${dynamic.property}")
    private String dynamicProperty;

    @Override
    @NonNull
    public Map<String, String> getProperties() {
        return Maps.newHashMap("dynamic.property", "value");
    }

    @Property(tries = 1)
    @io.micronaut.context.annotation.Property(name = "test.method.property", value = "Hello method!")
    @io.micronaut.context.annotation.Property(name = "test.method.property2", value = "Hello method2!")
    void successOnInjectingApplicationPropertiesFromDifferentInjectionPoints() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(mainApplicationProperty).isEqualTo("Hello");
            softly.assertThat(testApplicationProperty).isEqualTo("world!");
            softly.assertThat(classProperty).isEqualTo("Hello world!");

            softly.assertThat(getTestApplicationProperty("test.method.property")).contains("Hello method!");
            softly.assertThat(getTestApplicationProperty("test.method.property2")).contains("Hello method2!");

            softly.assertThat(dynamicProperty).isEqualTo("value");
        });
    }

    private Optional<String> getTestApplicationProperty(String propertyName) {
        return application.getApplicationContext().getProperty(propertyName, String.class);
    }
}