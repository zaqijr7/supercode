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
import org.hibernate.event.spi.SaveOrUpdateEvent;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
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

    public List<BigDecimal> getAmountBank(GeneralRequest request, String payMeth) {
        String notesLike = "";
        LocalDate settlementDate = LocalDate.parse(request.getTransDate()); // Konversi String ke LocalDate
        LocalDate settlementDateNew = settlementDate;
        if (payMeth.equalsIgnoreCase(MessageConstant.GRABFOOD)) {
            notesLike = "atmb"; // Ubah ke lowercase
        } else if (payMeth.equalsIgnoreCase(MessageConstant.GOFOOD) || payMeth.equalsIgnoreCase(MessageConstant.GOPAY)) {
            notesLike = "dompet anak bangsa"; // Ubah ke lowercase
            DayOfWeek dayOfWeek = settlementDate.getDayOfWeek();

            switch (dayOfWeek) {
                case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, SUNDAY -> settlementDateNew = settlementDate.plusDays(1);
                case FRIDAY -> settlementDateNew = settlementDate.plusDays(3);
                case SATURDAY -> settlementDateNew = settlementDate.plusDays(2);
                default -> {} // Tidak ada perubahan
            }

        }

        String query = "SELECT DISTINCT bm.amount \n" +
                "    FROM bank_mutation bm \n" +
                "    JOIN payment_method pm \n" +
                "        ON pm.bank_disburse = bm.bank \n" +
                "        AND pm.bank_acc_no = bm.account_no \n" +
                "    WHERE trans_date = ?1 and debit_credit = 'Credit' and LOWER(notes) LIKE LOWER(?2)";
        Query nativeQuery = entityManager.createNativeQuery(query)
                .setParameter(1, settlementDateNew.toString())
                .setParameter(2, "%" + notesLike + "%");

        return nativeQuery.getResultList();
    }



    public List<Map<String, Object>> getDataBank(GeneralRequest request, String payMeth) {
        String notesLike = "";
        LocalDate settlementDateNew = LocalDate.parse(request.getTransDate());
        System.out.println("ini transdate "+ request.getTransDate());
        if (payMeth.equalsIgnoreCase(MessageConstant.GRABFOOD)) {
            notesLike = "atmb"; // Konversi ke lowercase
        } else if (payMeth.equalsIgnoreCase(MessageConstant.GOFOOD)
        || payMeth.equalsIgnoreCase(MessageConstant.GOPAY)) {
            notesLike = "dompet anak bangsa"; // Konversi ke lowercase

        }else if(payMeth.equalsIgnoreCase(MessageConstant.SHOPEEFOOD)){
            System.out.println("ini request "+ request.getTransDate());
            notesLike="AIRPAY";
        }
        String query = "SELECT DISTINCT bm.amount, bm.bank_mutation_id " +
                "FROM bank_mutation bm " +
                "JOIN payment_method pm " +
                "ON pm.bank_disburse = bm.bank " +
                "AND pm.bank_acc_no = bm.account_no " +
                "WHERE trans_date = ?1 " +
                "AND bank_mutation_id NOT IN (SELECT flag_id_bank FROM detail_agregator_payment) " +
                "AND LOWER(notes) LIKE LOWER(?2) " +
                "ORDER BY bm.bank_mutation_id ASC";

        Query nativeQuery = entityManager.createNativeQuery(query)
                .setParameter(1, settlementDateNew.toString())
                .setParameter(2, "%" + notesLike + "%");
        List<Object[]> rawResults = nativeQuery.getResultList();
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Object[] row : rawResults) {
            Map<String, Object> map = new HashMap<>();
            map.put("netAmount", row[0]);  // amount ada di index 0
            map.put("bankMutationId", row[1]);
            map.put("settDate", settlementDateNew.toString());// bank_mutation_id ada di index 1
            resultList.add(map);
        }

        return resultList;
    }


}
