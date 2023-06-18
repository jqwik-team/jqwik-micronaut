package net.jqwik.micronaut.internal.extension;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.concurrent.Callable;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.annotation.MicronautTestValue;
import io.micronaut.test.context.TestContext;
import io.micronaut.test.extensions.AbstractMicronautExtension;
import io.micronaut.test.support.TestPropertyProvider;

import net.jqwik.api.lifecycle.ContainerLifecycleContext;
import net.jqwik.api.lifecycle.LifecycleContext;
import net.jqwik.api.lifecycle.Lifespan;
import net.jqwik.api.lifecycle.MethodLifecycleContext;
import net.jqwik.api.lifecycle.PropertyLifecycleContext;
import net.jqwik.api.lifecycle.Store;
import net.jqwik.api.lifecycle.TryLifecycleContext;

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
        final TestContext testContext = TestContextUtils.buildContext(applicationContext, context);
        final Object testInstance = context.testInstance();
        final AnnotatedElement method = context.targetMethod();
        if (context instanceof PropertyLifecycleContext) {
            context.testInstances().forEach(applicationContext::inject);
        }
        if (context instanceof TryLifecycleContext) {
            applicationContext.inject(context.testInstance());
        }
        beforeEach(
                context,
                testInstance,
                method,
                context.findRepeatableAnnotations(Property.class)
        );
        beforeTestMethod(testContext);
    }

    public void preBeforeMethod(final MethodLifecycleContext context) throws Exception {
        final TestContext testContext = TestContextUtils.buildContext(applicationContext, context);
        beforeSetupTest(testContext);
    }

    public void postBeforeMethod(final MethodLifecycleContext context) throws Exception {
        final TestContext testContext = TestContextUtils.buildContext(applicationContext, context);
        afterSetupTest(testContext);
    }

    public void preAfterMethod(final MethodLifecycleContext context) {
        final TestContext testContext = TestContextUtils.buildContext(applicationContext, context);
        runHooks(() -> {
            beforeCleanupTest(testContext);
            return null;
        });
    }

    public void postAfterMethod(final MethodLifecycleContext context) {
        final TestContext testContext = TestContextUtils.buildContext(applicationContext, context);
        runHooks(() -> {
            afterCleanupTest(testContext);
            return null;
        });
    }

    public void beforeExecution(final MethodLifecycleContext context) throws Exception {
        final TestContext testContext = TestContextUtils.buildContext(applicationContext, context);
        beforeTestExecution(testContext);
    }

    public void afterExecution(final MethodLifecycleContext context) {
        final TestContext testContext = TestContextUtils.buildContext(applicationContext, context);
        runHooks(() -> {
            afterTestExecution(testContext);
            return null;
        });
    }

    public void after(final MethodLifecycleContext context) {
        final TestContext testContext = TestContextUtils.buildContext(applicationContext, context);
        runHooks(() -> {
            afterEach(context);
            afterTestMethod(testContext);
            return null;
        });
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
        if (context instanceof TryLifecycleContext) {
            MockInjector.inject(specDefinition).accept(((TryLifecycleContext) context).testInstance());
        }
        if (context instanceof PropertyLifecycleContext) {
            ((PropertyLifecycleContext) context).testInstances()
                                                .stream()
                                                .filter(e -> e.getClass().equals(specDefinition.getBeanType()))
                                                .findAny()
                                                .ifPresent(MockInjector.inject(specDefinition));
        }
    }

    private void runHooks(final Callable<Void> hooks) {
        try {
            hooks.call();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}