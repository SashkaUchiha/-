package com.example.regapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 4, max = 4)
    @Column(name = "identifier", nullable = false, unique = true)
    @NumberFormat
    private String identifier;
    @Size(min = 6, max = 6)
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "salt", nullable = false)
    private String salt;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "phone", nullable = false)
    private String phoneNumber;
    @Column(name = "email", nullable = false)
    @Email
    private String email;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "role")
    private String role;
}
