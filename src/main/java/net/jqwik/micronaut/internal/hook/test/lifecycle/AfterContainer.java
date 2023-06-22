package net.jqwik.micronaut.internal.hook.test.lifecycle;

import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.AfterContainerHook;
import net.jqwik.api.lifecycle.ContainerLifecycleContext;
import net.jqwik.micronaut.internal.extension.JqwikMicronautExtension;

public class AfterContainer implements AfterContainerHook {
    private final JqwikMicronautExtension extension;

    AfterContainer() {
        this.extension = JqwikMicronautExtension.STORE.get();
    }

    @Override
    @NonNullApi
    public void afterContainer(final ContainerLifecycleContext context) throws Exception {
        extension.afterContainer(context);
    }

    @Override
    public int afterContainerProximity() {
        return -20;     // Run it after @AfterContainer methods
    }
}
