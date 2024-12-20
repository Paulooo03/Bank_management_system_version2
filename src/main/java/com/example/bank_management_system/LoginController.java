package com.example.bank_management_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class LoginController {

    @FXML
    private TextField accountNumberField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button debugButton;

    private MyArrayList<Account> accounts;

    @FXML
    public void initialize() {
        accounts = new MyArrayList<>();
        try {
            accounts.addAll(loadAccountsFromCSV("src/main/resources/com/example/bank_management_system/bank_database.csv"));
            System.out.println("Total accounts loaded: " + (accounts.size() - 1 ));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load account data.");
        }

        loginButton.setOnAction(this::handleLoginButtonAction);
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String accountNumber = accountNumberField.getText().trim();
        String password = passwordField.getText().trim();

        Account account = validateLogin(accountNumber, password);
        if (account == null) {
            return;
        }

        switch (account.getPosition().toLowerCase()) {
            case "admin":
                navigateToAdminScreen(event);
                break;
            case "manager":
                navigateToManagerScreen(event);
                break;
            case "teller":
                navigateToTellerScreen(event);
                break;
            default:
                showUserTransactionHistory(account.getAccountNumber(), event);
                break;
        }
    }

    private MyArrayList<Account> loadAccountsFromCSV(String filePath) throws IOException {
        MyArrayList<Account> accounts = new MyArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4) {
                    String position = values[0].trim();
                    String accountNumber = values[1].trim();
                    String username = values[2].trim();
                    String password = values[3].trim();
                    String status = values.length > 4 ? values[4].trim() : "";

                    Account account = new Account(position, accountNumber, username, password, status);
                    accounts.add(account);
                }
            }
        }
        return accounts;
    }

    private Account validateLogin(String accountNumber, String password) {
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            if (account.getAccountNumber().equals(accountNumber) && account.getPassword().equals(password)) {
                if (account.getPosition().equalsIgnoreCase("admin") || account.getStatus().equals("Active")) {
                    return account;
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Account inactive, contact manager or administrator.");
                }
            } else if ((!account.getAccountNumber().equals(accountNumber)) && account.getPassword().equals(password)) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid account number or password. Please try again.");
            } else if ((account.getAccountNumber().equals(accountNumber)) && (!account.getPassword().equals(password))) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid account number or password. Please try again.");
            }
        }
        return null;
    }

    private void navigateToAdminScreen(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("admin_view.fxml")));
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Admin Dashboard");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load admin dashboard.");
        }
    }

    private void navigateToManagerScreen(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("manager.fxml")));
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Manager Dashboard");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load manager dashboard.");
        }
    }

    private void navigateToTellerScreen(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("teller.fxml")));
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Teller Dashboard");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load teller dashboard.");
        }
    }

    private void showUserTransactionHistory(String accountNumber, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user_transaction_history.fxml"));
            Parent root = loader.load();

            UserTransactionHistoryController controller = loader.getController();
            ObservableList<UserTransactionHistoryController.Transaction> transactions = loadTransactions(accountNumber);

            // Set current client account number in the controller
            controller.setCurrentClientAccountNumber(accountNumber);

            // Pass transactions and clientName
            String clientName = getClientNameFromAccount(accountNumber);
            controller.setTransactions(transactions, clientName);

            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Transaction History");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load transaction history.");
        }
    }

    private String getClientNameFromAccount(String accountNumber) {
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            if (account.getAccountNumber().equals(accountNumber)) {
                return account.getUsername();
            }
        }
        return "Unknown";
    }

    private ObservableList<UserTransactionHistoryController.Transaction> loadTransactions(String accountNumber) {
        ObservableList<UserTransactionHistoryController.Transaction> transactions = FXCollections.observableArrayList();
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4 && "Client".equalsIgnoreCase(values[0]) && values[1].trim().equals(accountNumber)) {
                    for (int i = 5; i < values.length; i += 3) {
                        if (i + 2 < values.length) {
                            String date = values[i].trim();
                            double amount = Double.parseDouble(values[i + 1].trim());
                            String description = values[i + 2].trim();

                            double balance = transactions.isEmpty() ? amount : transactions.get(transactions.size() - 1).getBalance() + amount;

                            transactions.add(new UserTransactionHistoryController.Transaction(date, amount, description, balance));
                        }
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Account {
        private String position;
        private String accountNumber;
        private String username;
        private String password;
        private String status;

        public Account(String position, String accountNumber, String username, String password, String status) {
            this.position = position;
            this.accountNumber = accountNumber;
            this.username = username;
            this.password = password;
            this.status = status;
        }

        public String getPosition() {
            return position;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getStatus() {
            return status;
        }
    }
}
