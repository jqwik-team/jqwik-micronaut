package net.jqwik.micronaut.registrar;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.apiguardian.api.API;

import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.LifecycleHook;
import net.jqwik.api.lifecycle.PropagationMode;
import net.jqwik.api.lifecycle.RegistrarHook;
import net.jqwik.micronaut.hook.Disabled;
import net.jqwik.micronaut.hook.ParameterResolver;
import net.jqwik.micronaut.hook.test.lifecycle.AfterAll;
import net.jqwik.micronaut.hook.test.lifecycle.BeforeAll;
import net.jqwik.micronaut.hook.test.lifecycle.properties.AroundPropertyExecution;
import net.jqwik.micronaut.hook.test.lifecycle.properties.AroundPropertyLifecycleMethods;
import net.jqwik.micronaut.hook.test.lifecycle.properties.InterceptAfterPropertyMethod;
import net.jqwik.micronaut.hook.test.lifecycle.properties.InterceptBeforePropertyMethod;
import net.jqwik.micronaut.hook.test.lifecycle.tries.AroundTryExecution;
import net.jqwik.micronaut.hook.test.lifecycle.tries.AroundTryLifecycleMethods;
import net.jqwik.micronaut.hook.test.lifecycle.tries.InterceptAfterTryMethod;
import net.jqwik.micronaut.hook.test.lifecycle.tries.InterceptBeforeTryMethod;

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
                BeforeAll.class,
                AfterAll.class,
                ParameterResolver.class,
                Disabled.class
        );
    }

    private List<Class<? extends LifecycleHook>> getPropertyHooks() {
        return Arrays.asList(
                AroundPropertyLifecycleMethods.class,
                InterceptBeforePropertyMethod.Pre.class,
                InterceptBeforePropertyMethod.Post.class,
                InterceptAfterPropertyMethod.Pre.class,
                InterceptAfterPropertyMethod.Post.class,
                AroundPropertyExecution.class
        );
    }

    private List<Class<? extends LifecycleHook>> getTryHooks() {
        return Arrays.asList(
                AroundTryLifecycleMethods.class,
                AroundTryExecution.class,
                InterceptBeforeTryMethod.Pre.class,
                InterceptBeforeTryMethod.Post.class,
                InterceptAfterTryMethod.Pre.class,
                InterceptAfterTryMethod.Post.class
        );
    }
}