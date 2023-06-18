package net.jqwik.micronaut.internal.hook.test.lifecycle.properties;

import jakarta.annotation.Nonnull;

import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.AroundPropertyHook;
import net.jqwik.api.lifecycle.PropertyExecutionResult;
import net.jqwik.api.lifecycle.PropertyExecutor;
import net.jqwik.api.lifecycle.PropertyLifecycleContext;
import net.jqwik.micronaut.internal.extension.JqwikMicronautExtension;
import net.jqwik.micronaut.internal.hook.test.lifecycle.utils.LifecycleContextUtils;

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
        if (LifecycleContextUtils.isPerProperty(context)) {
            extension.beforeExecution(context);
            return property.executeAndFinally(() -> extension.afterExecution(context));
        }
        return property.execute();
    }

    @Override
    public int aroundPropertyProximity() {
        // In-between @BeforeProperty, @AfterProperty and actual property execution
        return -5;
    }
}
