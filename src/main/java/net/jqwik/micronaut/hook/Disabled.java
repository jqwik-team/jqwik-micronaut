package net.jqwik.micronaut.hook;

import io.micronaut.context.annotation.Requires;
import jakarta.annotation.Nonnull;
import net.jqwik.api.lifecycle.LifecycleContext;
import net.jqwik.api.lifecycle.SkipExecutionHook;
import net.jqwik.micronaut.extension.JqwikMicronautExtension;

public class Disabled implements SkipExecutionHook {
    @Override
    @Nonnull
    public SkipResult shouldBeSkipped(final LifecycleContext context) {
        final io.micronaut.context.ApplicationContext applicationContext = JqwikMicronautExtension.STORE.get().getApplicationContext();
        final boolean isAnyPropertyMissing = context.findAnnotationsInContainer(Requires.class)
                .stream()
                .map(Requires::property)
                .anyMatch(e -> !applicationContext.containsProperties(e));

        if (isAnyPropertyMissing) {
            return SkipResult.skip("@Requires contains a property that doesn't exists!");
        }
        return SkipResult.doNotSkip();
    }
}
