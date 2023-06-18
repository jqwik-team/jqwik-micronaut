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

public class AroundAfterTry {
    public static class Pre implements AroundTryHook {
        private final JqwikMicronautExtension micronautExtension;

        Pre() {
            micronautExtension = JqwikMicronautExtension.STORE.get();
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
            final TryExecutionResult execute = aTry.execute(parameters);
            micronautExtension.preAfter(context);
            return execute;
        }

        @Override
        public int aroundTryProximity() {
            return -9;
        }
    }

    public static class Post implements AroundTryHook {
        private final JqwikMicronautExtension micronautExtension;

        Post() {
            micronautExtension = JqwikMicronautExtension.STORE.get();
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
            final TryExecutionResult execute = aTry.execute(parameters);
            micronautExtension.postAfter(context);
            return execute;
        }

        @Override
        public int aroundTryProximity() {
            return -11;
        }
    }
}
