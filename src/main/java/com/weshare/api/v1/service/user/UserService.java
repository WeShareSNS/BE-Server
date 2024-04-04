package com.weshare.api.v1.service.user;

import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.user.UserRepository;
import com.weshare.api.v1.service.user.dto.UserUpdateDto;
import com.weshare.api.v1.service.user.dto.PasswordUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void updateUser(UserUpdateDto updateDto) {
        User user = findUserOrElseThrow(updateDto.getUserEmail());
        updateDto.getName().ifPresent(user::updateName);
        updateDto.getProfileImg().ifPresent(user::updateProfileImg);
        updateDto.getBirthDate().ifPresent(user::updateBirthDate);
    }

    private User findUserOrElseThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
                });
    }

    public void updatePassword(PasswordUpdateDto passwordUpdateDto) {
        String newPassword = passwordUpdateDto.getNewPassword();
        if (isMatchPassword(newPassword, passwordUpdateDto.getVerifyPassword())) {
            throw new IllegalArgumentException("패스워드 재입력이 올바르지 않습니다.");
        }

        User user = findUserOrElseThrow(passwordUpdateDto.getUserEmail());
        if (!user.isSamePassword(passwordUpdateDto.getOldPassword(), passwordEncoder)){
            throw new IllegalArgumentException("기존 패스워드가 올바르지 않습니다.");
        }

        user.updatePassword(newPassword, passwordEncoder);
    }

    private boolean isMatchPassword(String newPassword, String verifyPassword) {
        return !Objects.equals(newPassword, verifyPassword);
    }
}
