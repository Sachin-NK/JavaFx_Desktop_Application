
package com.example.demo3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static Connection.DBConnection.getConnection;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static Connection.DBConnection.getConnection;

public class LogingController {
    @FXML
    private VBox loginVBox;

    @FXML
    private TextField userdetails;

    @FXML
    private PasswordField pwd;  // use PasswordField for secure input

    @FXML
    private Button login;

    @FXML
    private ImageView img;

    @FXML
    private Button signup;

    @FXML
    private void handleSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signup.fxml"));
            Parent signupRoot = loader.load();
            Stage stage = (Stage) signup.getScene().getWindow();
            stage.setScene(new Scene(signupRoot));
            stage.setMaximized(false);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to open signup form.");
        }
    }


    public void initialize() {
        // Fade-in animation for login form
        if (loginVBox != null) {
            loginVBox.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.seconds(1.2), loginVBox);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }

    }

    @FXML
    public void handleLogin() {
        String uname = userdetails.getText();
        String pwds = pwd.getText();



        try (Connection connection = getConnection()) {
            // Try admin table first
            String sqlAdmin = "SELECT COUNT(*) FROM admin WHERE firstname = ? AND password = ?";
            PreparedStatement stmtAdmin = connection.prepareStatement(sqlAdmin);
            stmtAdmin.setString(1, uname);
            stmtAdmin.setString(2, pwds);
            ResultSet rsAdmin = stmtAdmin.executeQuery();

            boolean isAdmin = rsAdmin.next() && rsAdmin.getInt(1) == 1;

            // Try user table if not admin
            boolean isUser = false;
            if (!isAdmin) {
                String sqlUser = "SELECT COUNT(*) FROM user WHERE name = ? AND password = ?";
                PreparedStatement stmtUser = connection.prepareStatement(sqlUser);
                stmtUser.setString(1, uname);
                stmtUser.setString(2, pwds);
                ResultSet rsUser = stmtUser.executeQuery();
                isUser = rsUser.next() && rsUser.getInt(1) == 1;
            }

            if (isAdmin || isUser) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
                Parent loginRoot = loader.load();
                MainController mainController = loader.getController();
                mainController.setUsername(uname);
                Stage stage = (Stage) login.getScene().getWindow();
                stage.setScene(new Scene(loginRoot));
                stage.setMaximized(true);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred during login.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
