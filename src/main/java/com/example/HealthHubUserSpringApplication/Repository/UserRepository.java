package com.example.HealthHubUserSpringApplication.Repository;

import com.example.HealthHubUserSpringApplication.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email); // ✅ Needed for login
}
