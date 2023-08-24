package com.tenius.sns.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tenius.sns.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

}
