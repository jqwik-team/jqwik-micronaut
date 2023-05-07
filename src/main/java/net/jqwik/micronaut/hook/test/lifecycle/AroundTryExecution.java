package net.jqwik.micronaut.hook.test.lifecycle;

import jakarta.annotation.Nonnull;
import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.AroundTryHook;
import net.jqwik.api.lifecycle.TryExecutionResult;
import net.jqwik.api.lifecycle.TryExecutor;
import net.jqwik.api.lifecycle.TryLifecycleContext;
import net.jqwik.micronaut.extension.JqwikMicronautExtension;

import java.util.List;

public class AroundTryExecution implements AroundTryHook {
    private final JqwikMicronautExtension extension;

    AroundTryExecution() {
        this.extension = JqwikMicronautExtension.STORE.get();
    }

    @Override
    @NonNullApi
    @Nonnull
    public TryExecutionResult aroundTry(final TryLifecycleContext context, final TryExecutor aTry,
                                        final List<Object> parameters) throws Throwable {
        if (JqwikMicronautExtension.PER_TRY) {
            extension.beforeTryExecution(context);
            final TryExecutionResult result = aTry.execute(parameters);
            extension.afterTryExecution(context);
            return result;
        }
        return aTry.execute(parameters);
    }

    @Override
    public int aroundTryProximity() {
        // In-between @BeforeProperty, @AfterProperty and actual property execution
        return -5;
    }
}
