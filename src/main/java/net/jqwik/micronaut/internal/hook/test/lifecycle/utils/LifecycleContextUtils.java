package net.jqwik.micronaut.internal.hook.test.lifecycle.utils;

import net.jqwik.api.lifecycle.LifecycleContext;
import net.jqwik.micronaut.JqwikMicronautTest;

public class LifecycleContextUtils {
    private static final String ANNOTATION_NOT_FOUND =
            "Expected '@JqwikMicronautTest' to be present in the test class!";

    private LifecycleContextUtils() {
    }

    public static boolean isPerProperty(final LifecycleContext context) {
        return !isPerTry(context);
    }

    public static boolean isPerTry(final LifecycleContext context) {
        return context.findAnnotationsInContainer(JqwikMicronautTest.class)
                      .stream()
                      .map(JqwikMicronautTest::perTry)
                      .findFirst()
                      .orElseThrow(() -> new IllegalArgumentException(ANNOTATION_NOT_FOUND));
    }
}
