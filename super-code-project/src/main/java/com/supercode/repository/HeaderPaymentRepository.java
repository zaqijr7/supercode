package com.supercode.repository;

import com.supercode.entity.HeaderPayment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<String> getParentIdByTransDate(String transDate) {
        return  entityManager.createNativeQuery(
                        "SELECT parent_id FROM header_payment WHERE trans_date = ?1")
                .setParameter(1, transDate)
                .getResultList();
    }

    public List<HeaderPayment> getByTransDateAndBranchId(String transDate, String branchId) {
        String sql = """
        WITH Ranked AS (
            SELECT hp.pm_id, hp.trans_date, hp.status_rekon_pos_vs_ecom, hp.status_rekom_ecom_vs_pos, hp.status_rekon_ecom_vs_bank, hp.created_at, hp.parent_id,
                   ROW_NUMBER() OVER (PARTITION BY hp.pm_id ORDER BY hp.created_at DESC) AS row_num
            FROM header_payment hp
            WHERE hp.trans_date = :transDate 
            AND hp.branch_id = :branchId
        )
        SELECT hp.pm_id, hp.trans_date, hp.status_rekon_pos_vs_ecom, hp.status_rekom_ecom_vs_pos, hp.status_rekon_ecom_vs_bank, hp.created_at, hp.parent_id
        FROM Ranked r
        JOIN header_payment hp ON hp.pm_id = r.pm_id AND hp.created_at = r.created_at
        WHERE r.row_num = 1
        ORDER BY hp.created_at DESC;
    """;

        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("transDate", transDate)
                .setParameter("branchId", branchId)
                .getResultList();

        return results.stream()
                .map(row -> {
                    HeaderPayment hp = new HeaderPayment();
                    hp.setPmId(String.valueOf(row[0])); // pm_id (Long atau Integer)
                    hp.setTransDate(row[1] != null ? row[1].toString() : null); // trans_date (java.sql.Date → String)
                    hp.setStatusRekonPosVsEcom(String.valueOf((boolean) row[2])); // status_rekon_pos_vs_ecom
                    hp.setStatusRekonEcomVsPos(row[3] != null ? row[3].toString() : "0");
                    hp.setStatusRekonEcomVsBank(row[4] != null ? row[4].toString() : "0");// status_rekom_ecom_vs_pos
                    hp.setCreatedAt(((Timestamp) row[5]).toLocalDateTime());
                    hp.setParentId((String) row[6]);// created_at (Timestamp)
                    return hp;
                })
                .collect(Collectors.toList());
    }


    public void updateHeader(String parentId) {
        entityManager.createNativeQuery(
                        "update header_payment set status_rekon_pos_vs_ecom = '1' where parent_id = ?1")
                .setParameter(1, parentId)
                .executeUpdate();
    }

    public void updateHeaderEcom(String parentId) {
        entityManager.createNativeQuery(
                        "update header_payment set status_rekom_ecom_vs_pos = '1' where parent_id = ?1")
                .setParameter(1, parentId)
                .executeUpdate();
    }

    public List<String> getPaymentMethodByDate(String transDate) {
        return  entityManager.createNativeQuery(
                        "SELECT pm_id FROM header_payment WHERE  pm_id!='0' and trans_date = ?1 ").setParameter(1, transDate)
                .getResultList();
    }

    public void updateHeaderBank(String parentId) {
        entityManager.createNativeQuery(
                        "update header_payment set status_rekon_ecom_vs_bank = '1' where parent_id = ?1")
                .setParameter(1, parentId)
                .executeUpdate();
    }
}
