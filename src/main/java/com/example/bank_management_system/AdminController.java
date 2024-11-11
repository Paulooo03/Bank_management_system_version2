package com.example.bank_management_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;

public class AdminController {

    @FXML
    private TextField accountNumberTextField;

    @FXML
    private Button checkAccountStatusButton;

    @FXML
    private Label accountNumberLabel;

    @FXML
    private Label accountHolderLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label transactionHistoryLabel;

    @FXML
    private Label accountTypeLabel;

    @FXML
    private TableView<Transaction> transactionsTableView;

    @FXML
    private TableColumn<Transaction, String> dateColumn;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    @FXML
    private TableColumn<Transaction, String> descriptionColumn;

    @FXML
    private Button activateOrDeactivateButton;

    @FXML
    private VBox transferMoneyBox;

    @FXML
    private TextField transferAmountTextField;

    @FXML
    private TextField transferOperationTextField;

    @FXML
    private Button transferMoneyButton;

    @FXML
    private Button submitTransferButton;

    @FXML
    private Button cancelTransferButton;

    private final ObservableList<Client> clients = FXCollections.observableArrayList();
    private Client currentClient;

    public void handleBackButton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Client {
        String position;
        String username;
        String status;
        int accountNumber;
        ObservableList<Transaction> transactions;

        public Client(String position, String username, String status, int accountNumber, ObservableList<Transaction> transactions) {
            this.position = position;
            this.username = username;
            this.status = status;
            this.accountNumber = accountNumber;
            this.transactions = transactions;
        }

        public ObservableList<Transaction> getTransactions() {
            return transactions;
        }

        public int getAccountNumber() {
            return accountNumber;
        }

        public double getBalance() {
            double balance = 0;
            for (Transaction transaction : transactions) {
                balance += transaction.getAmount();
            }
            return balance;
        }

        public String getPosition() {
            return position;
        }
    }

    public static class Transaction {
        private final String date;
        private final double amount;
        private final String description;

        public Transaction(String date, double amount, String description) {
            this.date = date;
            this.amount = amount;
            this.description = description;
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
    }

    @FXML
    public void initialize() {
        clearLabels();
        accountNumberTextField.setPromptText("Enter Account Number");
        checkAccountStatusButton.setDisable(true);
        loadClients();

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        accountNumberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkAccountStatusButton.setDisable(newValue.trim().isEmpty());
        });

        transactionsTableView.setVisible(false);
        balanceLabel.setVisible(false);
        transactionHistoryLabel.setVisible(false);
        transferMoneyBox.setVisible(false);
    }

    private void loadClients() {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4 && ("Client".equalsIgnoreCase(values[0]) || "Manager".equalsIgnoreCase(values[0]) || "Teller".equalsIgnoreCase(values[0]))) {
                    int accountNumber = Integer.parseInt(values[1].trim());
                    ObservableList<Transaction> transactions = FXCollections.observableArrayList();

                    for (int i = 5; i < values.length; i += 3) {
                        if (i + 2 < values.length && !values[i].isEmpty() && !values[i + 1].isEmpty()) {
                            String date = values[i].trim();
                            double amount = Double.parseDouble(values[i + 1].trim());
                            String description = values[i + 2].trim();

                            transactions.add(new Transaction(date, amount, description));
                        }
                    }

                    clients.add(new Client(values[0], values[2], values[4], accountNumber, transactions));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCheckAccountStatusButtonAction() {
        String accountNumber = accountNumberTextField.getText();
        Client client = getClientDetails(accountNumber);
        currentClient = client;
        if (client == null) {
            clearLabels();
            accountNumberLabel.setText("Account not found.");
            return;
        }

        accountNumberLabel.setText(String.valueOf(client.getAccountNumber()));
        accountHolderLabel.setText(client.username);
        statusLabel.setText(client.status);
        balanceLabel.setText(String.format("%.2f", client.getBalance()));
        accountTypeLabel.setText(client.getPosition());
        updateButtonText(client.status);

        transactionsTableView.setItems(client.getTransactions());

        transactionsTableView.setVisible(true);
        balanceLabel.setVisible(true);
        transactionHistoryLabel.setVisible(true);
    }

    private void updateButtonText(String status) {
        if ("Active".equalsIgnoreCase(status)) {
            activateOrDeactivateButton.setText("Deactivate");
        } else {
            activateOrDeactivateButton.setText("Activate");
        }
    }

    @FXML
    public void handleActivateOrDeactivateButtonAction(ActionEvent actionEvent) {
        if (currentClient != null) {
            if ("Active".equalsIgnoreCase(currentClient.status)) {
                currentClient.status = "Inactive";
            } else {
                currentClient.status = "Active";
            }
            updateButtonText(currentClient.status);
            updateCSVStatus();
            statusLabel.setText(currentClient.status);
        }
    }

    private void updateCSVStatus() {
        String filePath = "src/main/resources/com/example/bank_management_system/bank_database.csv";
        MyArrayList<String> lines = new MyArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",\\s*");

                if (values.length >= 5 && ("Client".equalsIgnoreCase(values[0]) || "Manager".equalsIgnoreCase(values[0]) || "Teller".equalsIgnoreCase(values[0]))) {
                    try {
                        int parsedAccountNumber = Integer.parseInt(values[1].trim());
                        if (parsedAccountNumber == currentClient.accountNumber) {
                            // Update status of the current client
                            values[4] = currentClient.status;
                            line = String.join(",", values);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping line due to number format issue: " + line);
                    }
                }
                lines.add(line);  // Add updated or unchanged line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write the updated lines back to the file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < lines.size(); i++) {
                bw.write(lines.get(i));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleTransferMoney(ActionEvent event) {
        transferMoneyBox.setVisible(true);
    }

    @FXML
    private void handleSubmitTransfer(ActionEvent event) {
        String accountNumber = accountNumberTextField.getText().trim();
        String amountText = transferAmountTextField.getText().trim();

        if (accountNumber.isEmpty() || amountText.isEmpty()) {
            return;
        }

        double amount = Double.parseDouble(amountText);

        Transaction newTransaction = new Transaction("2024-11-11", amount, transferOperationTextField.getText());

        currentClient.getTransactions().add(newTransaction);

        transferMoneyBox.setVisible(false);
        transferAmountTextField.clear();
        transferOperationTextField.clear();
        transactionsTableView.setItems(currentClient.getTransactions());
    }

    @FXML
    private void handleCancelTransfer(ActionEvent event) {
        transferMoneyBox.setVisible(false);
    }

    private Client getClientDetails(String accountNumber) {
        return clients.stream().filter(client -> String.valueOf(client.getAccountNumber()).equals(accountNumber)).findFirst().orElse(null);
    }

    private void clearLabels() {
        accountNumberLabel.setText("");
        accountHolderLabel.setText("");
        statusLabel.setText("");
        balanceLabel.setText("");
        accountTypeLabel.setText("");
        transactionHistoryLabel.setText("");
    }
}
