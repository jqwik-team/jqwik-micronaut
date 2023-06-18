package net.jqwik.micronaut.internal.extension;

import java.lang.reflect.AnnotatedElement;

import io.micronaut.context.ApplicationContext;
import io.micronaut.test.annotation.MicronautTestValue;
import io.micronaut.test.context.TestContext;

import net.jqwik.api.lifecycle.ContainerLifecycleContext;
import net.jqwik.api.lifecycle.MethodLifecycleContext;
import net.jqwik.micronaut.JqwikMicronautTest;

class TestContextUtils {

    private TestContextUtils() {
    }

    /**
     * Builds a {@link MicronautTestValue} object from the provided context (e.g. by scanning annotations).
     *
     * @param context the container context to extract builder configuration from
     * @return a MicronautTestValue to configure the test application context
     */
    static MicronautTestValue buildMicronautTestValue(final ContainerLifecycleContext context) {
        return context.findAnnotation(JqwikMicronautTest.class)
                      .map(TestContextUtils::buildValueObject)
                      .orElse(null);
    }

    static TestContext buildContext(
            final ApplicationContext applicationContext,
            final MethodLifecycleContext context
    ) {
        return buildContext(
                applicationContext,
                context.containerClass(),
                context.targetMethod(),
                context.testInstance()
        );
    }

    static TestContext buildContext(
            final ApplicationContext applicationContext,
            final ContainerLifecycleContext context
    ) {
        return buildContext(
                applicationContext,
                context.optionalContainerClass().orElse(null),
                context.optionalElement().orElse(null),
                null
        );
    }

    private static MicronautTestValue buildValueObject(final JqwikMicronautTest micronautTest) {
        return new MicronautTestValue(
                micronautTest.application(),
                micronautTest.environments(),
                micronautTest.packages(),
                micronautTest.propertySources(),
                micronautTest.rollback(),
                micronautTest.transactional(),
                micronautTest.rebuildContext(),
                micronautTest.contextBuilder(),
                micronautTest.transactionMode(),
                micronautTest.startApplication(),
                micronautTest.resolveParameters()
        );
    }

    private static TestContext buildContext(
            final ApplicationContext applicationContext,
            final Class<?> testClass,
            final AnnotatedElement testMethod,
            final Object testInstance
    ) {
        return new TestContext(
                applicationContext,
                testClass,
                testMethod,
                testInstance,
                null // TODO: How to handle exceptions that occur during hook executions?
        );
    }
}
