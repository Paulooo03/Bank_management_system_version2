package com.example.bank_management_system;
public class Transaction {
    private String date;
    private double amount;
    private String description;
    private double balance;

    // Constructor
    public Transaction(String date, double amount, String description, double balance) {
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.balance = balance;
    }

    // Getter methods
    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public double getBalance() {
        return balance;
    }
}

