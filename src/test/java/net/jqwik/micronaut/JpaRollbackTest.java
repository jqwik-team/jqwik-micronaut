package net.jqwik.micronaut;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import io.micronaut.test.annotation.TransactionMode;
import jakarta.inject.Inject;

import net.jqwik.api.Property;
import net.jqwik.micronaut.annotation.DbProperties;
import net.jqwik.micronaut.beans.Book;

import static org.assertj.core.api.Assertions.assertThat;

@DbProperties
@JqwikMicronautTest(transactionMode = TransactionMode.SINGLE_TRANSACTION, perTry = false)
class JpaRollbackTest {
    @Inject
    @PersistenceContext
    private EntityManager entityManager;

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
        assertThat(entityManager.createQuery(query).getResultList().size()).isEqualTo(1);
    }
}