package com.supercode.entity;

import jakarta.persistence.*;

@Entity
@Table(name="role_assignment")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 50, nullable = false)
    private Long userId;

    @Column(name = "user", length = 100, nullable = false)
    private String user;

    @Column(name = "branch", length = 100, nullable = false)
    private String branch;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "role", length = 100, nullable = false)
    private String role;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
