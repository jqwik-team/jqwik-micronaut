package net.jqwik.micronaut.internal.hook.test.lifecycle.utils;

import net.jqwik.api.lifecycle.LifecycleContext;
import net.jqwik.api.lifecycle.TryLifecycleContext;
import net.jqwik.micronaut.JqwikMicronautTest;

public class LifecycleContextUtils {
    public static boolean isPerProperty(final LifecycleContext context) {
        return context.findAnnotationsInContainer(JqwikMicronautTest.class)
                      .stream()
                      .map(JqwikMicronautTest::perTry)
                      .map(e -> !e)
                      .findFirst()
                      .orElseThrow(() -> new IllegalArgumentException("Expected 'perTry' to contain a value!"));
    }

    public static boolean isPerTry(final TryLifecycleContext context) {
        return !isPerProperty(context);
    }
}
