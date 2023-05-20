package net.jqwik.micronaut.registrar;

import org.apiguardian.api.API;

import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.PropagationMode;
import net.jqwik.api.lifecycle.RegistrarHook;
import net.jqwik.micronaut.extension.JqwikMicronautExtension;
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
    public static Registrar REGISTRAR;

    public static void registerHooks() {
        registerPropertyHooks();
        registerTryHooks();
    }

    private static void registerPropertyHooks() {
        if (JqwikMicronautExtension.PER_TRY) {
            return;
        }
        REGISTRAR.register(AroundPropertyLifecycleMethods.class, PropagationMode.ALL_DESCENDANTS);
        REGISTRAR.register(InterceptBeforePropertyMethod.Pre.class, PropagationMode.ALL_DESCENDANTS);
        REGISTRAR.register(InterceptBeforePropertyMethod.Post.class, PropagationMode.ALL_DESCENDANTS);
        REGISTRAR.register(InterceptAfterPropertyMethod.Pre.class, PropagationMode.ALL_DESCENDANTS);
        REGISTRAR.register(InterceptAfterPropertyMethod.Post.class, PropagationMode.ALL_DESCENDANTS);
        REGISTRAR.register(AroundPropertyExecution.class, PropagationMode.ALL_DESCENDANTS);
    }

    private static void registerTryHooks() {
        if (!JqwikMicronautExtension.PER_TRY) {
            return;
        }
        REGISTRAR.register(AroundTryLifecycleMethods.class, PropagationMode.ALL_DESCENDANTS);
        REGISTRAR.register(AroundTryExecution.class, PropagationMode.ALL_DESCENDANTS);
        REGISTRAR.register(InterceptBeforeTryMethod.Pre.class, PropagationMode.ALL_DESCENDANTS);
        REGISTRAR.register(InterceptBeforeTryMethod.Post.class, PropagationMode.ALL_DESCENDANTS);
        REGISTRAR.register(InterceptAfterTryMethod.Pre.class, PropagationMode.ALL_DESCENDANTS);
        REGISTRAR.register(InterceptAfterTryMethod.Post.class, PropagationMode.ALL_DESCENDANTS);
    }

    @Override
    @NonNullApi
    public void registerHooks(final Registrar registrar) {
        JqwikMicronautRegistrar.REGISTRAR = registrar;
        registerCommonHooks();
    }

    private void registerCommonHooks() {
        REGISTRAR.register(BeforeAll.class, PropagationMode.ALL_DESCENDANTS);
        REGISTRAR.register(AfterAll.class, PropagationMode.ALL_DESCENDANTS);
        REGISTRAR.register(ParameterResolver.class, PropagationMode.ALL_DESCENDANTS);
        REGISTRAR.register(Disabled.class, PropagationMode.ALL_DESCENDANTS);
    }
}