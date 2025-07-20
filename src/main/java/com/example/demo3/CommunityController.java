
package com.example.demo3;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class CommunityController implements Initializable {

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private VBox messagesContainer;

    @FXML
    private TextField messageInput;

    @FXML
    private Button sendButton;

    // Real-time simulation components
    private Timeline realTimeUpdater;
    private Random random = new Random();
    private int messageCounter = 0;

    // Sample users and messages for real-time simulation
    private final String[] sampleUsers = {
            "Priya Mendis", "Chaminda Perera", "Lakmal Silva", "Nishani Fernando",
            "Roshan Jayawardena", "Sanduni Wickrama", "Thilak Rajapaksa", "Malini Gunasekara"
    };

    private final String[] sampleMessages = {
            "Great fishing conditions today at Mirissa!",
            "Fish market prices updated - check the latest rates",
            "Weather looks good for tomorrow's trip",
            "Anyone seen the new fishing regulations?",
            "Sharing some tips about net maintenance",
            "Best spots for tuna fishing this season",
            "Community meeting scheduled for next week",
            "New boat equipment available at the harbor",
            "Safety reminder: always check weather before heading out",
            "Fresh catch available at reasonable prices"
    };

    private final String[] avatarClasses = {
            "avatar-blue", "avatar-green", "avatar-orange", "avatar-purple", "avatar-teal"
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Auto-scroll to bottom when new messages are added
        chatScrollPane.vvalueProperty().bind(messagesContainer.heightProperty());

        // Enable send button only when there's text
        sendButton.disableProperty().bind(messageInput.textProperty().isEmpty());

        // Allow Enter key to send message
        messageInput.setOnAction(e -> sendMessage());

        // Start real-time message simulation
        startRealTimeUpdates();
    }

    @FXML
    private void sendMessage() {
        String messageText = messageInput.getText().trim();
        if (!messageText.isEmpty()) {
            addMessage("You", messageText, "now", "avatar-current-user");
            messageInput.clear();
        }
    }

    private void addMessage(String username, String message, String timestamp, String avatarClass) {
        // Create message container
        HBox messageItem = new HBox();
        messageItem.getStyleClass().add("message-item");
        messageItem.setSpacing(15);

        // Create avatar
        Label avatar = new Label(username.substring(0, 1).toUpperCase());
        avatar.getStyleClass().addAll("avatar", avatarClass);

        // Create message content
        VBox messageContent = new VBox();
        messageContent.setSpacing(5);

        // Username and timestamp
        HBox userInfo = new HBox();
        userInfo.setSpacing(10);

        Label usernameLabel = new Label(username);
        usernameLabel.getStyleClass().add("username");

        Label timestampLabel = new Label(timestamp);
        timestampLabel.getStyleClass().add("timestamp");

        userInfo.getChildren().addAll(usernameLabel, timestampLabel);

        // Message text
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("message-text");
        messageLabel.setWrapText(true);

        messageContent.getChildren().addAll(userInfo, messageLabel);
        messageItem.getChildren().addAll(avatar, messageContent);

        // Add to messages container
        messagesContainer.getChildren().add(messageItem);

        // Auto-scroll to bottom
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }

    /**
     * Start real-time message simulation
     */
    private void startRealTimeUpdates() {
        // Create timeline that triggers every 15-45 seconds
        realTimeUpdater = new Timeline(
                new KeyFrame(Duration.seconds(getRandomInterval()), e -> simulateIncomingMessage()));
        realTimeUpdater.setCycleCount(Timeline.INDEFINITE);
        realTimeUpdater.play();
    }

    /**
     * Stop real-time updates (useful when leaving the community page)
     */
    public void stopRealTimeUpdates() {
        if (realTimeUpdater != null) {
            realTimeUpdater.stop();
        }
    }

    /**
     * Simulate an incoming message from a random user
     */
    private void simulateIncomingMessage() {
        Platform.runLater(() -> {
            String randomUser = sampleUsers[random.nextInt(sampleUsers.length)];
            String randomMessage = sampleMessages[random.nextInt(sampleMessages.length)];
            String randomAvatarClass = avatarClasses[random.nextInt(avatarClasses.length)];

            // Generate realistic timestamp
            String timestamp = generateRealisticTimestamp();

            addMessage(randomUser, randomMessage, timestamp, randomAvatarClass);

            // Schedule next message
            realTimeUpdater.stop();
            realTimeUpdater = new Timeline(
                    new KeyFrame(Duration.seconds(getRandomInterval()), e -> simulateIncomingMessage()));
            realTimeUpdater.play();
        });
    }

    /**
     * Generate random interval between messages (15-45 seconds)
     */
    private double getRandomInterval() {
        return 15 + (random.nextDouble() * 30); // 15-45 seconds
    }

    /**
     * Generate realistic timestamp for incoming messages
     */
    private String generateRealisticTimestamp() {
        String[] timestamps = { "just now", "1 min ago", "2 min ago", "3 min ago" };
        return timestamps[random.nextInt(timestamps.length)];
    }

    /**
     * Add a real-time notification system
     */
    public void addRealTimeMessage(String username, String message) {
        Platform.runLater(() -> {
            String avatarClass = avatarClasses[random.nextInt(avatarClasses.length)];
            addMessage(username, message, "just now", avatarClass);
        });
    }
}