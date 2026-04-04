package com.finance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	// ========== FIND BY EMAIL ==========
    Optional<UserEntity> findByEmailAndDeletedFalse(String email);
    
    // ========== FIND BY ID ==========
    Optional<UserEntity> findByIdAndDeletedFalse(Long id);
    
    // ========== FIND ALL ==========
    List<UserEntity> findAllByDeletedFalse();
}