-- ============================================================
-- Bank Transaction System - Database Setup
-- Run this in MySQL Workbench or MySQL CLI
-- ============================================================

-- Step 1: Create the database
CREATE DATABASE IF NOT EXISTS bankdb;
USE bankdb;

-- Step 2: The 'account' table will be auto-created by Spring Boot (ddl-auto=update)
-- But you can also create it manually:

CREATE TABLE IF NOT EXISTS account (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    balance DOUBLE       NOT NULL
);

-- Step 3: Insert sample test data
-- (You can also use POST /bank/create API to create accounts)

INSERT INTO account (name, balance) VALUES ('Alice', 5000.00);
INSERT INTO account (name, balance) VALUES ('Bob',   3000.00);
INSERT INTO account (name, balance) VALUES ('Charlie', 500.00);

-- Verify data
SELECT * FROM account;

-- ============================================================
-- EXPECTED RESULTS AFTER TESTS:
-- ============================================================
-- Case 1 (Success): Transfer ₹1000 from Alice(id=1) to Bob(id=2)
--   Alice: 5000 - 1000 = 4000
--   Bob:   3000 + 1000 = 4000
--
-- Case 2 (Failure): Transfer ₹2000 from Charlie(id=3) to Bob(id=2)
--   Charlie only has ₹500 → BankException → ROLLBACK
--   Charlie: still 500 (unchanged)
--   Bob:     still 3000 (unchanged)
-- ============================================================
