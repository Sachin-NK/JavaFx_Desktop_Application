package com.example.demo3;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the sliding chat window component
 * Manages chat window animations, message display, and user interactions
 */
public class ChatWindow extends VBox implements Initializable {
    
    @FXML
    private VBox chatWindowContainer;
    
    @FXML
    private HBox headerBar;
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private Button closeButton;
    
    @FXML
    private ScrollPane chatScrollPane;
    
    @FXML
    private VBox messageContainer;
    
    @FXML
    private HBox inputContainer;
    
    @FXML
    private TextField messageInput;
    
    @FXML
    private Button sendButton;
    
    private FloatingChatbotIcon floatingChatbotIcon;
    private ChatbotController chatbotController;
    private boolean isVisible = false;
    
    public ChatWindow() {
        loadFXML();
    }
    
    private void loadFXML() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("chat-window.fxml"));
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
        setupChatWindow();
        setupEventHandlers();
        setupInitialState();
        initializeChatbotController();
    }
    
    private void setupChatWindow() {
        // Apply styling
        this.getStyleClass().add("chat-window-enhanced");
        headerBar.getStyleClass().add("chat-header");
        titleLabel.setText("Smart Fishing Assistant 🤖");
        titleLabel.getStyleClass().add("chat-title");
        
        // Setup close button
        closeButton.setText("✕");
        closeButton.getStyleClass().add("chat-close-btn");
        
        // Setup message container
        messageContainer.getStyleClass().add("chat-messages");
        chatScrollPane.getStyleClass().add("chat-scroll");
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // Setup input area
        inputContainer.getStyleClass().add("chat-input-container");
        messageInput.getStyleClass().add("chat-input");
        messageInput.setPromptText("Ask about weather, fish prices, or fishing tips...");
        sendButton.getStyleClass().add("chat-send-btn");
        sendButton.setText("Send");
        
        // Set initial size
        this.setPrefWidth(380);
        this.setPrefHeight(500);
        this.setMaxWidth(380);
        this.setMaxHeight(500);
    }
    
    private void setupEventHandlers() {
        // Close button handler
        closeButton.setOnAction(event -> slideDown(null));
        
        // Send button handler
        sendButton.setOnAction(event -> sendMessage());
        
        // Enter key handler for message input
        messageInput.setOnAction(event -> sendMessage());
        
        // Auto-scroll to bottom when new messages are added
        messageContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            chatScrollPane.setVvalue(1.0);
        });
    }
    
    private void setupInitialState() {
        // Initially hidden and positioned at bottom
        this.setOpacity(0);
        this.setTranslateY(500); // Start below visible area
        this.setVisible(false);
        
        // Add welcome message
        addWelcomeMessage();
    }
    
    private void initializeChatbotController() {
        // Initialize the chatbot controller for handling messages
        try {
            chatbotController = new ChatbotController();
            // Note: ChatbotController integration will be handled separately
        } catch (Exception e) {
            System.err.println("Could not initialize chatbot controller: " + e.getMessage());
        }
    }
    
    private void addWelcomeMessage() {
        addBotMessage("Hello! I'm your Smart Fishing Assistant. I can help you with:\n\n" +
                     "🌤️ Weather information\n" +
                     "🐟 Fish prices and market data\n" +
                     "🎣 Fishing tips and advice\n" +
                     "📊 Dashboard insights\n\n" +
                     "What would you like to know?");
    }
    
    public void slideUp() {
        this.setVisible(true);
        isVisible = true;
        
        // Create slide up animation
        Timeline slideAnimation = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(this.opacityProperty(), 0),
                new KeyValue(this.translateYProperty(), 500)
            ),
            new KeyFrame(Duration.millis(300),
                new KeyValue(this.opacityProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(this.translateYProperty(), 0, Interpolator.EASE_OUT)
            )
        );
        
        slideAnimation.play();
        
        // Focus on input field after animation
        slideAnimation.setOnFinished(event -> messageInput.requestFocus());
    }
    
    public void slideDown(Runnable onComplete) {
        // Create slide down animation
        Timeline slideAnimation = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(this.opacityProperty(), 1),
                new KeyValue(this.translateYProperty(), 0)
            ),
            new KeyFrame(Duration.millis(300),
                new KeyValue(this.opacityProperty(), 0, Interpolator.EASE_IN),
                new KeyValue(this.translateYProperty(), 500, Interpolator.EASE_IN)
            )
        );
        
        slideAnimation.setOnFinished(event -> {
            this.setVisible(false);
            isVisible = false;
            if (onComplete != null) {
                onComplete.run();
            }
        });
        
        slideAnimation.play();
    }
    
    @FXML
    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty()) {
            // Add user message to chat
            addUserMessage(message);
            
            // Clear input
            messageInput.clear();
            
            // Process message with simple responses for now
            processUserMessage(message);
        }
    }
    
    public void addUserMessage(String message) {
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("user-message-enhanced");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(280);
        
        HBox messageBox = new HBox();
        messageBox.getStyleClass().add("user-message-container");
        messageBox.getChildren().add(messageLabel);
        messageBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        
        messageContainer.getChildren().add(messageBox);
        
        // Animate message appearance
        animateMessageIn(messageBox);
    }
    
    public void addBotMessage(String message) {
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("bot-message-enhanced");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(280);
        
        HBox messageBox = new HBox();
        messageBox.getStyleClass().add("bot-message-container");
        messageBox.getChildren().add(messageLabel);
        messageBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        messageContainer.getChildren().add(messageBox);
        
        // Animate message appearance
        animateMessageIn(messageBox);
    }
    
    private void animateMessageIn(HBox messageBox) {
        // Start with message hidden
        messageBox.setOpacity(0);
        messageBox.setTranslateY(20);
        
        // Animate in
        Timeline animation = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(messageBox.opacityProperty(), 0),
                new KeyValue(messageBox.translateYProperty(), 20)
            ),
            new KeyFrame(Duration.millis(200),
                new KeyValue(messageBox.opacityProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(messageBox.translateYProperty(), 0, Interpolator.EASE_OUT)
            )
        );
        
        animation.play();
    }
    
    public void setFloatingChatbotIcon(FloatingChatbotIcon floatingChatbotIcon) {
        this.floatingChatbotIcon = floatingChatbotIcon;
    }
    
    public boolean isChatVisible() {
        return isVisible;
    }
    
    public void clearMessages() {
        messageContainer.getChildren().clear();
        addWelcomeMessage();
    }
    
    /**
     * Process user message and generate appropriate response
     */
    private void processUserMessage(String message) {
        // Simple chatbot responses based on keywords
        String response = generateResponse(message.toLowerCase());
        
        // Add a small delay to simulate processing
        Timeline delay = new Timeline(new KeyFrame(Duration.millis(500), e -> addBotMessage(response)));
        delay.play();
    }
    
    /**
     * Generate response based on user message
     */
    private String generateResponse(String message) {
        if (message.contains("weather") || message.contains("rain") || message.contains("sunny")) {
            return "🌤️ For weather information, you can check the Weather section in the sidebar. I can help you understand current conditions and forecasts for your fishing trips!";
        } else if (message.contains("price") || message.contains("fish") || message.contains("market")) {
            return "🐟 Fish prices are updated daily on the dashboard. Check the 'Today Max Price' card for current market rates. Would you like to know about specific fish types?";
        } else if (message.contains("tip") || message.contains("advice") || message.contains("fishing")) {
            return "🎣 Here are some fishing tips:\n• Early morning and late evening are the best times\n• Check weather conditions before heading out\n• Use fresh bait for better results\n• Be patient and quiet near the water";
        } else if (message.contains("hello") || message.contains("hi") || message.contains("hey")) {
            return "Hello! 👋 I'm here to help you with fishing-related questions. Ask me about weather, fish prices, or fishing tips!";
        } else if (message.contains("help") || message.contains("what") || message.contains("how")) {
            return "I can help you with:\n\n🌤️ Weather forecasts\n🐟 Fish market prices\n🎣 Fishing tips and advice\n📊 Dashboard data explanation\n\nWhat would you like to know more about?";
        } else {
            return "That's an interesting question! While I'm still learning, I can help you with weather information, fish prices, and fishing tips. Try asking me about those topics! 🤖";
        }
    }
    
    /**
     * Handle click outside to close chat window
     */
    public void handleClickOutside() {
        slideDown(null);
    }
}