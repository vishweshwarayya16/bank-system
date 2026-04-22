package com.example.bank.repository;

import com.example.bank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Account entity.
 * JpaRepository provides built-in CRUD methods:
 *   save(), findById(), findAll(), deleteById(), etc.
 *
 * Spring Data JPA automatically creates the implementation at runtime.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // No custom methods needed for this project.
    // All required operations are covered by JpaRepository.
}
