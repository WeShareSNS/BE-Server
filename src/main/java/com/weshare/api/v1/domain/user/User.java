package com.weshare.api.v1.domain.user;

import com.weshare.api.v1.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "name", nullable = false, unique = true, length = 20)
    private String name;

    @Column(name = "profile_img")
    private String profileImg;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Social social;

    @Builder
    private User(
            String email, String name,
            String profileImg,
            LocalDate birthDate,
            String password,
            Role role,
            Social social
    ) {
        this.email = email;
        this.name = name;
        this.profileImg = profileImg;
        this.birthDate = birthDate;
        this.password = password;
        this.role = role;
        this.social = social;
    }

    public void updateName(String name) {
        int length = name.length();
        if (2 > length || length > 20) {
            throw new IllegalArgumentException("이름의 길이는 2~20 사이어야 합니다.");
        }
        this.name = name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(name, user.name) && Objects.equals(profileImg, user.profileImg) && Objects.equals(birthDate, user.birthDate) && Objects.equals(password, user.password) && role == user.role && social == user.social;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name, profileImg, birthDate, password, role, social);
    }
}
