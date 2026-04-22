package com.example.bank.model;

import jakarta.persistence.*;

/**
 * Entity class representing the 'account' table in the database.
 * Each Account has an id, owner name, and current balance.
 */
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double balance;

    // ---- Constructors ----

    public Account() {}

    public Account(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    // ---- Getters & Setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{id=" + id + ", name='" + name + "', balance=" + balance + "}";
    }
}
