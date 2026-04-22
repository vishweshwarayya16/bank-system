package com.example.bank.service;

import com.example.bank.exception.BankException;
import com.example.bank.model.Account;
import com.example.bank.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * BankService contains all business logic.
 *
 * KEY CONCEPT — @Transactional:
 *   - Wraps the method in a database transaction.
 *   - If the method completes successfully → COMMIT (changes saved).
 *   - If a RuntimeException is thrown → ROLLBACK (all changes undone).
 *   - This ensures atomicity: either BOTH debit AND credit happen, or NEITHER.
 */
@Service
public class BankService {

    private static final Logger log = LoggerFactory.getLogger(BankService.class);

    private final AccountRepository accountRepository;

    // Constructor injection (preferred over @Autowired on field)
    public BankService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // =========================================================
    // CREATE ACCOUNT
    // =========================================================

    /**
     * Creates a new bank account with the given name and initial balance.
     *
     * @param name    account owner's name
     * @param balance starting balance (must be >= 0)
     * @return the saved Account object
     */
    @Transactional
    public Account createAccount(String name, double balance) {
        if (balance < 0) {
            throw new BankException("Initial balance cannot be negative.");
        }
        Account account = new Account(name, balance);
        Account saved = accountRepository.save(account);
        log.info("Account created: {}", saved);
        return saved;
    }

    // =========================================================
    // GET BALANCE
    // =========================================================

    /**
     * Fetches an account by its ID.
     *
     * @param id account ID
     * @return the Account object
     * @throws BankException if account is not found
     */
    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new BankException("Account not found with ID: " + id));
    }

    // =========================================================
    // TRANSFER MONEY  ← MAIN TASK
    // =========================================================

    /**
     * Transfers money from one account to another.
     *
     * HOW @Transactional WORKS HERE:
     *   Step 1: Deduct amount from sender's balance → saved to DB
     *   Step 2: Add amount to receiver's balance    → saved to DB
     *
     *   CASE 1 — SUCCESS: Both steps complete → COMMIT
     *     → Both balances are updated in the database.
     *
     *   CASE 2 — FAILURE: An exception is thrown (e.g., insufficient funds,
     *     invalid account) → ROLLBACK
     *     → The deduction from Step 1 is UNDONE.
     *     → The database returns to its original state.
     *     → No money is lost or created.
     *
     * @param fromId account ID to deduct money from
     * @param toId   account ID to add money to
     * @param amount amount to transfer (must be > 0)
     */
    @Transactional
    public void transferMoney(Long fromId, Long toId, double amount) {
        log.info("Transfer initiated: from account {} to account {}, amount={}", fromId, toId, amount);

        // --- Validation ---
        if (amount <= 0) {
            throw new BankException("Transfer amount must be greater than zero.");
        }

        if (fromId.equals(toId)) {
            throw new BankException("Cannot transfer money to the same account.");
        }

        // --- Fetch sender account ---
        Account sender = accountRepository.findById(fromId)
                .orElseThrow(() -> new BankException("Sender account not found with ID: " + fromId));

        // --- Fetch receiver account ---
        Account receiver = accountRepository.findById(toId)
                .orElseThrow(() -> new BankException("Receiver account not found with ID: " + toId));

        // --- Check sufficient balance ---
        if (sender.getBalance() < amount) {
            // ★ RuntimeException thrown → @Transactional triggers ROLLBACK
            throw new BankException(
                "Insufficient funds! Sender balance: " + sender.getBalance() +
                ", Requested: " + amount
            );
        }

        // --- Step 1: Deduct from sender ---
        sender.setBalance(sender.getBalance() - amount);
        accountRepository.save(sender);
        log.info("Deducted {} from account {}. New balance: {}", amount, fromId, sender.getBalance());

        // ★ Simulate an error to test ROLLBACK (uncomment the line below to test failure):
        // if (true) throw new BankException("Simulated system error after deduction!");

        // --- Step 2: Add to receiver ---
        receiver.setBalance(receiver.getBalance() + amount);
        accountRepository.save(receiver);
        log.info("Added {} to account {}. New balance: {}", amount, toId, receiver.getBalance());

        // If we reach here without exception → COMMIT (both changes saved)
        log.info("Transfer successful! {} transferred from account {} to account {}", amount, fromId, toId);
    }
}
