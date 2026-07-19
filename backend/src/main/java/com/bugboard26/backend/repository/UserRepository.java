package com.bugboard26.backend.repository;

import com.bugboard26.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> { //estende per ottenere metodi gia pronti

    Optional<User> findByEmail(String email);   //grazie a SpringData genera automaticamente la query SQL corrispondente (SELECT * FROM users WHERE email = ?

    boolean existsByEmail(String email);
}