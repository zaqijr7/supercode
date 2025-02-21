package com.supercode.repository;

import com.supercode.entity.HeaderPayment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class HeaderPaymentRepository implements PanacheRepository<HeaderPayment> {
    @PersistenceContext
    EntityManager entityManager;

    public void updateHeaderPaymentByCondition(String transDate) {
        entityManager.createNativeQuery(
                        "update header_payment set status_rekon_pos_vs_ecom = 1 where trans_date = ?1")
                .setParameter(1, transDate)
                .executeUpdate();
    }

    public void updateDate(String parentId, String getTransDate) {
        entityManager.createNativeQuery(
                        "update header_payment set trans_date = ?1 where parent_id = ?2")
                .setParameter(1, getTransDate)
                .setParameter(2, parentId)
                .executeUpdate();
    }
}
