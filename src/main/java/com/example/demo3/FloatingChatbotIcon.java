package com.example.demo3;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the floating chatbot icon component
 * Manages the floating icon display, animations, and chat window visibility
 */
public class FloatingChatbotIcon extends StackPane implements Initializable {
    
    @FXML
    private Button chatbotIcon;
    
    @FXML
    private Label iconLabel;
    
    private ChatWindow chatWindow;
    private boolean isChatVisible = false;
    private Timeline pulseAnimation;
    private MainController mainController;
    
    public FloatingChatbotIcon() {
        loadFXML();
    }
    
    private void loadFXML() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("floating-chatbot.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupIcon();
        setupAnimations();
        setupEventHandlers();
    }
    
    private void setupIcon() {
        // Set up the chatbot icon styling
        chatbotIcon.getStyleClass().add("floating-chatbot-icon");
        iconLabel.setText("🤖");
        iconLabel.getStyleClass().add("chatbot-icon-emoji");
        
        // Position the floating icon
        this.setTranslateX(24); // 24px from left edge
        this.setTranslateY(-24); // 24px from bottom edge
        this.getStyleClass().add("floating-chatbot-container");
    }
    
    private void setupAnimations() {
        // Create subtle pulse animation
        pulseAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(chatbotIcon.scaleXProperty(), 1.0),
                new KeyValue(chatbotIcon.scaleYProperty(), 1.0)
            ),
            new KeyFrame(Duration.seconds(1.5), 
                new KeyValue(chatbotIcon.scaleXProperty(), 1.05),
                new KeyValue(chatbotIcon.scaleYProperty(), 1.05)
            ),
            new KeyFrame(Duration.seconds(3.0), 
                new KeyValue(chatbotIcon.scaleXProperty(), 1.0),
                new KeyValue(chatbotIcon.scaleYProperty(), 1.0)
            )
        );
        pulseAnimation.setCycleCount(Timeline.INDEFINITE);
        pulseAnimation.play();
    }
    
    private void setupEventHandlers() {
        chatbotIcon.setOnAction(event -> toggleChatWindow());
        
        // Enhanced hover effects
        chatbotIcon.setOnMouseEntered(event -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), chatbotIcon);
            scaleUp.setToX(1.1);
            scaleUp.setToY(1.1);
            scaleUp.play();
            
            // Pause pulse animation during hover
            pulseAnimation.pause();
        });
        
        chatbotIcon.setOnMouseExited(event -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), chatbotIcon);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
            
            // Resume pulse animation
            pulseAnimation.play();
        });
    }
    
    @FXML
    private void toggleChatWindow() {
        if (isChatVisible) {
            hideChatWindow();
        } else {
            showChatWindow();
        }
    }
    
    private void showChatWindow() {
        if (chatWindow == null) {
            chatWindow = new ChatWindow();
            chatWindow.setFloatingChatbotIcon(this);
        }
        
        // Add chat window to the main container
        if (mainController != null) {
            mainController.showFloatingChatWindow(chatWindow);
        }
        
        // Animate chat window appearance
        chatWindow.slideUp();
        isChatVisible = true;
        
        // Update icon appearance
        chatbotIcon.getStyleClass().add("chat-active");
        pulseAnimation.stop();
    }
    
    private void hideChatWindow() {
        if (chatWindow != null) {
            chatWindow.slideDown(() -> {
                if (mainController != null) {
                    mainController.hideFloatingChatWindow();
                }
                isChatVisible = false;
                
                // Reset icon appearance
                chatbotIcon.getStyleClass().remove("chat-active");
                pulseAnimation.play();
            });
        }
    }
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    public boolean isChatVisible() {
        return isChatVisible;
    }
    
    public void closeChatWindow() {
        hideChatWindow();
    }
    
    /**
     * Method to handle click outside chat window to close it
     */
    public void handleClickOutside() {
        if (isChatVisible) {
            hideChatWindow();
        }
    }
    
    /**
     * Cleanup method to stop animations when component is destroyed
     */
    public void cleanup() {
        if (pulseAnimation != null) {
            pulseAnimation.stop();
        }
    }
}