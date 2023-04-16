package net.jqwik.micronaut;

import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.annotation.MockBean;
import jakarta.inject.Inject;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;
import net.jqwik.micronaut.beans.AppBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@JqwikMicronautTest
class JqwikMicronautExtensionTest {
    @Inject
    private EmbeddedApplication<?> application;

    @Inject
    private AppBean appBean;

    @MockBean(AppBean.class)
    AppBean appBean() {
        return mock(AppBean.class);
    }

    @Property(tries = 1)
    void successOnRunningApplicationContextUsingProperty() {
        assertThat(application.isRunning()).isTrue();
    }

    @Property(tries = 1)
    void successOnRunningApplicationContextUsingForAll(@ForAll boolean ignored) {
        assertThat(application.isRunning()).isTrue();
    }

    @Property(tries = 1)
    void successOnReplacingApplicationBeanWithMockBean() {
        // given
        final var mockedMessage = "Goodbye world!";
        when(appBean.method()).thenReturn(mockedMessage);

        // then
        assertThat(appBean.method()).isEqualTo(mockedMessage);
    }
}