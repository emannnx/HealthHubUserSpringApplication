package com.example.HealthHubUserSpringApplication.Repository;

import com.example.HealthHubUserSpringApplication.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email); // ✅ Needed for login
}
