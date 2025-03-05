package com.supercode.repository;

import com.supercode.entity.BankMutation;
import com.supercode.request.GeneralRequest;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.util.List;


@ApplicationScoped
public class BankMutationRepository implements PanacheRepository<BankMutation> {

    @PersistenceContext
    EntityManager entityManager;

    public int getCountBank(GeneralRequest request) {
        String query ="SELECT COUNT(*) \n" +
                "FROM (\n" +
                "    SELECT DISTINCT bm.* \n" +
                "    FROM bank_mutation bm \n" +
                "    JOIN payment_method pm \n" +
                "        ON pm.bank_disburse = bm.bank \n" +
                "        AND pm.bank_acc_no = bm.account_no \n" +
                "    WHERE trans_date = ?1\n" +
                ") AS subquery";
        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate());
        Object result = nativeQuery.getSingleResult();
        return ((Number) result).intValue();
    }

    public List<BigDecimal> getAmontBank(GeneralRequest request) {
        String query ="SELECT DISTINCT bm.amount \n" +
                "    FROM bank_mutation bm \n" +
                "    JOIN payment_method pm \n" +
                "        ON pm.bank_disburse = bm.bank \n" +
                "        AND pm.bank_acc_no = bm.account_no \n" +
                "    WHERE trans_date = ?1 ";
        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate());


        List<BigDecimal> result = nativeQuery.getResultList();
        return result;
    }

    public void updateDataBank(GeneralRequest request) {
        String query = "update bank_mutation set ";
    }
}
