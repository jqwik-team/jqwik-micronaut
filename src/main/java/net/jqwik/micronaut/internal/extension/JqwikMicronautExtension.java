package net.jqwik.micronaut.internal.extension;

import java.util.Map;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.annotation.MicronautTestValue;
import io.micronaut.test.extensions.AbstractMicronautExtension;
import io.micronaut.test.support.TestPropertyProvider;

import net.jqwik.api.lifecycle.ContainerLifecycleContext;
import net.jqwik.api.lifecycle.LifecycleContext;
import net.jqwik.api.lifecycle.Lifespan;
import net.jqwik.api.lifecycle.MethodLifecycleContext;
import net.jqwik.api.lifecycle.Store;

public class JqwikMicronautExtension extends AbstractMicronautExtension<LifecycleContext> {
    public static final Store<JqwikMicronautExtension> STORE = Store.getOrCreate(
            JqwikMicronautExtension.class,
            Lifespan.RUN,
            JqwikMicronautExtension::new
    );

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void beforeContainer(final ContainerLifecycleContext context) throws Exception {
        final MicronautTestValue micronautTestValue = TestContextUtils.buildMicronautTestValue(context);
        beforeClass(context, context.optionalContainerClass().orElse(null), micronautTestValue);
        beforeTestClass(TestContextUtils.buildContext(applicationContext, context));
    }

    public void before(final MethodLifecycleContext context) throws Exception {
        beforeEach(
                context,
                context.testInstance(),
                context.targetMethod(),
                context.findRepeatableAnnotations(Property.class)
        );
        beforeTestMethod(TestContextUtils.buildContext(applicationContext, context));
    }

    public void preBefore(final MethodLifecycleContext context) throws Exception {
        beforeSetupTest(TestContextUtils.buildContext(applicationContext, context));
    }

    public void postBefore(final MethodLifecycleContext context) throws Exception {
        afterSetupTest(TestContextUtils.buildContext(applicationContext, context));
    }

    public void preAfter(final MethodLifecycleContext context) throws Exception {
        beforeCleanupTest(TestContextUtils.buildContext(applicationContext, context));
    }

    public void postAfter(final MethodLifecycleContext context) throws Exception {
        afterCleanupTest(TestContextUtils.buildContext(applicationContext, context));
    }

    public void beforeExecution(final MethodLifecycleContext context) throws Exception {
        beforeTestExecution(TestContextUtils.buildContext(applicationContext, context));
    }

    public void afterExecution(final MethodLifecycleContext context) throws Exception {
        afterTestExecution(TestContextUtils.buildContext(applicationContext, context));
    }

    public void after(final MethodLifecycleContext context) throws Exception {
        afterEach(context);
        afterTestMethod(TestContextUtils.buildContext(applicationContext, context));
    }

    public void afterContainer(final ContainerLifecycleContext context) throws Exception {
        afterTestClass(TestContextUtils.buildContext(applicationContext, context));
        afterClass(context);
    }

    @Override
    protected void resolveTestProperties(
            final LifecycleContext context,
            final MicronautTestValue testAnnotationValue,
            final Map<String, Object> testProperties
    ) {
        context.optionalContainerClass()
               .map(context::newInstance)
               .filter(TestPropertyProvider.class::isInstance)
               .map(TestPropertyProvider.class::cast)
               .map(TestPropertyProvider::getProperties)
               .ifPresent(testProperties::putAll);
    }

    @Override
    protected void alignMocks(final LifecycleContext context, final Object instance) {
        if (specDefinition == null) {
            return;
        }
        ((MethodLifecycleContext) context).testInstances()
                                          .stream()
                                          .filter(e -> e.getClass().equals(specDefinition.getBeanType()))
                                          .findAny()
                                          .ifPresent(MockInjector.inject(specDefinition));
    }
}