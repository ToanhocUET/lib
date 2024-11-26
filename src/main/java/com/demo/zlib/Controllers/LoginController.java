package com.demo.zlib.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class LoginController {
    @FXML
    public Label welcome_lbl;
    @FXML
    public Label account_lbl;
    @FXML
    public TextField account_fid;
    @FXML
    public Label pass_lbl;
    @FXML
    public PasswordField pass_fid;
    @FXML
    public Button login_btn;

    // Dummy data for testing purposes (you can replace this with database validation)
    private final Map<String, String> users = new HashMap<>();

    // Initialize the dummy user data
    @FXML
    public void initialize() {
        users.put("admin", "123456"); // Username: admin, Password: 123456
        users.put("user", "password"); // Username: user, Password: password
    }

    @FXML
    private void handleLoginButton(ActionEvent event) {
        String username = account_fid.getText().trim();
        String password = pass_fid.getText().trim();

        // Validate the input
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both username and password.", Alert.AlertType.ERROR);
            return;
        }

        // Check if the username and password are correct
        if (users.containsKey(username) && users.get(username).equals(password)) {
            // Login successful
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/AdminMenu.fxml"));
                Parent adminMenuRoot = loader.load();
                Scene adminMenuScene = new Scene(adminMenuRoot);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(adminMenuScene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Unable to load Admin Menu.", Alert.AlertType.ERROR);
            }
        } else {
            // Login failed
            showAlert("Error", "Invalid username or password.", Alert.AlertType.ERROR);
        }
    }

    // Helper method to show alert dialogs
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
