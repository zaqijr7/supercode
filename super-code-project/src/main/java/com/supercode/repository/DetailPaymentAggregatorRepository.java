package com.supercode.repository;

import com.supercode.request.GeneralRequest;
import com.supercode.util.MessageConstant;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class DetailPaymentAggregatorRepository implements PanacheRepository<com.supercode.entity.DetailPaymentAggregator> {

    @PersistenceContext
    EntityManager entityManager;
    public int getCountDataAggregator(GeneralRequest request,String branchId, List<BigDecimal> grossAmounts) {
        String query ="select count(*) from detail_agregator_payment dpos " +
                "where trans_date = ?1  " +
                "and gross_amount IN (?2) and pm_id = ?3 and branch_id = ?4 " +
                "and flag_rekon_pos ='0'";
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            query += "AND SUBSTRING(trans_time, 1, 2) = :transTime ";
        }
        query+="  order by trans_date, trans_time asc";

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, grossAmounts) // Menggunakan IN dengan ()
                .setParameter(3, request.getPmId())
                .setParameter(4, branchId);
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            nativeQuery.setParameter("transTime", request.getTransTime().substring(0, 2));
        }

        Object result = nativeQuery.getSingleResult();
        return ((Number) result).intValue();
    }

   /* public void updateFlagByCondition(GeneralRequest request, List<BigDecimal> grossAmounts) {
        // Jika Anda ingin menggunakan BigDecimal, maka parameter "grossAmounts" sebaiknya menggunakan tipe BigDecimal
        String query = "UPDATE detail_agregator_payment dpos " +
                "SET flag_rekon_pos = :newFlag " +
                "WHERE trans_date = :transDate " +
                "AND SUBSTRING(trans_time, 1, 2) = :transTime " +
                "AND gross_amount IN :grossAmounts " +  // Menggunakan parameter untuk 'IN'
                "AND pm_id = :pmId " +
                "AND branch_id = :branchId " +
                "AND flag_rekon_pos = '0'";

        entityManager.createNativeQuery(query)
                .setParameter("newFlag", "1")
                .setParameter("transDate", request.getTransDate())
                .setParameter("transTime", request.getTransTime())
                .setParameter("grossAmounts", grossAmounts)  // Menggunakan parameter list untuk IN
                .setParameter("pmId", request.getPmId())
                .setParameter("branchId", request.getBranchId())
                .executeUpdate();
    }*/

    /*public void updateFlagByCondition(GeneralRequest request, List<BigDecimal> grossAmounts) {
        String newFlag= MessageConstant.TWO_VALUE;
        // Langkah 1: Identifikasi data unik berdasarkan gross_amount dengan batas jumlah yang sama di POS
        String findUniqueQuery = "SELECT detail_payment_id FROM ( " +
                "    SELECT dap1.detail_payment_id, dap1.gross_amount, " +
                "           ROW_NUMBER() OVER (PARTITION BY dap1.gross_amount ORDER BY dap1.detail_payment_id) AS rn " +
                "    FROM detail_agregator_payment dap1 " +
                "    WHERE dap1.trans_date = :transDate " +
                "    AND dap1.pm_id = :pmId " +
                "    AND dap1.branch_id = :branchId " +
                "    AND dap1.flag_rekon_pos = '0' " +
                "    AND dap1.gross_amount IN (:grossAmounts) order by dap1.trans_date, dap1.trans_time asc" +
                ") temp WHERE rn <= ( " +
                "    SELECT COUNT(*) FROM detail_point_of_sales pos " +
                "    WHERE pos.trans_date = :transDate " +
                "    AND pos.pm_id = :pmId " +
                "    AND pos.branch_id = :branchId " +
                "    AND pos.gross_amount = temp.gross_amount order by pos.trans_date, pos.trans_time asc" +
                ")";

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            findUniqueQuery += " AND SUBSTRING(trans_time, 1, 2) = :transTime";
            newFlag= MessageConstant.ONE_VALUE;
        }

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
            String updateQuery = "UPDATE detail_agregator_payment " +
                    "SET flag_rekon_pos =  " + newFlag+
                    " WHERE detail_payment_id IN (:uniqueIds)";

            Query updateNativeQuery = entityManager.createNativeQuery(updateQuery)
                    .setParameter("uniqueIds", uniqueIds);

            updateNativeQuery.executeUpdate();
        }
    }*/

    public void updateFlagByCondition(GeneralRequest request, List<BigDecimal> grossAmounts) {
        String newFlag = MessageConstant.TWO_VALUE;

        // Langkah 1: Identifikasi data unik berdasarkan gross_amount dengan batas jumlah yang sama di POS
        String findUniqueQuery = "SELECT detail_payment_id FROM ( " +
                "    SELECT dap1.detail_payment_id " +
                "    FROM detail_agregator_payment dap1 " +
                "    WHERE dap1.trans_date = :transDate " +
                "      AND dap1.pm_id = :pmId " +
                "      AND dap1.branch_id = :branchId " +
                "      AND dap1.flag_rekon_pos = '0' " +
                "      AND dap1.gross_amount IN (:grossAmounts) ";

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            findUniqueQuery += " AND SUBSTRING(dap1.trans_time, 1, 2) = :transTime ";
            newFlag = MessageConstant.ONE_VALUE;
        }

        findUniqueQuery += "    ORDER BY dap1.trans_date, dap1.trans_time " +
                "    LIMIT 1 " + // Ambil hanya satu baris dengan trans_time paling awal
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

        // Langkah 2: Update hanya data yang sesuai dengan kondisi dari POS
        if (!uniqueIds.isEmpty()) {
            String updateQuery = "UPDATE detail_agregator_payment " +
                    "SET flag_rekon_pos = :newFlag " +
                    "WHERE detail_payment_id = :uniqueId"; // Update hanya satu baris

            Query updateNativeQuery = entityManager.createNativeQuery(updateQuery)
                    .setParameter("newFlag", newFlag)
                    .setParameter("uniqueId", uniqueIds.get(0)); // Ambil ID pertama dari hasil query

            updateNativeQuery.executeUpdate();
        }
    }




    public String getTransDateByParentId(String parentId) {
        String query ="select trans_date from (" +
                "  select trans_date, created_at from detail_agregator_payment " +
                "  where parent_id = ?1 " +
                "  order by created_at desc" +
                ") as subquery group by trans_date limit 1";
        System.out.println(query);
        System.out.println(parentId);
        return entityManager.createNativeQuery(
                        query)
                .setParameter(1, parentId)
                .getSingleResult().toString();
    }


    public void updateFlagNormalByCondition(GeneralRequest request, List<BigDecimal> grossAmounts) {
        String newFlag= MessageConstant.TWO_VALUE;
        String query = "UPDATE detail_agregator_payment dpos " +
                "SET flag_rekon_pos = :newFlag " +
                "WHERE trans_date = :transDate " +
                "AND gross_amount IN :grossAmounts " +  // Menggunakan parameter untuk 'IN'
                "AND pm_id = :pmId " +
                "AND branch_id = :branchId " +
                "AND flag_rekon_pos = '0' ";

        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            query += "AND SUBSTRING(trans_time, 1, 2) = :transTime ";
            newFlag= MessageConstant.ONE_VALUE;
        }
        query += " order by trans_date, trans_time asc";

       Query nativeQuery =  entityManager.createNativeQuery(query)
                .setParameter("newFlag", newFlag)
                .setParameter("transDate", request.getTransDate())
                .setParameter("grossAmounts", grossAmounts)  // Menggunakan parameter list untuk IN
                .setParameter("pmId", request.getPmId())
                .setParameter("branchId", request.getBranchId());
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            nativeQuery.setParameter("transTime", request.getTransTime().substring(0, 2));
        }
        nativeQuery.executeUpdate();
    }

    public List<BigDecimal> getAllGrossAmount(GeneralRequest request) {
        String query ="select gross_amount from detail_agregator_payment dpos " +
                "where trans_date = ?1   " +
                "and pm_id = ?2 and branch_id =?3 and flag_rekon_pos ='0' ";
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            query += "AND SUBSTRING(trans_time, 1, 2) = :transTime ";
        }
        query += " order by trans_date, trans_time asc";

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, request.getPmId())
                .setParameter(3, request.getBranchId());
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            nativeQuery.setParameter("transTime", request.getTransTime().substring(0, 2));
        }

        List<BigDecimal> result = nativeQuery.getResultList();
        return result;
    }

    public int getFailedRecon(String parentId) {
        Object result = entityManager.createNativeQuery(
                        "select count(*) from detail_agregator_payment dpos " +
                                "where parent_id = ?1  and flag_rekon_pos=0 order by trans_date, trans_time asc")
                .setParameter(1, parentId)
                .getSingleResult();

        return ((Number) result).intValue();
    }

    public int getCountDataAggregatorByBranch(GeneralRequest request, List<BigDecimal> grossAmounts) {
        String query ="select count(*) from detail_agregator_payment dpos " +
                "where trans_date = ?1  " +
                "and gross_amount IN (?2)  and branch_id = ?3 " +
                "and flag_rekon_pos ='0'";

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, grossAmounts) // Menggunakan IN dengan ()
                .setParameter(3, request.getBranchId());
        Object result = nativeQuery.getSingleResult();
        return ((Number) result).intValue();
    }

    public List<BigDecimal> getAllGrossAmountByBranch(GeneralRequest request) {
        String query ="select gross_amount from detail_agregator_payment dpos " +
                "where trans_date = ?1   " +
                " and branch_id =?2 and flag_rekon_pos ='0' ";

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, request.getBranchId());
        List<BigDecimal> result = nativeQuery.getResultList();
        return result;
    }

    public void updateFlagByBranchCondition(GeneralRequest request, List<BigDecimal> grossAmounts) {
        String newFlag = MessageConstant.THREE_VALUE;
        // Langkah 1: Identifikasi data unik berdasarkan gross_amount dengan batas jumlah yang sama di POS
        String findUniqueQuery = "SELECT detail_payment_id FROM ( " +
                "    SELECT detail_payment_id, trans_date, trans_time FROM ( " +
                "         SELECT dap1.detail_payment_id, dap1.gross_amount, dap1.trans_date, dap1.trans_time, " +
                "                ROW_NUMBER() OVER (PARTITION BY dap1.gross_amount " +
                "                                   ORDER BY dap1.trans_date, dap1.trans_time, dap1.detail_payment_id) AS rn " +
                "         FROM detail_agregator_payment dap1 " +
                "         WHERE dap1.trans_date = :transDate " +
                "           AND dap1.branch_id = :branchId " +
                "           AND dap1.flag_rekon_pos = '0' " +
                "           AND dap1.gross_amount IN (:grossAmounts) ";


        findUniqueQuery += "    ) innerQuery " +
                "    WHERE rn <= ( " +
                "         SELECT COUNT(*) FROM detail_point_of_sales pos " +
                "         WHERE pos.trans_date = :transDate " +
                "           AND pos.branch_id = :branchId " +
                "           AND pos.gross_amount = innerQuery.gross_amount ";

        findUniqueQuery += "         ) " +
                "    ORDER BY trans_date, trans_time " +
                ") ordered";

        Query uniqueQuery = entityManager.createNativeQuery(findUniqueQuery)
                .setParameter("transDate", request.getTransDate())
                .setParameter("branchId", request.getBranchId())
                .setParameter("grossAmounts", grossAmounts);



        List<BigDecimal> uniqueIds = uniqueQuery.getResultList();

        // Langkah 2: Update hanya data yang sesuai dengan kondisi dari POS
        if (!uniqueIds.isEmpty()) {
            String updateQuery = "UPDATE detail_agregator_payment " +
                    "SET flag_rekon_pos = " + newFlag +
                    " WHERE detail_payment_id IN (:uniqueIds)";

            Query updateNativeQuery = entityManager.createNativeQuery(updateQuery)
                    .setParameter("uniqueIds", uniqueIds);

            updateNativeQuery.executeUpdate();
        }
    }

    public void updateFlagNormalByBranchCondition(GeneralRequest request, List<BigDecimal> grossAmounts) {
        String newFlag= MessageConstant.THREE_VALUE;
        String query = "UPDATE detail_agregator_payment dpos " +
                "SET flag_rekon_pos = :newFlag " +
                "WHERE trans_date = :transDate " +
                "AND gross_amount IN :grossAmounts " +
                "AND branch_id = :branchId " +
                "AND flag_rekon_pos = '0' ";

        query += " order by trans_date, trans_time asc";

        Query nativeQuery =  entityManager.createNativeQuery(query)
                .setParameter("newFlag", newFlag)
                .setParameter("transDate", request.getTransDate())
                .setParameter("grossAmounts", grossAmounts)
                .setParameter("branchId", request.getBranchId());
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            nativeQuery.setParameter("transTime", request.getTransTime().substring(0, 2));
        }
        nativeQuery.executeUpdate();
    }

    public List<Long> getDetailIdByRequest(GeneralRequest request, List<BigDecimal> grossAmounts) {
        String query ="select detail_payment_id from detail_agregator_payment dpos " +
                "where trans_date = ?1  " +
                "and gross_amount IN (?2)  and branch_id = ?3 and pm_id = ?4 " +
                "and flag_rekon_pos ='0' order by trans_time asc";

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, grossAmounts) // Menggunakan IN dengan ()
                .setParameter(3, request.getBranchId())
                .setParameter(4, request.getPmId());
        List<Long> result = nativeQuery.getResultList();
        return result;
    }

    public void updateData(Long detailAggStr, String updatedVersion) {
        String query = "UPDATE detail_agregator_payment dpos " +
                "SET flag_rekon_pos = :newFlag " +
                "WHERE detail_payment_id = :detailPaymentId ";
        Query nativeQuery =  entityManager.createNativeQuery(query)
                .setParameter("newFlag", updatedVersion)
                .setParameter("detailPaymentId",detailAggStr);
        nativeQuery.executeUpdate();
    }

    public List<Long> getDetailIdByRequestByBranch(GeneralRequest request, List<BigDecimal> grossAmounts) {
        String query ="select detail_payment_id from detail_agregator_payment dpos " +
                "where trans_date = ?1  " +
                "and gross_amount IN (?2)  and branch_id = ?3 " +
                "and flag_rekon_pos ='0' order by trans_time asc";

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, grossAmounts) // Menggunakan IN dengan ()
                .setParameter(3, request.getBranchId());
        List<Long> result = nativeQuery.getResultList();
        return result;
    }

    public BigDecimal getAmountByParentId(String parentId) {
        String query ="select sum(gross_amount) from detail_agregator_payment dpos " +
                "where parent_id = ?1  ";

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, parentId);
        BigDecimal result = (BigDecimal) nativeQuery.getSingleResult();
        return result;
    }

    public List<String> getListTransTime(GeneralRequest request) {
        String query ="select DISTINCT SUBSTRING(dpos.trans_time, 1, 2)  from detail_agregator_payment dpos\n" +
                "where dpos.branch_id = ?1 and dpos.trans_date = ?2 and flag_rekon_pos = '0' \n" +
                "order by SUBSTRING(dpos.trans_time, 1, 2) ";
        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(2, request.getTransDate())
                .setParameter(1,  request.getBranchId());

        List<String> result = nativeQuery.getResultList();
        return result;
    }

    public List<Long> getDetailIdByRequestByBranch(GeneralRequest request) {
        String query ="select distinct detail_payment_id from detail_agregator_payment dpos " +
                " join detail_point_of_sales dap" +
                " where dpos.trans_date = ?1  " +
                "AND SUBSTRING(dpos.trans_time, 1, 2) = ?2  and dpos.branch_id = ?3 and dpos.pm_id= ?4 " +
                "and dpos.flag_rekon_pos ='0' order by dpos.trans_time asc";

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, request.getTransTime())
                .setParameter(3, request.getBranchId())
                .setParameter(4, request.getPmId());
        List<Long> result = nativeQuery.getResultList();
        return result;
    }

    public int getCountDataAggByDate(GeneralRequest request, List<BigDecimal> netAmountBank) {
        String query ="select count(*) from detail_agregator_payment dpos " +
                "where settlement_date = ?1 and flag_rekon_bank='0' and net_amount in(?2) and pm_id = ?3";
        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, netAmountBank)
                .setParameter(3, request.getPmId());


        Object result = nativeQuery.getSingleResult();
        return ((Number) result).intValue();

    }

    public void updateDataReconBank(GeneralRequest request, BigDecimal netAmountBank, String bankId) {
        String query ="update detail_agregator_payment set flag_rekon_bank ='1', flag_id_bank= ?1 " +
                "where settlement_date = ?2 and flag_rekon_bank='0' and net_amount in(?3) and pm_id = ?4 ";
        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, bankId)
                .setParameter(2, request.getTransDate())
                .setParameter(3, netAmountBank)
                .setParameter(4, request.getPmId());
        nativeQuery.executeUpdate();
    }

    public List<Map<String, Object>> getDataAgg(GeneralRequest request, List<BigDecimal> netAmountBank, String payMeth) {
        boolean isGoPayOrGoFood = payMeth.equalsIgnoreCase(MessageConstant.GOFOOD) ||
                payMeth.equalsIgnoreCase(MessageConstant.GOPAY);

        // Query SQL
        String query = "SELECT detail_payment_id, net_amount, settlement_date " +
                "FROM detail_agregator_payment dpos " +
                "WHERE settlement_date = ?1 AND flag_rekon_bank='0' AND pm_id = ?2 ";

        if (isGoPayOrGoFood) {
            query += " AND net_amount IN (?3) ";
        }

        // Buat query native
        Query nativeQuery = entityManager.createNativeQuery(query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, request.getPmId());

        if (isGoPayOrGoFood) {
            nativeQuery.setParameter(3, netAmountBank);
        }

        // Eksekusi query
        List<Object[]> rawResults = nativeQuery.getResultList();

        return rawResults.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("detailPaymentId", row[0]);
            map.put("netAmount", row[1]);

            // Konversi tanggal dengan memastikan hanya mengambil bagian "yyyy-MM-dd"
            LocalDate settlementDate = LocalDate.parse(row[2].toString().substring(0, 10));

            // Hitung settlement date baru jika GoFood atau GoPay
            if (isGoPayOrGoFood) {
                settlementDate = switch (settlementDate.getDayOfWeek()) {
                    case FRIDAY -> settlementDate.plusDays(3);
                    case SATURDAY -> settlementDate.plusDays(2);
                    default -> settlementDate.plusDays(1);
                };
            }

            map.put("settDate", settlementDate.toString());
            return map;
        }).collect(Collectors.toList());
    }


    public void updateDataReconAgg2Bank(Long paymentId, String bankMutationId) {
        String query ="update detail_agregator_payment set flag_rekon_bank ='1', flag_id_bank= ?1 " +
                "where detail_payment_id = ?2";
        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter(1, bankMutationId)
                .setParameter(2, paymentId);
        nativeQuery.executeUpdate();
    }

    public List<Map<String, Object>> getDataAggByTransTime(GeneralRequest request) {
        String query = "SELECT detail_payment_id, gross_amount FROM detail_agregator_payment dpos " +
                "WHERE trans_date = :transDate " +
                "AND branch_id = :branchId " +
                "AND parent_id = ( " +
                "    SELECT parent_id " +
                "    FROM detail_agregator_payment " +
                "    ORDER BY detail_payment_id DESC " +
                "    LIMIT 1 " +
                ")";

        // Optional filter by trans_time (only if provided)
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            query += " AND SUBSTRING(trans_time, 1, 2) = :transTime ";
        }

        // Optional filter by pm_id (only if provided)
        if (request.getPmId() != null && !request.getPmId().isEmpty()) {
            query += " AND pm_id = :pmId ";
        }

        // Order by trans_time in ascending order
        query += " ORDER BY trans_time ASC";

        // Create the native query
        Query nativeQuery = entityManager.createNativeQuery(query)
                .setParameter("transDate", request.getTransDate())
                .setParameter("branchId", request.getBranchId());

        // Set optional parameters if provided
        if (request.getTransTime() != null && !request.getTransTime().isEmpty()) {
            nativeQuery.setParameter("transTime", request.getTransTime());
        }
        if (request.getPmId() != null && !request.getPmId().isEmpty()) {
            nativeQuery.setParameter("pmId", request.getPmId());
        }

        // Execute the query and process the result
        List<Object[]> rawResults = nativeQuery.getResultList();
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Object[] row : rawResults) {
            Map<String, Object> map = new HashMap<>();
            map.put("detailPaymentId", row[0]);
            map.put("grossAmount", row[1]);
            resultList.add(map);
        }

        return resultList;
    }


    /*public List<Map<String, Object>> getDataAggGoTo(GeneralRequest request, String payMeth) {
        String operator ="";
        if(payMeth.equalsIgnoreCase(MessageConstant.SHOPEEFOOD)){
            operator=MessageConstant.LESS_THAN_EQUALS;
        }else operator=MessageConstant.EQUALS;
        String query = "SELECT detail_payment_id, net_amount, settlement_date " +
                "FROM detail_agregator_payment dpos " +
                "WHERE trans_date "+operator+" ?1 AND flag_rekon_bank='0' AND pm_id = ?2 ";
        // Buat query native
        Query nativeQuery = entityManager.createNativeQuery(query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, request.getPmId());

        // Eksekusi query
        List<Object[]> rawResults = nativeQuery.getResultList();

        return rawResults.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("detailPaymentId", row[0]);
            map.put("netAmount", row[1]);

            LocalDate settlementDate = LocalDate.parse(row[2].toString().substring(0, 10));
            if(payMeth.equalsIgnoreCase(MessageConstant.SHOPEEFOOD)){
                settlementDate = settlementDate.plusDays(1);
            }else if(payMeth.equalsIgnoreCase(MessageConstant.GRABFOOD)){
                settlementDate = settlementDate;
            }else{
                settlementDate = switch (settlementDate.getDayOfWeek()) {
                    case FRIDAY -> settlementDate.plusDays(3);
                    case SATURDAY -> settlementDate.plusDays(2);
                    default -> settlementDate.plusDays(1);
                };
            }

            map.put("settDate", settlementDate.toString());
            return map;
        }).collect(Collectors.toList());
    }*/
    /*public List<Map<String, Object>> getDataAggGoTo(GeneralRequest request, String payMeth) {
        String operator = payMeth.equalsIgnoreCase(MessageConstant.SHOPEEFOOD)
                ? MessageConstant.LESS_THAN_EQUALS
                : MessageConstant.EQUALS;

        String query = """
        SELECT detail_payment_id, net_amount, settlement_date
        FROM detail_agregator_payment dpos
        WHERE trans_date "" + operator + "" ?1
          AND flag_rekon_bank = '0'
          AND pm_id = ?2
          AND parent_id = (
              SELECT parent_id
              FROM detail_agregator_payment
              WHERE trans_date "" + operator + "" ?1
                AND pm_id = ?2
              ORDER BY created_time DESC
              LIMIT 1
          )
    """;

        Query nativeQuery = entityManager.createNativeQuery(query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, request.getPmId());

        List<Object[]> rawResults = nativeQuery.getResultList();

        return rawResults.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("detailPaymentId", row[0]);
            map.put("netAmount", row[1]);

            LocalDate settlementDate = LocalDate.parse(row[2].toString().substring(0, 10));
            if (payMeth.equalsIgnoreCase(MessageConstant.SHOPEEFOOD)) {
                settlementDate = settlementDate.plusDays(1);
            } else if (payMeth.equalsIgnoreCase(MessageConstant.GRABFOOD)) {
                // do nothing
            } else {
                settlementDate = switch (settlementDate.getDayOfWeek()) {
                    case FRIDAY -> settlementDate.plusDays(3);
                    case SATURDAY -> settlementDate.plusDays(2);
                    default -> settlementDate.plusDays(1);
                };
            }

            map.put("settDate", settlementDate.toString());
            return map;
        }).collect(Collectors.toList());
    }*/

    public List<Map<String, Object>> getDataAggGoTo(GeneralRequest request, String payMeth) {
        String subQuery = """
        SELECT parent_id
        FROM detail_agregator_payment
        WHERE trans_date = ?1
          AND pm_id = ?2
          AND flag_rekon_bank = '0'
        ORDER BY CONCAT(created_on, ' ', created_at) DESC
        LIMIT 1
        """;

        String query = """
        SELECT detail_payment_id, net_amount, settlement_date
        FROM detail_agregator_payment
        WHERE parent_id = (""" + subQuery + """
        )
          AND flag_rekon_bank = '0'
        """;

        System.out.println("ini query "+ query);
        System.out.println("ini pm id "+ request.getPmId());
        Query nativeQuery = entityManager.createNativeQuery(query)
                .setParameter(1, request.getTransDate())
                .setParameter(2, request.getPmId());

        List<Object[]> rawResults = nativeQuery.getResultList();

        return rawResults.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("detailPaymentId", row[0]);
            map.put("netAmount", row[1]);

            LocalDate settlementDate = LocalDate.parse(row[2].toString().substring(0, 10));
            if (payMeth.equalsIgnoreCase(MessageConstant.SHOPEEFOOD)) {
                settlementDate = settlementDate.plusDays(1);
            } else if (payMeth.equalsIgnoreCase(MessageConstant.GRABFOOD)) {
                settlementDate = settlementDate;
            } else {
                settlementDate = switch (settlementDate.getDayOfWeek()) {
                    case FRIDAY -> settlementDate.plusDays(3);
                    case SATURDAY -> settlementDate.plusDays(2);
                    default -> settlementDate.plusDays(1);
                };
            }

            map.put("settDate", settlementDate.toString());
            return map;
        }).collect(Collectors.toList());
    }



    public BigDecimal getGrossAmountByParentId(String parentId) {
        String query ="select sum(dap.gross_amount) from detail_agregator_payment dap \n" +
                "join detail_point_of_sales dpos \n" +
                "on dap.detail_payment_id = dpos.detail_id_agg and dpos.parent_id = :parentId ";

        Query nativeQuery = entityManager.createNativeQuery(
                        query)
                .setParameter("parentId", parentId);
        BigDecimal result = (BigDecimal) nativeQuery.getSingleResult();
        return result;
    }

    public void updateDataAggWithChange(Long detailPaymentId, String updateMessage, String now, String timeOnly) {
        String query = "UPDATE detail_agregator_payment dpos " +
                "SET flag_rekon_pos = :newFlag " +
                " , changed_on = :co, changed_at = :ca " +
                "WHERE detail_payment_id = :detailPaymentId ";
        Query nativeQuery =  entityManager.createNativeQuery(query)
                .setParameter("newFlag", updateMessage)
                .setParameter("detailPaymentId",detailPaymentId)
                .setParameter("co", now)
                .setParameter("ca", timeOnly);
        nativeQuery.executeUpdate();
    }

    public void updateToZeroByRequest(GeneralRequest request) {
        String query = "update detail_agregator_payment dpos set flag_rekon_pos = '0', flag_rekon_bank = '0', flag_id_bank = null  " +
                "where trans_date = :transDate and branch_id = :branchId ";


        Query nativeQuery = entityManager.createNativeQuery(query)
                .setParameter("transDate", request.getTransDate())
                .setParameter("branchId", request.getBranchId());


        nativeQuery.executeUpdate();
    }
}
