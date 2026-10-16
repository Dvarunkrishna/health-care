package com.healthcare.service;

import com.healthcare.dto.UserDto;
import com.healthcare.entity.User;
import com.healthcare.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toDto(user);
    }

    public UserDto updateMe(String email, UserDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (dto != null) {
            if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
                user.setFullName(dto.getFullName());
            }
            if (dto.getPhone() != null) {
                user.setPhone(dto.getPhone());
            }
        }
        userRepository.save(user);
        return toDto(user);
    }

    private UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(),
                user.getRole() == null ? null : user.getRole().name(), user.getCreatedAt());
    }
}
