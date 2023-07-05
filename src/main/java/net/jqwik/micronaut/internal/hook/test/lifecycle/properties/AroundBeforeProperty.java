package net.jqwik.micronaut.internal.hook.test.lifecycle.properties;

import jakarta.annotation.Nonnull;

import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.AroundPropertyHook;
import net.jqwik.api.lifecycle.PropertyExecutionResult;
import net.jqwik.api.lifecycle.PropertyExecutor;
import net.jqwik.api.lifecycle.PropertyLifecycleContext;
import net.jqwik.micronaut.internal.extension.JqwikMicronautExtension;
import net.jqwik.micronaut.internal.hook.test.lifecycle.utils.LifecycleContextUtils;

public class AroundBeforeProperty {
    private AroundBeforeProperty() {
    }

    public static class Pre implements AroundPropertyHook {
        private final JqwikMicronautExtension micronautExtension;

        Pre() {
            micronautExtension = JqwikMicronautExtension.STORE.get();
        }

        @Override
        @NonNullApi
        @Nonnull
        public PropertyExecutionResult aroundProperty(
                final PropertyLifecycleContext context,
                final PropertyExecutor property
        ) throws Exception {
            if (LifecycleContextUtils.isPerProperty(context)) {
                micronautExtension.preBefore(context);
            }
            return property.execute();
        }

        @Override
        public int aroundPropertyProximity() {
            return -11;
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
        public PropertyExecutionResult aroundProperty(
                final PropertyLifecycleContext context,
                final PropertyExecutor property
        ) throws Exception {
            if (LifecycleContextUtils.isPerProperty(context)) {
                micronautExtension.postBefore(context);
            }
            return property.execute();
        }

        @Override
        public int aroundPropertyProximity() {
            return -9;
        }
    }
}
