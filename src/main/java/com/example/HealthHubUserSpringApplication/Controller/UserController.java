package com.example.HealthHubUserSpringApplication.Controller;

import com.example.HealthHubUserSpringApplication.Model.User;
import com.example.HealthHubUserSpringApplication.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/home")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ CREATE USER
    @PostMapping("/createuser")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            return ResponseEntity.badRequest().body("Username is required.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // ✅ LOGIN USER
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
        }

        User user = userOpt.get();
        boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());

        if (!passwordMatch) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());

        return ResponseEntity.ok(response);
    }

    // ✅ GET ALL USERS
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // ✅ GET USER BY USERNAME
    @GetMapping("/get/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ GET USER BY EMAIL
    @GetMapping("/profile/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ UPDATE USER
    @PutMapping("/user/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody User updatedUser) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (updatedUser.getUsername() == null || updatedUser.getUsername().isBlank()) {
            return ResponseEntity.badRequest().body("Username cannot be null or blank.");
        }

        if (updatedUser.getEmail() == null || updatedUser.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email cannot be null or blank.");
        }

        User existingUser = optionalUser.get();

        // Check if new username is different and already taken
        if (!existingUser.getUsername().equals(updatedUser.getUsername())) {
            if (userRepository.findByUsername(updatedUser.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
            }
        }

        // Check if new email is different and already taken
        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
            }
        }

        existingUser.setFamilyMedicalHistory(updatedUser.isFamilyMedicalHistory());

        if (!updatedUser.isFamilyMedicalHistory()) {
            existingUser.setFamilyHistoryText(null);
        } else {
            existingUser.setFamilyHistoryText(updatedUser.getFamilyHistoryText());
        }


        // Update fields
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setAge(updatedUser.getAge());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setHeight(updatedUser.getHeight());
        existingUser.setWeight(updatedUser.getWeight());
        existingUser.setBloodType(updatedUser.getBloodType());
        existingUser.setGenotype(updatedUser.getGenotype());
        existingUser.setOxygenLevel(updatedUser.getOxygenLevel());
        existingUser.setMedicalConditions(updatedUser.getMedicalConditions());

        try {
            User savedUser = userRepository.save(existingUser);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user");
        }
    }

    // ✅ DELETE USER
    @DeleteMapping("/user/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
