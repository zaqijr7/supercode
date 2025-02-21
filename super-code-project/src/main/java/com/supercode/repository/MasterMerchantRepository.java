package com.supercode.repository;

import com.supercode.entity.MasterMerchant;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class MasterMerchantRepository implements PanacheRepository<MasterMerchant> {
    @PersistenceContext
    EntityManager entityManager;
    public List<String> getBranchId() {
        return entityManager.createNativeQuery("select branch_id from master_merchant mm ").getResultList();

    }
}
