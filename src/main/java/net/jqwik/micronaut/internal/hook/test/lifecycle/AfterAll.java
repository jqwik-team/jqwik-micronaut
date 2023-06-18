package net.jqwik.micronaut.internal.hook.test.lifecycle;

import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.AfterContainerHook;
import net.jqwik.api.lifecycle.ContainerLifecycleContext;
import net.jqwik.micronaut.internal.extension.JqwikMicronautExtension;

public class AfterAll implements AfterContainerHook {
    private final JqwikMicronautExtension extension;

    AfterAll() {
        this.extension = JqwikMicronautExtension.STORE.get();
    }

    @Override
    @NonNullApi
    public void afterContainer(final ContainerLifecycleContext context) throws Exception {
        extension.afterContainer(context);
    }

    @Override
    public int afterContainerProximity() {
        // Run it after @AfterContainer methods
        return -20;
    }
}
