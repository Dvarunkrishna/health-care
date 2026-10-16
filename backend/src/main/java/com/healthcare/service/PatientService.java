package com.healthcare.service;

import com.healthcare.dto.UserDto;
import com.healthcare.entity.User;
import com.healthcare.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final UserRepository userRepository;

    public PatientService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toDto(user);
    }

    public UserDto updateProfile(String email, UserDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        userRepository.save(user);
        return toDto(user);
    }

    private UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(),
                user.getRole() == null ? null : user.getRole().name(), user.getCreatedAt());
    }
}