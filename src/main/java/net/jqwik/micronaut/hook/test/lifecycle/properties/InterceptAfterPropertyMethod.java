package net.jqwik.micronaut.hook.test.lifecycle.properties;

import jakarta.annotation.Nonnull;

import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.AroundPropertyHook;
import net.jqwik.api.lifecycle.PropertyExecutionResult;
import net.jqwik.api.lifecycle.PropertyExecutor;
import net.jqwik.api.lifecycle.PropertyLifecycleContext;
import net.jqwik.micronaut.extension.JqwikMicronautExtension;

public class InterceptAfterPropertyMethod {
    public static class Pre implements AroundPropertyHook {
        private final JqwikMicronautExtension micronautExtension;

        Pre() {
            micronautExtension = JqwikMicronautExtension.STORE.get();
        }

        @Override
        @NonNullApi
        @Nonnull
        public PropertyExecutionResult aroundProperty(final PropertyLifecycleContext context,
                                                      final PropertyExecutor property) {
            return property.executeAndFinally(() -> micronautExtension.preAfterPropertyMethod(context));
        }

        @Override
        public int aroundPropertyProximity() {
            return -9;
        }
    }

    public static class Post implements AroundPropertyHook {
        private final JqwikMicronautExtension micronautExtension;

        Post() {
            micronautExtension = JqwikMicronautExtension.STORE.get();
        }

        @Override
        @NonNullApi
        @Nonnull
        public PropertyExecutionResult aroundProperty(final PropertyLifecycleContext context,
                                                      final PropertyExecutor property) {
            return property.executeAndFinally(() -> micronautExtension.postAfterPropertyMethod(context));
        }

        @Override
        public int aroundPropertyProximity() {
            return -11;
        }
    }
}
