package net.jqwik.micronaut.internal.hook.test.lifecycle;

import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.BeforeContainerHook;
import net.jqwik.api.lifecycle.ContainerLifecycleContext;
import net.jqwik.micronaut.internal.extension.JqwikMicronautExtension;

public class BeforeAll implements BeforeContainerHook {
    private final JqwikMicronautExtension extension;

    BeforeAll() {
        this.extension = JqwikMicronautExtension.STORE.get();
    }

    @Override
    @NonNullApi
    public void beforeContainer(final ContainerLifecycleContext context) throws Exception {
        extension.beforeContainer(context);
    }

    @Override
    public int beforeContainerProximity() {
        // Run it before @BeforeContainer methods
        return -20;
    }
}
