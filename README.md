# Bank Transaction System — Spring Boot

## Project Overview
A Spring Boot application demonstrating how database transactions work
using `@Transactional` annotation — including commit on success and
automatic rollback on failure.

---

## Project Structure

```
com.example.bank/
├── BankApplication.java          ← App entry point
├── controller/
│   └── BankController.java       ← REST API endpoints
├── service/
│   └── BankService.java          ← Business logic + @Transactional
├── repository/
│   └── AccountRepository.java    ← Database access (JPA)
├── model/
│   └── Account.java              ← Entity (maps to 'account' table)
└── exception/
    └── BankException.java        ← Custom RuntimeException → triggers rollback
```

---

## Setup Steps

### 1. MySQL Setup
```sql
CREATE DATABASE bankdb;
```
Or run `setup.sql` in MySQL Workbench.

### 2. Configure Database
Edit `src/main/resources/application.properties`:
```
spring.datasource.password=your_mysql_password
```

### 3. Run the Application
```bash
mvn spring-boot:run
```
Or run `BankApplication.java` from your IDE (IntelliJ/Eclipse).

App starts at: `http://localhost:8080`

---

## API Reference

### Create Account
```
POST http://localhost:8080/bank/create
Content-Type: application/json

{
  "name": "Alice",
  "balance": 5000
}
```

### Check Balance
```
GET http://localhost:8080/bank/1
```

### Transfer Money
```
POST http://localhost:8080/bank/transfer
Content-Type: application/json

{
  "fromId": 1,
  "toId": 2,
  "amount": 1000
}
```

---

## Testing with curl

### Step 1 — Create accounts
```bash
curl -X POST http://localhost:8080/bank/create \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","balance":5000}'

curl -X POST http://localhost:8080/bank/create \
  -H "Content-Type: application/json" \
  -d '{"name":"Bob","balance":3000}'

curl -X POST http://localhost:8080/bank/create \
  -H "Content-Type: application/json" \
  -d '{"name":"Charlie","balance":500}'
```

### Step 2 — Check balances
```bash
curl http://localhost:8080/bank/1
curl http://localhost:8080/bank/2
curl http://localhost:8080/bank/3
```

### Case 1: SUCCESS — Transfer ₹1000 from Alice to Bob
```bash
curl -X POST http://localhost:8080/bank/transfer \
  -H "Content-Type: application/json" \
  -d '{"fromId":1,"toId":2,"amount":1000}'
```
Expected: Alice = 4000, Bob = 4000 ✅

### Case 2: FAILURE — Transfer ₹2000 from Charlie (only has ₹500)
```bash
curl -X POST http://localhost:8080/bank/transfer \
  -H "Content-Type: application/json" \
  -d '{"fromId":3,"toId":2,"amount":2000}'
```
Expected: Error response, Charlie still = 500, Bob unchanged ✅ (ROLLBACK)

---

## How @Transactional Works

```
transferMoney(fromId, toId, amount)
        │
        ▼
  Spring opens transaction
        │
        ├─► Step 1: Deduct from sender   ──┐
        │                                  │
        ├─► Step 2: Add to receiver        │  If ANY exception
        │                                  │  is thrown here:
        │                                  │
        ▼                                  ▼
  No exception?                     RuntimeException?
  → COMMIT ✅                        → ROLLBACK ❌
  (both saved)                      (nothing saved)
```

- `@Transactional` wraps the method in a single DB transaction
- Any `RuntimeException` (like `BankException`) triggers automatic rollback
- This ensures atomicity: either ALL operations succeed, or NONE do

---

## Tools Used
- Java 17
- Spring Boot 3.2
- Spring Data JPA
- MySQL 8
- Maven
