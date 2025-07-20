
package com.example.demo3;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static Connection.DBConnection.getConnection;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class SignupController {
    @FXML
    private VBox signupVBox;
    @FXML
    private javafx.scene.control.Button backToLoginButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    public void initialize() {
        // Fade-in animation for signup form
        if (signupVBox != null) {
            signupVBox.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.seconds(1.2), signupVBox);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }

    @FXML
    private void handleSignup() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields are required.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
            return;
        }
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO user (name, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully!");
                usernameField.clear();
                passwordField.clear();
                confirmPasswordField.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create account.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("loging.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setMaximized(false);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to open login form.");
        }
    }
}
