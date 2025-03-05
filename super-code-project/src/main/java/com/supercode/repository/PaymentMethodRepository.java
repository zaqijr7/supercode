package com.supercode.repository;
import com.supercode.entity.PaymentMethod;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class PaymentMethodRepository implements PanacheRepository<PaymentMethod> {
    @PersistenceContext
    EntityManager entityManager;

    public String getPaymentMethodByPmId(String pmId) {
        return (String) entityManager.createNativeQuery(
                        "SELECT payment_method FROM payment_method WHERE pm_id = ?1")
                .setParameter(1, pmId)
                .getSingleResult();
    }

    public List<String> getPaymentMethods() {
        return  entityManager.createNativeQuery(
                        "SELECT pm_id FROM payment_method WHERE status = 1 and pm_id!='0'")
                .getResultList();
    }

    public String getPaymentIdByPaymentMethod(String paymentMethod) {
        return (String) entityManager.createNativeQuery(
                        "SELECT pm_id  \n" +
                                "FROM payment_method pm  \n" +
                                "WHERE UPPER(payment_method) = UPPER(?1);\n")
                .setParameter(1, paymentMethod)
                .getSingleResult();

    }


}
