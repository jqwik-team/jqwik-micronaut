package net.jqwik.micronaut.internal.hook.test.lifecycle.tries;

import java.util.List;

import jakarta.annotation.Nonnull;

import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.AroundTryHook;
import net.jqwik.api.lifecycle.TryExecutionResult;
import net.jqwik.api.lifecycle.TryExecutor;
import net.jqwik.api.lifecycle.TryLifecycleContext;
import net.jqwik.micronaut.internal.extension.JqwikMicronautExtension;
import net.jqwik.micronaut.internal.hook.test.lifecycle.utils.LifecycleContextUtils;

public class AroundTry implements AroundTryHook {
    private final JqwikMicronautExtension extension;

    AroundTry() {
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
        extension.before(context);
        final TryExecutionResult execute = aTry.execute(parameters);
        extension.after(context);
        return execute;
    }

    @Override
    public int aroundTryProximity() {
        /* Property lifecycle methods (@BeforeTry, @AfterTry) use -10.
           Smaller numbers means "further away" from actual invocation of try.
           -20 is therefore around the lifecycle methods. */
        return -20;
    }
}
