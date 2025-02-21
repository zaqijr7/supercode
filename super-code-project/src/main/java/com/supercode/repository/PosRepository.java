package com.supercode.repository;

import com.supercode.entity.DetailPaymentPos;
import com.supercode.request.GeneralRequest;
import com.supercode.util.MessageConstant;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PosRepository implements PanacheRepository<DetailPaymentPos> {

    @PersistenceContext
    EntityManager entityManager;

    public int getCountDataPost(GeneralRequest request, String branchId, String pmId) {
        String query ="select count(*) from detail_point_of_sales dpos " +
                "where trans_date = ?1 and branch_id =?2 " +
                "and flag_rekon_ecom ='0' and pay_method_aggregator = ?3 order by trans_date, trans_time asc";
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            query += "AND SUBSTRING(trans_time, 1, 2) = :transTime ";
        }

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2,  branchId)
                .setParameter(3, pmId);
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            nativeQuery.setParameter("transTime", request.getTransTime().substring(0, 2));
        }

        Object result = nativeQuery.getSingleResult();
        return ((Number) result).intValue();
    }

    public List<BigDecimal> getAllGrossAmount(GeneralRequest request, String branchId) {

        String query ="select gross_amount from detail_point_of_sales dpos " +
                "where trans_date = ?1   " +
                "and pm_id = ?2 and branch_id =?3 and flag_rekon_ecom ='0' order by trans_date, trans_time asc";
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            query += "AND SUBSTRING(trans_time, 1, 2) = :transTime ";
        }

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, request.getPmId())
                .setParameter(3, branchId);
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            nativeQuery.setParameter("transTime", request.getTransTime().substring(0, 2));
        }

        List<BigDecimal> result = nativeQuery.getResultList();
        return result;
    }

    /*public void updateFlagByCondition(GeneralRequest request) {
        entityManager.createNativeQuery(
                        "UPDATE detail_point_of_sales dpos " +
                                "SET flag_rekon_ecom = :newFlag " +
                                "WHERE trans_date = :transDate " +
                                "AND SUBSTRING(trans_time, 1, 2) = :transTime " +
                                "AND pm_id = :pmId " +
                                "AND branch_id = :branchId " +
                                "AND flag_rekon_ecom ='0' and pay_method_aggregator = :pm")
                .setParameter("newFlag", "1") // Ganti "Y" dengan nilai flag yang diinginkan
                .setParameter("transDate", request.getTransDate())
                .setParameter("transTime", request.getTransTime())
                .setParameter("pmId", "0")
                .setParameter("branchId", request.getBranchId())
                .setParameter("pm", request.getPmId())
                .executeUpdate();
    }*/

    /*public void updateFlagByCondition(GeneralRequest request) {
        // Langkah 1: Identifikasi data yang unik (tidak duplikat) berdasarkan gross_amount
        String findUniqueQuery = "SELECT detail_pos_id FROM detail_point_of_sales dpos " +
                "WHERE trans_date = :transDate " +
                "AND pm_id = :pmId " +
                "AND branch_id = :branchId " +
                "AND flag_rekon_ecom = '0' " +
                "AND pay_method_aggregator = :pm " +
                "AND gross_amount IN ( " +
                "   SELECT gross_amount FROM detail_point_of_sales " +
                "   GROUP BY gross_amount " +
                "   HAVING COUNT(*) = 1 " + // Hanya ambil gross_amount yang tidak duplikat
                ")";

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            findUniqueQuery += " AND SUBSTRING(trans_time, 1, 2) = :transTime";
        }

        Query uniqueQuery = entityManager.createNativeQuery(findUniqueQuery)
                .setParameter("transDate", request.getTransDate())
                .setParameter("pmId", request.getPmId())
                .setParameter("branchId", request.getBranchId())
                .setParameter("pm", request.getPmId());

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            uniqueQuery.setParameter("transTime", request.getTransTime().substring(0, 2));
        }

        List<Long> uniqueIds = uniqueQuery.getResultList();

        // Langkah 2: Update hanya data yang unik
        if (!uniqueIds.isEmpty()) {
            String updateQuery = "UPDATE detail_point_of_sales dpos " +
                    "SET flag_rekon_ecom = :newFlag " +
                    "WHERE detail_pos_id IN (:uniqueIds)"; // Update hanya data yang unik

            Query updateNativeQuery = entityManager.createNativeQuery(updateQuery)
                    .setParameter("newFlag", 1)  // Gunakan Integer, bukan String
                    .setParameter("uniqueIds", uniqueIds);

            updateNativeQuery.executeUpdate();
        }*/
    //}

    public int getCountFailed(String pmId, String transDate) {
        Object result = entityManager.createNativeQuery(
                        "select count(*) from detail_point_of_sales dpos " +
                                "where trans_date = ?1  and pay_method_aggregator = ?2 and flag_rekon_ecom=0 order by trans_date, trans_time asc")
                .setParameter(1, transDate)
                .setParameter(2, pmId)
                .getSingleResult();

        return ((Number) result).intValue();
    }

    public String getTransDateByParentId(String parentId) {
        return entityManager.createNativeQuery(
                        "select distinct trans_date from detail_point_of_sales dpos " +
                                "where parent_id = ?1 ")
                .setParameter(1, parentId)
                .getSingleResult().toString();
    }

    public String getParentId(GeneralRequest request, String branchId, String pmId) {
        String query ="select parent_id from detail_point_of_sales dpos " +
                "where trans_date = ?1 and branch_id =?2 " +
                "and flag_rekon_ecom ='0' and pay_method_aggregator = ?3 ";
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            query += "AND SUBSTRING(trans_time, 1, 2) = :transTime ";
        }

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2,  branchId)
                .setParameter(3, pmId);
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            nativeQuery.setParameter("transTime", request.getTransTime().substring(0, 2));
        }

        Object result = nativeQuery.getSingleResult();
        return result.toString();
    }

    public void updateFlagNormalByCondition(GeneralRequest request) {
        String newFlag = MessageConstant.TWO_VALUE;
        String query ="update detail_point_of_sales dpos set flag_rekon_ecom= ?1" +
                "where trans_date = ?2 and branch_id =?3 " +
                "and flag_rekon_ecom ='0' and pay_method_aggregator = ?4 ";
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            query += "AND SUBSTRING(trans_time, 1, 2) = :transTime ";
            newFlag=MessageConstant.ONE_VALUE;
        }

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, newFlag)
                .setParameter(2, request.getTransDate())
                .setParameter(3,  request.getBranchId())
                .setParameter(4, request.getPmId());
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            nativeQuery.setParameter("transTime", request.getTransTime().substring(0, 2));
        }

        nativeQuery.executeUpdate();

    }

    public void updatePosFlag(GeneralRequest request, List<BigDecimal> grossAmounts) {
        String newFlag = MessageConstant.TWO_VALUE;
        String findUniqueQuery = "SELECT detail_pos_id FROM ( " +
                "    SELECT dap1.detail_pos_id, dap1.gross_amount, " +
                "           ROW_NUMBER() OVER (PARTITION BY dap1.gross_amount ORDER BY dap1.detail_pos_id) AS rn " +
                "    FROM detail_point_of_sales dap1 " +
                "    WHERE dap1.trans_date = :transDate " +
                "    AND dap1.pm_id = :pmId " +
                "    AND dap1.branch_id = :branchId " +
                "    AND dap1.flag_rekon_ecom = '0' " +
                "    AND dap1.gross_amount IN (:grossAmounts) order by dap1.trans_date, dap1.trans_time asc";
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            findUniqueQuery += " AND SUBSTRING(trans_time, 1, 2) = :transTime ";
            newFlag=MessageConstant.ONE_VALUE;
        }
        findUniqueQuery+=  ") temp WHERE rn <= ( " +
                "    SELECT COUNT(*) FROM detail_agregator_payment pos " +
                "    WHERE pos.trans_date = :transDate " +
                "    AND pos.pm_id = :pmId " +
                "    AND pos.branch_id = :branchId " +
                "    AND pos.gross_amount = temp.gross_amount order by pos.trans_date, pos.trans_time asc";
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            findUniqueQuery += " AND SUBSTRING(trans_time, 1, 2) = :transTime";
        }
        findUniqueQuery+= ")";
        Query uniqueQuery = entityManager.createNativeQuery(findUniqueQuery)
                .setParameter("transDate", request.getTransDate())
                .setParameter("pmId", request.getPmId())
                .setParameter("branchId", request.getBranchId())
                .setParameter("grossAmounts", grossAmounts);

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            uniqueQuery.setParameter("transTime", request.getTransTime().substring(0, 2));
        }

        List<BigDecimal> uniqueIds = uniqueQuery.getResultList();

        // Langkah 2: Update hanya jumlah yang sesuai dengan data POS
        if (!uniqueIds.isEmpty()) {
            String updateQuery = "UPDATE detail_point_of_sales " +
                    "SET flag_rekon_ecom = " + newFlag+
                    " WHERE detail_pos_id IN (:uniqueIds)";

            Query updateNativeQuery = entityManager.createNativeQuery(updateQuery)
                    .setParameter("uniqueIds", uniqueIds);

            updateNativeQuery.executeUpdate();
        }
    }

    public int getCountFailedByParentId(String parentId) {
        Object result = entityManager.createNativeQuery(
                        "select count(*) from detail_point_of_sales dpos " +
                                "where parent_id = ?1  and flag_rekon_ecom=0 order by trans_date, trans_time asc")
                .setParameter(1, parentId)
                .getSingleResult();

        return ((Number) result).intValue();
    }
}
