package net.jqwik.micronaut.internal.registrar;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.apiguardian.api.API;

import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.LifecycleHook;
import net.jqwik.api.lifecycle.PropagationMode;
import net.jqwik.api.lifecycle.RegistrarHook;
import net.jqwik.micronaut.internal.hook.test.Disabled;
import net.jqwik.micronaut.internal.hook.test.ParameterResolver;
import net.jqwik.micronaut.internal.hook.test.lifecycle.AfterContainer;
import net.jqwik.micronaut.internal.hook.test.lifecycle.BeforeContainer;
import net.jqwik.micronaut.internal.hook.test.lifecycle.properties.AroundAfterProperty;
import net.jqwik.micronaut.internal.hook.test.lifecycle.properties.AroundBeforeProperty;
import net.jqwik.micronaut.internal.hook.test.lifecycle.properties.AroundProperty;
import net.jqwik.micronaut.internal.hook.test.lifecycle.properties.AroundPropertyExecution;
import net.jqwik.micronaut.internal.hook.test.lifecycle.tries.AroundAfterTry;
import net.jqwik.micronaut.internal.hook.test.lifecycle.tries.AroundBeforeTry;
import net.jqwik.micronaut.internal.hook.test.lifecycle.tries.AroundTry;
import net.jqwik.micronaut.internal.hook.test.lifecycle.tries.AroundTryExecution;

@API(status = API.Status.INTERNAL)
public class JqwikMicronautRegistrar implements RegistrarHook {
    @Override
    @NonNullApi
    public void registerHooks(final Registrar registrar) {
        final List<Class<? extends LifecycleHook>> commonHooks = getCommonHooks();
        final List<Class<? extends LifecycleHook>> propertyHooks = getPropertyHooks();
        final List<Class<? extends LifecycleHook>> tryHooks = getTryHooks();
        Stream.of(commonHooks, propertyHooks, tryHooks)
              .flatMap(Collection::stream)
              .forEach(hook -> registrar.register(hook, PropagationMode.ALL_DESCENDANTS));
    }

    private List<Class<? extends LifecycleHook>> getCommonHooks() {
        return Arrays.asList(
                BeforeContainer.class,
                AfterContainer.class,
                ParameterResolver.class,
                Disabled.class
        );
    }

    private List<Class<? extends LifecycleHook>> getPropertyHooks() {
        return Arrays.asList(
                AroundProperty.class,
                AroundBeforeProperty.Pre.class,
                AroundBeforeProperty.Post.class,
                AroundAfterProperty.Pre.class,
                AroundAfterProperty.Post.class,
                AroundPropertyExecution.class
        );
    }

    private List<Class<? extends LifecycleHook>> getTryHooks() {
        return Arrays.asList(
                AroundTry.class,
                AroundTryExecution.class,
                AroundBeforeTry.Pre.class,
                AroundBeforeTry.Post.class,
                AroundAfterTry.Pre.class,
                AroundAfterTry.Post.class
        );
    }
}