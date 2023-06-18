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
        beforeTestClass(TestContextUtils.buildContainerContext(applicationContext, context));
    }

    public void afterContainer(final ContainerLifecycleContext context) throws Exception {
        afterTestClass(TestContextUtils.buildContainerContext(applicationContext, context));
        afterClass(context);
    }

    public void beforeProperty(final PropertyLifecycleContext context) throws Exception {
        final TestContext testContext = TestContextUtils.buildPropertyContext(applicationContext, context);
        before(
                testContext,
                context,
                context.testInstance(),
                context.targetMethod()
        );
    }

    public void afterProperty(final PropertyLifecycleContext context) {
        final TestContext testContext = TestContextUtils.buildPropertyContext(applicationContext, context);
        after(testContext, context);
    }

    public void preBeforePropertyMethod(final PropertyLifecycleContext context) throws Exception {
        final TestContext testContext = TestContextUtils.buildPropertyContext(applicationContext, context);
        beforeSetupTest(testContext);
    }

    public void postBeforePropertyMethod(final PropertyLifecycleContext context) throws Exception {
        final TestContext testContext = TestContextUtils.buildPropertyContext(applicationContext, context);
        afterSetupTest(testContext);
    }

    public void preAfterPropertyMethod(final PropertyLifecycleContext context) {
        final TestContext testContext = TestContextUtils.buildPropertyContext(applicationContext, context);
        runHooks(() -> {
            beforeCleanupTest(testContext);
            return null;
        });
    }

    public void postAfterPropertyMethod(final PropertyLifecycleContext context) {
        final TestContext testContext = TestContextUtils.buildPropertyContext(applicationContext, context);
        runHooks(() -> {
            afterCleanupTest(testContext);
            return null;
        });
    }

    public void beforePropertyExecution(final PropertyLifecycleContext context) throws Exception {
        final TestContext testContext = TestContextUtils.buildPropertyContext(applicationContext, context);
        beforeTestExecution(testContext);
    }

    public void afterPropertyExecution(final PropertyLifecycleContext context) {
        final TestContext testContext = TestContextUtils.buildPropertyContext(applicationContext, context);
        runHooks(() -> {
            afterTestExecution(testContext);
            return null;
        });
    }

    public void beforeTry(final TryLifecycleContext context) throws Exception {
        final TestContext testContext = TestContextUtils.buildTryContext(applicationContext, context);
        before(
                testContext,
                context,
                context.testInstance(),
                context.targetMethod()
        );
    }

    public void afterTry(final TryLifecycleContext context) {
        final TestContext testContext = TestContextUtils.buildTryContext(applicationContext, context);
        after(testContext, context);
    }

    public void preBeforeTryMethod(final TryLifecycleContext context) throws Exception {
        final TestContext testContext = TestContextUtils.buildTryContext(applicationContext, context);
        beforeSetupTest(testContext);
    }

    public void postBeforeTryMethod(final TryLifecycleContext context) throws Exception {
        final TestContext testContext = TestContextUtils.buildTryContext(applicationContext, context);
        afterSetupTest(testContext);
    }

    public void preAfterTryMethod(final TryLifecycleContext context) {
        final TestContext testContext = TestContextUtils.buildTryContext(applicationContext, context);
        runHooks(() -> {
            beforeCleanupTest(testContext);
            return null;
        });
    }

    public void postAfterTryMethod(final TryLifecycleContext context) {
        final TestContext testContext = TestContextUtils.buildTryContext(applicationContext, context);
        runHooks(() -> {
            afterCleanupTest(testContext);
            return null;
        });
    }

    public void beforeTryExecution(final TryLifecycleContext context) throws Exception {
        final TestContext testContext = TestContextUtils.buildTryContext(applicationContext, context);
        beforeTestExecution(testContext);
    }

    public void afterTryExecution(final TryLifecycleContext context) {
        final TestContext testContext = TestContextUtils.buildTryContext(applicationContext, context);
        runHooks(() -> {
            afterTestExecution(testContext);
            return null;
        });
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

    private void before(
            final TestContext testContext,
            final LifecycleContext context,
            final Object testInstance,
            final AnnotatedElement method
    ) throws Exception {
        if (context instanceof PropertyLifecycleContext) {
            ((PropertyLifecycleContext) context).testInstances().forEach(applicationContext::inject);
        }
        if (context instanceof TryLifecycleContext) {
            applicationContext.inject(((TryLifecycleContext) context).testInstance());
        }
        beforeEach(
                context,
                testInstance,
                method,
                context.findRepeatableAnnotations(Property.class)
        );
        beforeTestMethod(testContext);
    }

    private void after(final TestContext testContext, final LifecycleContext context) {
        runHooks(() -> {
            afterEach(context);
            afterTestMethod(testContext);
            return null;
        });
    }

    private void runHooks(final Callable<Void> hooks) {
        try {
            hooks.call();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}