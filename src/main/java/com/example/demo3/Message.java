package com.example.demo3;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;
import java.util.Optional;
import java.util.Random;

import static Connection.DBConnection.getConnection;

public class Message {
    public String sendname;
    @FXML public Button closebtn;
    @FXML private VBox messageContainer;
    @FXML private VBox userListVBox;
    @FXML private Button btn;
    @FXML private TextField txt;
    public String name;
    
    // Real-time update components
    private Timeline realTimeUpdater;
    private Timeline userListUpdater;
    private int lastMessageCount = 0;
    private Random random = new Random();
    
    // Sample messages for simulation
    private final String[] sampleMessages = {
        "Hey, how's the fishing today?",
        "Weather looks good for tomorrow",
        "Did you check the new regulations?",
        "Great catch yesterday!",
        "Meeting at 3 PM today",
        "Fish prices are up this week",
        "New equipment arrived at the harbor",
        "Safety first - check your gear!"
    };

    @FXML
    public void initialize() {
        loadUserList();
        startRealTimeUpdates();
    }



    public void loadUserList() {
        userListVBox.getChildren().clear();
        String sql = "SELECT DISTINCT sender_username FROM message";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String senderName = rs.getString("sender_username");

                Label userLabel = new Label(senderName);
                userLabel.getStyleClass().add("list-item");
                userLabel.setMaxWidth(Double.MAX_VALUE);
                VBox.setVgrow(userLabel, Priority.ALWAYS);
                userLabel.setAlignment(Pos.CENTER_LEFT);

                userLabel.setOnMouseClicked(event -> {
                    loadMessages(senderName);
                    name = senderName;
                });
                userListVBox.getChildren().add(userLabel);
                userListVBox.getStyleClass().add("userlistvbox");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadMessages(String selectedUser) {
        sendname = selectedUser;
        btn.setVisible(true);
        txt.setVisible(true);
        messageContainer.getChildren().clear();
        messageContainer.getStyleClass().add("messageContainer");
        String sql = "SELECT * FROM message WHERE sender_username = ? OR resiver_name = ? ORDER BY timestamp";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, selectedUser);
            stmt.setString(2, selectedUser);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String senderMessage = rs.getString("senser_message");
                String userMessage = rs.getString("usermessage");

                if (senderMessage != null && !senderMessage.isBlank()) {
                    createMessageBubble(senderMessage, false);
                }
                if (userMessage != null && !userMessage.isBlank()) {
                    createMessageBubble(userMessage, true);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createMessageBubble(String message, boolean isUserMessage) {
        HBox messageBox = new HBox();
        messageBox.setPrefWidth(Double.MAX_VALUE);
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add(isUserMessage ? "user-message" : "sender-message");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (isUserMessage) {
            messageBox.getChildren().addAll(spacer, messageLabel);
        } else {
            messageBox.getChildren().addAll(messageLabel, spacer);
        }

        messageContainer.getChildren().add(messageBox);
    }

    @FXML
    public void insertData() {
        String text = txt.getText().trim();
        if (text.isEmpty() || sendname == null) return;

        String sql = "INSERT INTO message (sender_username, resiver_name, usermessage) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sendname);
            stmt.setString(2, "Numidu");
            stmt.setString(3, text);
            stmt.executeUpdate();
            txt.clear();
            loadMessages(sendname);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New User");
        dialog.setHeaderText("Enter the sender's name:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            if (!name.isBlank()) {
                String sql = "INSERT INTO message (sender_username, resiver_name, usermessage) VALUES (?, ?, '')";

                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setString(1, name);
                    stmt.setString(2, "Numidu");
                    stmt.executeUpdate();
                    loadUserList();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void deleteUser() {
        if (name == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this user?");
        alert.setContentText("User: " + name);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM message WHERE sender_username = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, name);
                stmt.executeUpdate();
                loadUserList();
                messageContainer.getChildren().clear();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void SwitchToMainMenu() {
        // Stop real-time updates when leaving
        stopRealTimeUpdates();
        
        try {
            Stage oldStage = (Stage) closebtn.getScene().getWindow();
            oldStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo3/main.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setMaximized(true);
            newStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Start real-time updates for messages and user list
     */
    private void startRealTimeUpdates() {
        // Check for new messages every 5 seconds
        realTimeUpdater = new Timeline(
            new KeyFrame(Duration.seconds(5), e -> checkForNewMessages())
        );
        realTimeUpdater.setCycleCount(Timeline.INDEFINITE);
        realTimeUpdater.play();
        
        // Update user list every 10 seconds
        userListUpdater = new Timeline(
            new KeyFrame(Duration.seconds(10), e -> Platform.runLater(this::loadUserList))
        );
        userListUpdater.setCycleCount(Timeline.INDEFINITE);
        userListUpdater.play();
        
        // Simulate incoming messages every 20-60 seconds
        simulateIncomingMessages();
    }
    
    /**
     * Stop all real-time updates
     */
    private void stopRealTimeUpdates() {
        if (realTimeUpdater != null) {
            realTimeUpdater.stop();
        }
        if (userListUpdater != null) {
            userListUpdater.stop();
        }
    }
    
    /**
     * Check for new messages in the database
     */
    private void checkForNewMessages() {
        if (sendname == null) return;
        
        Platform.runLater(() -> {
            String sql = "SELECT COUNT(*) FROM message WHERE sender_username = ? OR resiver_name = ?";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, sendname);
                stmt.setString(2, sendname);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int currentCount = rs.getInt(1);
                    if (currentCount > lastMessageCount) {
                        loadMessages(sendname); // Reload messages if new ones found
                        lastMessageCount = currentCount;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Simulate incoming messages for demonstration
     */
    private void simulateIncomingMessages() {
        Timeline simulator = new Timeline(
            new KeyFrame(Duration.seconds(20 + random.nextDouble() * 40), e -> {
                // Simulate a new message from a random user
                simulateNewMessage();
                // Schedule next simulation
                simulateIncomingMessages();
            })
        );
        simulator.play();
    }
    
    /**
     * Simulate a new incoming message
     */
    private void simulateNewMessage() {
        if (sendname == null) return;
        
        String randomMessage = sampleMessages[random.nextInt(sampleMessages.length)];
        
        // Insert simulated message into database
        String sql = "INSERT INTO message (sender_username, resiver_name, senser_message) VALUES (?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sendname);
            stmt.setString(2, "Numidu");
            stmt.setString(3, randomMessage);
            stmt.executeUpdate();
            
            // Refresh messages on UI thread
            Platform.runLater(() -> loadMessages(sendname));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Add real-time message notification (can be called from external sources)
     */
    public void addRealTimeMessage(String username, String message, boolean isFromUser) {
        Platform.runLater(() -> {
            if (sendname != null && sendname.equals(username)) {
                createMessageBubble(message, isFromUser);
            }
        });
    }
}
