package com.weshare.api.v1.domain.user;

import com.weshare.api.v1.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Column(name = "profile_img", nullable = false)
    private String profileImg;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Social social;

//    delete 상태 추가해서 처리할지 고민하기
//    @Column(nullable = false, columnDefinition = "TINYINT(1) default 0")
//    private boolean isDeleted;

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
            throw new IllegalStateException("이름의 길이는 2~20 사이어야 합니다.");
        }
        this.name = name;
    }

    public void updateBirthDate(LocalDate birthDate) {
        if (!isBeforeDate(birthDate)) {
            throw new IllegalStateException("생년월일이 올바르지 않습니다.");
        }
        this.birthDate = birthDate;
    }

    private boolean isBeforeDate(LocalDate birthDate) {
        return birthDate != null && LocalDate.now().isAfter(birthDate);
    }

    public void updateProfileImg(String profileImg) {
        if (!ProfileImgValidator.isUrlPattern(profileImg)) {
            throw new IllegalStateException("프로필 이미지 정보가 올바르지 않습니다.");
        }
        this.profileImg = profileImg;
    }

    public void updatePassword(String newPassword, PasswordEncoder passwordEncoder) {
        int length = newPassword.length();
        if (8 > length || length > 16) {
            throw new IllegalStateException("패스워드의 길이는 8~16 사이어야 합니다.");
        }
        this.password = passwordEncoder.encode(newPassword);
    }

    public boolean isSamePassword(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, this.password);
    }

    // deletedAt 까지 받아서 JPA 콜백 이벤트로 로그 처리나 할 수 있을거 같음
//    public void deleteUser() {
//        this.isDeleted = true;
//    }

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
