package net.jqwik.micronaut.hook.test.lifecycle.tries;

import java.util.List;

import jakarta.annotation.Nonnull;

import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.AroundTryHook;
import net.jqwik.api.lifecycle.TryExecutionResult;
import net.jqwik.api.lifecycle.TryExecutor;
import net.jqwik.api.lifecycle.TryLifecycleContext;
import net.jqwik.micronaut.extension.JqwikMicronautExtension;
import net.jqwik.micronaut.hook.test.lifecycle.utils.LifecycleContextUtils;

public class AroundTryExecution implements AroundTryHook {
    private final JqwikMicronautExtension extension;

    AroundTryExecution() {
        this.extension = JqwikMicronautExtension.STORE.get();
    }

    @Override
    @NonNullApi
    @Nonnull
    public TryExecutionResult aroundTry(
            final TryLifecycleContext context,
            final TryExecutor aTry,
            final List<Object> parameters
    ) throws Exception {
        if (LifecycleContextUtils.isPerProperty(context)) {
            return aTry.execute(parameters);
        }
        extension.beforeTryExecution(context);
        final TryExecutionResult result = aTry.execute(parameters);
        extension.afterTryExecution(context);
        return result;
    }

    @Override
    public int aroundTryProximity() {
        // In-between @BeforeTry, @AfterTry and actual try execution
        return -5;
    }
}
