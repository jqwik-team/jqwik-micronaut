package net.jqwik.micronaut.registrar;

import net.jqwik.api.lifecycle.PropagationMode;
import net.jqwik.api.lifecycle.RegistrarHook;
import net.jqwik.micronaut.hook.Disabled;
import net.jqwik.micronaut.hook.ParameterResolver;
import net.jqwik.micronaut.hook.test.lifecycle.AfterAll;
import net.jqwik.micronaut.hook.test.lifecycle.AroundPropertyExecution;
import net.jqwik.micronaut.hook.test.lifecycle.AroundPropertyLifecycleMethods;
import net.jqwik.micronaut.hook.test.lifecycle.BeforeAll;
import net.jqwik.micronaut.hook.test.lifecycle.InterceptAfterPropertyMethod;
import net.jqwik.micronaut.hook.test.lifecycle.InterceptBeforePropertyMethod;
import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public class JqwikMicronautRegistrar implements RegistrarHook {
    @Override
    public void registerHooks(final RegistrarHook.Registrar registrar) {
        registrar.register(BeforeAll.class, PropagationMode.ALL_DESCENDANTS);
        registrar.register(AfterAll.class, PropagationMode.ALL_DESCENDANTS);
        registrar.register(AroundPropertyLifecycleMethods.class, PropagationMode.ALL_DESCENDANTS);

        registrar.register(InterceptBeforePropertyMethod.Pre.class, PropagationMode.ALL_DESCENDANTS);
        registrar.register(InterceptBeforePropertyMethod.Post.class, PropagationMode.ALL_DESCENDANTS);
        registrar.register(InterceptAfterPropertyMethod.Pre.class, PropagationMode.ALL_DESCENDANTS);
        registrar.register(InterceptAfterPropertyMethod.Post.class, PropagationMode.ALL_DESCENDANTS);

        registrar.register(AroundPropertyExecution.class, PropagationMode.ALL_DESCENDANTS);

        registrar.register(ParameterResolver.class, PropagationMode.ALL_DESCENDANTS);
        registrar.register(Disabled.class, PropagationMode.ALL_DESCENDANTS);
    }
}