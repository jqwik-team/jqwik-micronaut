package net.jqwik.micronaut;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import io.micronaut.test.annotation.TransactionMode;
import jakarta.inject.Inject;

import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.AfterProperty;
import net.jqwik.api.lifecycle.BeforeProperty;
import net.jqwik.micronaut.annotation.DbProperties;
import net.jqwik.micronaut.beans.Book;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest(transactionMode = TransactionMode.SINGLE_TRANSACTION, perTry = false)
@DbProperties
class JpaSingleTransactionMultipleSetupsTest {
    @Inject
    private EntityManager entityManager;

    @BeforeProperty
    void setUpOne() {
        final Book book = new Book();
        book.setTitle("The Stand");
        entityManager.persist(book);
    }

    @BeforeProperty
    void setUpTwo() {
        final Book book = new Book();
        book.setTitle("The Shining");
        entityManager.persist(book);
    }

    @AfterProperty
    void tearDown() {
        // check setups were rolled back
        final CriteriaQuery<Book> query = entityManager.getCriteriaBuilder().createQuery(Book.class);
        query.from(Book.class);
        assertThat(entityManager.createQuery(query).getResultList()).isEmpty();
    }

    @Property(tries = 1)
    void testPersistOne() {
        final CriteriaQuery<Book> query = entityManager.getCriteriaBuilder().createQuery(Book.class);
        query.from(Book.class);
        assertThat(entityManager.createQuery(query).getResultList().size()).isEqualTo(2);
    }

    @Property(tries = 1)
    void testPersistTwo() {
        final CriteriaQuery<Book> query = entityManager.getCriteriaBuilder().createQuery(Book.class);
        query.from(Book.class);
        assertThat(entityManager.createQuery(query).getResultList().size()).isEqualTo(2);
    }
}