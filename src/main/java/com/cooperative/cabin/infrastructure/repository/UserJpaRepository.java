package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Page<User> findByActiveTrue(Pageable pageable);

    Page<User> findByRole(User.UserRole role, Pageable pageable);

    Page<User> findByActiveAndRole(Boolean active, User.UserRole role, Pageable pageable);

    long countByActiveTrue();
}
