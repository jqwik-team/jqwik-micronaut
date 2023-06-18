package net.jqwik.micronaut;

import io.micronaut.context.ApplicationContext;
import io.micronaut.transaction.test.DefaultTestTransactionExecutionListener;
import jakarta.inject.Inject;

import net.jqwik.api.Property;
import net.jqwik.micronaut.annotation.DbProperties;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest(transactional = false)
@DbProperties
class NonTransactionalTest {
    @Inject
    private ApplicationContext applicationContext;

    @Property(tries = 1)
    void testMicronautTransactionListenerMissing() {
        assertThat(applicationContext.containsBean(DefaultTestTransactionExecutionListener.class)).isFalse();
    }
}