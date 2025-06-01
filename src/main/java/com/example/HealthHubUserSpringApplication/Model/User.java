package com.example.HealthHubUserSpringApplication.Model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_id_sequence"
    )
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private int age;
    private String gender;
    private int height; // in cm
    private double weight; // in kg
    private double bmi;   // Stored in DB
    private String bloodType;
    private String genotype;
    private int oxygenLevel;

    @ElementCollection
    @CollectionTable(name = "user_medical_conditions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "medical_condition")
    private List<String> medicalConditions;

    private boolean familyMedicalHistory;

    @Column(length = 1000)
    private String familyHistoryText; // only filled if familyMedicalHistory is true

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Constructors

    public User() {}

    public User(Long id, String username, String email, String password, int age, String gender,
                int height, double weight, String bloodType, String genotype,
                int oxygenLevel, List<String> medicalConditions, boolean familyMedicalHistory,
                String familyHistoryText) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        this.genotype = genotype;
        this.oxygenLevel = oxygenLevel;
        this.medicalConditions = medicalConditions;
        this.familyMedicalHistory = familyMedicalHistory;
        this.familyHistoryText = familyHistoryText;
        updateBMI();
    }

    // Getters and Setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    @Override
    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    @Override
    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }

    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public int getHeight() { return height; }

    public void setHeight(int height) {
        this.height = height;
        updateBMI();
    }

    public double getWeight() { return weight; }

    public void setWeight(double weight) {
        this.weight = weight;
        updateBMI();
    }

    public double getBmi() { return bmi; }

    private void updateBMI() {
        if (height > 0 && weight > 0) {
            this.bmi = weight / Math.pow(height / 100.0, 2);
        }
    }

    public String getBloodType() { return bloodType; }

    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getGenotype() { return genotype; }

    public void setGenotype(String genotype) { this.genotype = genotype; }

    public int getOxygenLevel() { return oxygenLevel; }

    public void setOxygenLevel(int oxygenLevel) { this.oxygenLevel = oxygenLevel; }

    public List<String> getMedicalConditions() { return medicalConditions; }

    public void setMedicalConditions(List<String> medicalConditions) { this.medicalConditions = medicalConditions; }

    public boolean isFamilyMedicalHistory() { return familyMedicalHistory; }

    public void setFamilyMedicalHistory(boolean familyMedicalHistory) { this.familyMedicalHistory = familyMedicalHistory; }

    public String getFamilyHistoryText() { return familyHistoryText; }

    public void setFamilyHistoryText(String familyHistoryText) { this.familyHistoryText = familyHistoryText; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // UserDetails methods

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // You can enhance this for role-based access
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
