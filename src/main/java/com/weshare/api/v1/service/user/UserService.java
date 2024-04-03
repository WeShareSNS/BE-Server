package com.weshare.api.v1.service.user;

import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.user.UserRepository;
import com.weshare.api.v1.service.user.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;

    @Transactional
    public void updateUser(UserUpdateDto updateDto) {
        User user = findUserOrElseThrow(updateDto);
        updateDto.getName().ifPresent(user::updateName);
        updateDto.getProfileImg().ifPresent(user::updateProfileImg);
        updateDto.getBirthDate().ifPresent(user::updateBirthDate);
    }

    private User findUserOrElseThrow(UserUpdateDto updateDto) {
        return userRepository.findByEmail(updateDto.getUserEmail())
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
                });
    }
}
