package com.example.demo3;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Locale;

public class ChatbotController {

    @FXML
    private VBox chatContainer;

    @FXML
    private TextField userInput;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isEmpty()) return;

        addMessage("You: " + input, "user-message");

        String response = getResponse(input.toLowerCase(Locale.ROOT));
        addMessage("Bot: " + response, "bot-message");

        userInput.clear();

        scrollPane.setVvalue(1.0); // Scroll to bottom
    }

    private void addMessage(String message, String styleClass) {
        Label label = new Label(message);
        label.getStyleClass().add(styleClass);
        label.setWrapText(true);
        chatContainer.getChildren().add(label);
    }

    private String getResponse(String input) {
        if (input.contains("weather") || input.contains("sea") || input.contains("wave")) {
            return "The sea is calm today 🌊\nTemperature: 26°C\nWind: Low\nGood day for fishing!";
        } else if (input.contains("tuna")) {
            return "Tuna 🐟\nPrice: $15/kg\nA popular fish, rich in protein and Omega-3.";
        } else if (input.contains("salmon")) {
            return "Salmon 🐠\nPrice: $20/kg\nKnown for its pink flesh and high nutritional value.";
        } else if (input.contains("mackerel")) {
            return "Mackerel 🐟\nPrice: $10/kg\nAffordable and rich in oil.";
        } else {
            return "Sorry, I couldn't find that. Try asking about sea weather or a fish name!";
        }
    }
}

