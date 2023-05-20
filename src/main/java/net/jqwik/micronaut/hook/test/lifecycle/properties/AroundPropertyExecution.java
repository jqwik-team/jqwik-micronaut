package net.jqwik.micronaut.hook.test.lifecycle.properties;

import jakarta.annotation.Nonnull;

import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.AroundPropertyHook;
import net.jqwik.api.lifecycle.PropertyExecutionResult;
import net.jqwik.api.lifecycle.PropertyExecutor;
import net.jqwik.api.lifecycle.PropertyLifecycleContext;
import net.jqwik.micronaut.extension.JqwikMicronautExtension;

public class AroundPropertyExecution implements AroundPropertyHook {
    private final JqwikMicronautExtension extension;

    AroundPropertyExecution() {
        this.extension = JqwikMicronautExtension.STORE.get();
    }

    @Override
    @NonNullApi
    @Nonnull
    public PropertyExecutionResult aroundProperty(
            final PropertyLifecycleContext context,
            final PropertyExecutor property
    ) throws Exception {
        extension.beforePropertyExecution(context);
        return property.executeAndFinally(() -> extension.afterPropertyExecution(context));
    }

    @Override
    public int aroundPropertyProximity() {
        // In-between @BeforeProperty, @AfterProperty and actual property execution
        return -5;
    }
}
