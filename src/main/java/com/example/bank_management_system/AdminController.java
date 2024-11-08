package com.example.bank_management_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AdminController {

    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, Double> amountColumn;
    @FXML
    private TableColumn<Transaction, String> descriptionColumn;
    @FXML
    private TableColumn<Transaction, Double> balanceColumn;

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
    }

    public void setTransactions(ObservableList<Transaction> transactions) {
        transactionTable.setItems(transactions);
    }

    // Example method to load all transactions
    public ObservableList<Transaction> loadAllTransactions() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 6 && "Client".equalsIgnoreCase(values[0])) {
                    String date = values[5].trim(); // Last entry could be the transaction date
                    double amount = Double.parseDouble(values[3].trim()); // Amount of transaction
                    String description = values[4].trim(); // Transaction description
                    double balance = Double.parseDouble(values[6].trim()); // Balance
                    transactions.add(new Transaction(date, amount, description, balance));
                }
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to load transactions.");
        }

        return transactions;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class Transaction {
        private final String date;
        private final double amount;
        private final String description;
        private final double balance;

        public Transaction(String date, double amount, String description, double balance) {
            this.date = date;
            this.amount = amount;
            this.description = description;
            this.balance = balance;
        }

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
}
