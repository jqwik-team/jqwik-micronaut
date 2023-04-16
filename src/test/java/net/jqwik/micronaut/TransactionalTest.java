package net.jqwik.micronaut;

import io.micronaut.context.ApplicationContext;
import io.micronaut.transaction.support.TransactionSynchronizationManager;
import io.micronaut.transaction.test.DefaultTestTransactionExecutionListener;
import jakarta.inject.Inject;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.AfterProperty;
import net.jqwik.api.lifecycle.BeforeProperty;
import net.jqwik.micronaut.annotation.DbProperties;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest
@DbProperties
// FIXME: flaky test
class TransactionalTest {
    @Inject
    private ApplicationContext applicationContext;

    @BeforeProperty
    void setup() {
        assertThat(TransactionSynchronizationManager.isSynchronizationActive()).isTrue();
    }

    @AfterProperty
    void cleanup() {
        assertThat(TransactionSynchronizationManager.isSynchronizationActive()).isTrue();
    }

    @Property(tries = 1)
    void testSpringTransactionListenerMissing() {
        assertThat(applicationContext.containsBean(DefaultTestTransactionExecutionListener.class)).isTrue();
    }
}