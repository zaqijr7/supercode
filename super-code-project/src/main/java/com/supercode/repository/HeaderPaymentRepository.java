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
            SELECT hp.pm_id, hp.trans_date, hp.status_rekon_pos_vs_ecom, hp.status_rekom_ecom_vs_pos,
                   hp.status_rekon_ecom_vs_bank, hp.created_at, hp.parent_id, hp.file_name, hp.payment_id,
                   ROW_NUMBER() OVER (
                       PARTITION BY hp.pm_id\s
                       ORDER BY hp.payment_id DESC
                   ) AS row_num
            FROM header_payment hp
            WHERE hp.trans_date = :transDate
              AND hp.branch_id = :branchId
        )
        SELECT r.pm_id, r.trans_date, r.status_rekon_pos_vs_ecom, r.status_rekom_ecom_vs_pos,
               r.status_rekon_ecom_vs_bank, r.created_at, r.parent_id, r.file_name, r.payment_id
        FROM Ranked r
        WHERE r.row_num = 1
        ORDER BY r.payment_id DESC;
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
                    hp.setCreatedAt((String) row[5]);
                    hp.setParentId((String) row[6]);// created_at (Timestamp)
                    hp.setFileName((String) row[7]);
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
