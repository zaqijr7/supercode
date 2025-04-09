package com.supercode.repository;

import com.supercode.entity.MasterMerchant;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class MasterMerchantRepository implements PanacheRepository<MasterMerchant> {
    @PersistenceContext
    EntityManager entityManager;
    public List<String> getBranchId() {
        return entityManager.createNativeQuery("select branch_id from master_merchant mm ").getResultList();

    }

    public String getBranchIdByBranchName(String branchName) {
        String normalizedBranchName = normalizeBranchName(branchName);
        String response ="";
        try {
            response =  (String) entityManager.createNativeQuery(
                            "SELECT branch_id FROM master_merchant pm WHERE UPPER(branch_name) LIKE UPPER(CONCAT('%', ?1, '%'))")
                    .setParameter(1, normalizedBranchName)
                    .getSingleResult();
        } catch (NoResultException e) {
            response = "NOT FOUND";
        } catch (Exception e) {
            response = "ERROR";
        }
        return response;
    }


    public String normalizeBranchName(String branchName) {
        if (branchName == null) return null;

        // Ubah " - " menjadi ", " (dengan spasi setelah koma)
        return branchName.replaceAll("\\s*-\\s*", ", ").trim();
    }


}
