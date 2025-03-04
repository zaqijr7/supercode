package com.supercode.repository;

import com.supercode.entity.LogRecon;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LogReconRepository implements PanacheRepository<LogRecon> {
}
