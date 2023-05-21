package net.jqwik.micronaut.extension;

import java.lang.reflect.AnnotatedElement;

import io.micronaut.context.ApplicationContext;
import io.micronaut.test.annotation.MicronautTestValue;
import io.micronaut.test.context.TestContext;

import net.jqwik.api.lifecycle.ContainerLifecycleContext;
import net.jqwik.api.lifecycle.PropertyLifecycleContext;
import net.jqwik.api.lifecycle.TryLifecycleContext;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;

class TestContextUtils {
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

    static TestContext buildPropertyContext(
            final ApplicationContext applicationContext,
            final PropertyLifecycleContext context
    ) {
        return buildTestContext(
                applicationContext,
                context.containerClass(),
                context.targetMethod(),
                context.testInstance()
        );
    }

    static TestContext buildTryContext(
            final ApplicationContext applicationContext,
            final TryLifecycleContext context
    ) {
        return buildTestContext(
                applicationContext,
                context.containerClass(),
                context.targetMethod(),
                context.testInstance()
        );
    }

    static TestContext buildContainerContext(
            final ApplicationContext applicationContext,
            final ContainerLifecycleContext context
    ) {
        return buildTestContext(
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

    private static TestContext buildTestContext(
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
