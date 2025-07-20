package com.example.demo3;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.io.IOException;
import java.util.Optional;

public class MainController {

    public Button lgout;
    public Label user;
    
    @FXML
    private StackPane mainContent;
    
    @FXML
    private AnchorPane rootContainer;
    
    private FloatingChatbotIcon floatingChatbotIcon;
    private ChatWindow currentChatWindow;
    
    // Navigation buttons for active state management
    @FXML private Button homeBtn;
    @FXML private Button weatherBtn;
    @FXML private Button messageBtn;
    @FXML private Button communityBtn;
    @FXML private Button usersBtn;
    @FXML private Button printBtn;

    public void initialize() {
        openHome();
        // initializeFloatingChatbot(); // Removed floating chatbot
    }
    
    private void initializeFloatingChatbot() {
        // Create floating chatbot icon
        floatingChatbotIcon = new FloatingChatbotIcon();
        floatingChatbotIcon.setMainController(this);
        
        // Position the floating chatbot in bottom-RIGHT corner (away from navigation)
        if (rootContainer != null) {
            AnchorPane.setBottomAnchor(floatingChatbotIcon, 24.0);
            AnchorPane.setRightAnchor(floatingChatbotIcon, 24.0);
            rootContainer.getChildren().add(floatingChatbotIcon);
        } else {
            // Fallback: add to main content if rootContainer is not available
            StackPane.setAlignment(floatingChatbotIcon, Pos.BOTTOM_RIGHT);
            floatingChatbotIcon.setTranslateX(-24);
            floatingChatbotIcon.setTranslateY(-24);
            mainContent.getChildren().add(floatingChatbotIcon);
        }
    }

    @FXML
    private void openHome() {
        setActiveButton(homeBtn);
        loadPage("/com/example/demo3/HomePage.fxml");
    }

    @FXML
    private void openWeather() {
        setActiveButton(weatherBtn);
        loadPage("/com/example/demo3/weathepage.fxml");
    }
    
    @FXML
    private void openMessage(){
        setActiveButton(messageBtn);
        loadPage("/com/example/demo3/message.fxml");
    }

    @FXML
    private void openCommunity() {
        setActiveButton(communityBtn);
        loadPage("/com/example/demo3/community.fxml");
    }

    @FXML
    private void openChatbot(){
        loadPage("/com/example/demo3/chatbot.fxml");
    }

    @FXML


    private void loadPage(String fxmlPath) {
        try {
            Parent newPage = FXMLLoader.load(getClass().getResource(fxmlPath));
            
            // Get current page if exists
            Node currentPage = mainContent.getChildren().isEmpty() ? null : mainContent.getChildren().get(0);
            
            // Apply smooth transition
            TransitionManager.fadeTransition(mainContent, currentPage, newPage, 
                TransitionManager.getDuration("normal"), null);
                
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to direct loading if transition fails
            try {
                Parent page = FXMLLoader.load(getClass().getResource(fxmlPath));
                mainContent.getChildren().setAll(page);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void openUser(ActionEvent event) {
        setActiveButton(usersBtn);
        loadPage("/com/example/demo3/user.fxml");
    }

    @FXML
    private void openPrint() {
        setActiveButton(printBtn);
        
        // For now, show a simple print dialog or placeholder
        // You can later implement actual printing functionality
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Function");
        alert.setHeaderText("Print Feature");
        alert.setContentText("Print functionality will be implemented here.\nThis could include:\n- Printing reports\n- Printing user data\n- Printing charts and analytics");
        alert.showAndWait();
        
        // Uncomment below if you create a print.fxml file
        // loadPage("/com/example/demo3/print.fxml");
    }
    
    /**
     * Set the active navigation button and remove active state from others
     */
    private void setActiveButton(Button activeButton) {
        // Remove active class from all buttons
        Button[] navButtons = {homeBtn, weatherBtn, messageBtn, communityBtn, usersBtn, printBtn};
        
        for (Button btn : navButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-btn-active");
                if (!btn.getStyleClass().contains("sidebar-btn")) {
                    btn.getStyleClass().add("sidebar-btn");
                }
            }
        }
        
        // Add active class to the clicked button
        if (activeButton != null) {
            activeButton.getStyleClass().add("sidebar-btn-active");
        }
    }


    public void confirmLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            logout();
        }
    }

    private void logout() {
        System.out.println("Logging out...");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("loging.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) lgout.getScene().getWindow();
            stage.setScene(new Scene(loginRoot,600,400));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void setUsername(String username) {
        user.setText("Logged User: " + username);
        System.out.println("Logged in user: " + username);
    }
    
    /**
     * Show the floating chat window
     */
    public void showFloatingChatWindow(ChatWindow chatWindow) {
        currentChatWindow = chatWindow;
        
        if (rootContainer != null) {
            // Position chat window in bottom-RIGHT, above the floating icon
            AnchorPane.setBottomAnchor(chatWindow, 90.0); // Above the 56px icon + 24px margin + 10px gap
            AnchorPane.setRightAnchor(chatWindow, 24.0);
            rootContainer.getChildren().add(chatWindow);
        } else {
            // Fallback: add to main content
            StackPane.setAlignment(chatWindow, Pos.BOTTOM_RIGHT);
            chatWindow.setTranslateX(-24);
            chatWindow.setTranslateY(-90);
            mainContent.getChildren().add(chatWindow);
        }
        
        // Set up click-outside-to-close functionality
        setupClickOutsideHandler(chatWindow);
    }
    
    /**
     * Hide the floating chat window
     */
    public void hideFloatingChatWindow() {
        if (currentChatWindow != null) {
            if (rootContainer != null) {
                rootContainer.getChildren().remove(currentChatWindow);
            } else {
                mainContent.getChildren().remove(currentChatWindow);
            }
            currentChatWindow = null;
        }
    }
    
    /**
     * Setup click outside handler to close chat window
     */
    private void setupClickOutsideHandler(ChatWindow chatWindow) {
        // Add mouse click handler to root container
        if (rootContainer != null) {
            rootContainer.setOnMouseClicked(event -> {
                // Check if click was outside the chat window and floating icon
                if (!chatWindow.getBoundsInParent().contains(event.getX(), event.getY()) &&
                    !floatingChatbotIcon.getBoundsInParent().contains(event.getX(), event.getY())) {
                    floatingChatbotIcon.handleClickOutside();
                }
            });
        } else {
            mainContent.setOnMouseClicked(event -> {
                if (!chatWindow.getBoundsInParent().contains(event.getX(), event.getY()) &&
                    !floatingChatbotIcon.getBoundsInParent().contains(event.getX(), event.getY())) {
                    floatingChatbotIcon.handleClickOutside();
                }
            });
        }
    }
    
    /**
     * Get the floating chatbot icon (for external access if needed)
     */
    public FloatingChatbotIcon getFloatingChatbotIcon() {
        return floatingChatbotIcon;
    }
    
    /**
     * Check if chat window is currently visible
     */
    public boolean isChatWindowVisible() {
        return currentChatWindow != null && currentChatWindow.isChatVisible();
    }
}
