package net.jqwik.micronaut.extension;

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
import net.jqwik.engine.support.JqwikAnnotationSupport;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

public class JqwikMicronautExtension extends AbstractMicronautExtension<LifecycleContext> {
    public static final Store<JqwikMicronautExtension> STORE = Store.getOrCreate(
            JqwikMicronautExtension.class,
            Lifespan.RUN,
            JqwikMicronautExtension::new
    );
    public static boolean PER_TRY;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void beforeContainer(final ContainerLifecycleContext context) throws Exception {
        final MicronautTestValue micronautTestValue = buildMicronautTestValue(context);
        beforeClass(context, context.optionalContainerClass().orElse(null), micronautTestValue);
        beforeTestClass(buildContainerContext(context));
    }

    public void afterContainer(final ContainerLifecycleContext context) throws Exception {
        afterTestClass(buildContainerContext(context));
        afterClass(context);
    }

    public void beforeProperty(final PropertyLifecycleContext context) throws Exception {
        final TestContext testContext = buildPropertyContext(context);
        injectEnclosingTestInstances(context);
        beforeEach(
                context,
                context.testInstance(),
                context.targetMethod(),
                context.findRepeatableAnnotations(Property.class)
        );
        beforeTestMethod(testContext);
    }

    public void afterProperty(final PropertyLifecycleContext context) {
        final TestContext testContext = buildPropertyContext(context);
        runHooks(() -> {
            afterEach(context);
            afterTestMethod(testContext);
            return null;
        });
    }

    public void preBeforePropertyMethod(final PropertyLifecycleContext context) throws Throwable {
        final TestContext testContext = buildPropertyContext(context);
        beforeSetupTest(testContext);
    }

    public void postBeforePropertyMethod(final PropertyLifecycleContext context) throws Throwable {
        final TestContext testContext = buildPropertyContext(context);
        afterSetupTest(testContext);
    }

    public void preAfterPropertyMethod(final PropertyLifecycleContext context) {
        final TestContext testContext = buildPropertyContext(context);
        runHooks(() -> {
            beforeCleanupTest(testContext);
            return null;
        });
    }

    public void postAfterPropertyMethod(final PropertyLifecycleContext context) {
        final TestContext testContext = buildPropertyContext(context);
        runHooks(() -> {
            afterCleanupTest(testContext);
            return null;
        });
    }

    public void beforePropertyExecution(final PropertyLifecycleContext context) throws Exception {
        final TestContext testContext = buildPropertyContext(context);
        beforeTestExecution(testContext);
    }

    public void afterPropertyExecution(final PropertyLifecycleContext context) {
        final TestContext testContext = buildPropertyContext(context);
        runHooks(() -> {
            afterTestExecution(testContext);
            return null;
        });
    }

    public void beforeTryExecution(final TryLifecycleContext context) throws Exception {
        final TestContext testContext = buildTryContext(context);
        beforeTestExecution(testContext);
    }

    public void afterTryExecution(final TryLifecycleContext context) {
        final TestContext testContext = buildTryContext(context);
        runHooks(() -> {
            afterTestExecution(testContext);
            return null;
        });
    }

    @Override
    protected void resolveTestProperties(
            final LifecycleContext context, final MicronautTestValue testAnnotationValue,
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
        if (specDefinition == null || !(context instanceof PropertyLifecycleContext)) {
            return;
        }
        ((PropertyLifecycleContext) context).testInstances()
                                            .stream()
                                            .filter(e -> e.getClass().equals(specDefinition.getBeanType()))
                                            .findAny()
                                            .ifPresent(MockInjector.inject(specDefinition));
    }

    private void runHooks(final Callable<Void> hooks) {
        try {
            hooks.call();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void injectEnclosingTestInstances(final LifecycleContext lifecycleContext) {
        if (lifecycleContext instanceof PropertyLifecycleContext) {
            ((PropertyLifecycleContext) lifecycleContext).testInstances().forEach(applicationContext::inject);
        }
    }

    /**
     * Builds a {@link MicronautTestValue} object from the provided context (e.g. by scanning annotations).
     *
     * @param context the container context to extract builder configuration from
     * @return a MicronautTestValue to configure the test application context
     */
    private MicronautTestValue buildMicronautTestValue(final ContainerLifecycleContext context) {
        return context.findAnnotation(JqwikMicronautTest.class)
                      .map(this::buildValueObject)
                      .orElse(null);
    }

    private MicronautTestValue buildValueObject(final JqwikMicronautTest micronautTest) {
        PER_TRY = micronautTest.perTry();
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

    private TestContext buildPropertyContext(final PropertyLifecycleContext context) {
        return new TestContext(
                applicationContext,
                context.containerClass(),
                context.targetMethod(),
                context.testInstance(),
                null // TODO: How to handle exceptions that occur during hook executions?
        );
    }

    private TestContext buildTryContext(final TryLifecycleContext context) {
        return new TestContext(
                applicationContext,
                context.containerClass(),
                context.targetMethod(),
                context.testInstance(),
                null // TODO: How to handle exceptions that occur during hook executions?
        );
    }

    private TestContext buildContainerContext(final ContainerLifecycleContext context) {
        return new TestContext(
                applicationContext,
                context.optionalContainerClass().orElse(null),
                context.optionalElement().orElse(null),
                null,
                null
        );
    }
}