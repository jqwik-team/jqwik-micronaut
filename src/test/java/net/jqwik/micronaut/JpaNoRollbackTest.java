package net.jqwik.micronaut;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;

import io.micronaut.test.annotation.TransactionMode;
import io.micronaut.transaction.SynchronousTransactionManager;
import io.micronaut.transaction.TransactionStatus;
import io.micronaut.transaction.support.DefaultTransactionDefinition;
import jakarta.inject.Inject;

import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.AfterContainer;
import net.jqwik.micronaut.annotation.DbProperties;
import net.jqwik.micronaut.beans.Book;

import static org.assertj.core.api.Assertions.assertThat;

@JqwikMicronautTest(rollback = false, transactionMode = TransactionMode.SINGLE_TRANSACTION)
@DbProperties
class JpaNoRollbackTest {
    @Inject
    private EntityManager entityManager;

    @Inject
    @SuppressWarnings("rawtypes")
    private SynchronousTransactionManager transactionManager;

    @AfterContainer
    @SuppressWarnings("unchecked")
    static void cleanup(JpaNoRollbackTest subject) {
        final TransactionStatus<Book> tx = subject.transactionManager.getTransaction(new DefaultTransactionDefinition());
        final CriteriaBuilder criteriaBuilder = subject.entityManager.getCriteriaBuilder();
        final CriteriaDelete<Book> delete = criteriaBuilder.createCriteriaDelete(Book.class);
        delete.from(Book.class);
        subject.entityManager.createQuery(delete).executeUpdate();
        subject.transactionManager.commit(tx);
    }

    @Property(tries = 1)
    void testPersistOne() {
        final Book book = new Book();
        book.setTitle("The Stand");
        entityManager.persist(book);
        assertThat(entityManager.find(Book.class, book.getId())).isNotNull();

        final CriteriaQuery<Book> query = entityManager.getCriteriaBuilder().createQuery(Book.class);
        query.from(Book.class);
        assertThat(entityManager.createQuery(query).getResultList().size()).isEqualTo(1);
    }

    @Property(tries = 1)
    void testPersistTwo() {
        final Book book = new Book();
        book.setTitle("The Shining");
        entityManager.persist(book);
        assertThat(entityManager.find(Book.class, book.getId())).isNotNull();

        final CriteriaQuery<Book> query = entityManager.getCriteriaBuilder().createQuery(Book.class);
        query.from(Book.class);
        assertThat(entityManager.createQuery(query).getResultList().size()).isEqualTo(2);
    }
}