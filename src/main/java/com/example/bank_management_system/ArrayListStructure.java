package com.example.bank_management_system;

public class ArrayListStructure {
    private Transaction[] transactions;
    private int size;

    // Initial capacity of the list
    private static final int INITIAL_CAPACITY = 10;

    // Constructor to initialize the list
    public ArrayListStructure() {
        this.transactions = new Transaction[INITIAL_CAPACITY];
        this.size = 0;
    }

    // Add a transaction to the list
    public void add(Transaction transaction) {
        // Check if the array needs to be resized
        if (size >= transactions.length) {
            resize();
        }
        transactions[size] = transaction; // Add the new transaction
        size++; // Increase the size of the list
    }

    // Get a transaction by its index
    public Transaction get(int index) {
        if (index >= 0 && index < size) {
            return transactions[index];
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds.");
        }
    }

    // Remove a transaction by its index
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds.");
        }

        // Shift all elements after the index one position to the left
        for (int i = index; i < size - 1; i++) {
            transactions[i] = transactions[i + 1];
        }

        // Set the last element to null and decrease the size
        transactions[size - 1] = null;
        size--;
    }

    // Get the size of the list (number of transactions)
    public int size() {
        return size;
    }

    // Resize the array when it reaches its capacity
    private void resize() {
        int newCapacity = transactions.length * 2; // Double the capacity
        Transaction[] newArray = new Transaction[newCapacity];
        System.arraycopy(transactions, 0, newArray, 0, transactions.length); // Copy the old array to the new one
        transactions = newArray; // Set transactions to the new array
    }

    // Find transactions by description
    public ArrayListStructure findTransactionsByDescription(String description) {
        ArrayListStructure result = new ArrayListStructure();
        for (int i = 0; i < size; i++) {
            if (transactions[i].getDescription().contains(description)) {
                result.add(transactions[i]);
            }
        }
        return result;
    }

    // Get the latest balance from the list (from the most recent transaction)
    public double getLatestBalance() {
        if (size == 0) {
            return 0.0;
        } else {
            return transactions[size - 1].getBalance();
        }
    }

    // Display all transactions for debugging or testing
    public void displayTransactions() {
        for (int i = 0; i < size; i++) {
            Transaction transaction = transactions[i];
            System.out.println(transaction.getDate() + " - " + transaction.getAmount() + " - " + transaction.getDescription() + " - " + transaction.getBalance());
        }
    }

    // Clear all transactions
    public void clear() {
        transactions = new Transaction[INITIAL_CAPACITY]; // Reset the array
        size = 0; // Reset the size
    }
}
