package com.supercode.repository;

import com.supercode.dto.PosReportDto;
import com.supercode.entity.DetailPaymentPos;
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
public class PosRepository implements PanacheRepository<DetailPaymentPos> {

    @PersistenceContext
    EntityManager entityManager;

    public int getCountDataPost(GeneralRequest request, String branchId, String pmId) {
        String query ="select count(*) from detail_point_of_sales dpos " +
                "where trans_date = ?1 and branch_id =?2 " +
                "and flag_rekon_ecom ='0' and pay_method_aggregator = ?3 ";
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            query += "AND SUBSTRING(trans_time, 1, 2) = :transTime ";
        }
//        query += " order by trans_date, trans_time asc";
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
                "and pm_id = ?2 and branch_id =?3 and flag_rekon_ecom ='0'";
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            query += "AND SUBSTRING(trans_time, 1, 2) = :transTime ";
        }
        query += " order by trans_date, trans_time asc";

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, "0")
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
        String query = "UPDATE detail_point_of_sales dpos " +
                "JOIN (SELECT dap1.detail_payment_id, dap1.trans_date, dap1.branch_id, dap1.pm_id, dap1.gross_amount " +
                "FROM detail_agregator_payment dap1 " +
                "WHERE dap1.trans_date = :transDate " +
                "AND dap1.branch_id = :branchId " +
                "AND dap1.pm_id = :pmId ";

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            query += "AND LEFT(dap1.trans_time, 2) = :transTime ";  // Perbaiki pencocokan waktu
            newFlag = MessageConstant.ONE_VALUE;
        }

        query += "ORDER BY dap1.trans_time ASC " +  // Prioritaskan transaksi pertama
                "LIMIT 1) dap " +  // Ambil hanya satu baris dengan trans_time paling awal
                "ON dpos.branch_id = dap.branch_id " +
                "AND dpos.pay_method_aggregator = dap.pm_id " +
                "AND dpos.gross_amount = dap.gross_amount " +
                "AND dpos.trans_date = dap.trans_date " +
                "SET dpos.flag_rekon_ecom = :newFlag, " +
                "dpos.detail_id_agg = dap.detail_payment_id " +
                "WHERE dpos.flag_rekon_ecom = '0' " +
                "AND dpos.pay_method_aggregator = :pmId " +
                "AND dpos.trans_date = :transDate " +
                "AND dpos.branch_id = :branchId ";

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            query += "AND LEFT(dpos.trans_time, 2) = :transTime ";
        }

        System.out.println("Executing update with flag: " + newFlag);  // Debugging log

        Query nativeQuery = entityManager.createNativeQuery(query)
                .setParameter("newFlag", newFlag)
                .setParameter("pmId", request.getPmId())
                .setParameter("transDate", request.getTransDate())
                .setParameter("branchId", request.getBranchId());

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            nativeQuery.setParameter("transTime", request.getTransTime());
        }

        nativeQuery.executeUpdate();
    }





    /*public void updatePosFlag(GeneralRequest request, List<BigDecimal> grossAmounts) {
        String newFlag = MessageConstant.TWO_VALUE;
        String findUniqueQuery = "SELECT detail_pos_id FROM ( " +
                "    SELECT dap1.detail_pos_id, dap1.gross_amount, " +
                "           ROW_NUMBER() OVER (PARTITION BY dap1.gross_amount ORDER BY dap1.detail_pos_id) AS rn " +
                "    FROM detail_point_of_sales dap1 " +
                "    WHERE dap1.trans_date = :transDate " +
                "    AND dap1.pm_id = :pmId " +
                "    AND dap1.branch_id = :branchId " +
                "    AND dap1.flag_rekon_ecom = '0' " +
                "    AND dap1.gross_amount IN (:grossAmounts) ";
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            findUniqueQuery += " AND SUBSTRING(trans_time, 1, 2) = :transTime ";
            newFlag=MessageConstant.ONE_VALUE;
        }
        findUniqueQuery +=" order by dap1.trans_date, dap1.trans_time asc";
        findUniqueQuery+=  ") temp WHERE rn <= ( " +
                "    SELECT COUNT(*) FROM detail_agregator_payment pos " +
                "    WHERE pos.trans_date = :transDate " +
                "    AND pos.pm_id = :pmId " +
                "    AND pos.branch_id = :branchId " +
                "    AND pos.gross_amount = temp.gross_amount ";
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            findUniqueQuery += " AND SUBSTRING(trans_time, 1, 2) = :transTime";
        }
        findUniqueQuery += " order by pos.trans_date, pos.trans_time asc";
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
    }*/


    /*public void updatePosFlag(GeneralRequest request, List<BigDecimal> grossAmounts) {
        String newFlag = MessageConstant.TWO_VALUE;
        String findUniqueQuery = "SELECT detail_pos_id FROM ( " +
                "    SELECT detail_pos_id, trans_date, trans_time FROM ( " +
                "         SELECT dap1.detail_pos_id, dap1.gross_amount, dap1.trans_date, dap1.trans_time, " +
                "                ROW_NUMBER() OVER (PARTITION BY dap1.gross_amount ORDER BY dap1.trans_date, dap1.trans_time, dap1.detail_pos_id) AS rn " +
                "         FROM detail_point_of_sales dap1 " +
                "         WHERE dap1.trans_date = :transDate " +
                "         AND dap1.pm_id = :pmId " +
                "         AND dap1.branch_id = :branchId " +
                "         AND dap1.flag_rekon_ecom = '0' " +
                "         AND dap1.gross_amount IN (:grossAmounts) ";

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            findUniqueQuery += " AND SUBSTRING(dap1.trans_time, 1, 2) = :transTime ";
            newFlag = MessageConstant.ONE_VALUE;
        }

        findUniqueQuery += "    ) innerQuery " +
                "    WHERE rn <= ( " +
                "         SELECT COUNT(*) FROM detail_agregator_payment pos " +
                "         WHERE pos.trans_date = :transDate " +
                "         AND pos.pm_id = :pmId " +
                "         AND pos.branch_id = :branchId " +
                "         AND pos.gross_amount = innerQuery.gross_amount ";

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            findUniqueQuery += " AND SUBSTRING(pos.trans_time, 1, 2) = :transTime ";
        }

        findUniqueQuery += "    ) " +
                "    ORDER BY trans_date, trans_time " +
                ") ordered";

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
                    "SET flag_rekon_ecom = " + newFlag +
                    " WHERE detail_pos_id IN (:uniqueIds)";

            Query updateNativeQuery = entityManager.createNativeQuery(updateQuery)
                    .setParameter("uniqueIds", uniqueIds);

            updateNativeQuery.executeUpdate();
        }
    }*/

    public void updatePosFlag(GeneralRequest request, List<BigDecimal> grossAmounts) {
        String newFlag = MessageConstant.TWO_VALUE;
        String findUniqueQuery = "WITH ranked_pos AS ( " +
                "    SELECT dap1.detail_pos_id, dap1.gross_amount, dap1.trans_date, dap1.trans_time, dap1.pay_method_aggregator, dap1.branch_id, " +
                "           ROW_NUMBER() OVER (PARTITION BY dap1.gross_amount ORDER BY dap1.trans_date, dap1.trans_time, dap1.detail_pos_id) AS rn " +
                "    FROM detail_point_of_sales dap1 " +
                "    WHERE dap1.trans_date = :transDate " +
                "    AND dap1.pay_method_aggregator = :pmId " +
                "    AND dap1.branch_id = :branchId " +
                "    AND dap1.flag_rekon_ecom = '0' " +
                "    AND dap1.gross_amount IN (:grossAmounts) ";

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            findUniqueQuery += " AND SUBSTRING(dap1.trans_time, 1, 2) = :transTime ";
            newFlag = MessageConstant.ONE_VALUE;
        }

        findUniqueQuery += " ), valid_pos AS ( " +
                "    SELECT detail_pos_id, pay_method_aggregator, trans_date, trans_time, gross_amount, branch_id, rn " +
                "    FROM ranked_pos " +
                "    WHERE rn <= ( " +
                "        SELECT COUNT(*) FROM detail_agregator_payment pos " +
                "        WHERE pos.trans_date = :transDate " +
                "        AND pos.pm_id = :pmId " +
                "        AND pos.branch_id = :branchId " +
                "        AND pos.flag_rekon_pos = '0' " +
                "        AND pos.gross_amount = ranked_pos.gross_amount ";

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            findUniqueQuery += " AND SUBSTRING(pos.trans_time, 1, 2) = :transTime ";
        }

        findUniqueQuery += "    ) " +
                " ), ranked_agg AS ( " +
                "    SELECT dap.detail_payment_id, dap.trans_date, dap.trans_time, dap.pm_id, dap.branch_id, dap.gross_amount, " +
                "           ROW_NUMBER() OVER (PARTITION BY dap.gross_amount ORDER BY dap.trans_date, dap.trans_time, dap.detail_payment_id) AS rn " +
                "    FROM detail_agregator_payment dap " +
                "    WHERE dap.trans_date = :transDate " +
                "    AND dap.pm_id = :pmId " +
                "    AND dap.branch_id = :branchId " +
                "    AND dap.flag_rekon_pos = '0' " +
                "    AND dap.gross_amount IN (:grossAmounts) ";

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            findUniqueQuery += " AND SUBSTRING(dap.trans_time, 1, 2) = :transTime ";
        }

        findUniqueQuery += " ) " +
                "SELECT vp.detail_pos_id, ra.detail_payment_id " +
                "FROM valid_pos vp " +
                "JOIN ranked_agg ra " +
                "    ON vp.rn = ra.rn " +
                "ORDER BY vp.trans_date, vp.trans_time ";

        Query uniqueQuery = entityManager.createNativeQuery(findUniqueQuery)
                .setParameter("transDate", request.getTransDate())
                .setParameter("pmId", request.getPmId())
                .setParameter("branchId", request.getBranchId())
                .setParameter("grossAmounts", grossAmounts);

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            uniqueQuery.setParameter("transTime", request.getTransTime().substring(0, 2));
        }

        List<Object[]> uniqueResults = uniqueQuery.getResultList();

        if (!uniqueResults.isEmpty()) {
            for (Object[] result : uniqueResults) {
                String updateQuery = "UPDATE detail_point_of_sales " +
                        "SET flag_rekon_ecom = :newFlag, detail_id_agg = :detailIdAgg " +
                        "WHERE detail_pos_id = :detailPosId";

                Query updateNativeQuery = entityManager.createNativeQuery(updateQuery)
                        .setParameter("newFlag", newFlag)
                        .setParameter("detailIdAgg", result[1])
                        .setParameter("detailPosId", result[0]);

                updateNativeQuery.executeUpdate();
            }
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

    public int getCountDataPostByBranch(GeneralRequest request) {
        String query ="select count(*) from detail_point_of_sales dpos " +
                "where trans_date = ?1 and branch_id =?2 " +
                "and flag_rekon_ecom ='0'";
//        query += " order by trans_date, trans_time asc";
        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2,  request.getBranchId());

        Object result = nativeQuery.getSingleResult();
        return ((Number) result).intValue();
    }

    public List<BigDecimal> getAllGrossAmountByBranch(GeneralRequest request) {
        String query ="select gross_amount from detail_point_of_sales dpos " +
                "where trans_date = ?1   " +
                "and branch_id =?2 and flag_rekon_ecom ='0'";

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, request.getBranchId());
        List<BigDecimal> result = nativeQuery.getResultList();
        return result;
    }

    public void updateFlagNormalByBranchCondition(GeneralRequest request) {
        String newFlag = MessageConstant.THREE_VALUE;
        String query = "update detail_point_of_sales dpos set flag_rekon_ecom = :newFlag " +
                "where trans_date = :transDate and branch_id = :branchId " +
                "and flag_rekon_ecom = '0' ";


        Query nativeQuery = entityManager.createNativeQuery(query)
                .setParameter("newFlag", newFlag)
                .setParameter("transDate", request.getTransDate())
                .setParameter("branchId", request.getBranchId());


        nativeQuery.executeUpdate();
    }

    public void updatePosFlagByBranch(GeneralRequest request, List<BigDecimal> grossAmounts) {
        String newFlag = MessageConstant.THREE_VALUE;
        String findUniqueQuery = "SELECT detail_pos_id FROM ( " +
                "    SELECT detail_pos_id, trans_date, trans_time FROM ( " +
                "         SELECT dap1.detail_pos_id, dap1.gross_amount, dap1.trans_date, dap1.trans_time, " +
                "                ROW_NUMBER() OVER (PARTITION BY dap1.gross_amount ORDER BY dap1.trans_date, dap1.trans_time, dap1.detail_pos_id) AS rn " +
                "         FROM detail_point_of_sales dap1 " +
                "         WHERE dap1.trans_date = :transDate " +
                "         AND dap1.branch_id = :branchId " +
                "         AND dap1.flag_rekon_ecom = '0' " +
                "         AND dap1.gross_amount IN (:grossAmounts) ";



        findUniqueQuery += "    ) innerQuery " +
                "    WHERE rn <= ( " +
                "         SELECT COUNT(*) FROM detail_agregator_payment pos " +
                "         WHERE pos.trans_date = :transDate " +
                "         AND pos.branch_id = :branchId " +
                "         AND pos.gross_amount = innerQuery.gross_amount ";


        findUniqueQuery += "    ) " +
                "    ORDER BY trans_date, trans_time " +
                ") ordered";

        Query uniqueQuery = entityManager.createNativeQuery(findUniqueQuery)
                .setParameter("transDate", request.getTransDate())
                .setParameter("branchId", request.getBranchId())
                .setParameter("grossAmounts", grossAmounts);

        List<BigDecimal> uniqueIds = uniqueQuery.getResultList();

        // Langkah 2: Update hanya jumlah yang sesuai dengan data POS
        if (!uniqueIds.isEmpty()) {
            String updateQuery = "UPDATE detail_point_of_sales " +
                    "SET flag_rekon_ecom = " + newFlag +
                    " WHERE detail_pos_id IN (:uniqueIds)";

            Query updateNativeQuery = entityManager.createNativeQuery(updateQuery)
                    .setParameter("uniqueIds", uniqueIds);

            updateNativeQuery.executeUpdate();
        }
    }



    public int getCountDataPostWithTransTime(GeneralRequest request, String pmId) {
        String query ="select count(*) from detail_point_of_sales dpos " +
                "where trans_date = ?1 and branch_id =?2 " +
                "and flag_rekon_ecom ='0' and pay_method_aggregator = ?3 ";
//        query += " order by trans_date, trans_time asc";
        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2,  request.getBranchId())
                .setParameter(3, pmId);

        Object result = nativeQuery.getSingleResult();
        return ((Number) result).intValue();
    }

    public List<Long> getDetailPosId(GeneralRequest request, String branchId, String pmId) {
        String query ="select detail_pos_id from detail_point_of_sales dpos " +
                "where trans_date = ?1 and branch_id =?2 " +
                "and flag_rekon_ecom ='0' and pay_method_aggregator = ?3 ";
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            query += "AND SUBSTRING(trans_time, 1, 2) = :transTime ";
        }
        query += " order by trans_date, trans_time asc";
        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2,  branchId)
                .setParameter(3, pmId);

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            nativeQuery.setParameter("transTime", request.getTransTime().substring(0, 2));
        }

        List<Long> result = nativeQuery.getResultList();
        return result;
    }

    public void updateDataPos(Long detailAggStr, String updatedVersion, Long s) {
        String query = "UPDATE detail_point_of_sales dpos " +
                "SET flag_rekon_ecom = :newFlag, " +
                " detail_id_agg= :detailIdAgg " +
                "WHERE detail_pos_id = :detailPosId ";
        Query nativeQuery =  entityManager.createNativeQuery(query)
                .setParameter("newFlag", updatedVersion)
                .setParameter("detailIdAgg",detailAggStr)
                .setParameter("detailPosId",s);
        nativeQuery.executeUpdate();
    }

    public void updateDataPos2(Long detailAggStr, String updatedVersion, Long s, String dateOnly, String timeOnly) {
        String query = "UPDATE detail_point_of_sales dpos " +
                "SET flag_rekon_ecom = :newFlag, " +
                " detail_id_agg= :detailIdAgg," +
                " changed_on = :co, changed_at=:ca " +
                "WHERE detail_pos_id = :detailPosId ";
        Query nativeQuery =  entityManager.createNativeQuery(query)
                .setParameter("newFlag", updatedVersion)
                .setParameter("detailIdAgg",detailAggStr)
                .setParameter("co", dateOnly)
                .setParameter("ca", timeOnly)
                .setParameter("detailPosId",s);
        nativeQuery.executeUpdate();
    }

    public List<Long> getDetailPosIdByBranch(GeneralRequest request) {
        String query ="select detail_pos_id from detail_point_of_sales dpos " +
                "where trans_date = ?1 and branch_id =?2 " +
                "and flag_rekon_ecom ='0' ";
        query += " order by trans_date, trans_time asc";
        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2,  request.getBranchId());


        List<Long> result = nativeQuery.getResultList();
        return result;
    }

    public BigDecimal getAmountByParentId(String parentId) {
        String query ="select sum(gross_amount) from detail_point_of_sales dpos " +
                "where parent_id = ?1  ";

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, parentId);
        BigDecimal result = (BigDecimal) nativeQuery.getSingleResult();
        return result;
    }

    public List<String> getListTransTime(GeneralRequest request) {
        String query ="select DISTINCT SUBSTRING(dpos.trans_time, 1, 2)  from detail_point_of_sales dpos\n" +
                "where dpos.branch_id = ?1 and dpos.trans_date = ?2 and flag_rekon_ecom = '0' \n" +
                "order by SUBSTRING(dpos.trans_time, 1, 2) ";
        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(2, request.getTransDate())
                .setParameter(1,  request.getBranchId());

        List<String> result = nativeQuery.getResultList();
        return result;
    }

    public List<Long> getDetailPosIdByBranchAndTransTime(GeneralRequest request) {
        String query = "SELECT distinct detail_pos_id FROM detail_point_of_sales dpos " +
                "join detail_agregator_payment dap" +
                " on dap.gross_amount = dpos.gross_amount and dap.pm_id = dpos.pay_method_aggregator" +
                " WHERE dpos.trans_date = :transDate AND dpos.branch_id = :branchId " +
                "AND dpos.flag_rekon_ecom = '0' " +
                "AND SUBSTRING(dpos.trans_time, 1, 2) = :transTime and dpos.pay_method_aggregator = :pmId " +
                "ORDER BY dpos.trans_date, dpos.trans_time ASC";

        Query nativeQuery = entityManager.createNativeQuery(query)
                .setParameter("transDate", request.getTransDate())
                .setParameter("branchId", request.getBranchId())
                .setParameter("transTime", request.getTransTime())
                .setParameter("pmId", request.getPmId());

        return nativeQuery.getResultList();
    }


    public List<Map<String, Object>> getDataPosByTransTime(GeneralRequest request) {
        // Membuat query dasar
        String query = "SELECT detail_pos_id, gross_amount FROM detail_point_of_sales dpos " +
                "WHERE trans_date = :transDate " +
                "AND branch_id = :branchId " +
                "AND parent_id = ( " +
                "    SELECT parent_id " +
                "    FROM detail_point_of_sales " +
                "    ORDER BY CONCAT(created_on, ' ', created_at) DESC " +
                "    LIMIT 1 " +
                ")";

        // Menambahkan filter berdasarkan trans_time jika diberikan
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            query += " AND SUBSTRING(trans_time, 1, 2) = :transTime ";
        }

        // Menambahkan filter berdasarkan pm_id jika diberikan
        if (request.getPmId() != null && !request.getPmId().isEmpty()) {
            query += " AND pay_method_aggregator = :pmId ";
        }

        // Mengurutkan hasil berdasarkan trans_time
        query += " ORDER BY trans_time ASC";

        // Menyiapkan query native
        Query nativeQuery = entityManager.createNativeQuery(query)
                .setParameter("transDate", request.getTransDate())
                .setParameter("branchId", request.getBranchId());

        // Menetapkan parameter opsional jika diberikan
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            nativeQuery.setParameter("transTime", request.getTransTime());
        }
        if (request.getPmId() != null && !request.getPmId().isEmpty()) {
            nativeQuery.setParameter("pmId", request.getPmId());
        }

        // Mengeksekusi query dan memproses hasilnya
        List<Object[]> rawResults = nativeQuery.getResultList();
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Object[] row : rawResults) {
            Map<String, Object> map = new HashMap<>();
            map.put("detailPosId", row[0]);
            map.put("grossAmount", row[1]);
            resultList.add(map);
        }

        return resultList;
    }


    public List<PosReportDto> getDataWithOffset(GeneralRequest request, String parentId) {
        String sql = """
        SELECT d.branch_id, d.trans_date, d.trans_time, d.trans_id,
               d.pay_method_aggregator, d.gross_amount, d.flag_rekon_ecom + 0 AS flag_rekon_ecom,
                dap.flag_rekon_bank + 0 as flag_rekon_bank 
        from detail_point_of_sales d 
        left join detail_agregator_payment dap on dap.detail_payment_id = d.detail_id_agg
        where d.parent_id =:parentId
        ORDER BY d.created_at DESC
        LIMIT :limit OFFSET :offset
    """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("parentId", parentId);
        query.setParameter("limit", request.getLimit());
        query.setParameter("offset", request.getOffset());

        List<Object[]> resultList = query.getResultList();
        List<PosReportDto> reports = new ArrayList<>();

        for (Object[] row : resultList) {
            String branchId = (String) row[0];
            String transDate = row[1] != null ? row[1].toString() : null; // java.sql.Date
            String transTime = row[2] != null ? row[2].toString() : null; // java.sql.Time
            String transId = (String) row[3];
            String payMethod = (String) row[4];
            BigDecimal grossAmount = (BigDecimal) row[5];
            String flagRekonEcom = String.valueOf(row[6]);
            String flagReconBank = String.valueOf(row[7]);
            if(flagRekonEcom.equals("1")|| flagRekonEcom.equals("2")){
                flagRekonEcom="OK";
            }else if(flagRekonEcom.equals("3")){
                flagRekonEcom = "OK With Notes";
            }else{
                flagRekonEcom = "Not OK";
            }

            if(flagReconBank.equals("1")){
                flagReconBank="OK";
            }else flagReconBank ="Not OK";
            reports.add(new PosReportDto(
                    branchId, transDate, transTime, transId,
                    payMethod, grossAmount, flagRekonEcom, flagReconBank
            ));
        }

        return reports;
    }

    public String getLatestParentId(GeneralRequest request) {
        String query = "SELECT parent_id FROM (" +
                "    SELECT parent_id, created_at FROM detail_point_of_sales dpos " +
                "    WHERE trans_date = ?1 AND branch_id = ?2 " +
                "    ORDER BY created_at DESC" +
                ") AS subquery " +
                "GROUP BY parent_id " +
                "LIMIT 1";

        Query nativeQuery = entityManager.createNativeQuery(query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, request.getBranchId());

        List<?> resultList = nativeQuery.getResultList();

        if (resultList.isEmpty()) {
            return null; // atau bisa return "", atau lempar custom exception
        }

        return resultList.get(0).toString();
    }

    public int getTotalPost(String parentId) {
        Object result = entityManager.createNativeQuery(
                        "select count(1) from detail_point_of_sales dpos " +
                                "where parent_id = ?1 ")
                .setParameter(1, parentId)
                .getSingleResult();

        return ((Number) result).intValue();
    }
}
