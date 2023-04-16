package net.jqwik.micronaut.listeners;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Order;
import io.micronaut.test.context.TestContext;
import io.micronaut.test.context.TestExecutionListener;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import net.jqwik.micronaut.TestExecutionListenerOrderTest;

@Singleton
@Order(99)
@Requires(env = TestExecutionListenerOrderTest.ENVIRONMENT)
public class FirstExecutionListener implements TestExecutionListener {
    private final Provider<SecondExecutionListener> second;
    boolean beforeTestClass;
    boolean afterTestClass;
    boolean beforeSetup;
    boolean afterSetup;
    boolean beforeCleanup;
    boolean afterCleanup;
    boolean beforeMethod;
    boolean afterMethod;
    boolean beforeExecution;
    boolean afterExecution;

    FirstExecutionListener(final Provider<SecondExecutionListener> second) {
        this.second = second;
    }

    @Override
    public void beforeTestClass(final TestContext testContext) {
        if (second.get().isBeforeTestClass()) {
            throw new RuntimeException("second beforeTestClass executed before first");
        }
        beforeTestClass = true;
        afterTestClass = false;
    }

    @Override
    public void beforeSetupTest(final TestContext testContext) {
        if (second.get().isBeforeSetup()) {
            throw new RuntimeException("second beforeSetupTest executed before first");
        }
        beforeSetup = true;
        afterSetup = false;
    }

    @Override
    public void afterSetupTest(final TestContext testContext) {
        if (!second.get().isAfterSetup()) {
            throw new RuntimeException("second afterSetupTest not executed before first");
        }
        afterSetup = true;
        beforeSetup = false;
    }

    @Override
    public void beforeCleanupTest(final TestContext testContext) {
        if (second.get().isBeforeCleanup()) {
            throw new RuntimeException("second beforeCleanupTest executed before first");
        }
        beforeCleanup = true;
        afterCleanup = false;
    }

    @Override
    public void afterCleanupTest(final TestContext testContext) {
        if (!second.get().isAfterCleanup()) {
            throw new RuntimeException("second afterCleanupTest not executed before first");
        }
        afterCleanup = true;
        beforeCleanup = false;
    }

    @Override
    public void beforeTestMethod(final TestContext testContext) {
        if (second.get().isBeforeMethod()) {
            throw new RuntimeException("second beforeTestMethod executed before first");
        }
        beforeMethod = true;
        afterMethod = false;
    }

    @Override
    public void beforeTestExecution(final TestContext testContext) {
        if (second.get().isBeforeExecution()) {
            throw new RuntimeException("second beforeTestExecution executed before first");
        }
        beforeExecution = true;
        afterExecution = false;
    }

    @Override
    public void afterTestExecution(final TestContext testContext) {
        if (!second.get().isAfterExecution()) {
            throw new RuntimeException("second afterTestExecution not executed before first");
        }
        afterExecution = true;
        beforeExecution = false;
    }

    @Override
    public void afterTestMethod(final TestContext testContext) {
        if (!second.get().isAfterMethod()) {
            throw new RuntimeException("second afterTestMethod not executed before first");
        }
        afterMethod = true;
        beforeMethod = false;
    }

    @Override
    public void afterTestClass(final TestContext testContext) {
        if (!second.get().isAfterTestClass()) {
            throw new RuntimeException("second afterTestClass not executed before first");
        }
        afterTestClass = true;
        beforeTestClass = false;
    }

    public boolean isBeforeTestClass() {
        return beforeTestClass;
    }

    public boolean isAfterTestClass() {
        return afterTestClass;
    }

    public boolean isBeforeSetup() {
        return beforeSetup;
    }

    public boolean isAfterSetup() {
        return afterSetup;
    }

    public boolean isBeforeCleanup() {
        return beforeCleanup;
    }

    public boolean isAfterCleanup() {
        return afterCleanup;
    }

    public boolean isBeforeMethod() {
        return beforeMethod;
    }

    public boolean isAfterMethod() {
        return afterMethod;
    }

    public boolean isBeforeExecution() {
        return beforeExecution;
    }

    public boolean isAfterExecution() {
        return afterExecution;
    }
}
