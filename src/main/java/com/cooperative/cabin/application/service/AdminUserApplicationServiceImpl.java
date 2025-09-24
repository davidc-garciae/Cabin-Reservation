package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.exception.UserNotFoundException;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import com.cooperative.cabin.presentation.dto.AdminUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUserApplicationServiceImpl implements AdminUserApplicationService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found with id: ";

    private final UserJpaRepository userRepository;

    public AdminUserApplicationServiceImpl(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AdminUserResponse upsertUser(Long id, String email, String fullName, String role, boolean active) {
        User user;
        if (id != null) {
            // Update existing user
            user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE + id));
            user.setEmail(email);
            user.setFullName(fullName);
            user.setRole(User.UserRole.valueOf(role));
            user.setActive(active);
        } else {
            // Create new user
            user = new User();
            user.setEmail(email);
            user.setFullName(fullName);
            user.setRole(User.UserRole.valueOf(role));
            user.setActive(active);
        }

        User savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    @Override
    public AdminUserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE + userId));
        return toResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse updateProfile(Long userId, String email, String fullName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE + userId));
        user.setEmail(email);
        user.setFullName(fullName);
        User savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    @Override
    public List<AdminUserResponse> listUsers() {
        Pageable pageable = PageRequest.of(0, 100); // Default pagination
        Page<User> users = userRepository.findAll(pageable);
        return users.getContent().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AdminUserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE + id));
        return toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE + id);
        }
        userRepository.deleteById(id);
    }

    private AdminUserResponse toResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getActive());
    }
}
