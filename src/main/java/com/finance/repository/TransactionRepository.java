package com.finance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.finance.model.TransactionEntity;
import com.finance.model.UserEntity;
import com.finance.model.enums.RecordType;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

	// ========== FIND BY CATEGORY ==========
    List<TransactionEntity> findByCategoryAndDeletedFalse(String category);

    // ========== FIND ALL WITH USER (PAGINATED) ==========
    @Query(
        value = "SELECT t FROM TransactionEntity t JOIN FETCH t.user WHERE t.deleted = false",
        countQuery = "SELECT COUNT(t) FROM TransactionEntity t WHERE t.deleted = false"
    )
    Page<TransactionEntity> findByDeletedFalseWithUser(Pageable pageable);

    // ========== FIND BY USER WITH USER (PAGINATED) =======
    @Query(
        value = "SELECT t FROM TransactionEntity t JOIN FETCH t.user WHERE t.user = :user AND t.deleted = false",
        countQuery = "SELECT COUNT(t) FROM TransactionEntity t WHERE t.user = :user AND t.deleted = false"
    )
    Page<TransactionEntity> findByUserAndDeletedFalseWithUser(UserEntity user, Pageable pageable);

    // ========== FIND BY TYPE WITH USER ==========
    @Query("SELECT t FROM TransactionEntity t JOIN FETCH t.user WHERE t.type = :type AND t.deleted = false")
    List<TransactionEntity> findByTypeAndDeletedFalseWithUser(RecordType type);

    // ========== FIND BY CATEGORY AND USER ==========
	List<TransactionEntity> findByCategoryAndUserAndDeletedFalse(String category, UserEntity loggedUser);
	
	// ========== FIND BY ID AND USER ==========
	Optional<TransactionEntity> findByIdAndUserAndDeletedFalse(Long id, UserEntity user);

	// ========== FIND BY ID WITH USER ==========
	Optional<TransactionEntity> findByIdAndDeletedFalse(Long id);
	
}