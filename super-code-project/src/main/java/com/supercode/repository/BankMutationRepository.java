package com.supercode.repository;

import com.mysql.cj.protocol.x.MessageConstants;
import com.supercode.entity.BankMutation;
import com.supercode.request.GeneralRequest;
import com.supercode.util.MessageConstant;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ApplicationScoped
public class BankMutationRepository implements PanacheRepository<BankMutation> {

    @PersistenceContext
    EntityManager entityManager;

    public int getCountBank(GeneralRequest request, String payMeth) {
        String query ="SELECT COUNT(*) \n" +
                "FROM (\n" +
                "    SELECT DISTINCT bm.* \n" +
                "    FROM bank_mutation bm \n" +
                "    JOIN payment_method pm \n" +
                "        ON pm.bank_disburse = bm.bank \n" +
                "        AND pm.bank_acc_no = bm.account_no \n" +
                "    WHERE trans_date = ?1 \n" +
                ") AS subquery";
        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate());
        Object result = nativeQuery.getSingleResult();
        return ((Number) result).intValue();
    }

    public List<BigDecimal> getAmontBank(GeneralRequest request, String payMeth) {
        String notesLike ="";
        if(payMeth.equalsIgnoreCase(MessageConstant.GRABFOOD)){
            notesLike = "ATMB";
        }
        String query ="SELECT DISTINCT bm.amount \n" +
                "    FROM bank_mutation bm \n" +
                "    JOIN payment_method pm \n" +
                "        ON pm.bank_disburse = bm.bank \n" +
                "        AND pm.bank_acc_no = bm.account_no \n" +
                "    WHERE trans_date = ?1 and debit_credit = 'Credit' and notes like '%"+notesLike+"%'";
        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate());


        List<BigDecimal> result = nativeQuery.getResultList();
        return result;
    }


    public List<Map<String, Object>> getDataBank(GeneralRequest request, String payMeth) {
        String notesLike ="";
        if(payMeth.equalsIgnoreCase(MessageConstant.GRABFOOD)){
            notesLike = "ATMB";
        }
        String query = "SELECT DISTINCT bm.amount, bm.bank_mutation_id " +
                "FROM bank_mutation bm " +
                "JOIN payment_method pm " +
                "ON pm.bank_disburse = bm.bank " +
                "AND pm.bank_acc_no = bm.account_no " +
                "WHERE trans_date = ?1 and bank_mutation_id not in(select flag_id_bank from detail_agregator_payment) " +
                " and notes like '%"+notesLike+"%'  order by bm.bank_mutation_id  asc";

        Query nativeQuery = entityManager.createNativeQuery(query)
                .setParameter(1, request.getTransDate());

        List<Object[]> rawResults = nativeQuery.getResultList();
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Object[] row : rawResults) {
            Map<String, Object> map = new HashMap<>();
            map.put("netAmount", row[0]);  // Assuming amount is in the first column
            map.put("bankMutationId", row[1]);  // Assuming bank_mutation_id is in the second column
            resultList.add(map);
        }

        return resultList;
    }

}
