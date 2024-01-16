package com.weShare.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "email",nullable = false, unique = true)
    private String email;

    @Column(name = "username",nullable = false, unique = true)
    private String username;
    @Column(name = "profile_img")
    private String profileImg;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    private String password;

    @Enumerated(value = EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    private List<UserRole> roles = new ArrayList<>();

    @Builder
    private User(String email, String username, String profileImg, LocalDate birthDate, String password, List<UserRole> roles) {
        this.email = email;
        this.username = username;
        this.profileImg = profileImg;
        this.birthDate = birthDate;
        this.password = password;
        this.roles = roles;
    }
}
