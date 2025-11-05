package com.cooperative.cabin.application.service;

import com.cooperative.cabin.presentation.dto.AdminUserResponse;

public interface AdminUserApplicationService {
    AdminUserResponse upsertUser(Long id, String email, String documentNumber, String fullName, String role,
            boolean active);

    AdminUserResponse getProfile(Long userId);

    AdminUserResponse updateProfile(Long userId, String email, String documentNumber, String fullName);

    java.util.List<AdminUserResponse> listUsers(String email, String documentNumber, Integer page, Integer size);

    AdminUserResponse getById(Long id);

    void deleteUser(Long id);

    void forcePasswordChange(Long userId);
}
