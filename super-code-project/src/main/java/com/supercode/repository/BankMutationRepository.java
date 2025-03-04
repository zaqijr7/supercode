package com.supercode.repository;

import com.supercode.entity.BankMutation;
import com.supercode.request.GeneralRequest;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class BankMutationRepository implements PanacheRepository<BankMutation> {
    public int getCountBank(GeneralRequest request) {
        return 0;
    }
}
