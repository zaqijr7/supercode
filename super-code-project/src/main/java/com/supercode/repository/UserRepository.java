package com.supercode.repository;

import com.supercode.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

public class UserRepository  implements PanacheRepository<User> {
}
