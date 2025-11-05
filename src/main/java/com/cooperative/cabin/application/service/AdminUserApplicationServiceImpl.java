package com.cooperative.cabin.application.service;

import com.cooperative.cabin.domain.exception.UserNotFoundException;
import com.cooperative.cabin.domain.model.User;
import com.cooperative.cabin.infrastructure.repository.UserJpaRepository;
import com.cooperative.cabin.presentation.dto.AdminUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUserApplicationServiceImpl implements AdminUserApplicationService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found with id: ";

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserApplicationServiceImpl(UserJpaRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public AdminUserResponse upsertUser(Long id, String email, String documentNumber, String fullName, String role,
            boolean active) {
        User user;
        User.UserRole oldRole = null;
        
        if (id != null) {
            // Update existing user
            user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE + id));
            oldRole = user.getRole(); // Guardar rol anterior
            user.setEmail(email);
            user.setIdentificationNumber(documentNumber);
            user.setName(fullName);
            user.setRole(User.UserRole.valueOf(role));
            user.setActive(active);
        } else {
            // Create new user
            user = new User();
            user.setEmail(email);
            user.setIdentificationNumber(documentNumber);
            user.setName(fullName);
            user.setRole(User.UserRole.valueOf(role));
            user.setActive(active);
        }

        // Si el usuario fue promovido a ADMIN, verificar si debe cambiar contraseña
        User.UserRole newRole = User.UserRole.valueOf(role);
        if (newRole == User.UserRole.ADMIN && oldRole != null && oldRole != User.UserRole.ADMIN) {
            user.setMustChangePassword(true);
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
    public AdminUserResponse updateProfile(Long userId, String email, String documentNumber, String fullName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE + userId));
        user.setEmail(email);
        user.setIdentificationNumber(documentNumber);
        user.setName(fullName);
        User savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    @Override
    public List<AdminUserResponse> listUsers(String email, String documentNumber, Integer page, Integer size) {
        // Filtros exactos prioritarios
        if (email != null && !email.isBlank()) {
            return userRepository.findByEmail(email)
                    .map(this::toResponse)
                    .map(java.util.List::of)
                    .orElseGet(java.util.List::of);
        }
        if (documentNumber != null && !documentNumber.isBlank()) {
            return userRepository.findByIdentificationNumber(documentNumber)
                    .map(this::toResponse)
                    .map(java.util.List::of)
                    .orElseGet(java.util.List::of);
        }
        // Sin filtros: paginado
        int p = page != null && page >= 0 ? page : 0;
        int s = size != null && size > 0 ? size : 100;
        Pageable pageable = PageRequest.of(p, s);
        Page<User> users = userRepository.findAll(pageable);
        return users.getContent().stream().map(this::toResponse).toList();
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

    @Override
    @Transactional
    public void forcePasswordChange(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE + userId));
        user.setMustChangePassword(true);
        userRepository.save(user);
    }

    private AdminUserResponse toResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getIdentificationNumber(),
                user.getName(),
                user.getRole().name(),
                user.getActive());
    }
}
