package com.supercode.service;

import com.supercode.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@ApplicationScoped
public class UserService {
    @Inject
    EntityManager em;

    public User authenticate(String username, String password) {
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.user = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();

            // Perbandingan langsung (belum aman, hanya untuk demo)
            if (user.getPassword().equals(password)) {
                return user;
            }

        } catch (NoResultException e) {
            // Username tidak ditemukan
        }

        return null;
    }
}
