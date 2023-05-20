package net.jqwik.micronaut.hook.test.lifecycle.tries;

import java.util.List;

import jakarta.annotation.Nonnull;

import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.AroundTryHook;
import net.jqwik.api.lifecycle.TryExecutionResult;
import net.jqwik.api.lifecycle.TryExecutor;
import net.jqwik.api.lifecycle.TryLifecycleContext;
import net.jqwik.micronaut.extension.JqwikMicronautExtension;

public class InterceptAfterTryMethod {
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
        ) {
            final TryExecutionResult execute = aTry.execute(parameters);
            micronautExtension.preAfterTryMethod(context);
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
        ) {
            final TryExecutionResult execute = aTry.execute(parameters);
            micronautExtension.postAfterTryMethod(context);
            return execute;
        }

        @Override
        public int aroundTryProximity() {
            return -11;
        }
    }
}
