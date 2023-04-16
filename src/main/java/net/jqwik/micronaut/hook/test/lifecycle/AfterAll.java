package net.jqwik.micronaut.hook.test.lifecycle;

import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.AfterContainerHook;
import net.jqwik.api.lifecycle.ContainerLifecycleContext;
import net.jqwik.micronaut.extension.JqwikMicronautExtension;

public class AfterAll implements AfterContainerHook {
    private final JqwikMicronautExtension extension;

    AfterAll() {
        this.extension = JqwikMicronautExtension.STORE.get();
    }

    @Override
    @NonNullApi
    public void afterContainer(final ContainerLifecycleContext context) throws Throwable {
        extension.afterContainer(context);
    }

    @Override
    public int afterContainerProximity() {
        // Run it after @AfterContainer methods
        return -20;
    }
}
