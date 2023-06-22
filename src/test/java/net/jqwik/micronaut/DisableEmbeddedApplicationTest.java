package net.jqwik.micronaut;

import io.micronaut.runtime.EmbeddedApplication;
import jakarta.inject.Inject;

import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest(startApplication = false, rebuildContext = true)
class DisableEmbeddedApplicationTest {
    @Inject
    private EmbeddedApplication<?> embeddedApplication;

    @Property(tries = 1)
    void embeddedApplicationIsNotStartedWhenContextIsStarted() {
        assertThat(embeddedApplication.isRunning()).isFalse();
    }

    @Property(tries = 1)
    void embeddedApplicationIsNotStartedWhenContextIsRebuilt() {
        assertThat(embeddedApplication.isRunning()).isFalse();
    }
}