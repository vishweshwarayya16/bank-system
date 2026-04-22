package com.example.bank.exception;

/**
 * Custom exception thrown when a bank operation fails.
 * Examples: insufficient funds, account not found, invalid amount.
 *
 * This is a RuntimeException so Spring's @Transactional
 * will automatically trigger a ROLLBACK when it is thrown.
 */
public class BankException extends RuntimeException {

    public BankException(String message) {
        super(message);
    }
}
