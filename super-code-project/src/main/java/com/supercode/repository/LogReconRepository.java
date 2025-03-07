package com.supercode.repository;

import com.supercode.entity.LogRecon;
import com.supercode.request.GeneralRequest;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class LogReconRepository implements PanacheRepository<LogRecon> {
    @PersistenceContext
    EntityManager entityManager;

    public int getSubmitStatus(GeneralRequest request) {
        String branchId = request.getBranchId();
        String date = request.getTransDate(); // Pastikan date dalam format 'YYYY-MM-DD'

        String sql = "SELECT EXISTS (SELECT 1 FROM log_process_recon WHERE branch_id = :branchId AND `date` = :date)";
        Object result = entityManager.createNativeQuery(sql)
                .setParameter("branchId", branchId)
                .setParameter("date", date)
                .getSingleResult();

        return ((Number) result).intValue(); // Mengonversi dengan aman ke int
    }

}
