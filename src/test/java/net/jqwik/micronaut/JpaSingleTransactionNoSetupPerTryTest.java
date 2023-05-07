package net.jqwik.micronaut;

import io.micronaut.test.annotation.TransactionMode;
import jakarta.inject.Inject;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.AfterProperty;
import net.jqwik.micronaut.annotation.DbProperties;
import net.jqwik.micronaut.annotation.JqwikMicronautTest;
import net.jqwik.micronaut.beans.Book;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest(transactionMode = TransactionMode.SINGLE_TRANSACTION, perTry = true)
@DbProperties
class JpaSingleTransactionNoSetupPerTryTest {
    @Inject
    private EntityManager entityManager;

    @AfterProperty
    void tearDown() {
        // check test was rolled back
        final CriteriaQuery<Book> query = entityManager.getCriteriaBuilder().createQuery(Book.class);
        query.from(Book.class);
        assertThat(entityManager.createQuery(query).getResultList()).isEmpty();
    }

    @Property(tries = 10)
    void testPersistOne() {
        System.out.println("#### testPersistOne");
        final Book book = new Book();
        book.setTitle("The Stand");
        entityManager.persist(book);

        final CriteriaQuery<Book> query = entityManager.getCriteriaBuilder().createQuery(Book.class);
        query.from(Book.class);
        assertThat(entityManager.createQuery(query).getResultList().size()).isEqualTo(1);
    }
}