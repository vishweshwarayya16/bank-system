package com.example.bank.controller;

import com.example.bank.exception.BankException;
import com.example.bank.model.Account;
import com.example.bank.service.BankService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller that exposes bank API endpoints.
 *
 * Endpoints:
 *   POST /bank/create       → Create a new account
 *   GET  /bank/{id}         → Get account details and balance
 *   POST /bank/transfer     → Transfer money between accounts
 */
@RestController
@RequestMapping("/bank")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    // =========================================================
    // POST /bank/create
    // Body: { "name": "Alice", "balance": 5000 }
    // =========================================================

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createAccount(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            double balance = Double.parseDouble(request.get("balance").toString());

            Account account = bankService.createAccount(name, balance);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Account created successfully");
            response.put("account", accountToMap(account));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (BankException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Invalid request data", HttpStatus.BAD_REQUEST);
        }
    }

    // =========================================================
    // GET /bank/{id}
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAccount(@PathVariable Long id) {
        try {
            Account account = bankService.getAccount(id);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("account", accountToMap(account));

            return ResponseEntity.ok(response);

        } catch (BankException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // =========================================================
    // POST /bank/transfer
    // Body: { "fromId": 1, "toId": 2, "amount": 1000 }
    // =========================================================

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transferMoney(@RequestBody Map<String, Object> request) {
        try {
            Long fromId = Long.parseLong(request.get("fromId").toString());
            Long toId   = Long.parseLong(request.get("toId").toString());
            double amount = Double.parseDouble(request.get("amount").toString());

            bankService.transferMoney(fromId, toId, amount);

            // Fetch updated balances to show in response
            Account sender   = bankService.getAccount(fromId);
            Account receiver = bankService.getAccount(toId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Transfer completed successfully");
            response.put("transferredAmount", amount);
            response.put("senderAccount", accountToMap(sender));
            response.put("receiverAccount", accountToMap(receiver));

            return ResponseEntity.ok(response);

        } catch (BankException e) {
            // @Transactional already rolled back at this point
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Invalid request data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private Map<String, Object> accountToMap(Account account) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", account.getId());
        map.put("name", account.getName());
        map.put("balance", account.getBalance());
        return map;
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", message);
        return ResponseEntity.status(status).body(error);
    }
}
