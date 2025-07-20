package com.example.demo3;

import Model.FishPrice;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static Connection.DBConnection.getConnection;

public class HomeController {

    public VBox pop_upvbox;
    public TextField text_field;
    public Label lbelname;
    public Label lbelprice;
    @FXML
    private Label boatsValueLabel;
    @FXML
    private Label tripsValueLabel;
    @FXML
    private Label fishermenValueLabel;
    @FXML
    private LineChart<String, Number> fishPriceChart;
    @FXML
    private ComboBox<String> fishTypeCombo;
    @FXML
    private ComboBox<String> timeRangeCombo;
    @FXML
    private TableView<FishPrice> agreementTable;
    
    // Weather UI elements
    @FXML private HBox weatherAlertBanner;
    @FXML private Label alertIcon;
    @FXML private Label alertTitle;
    @FXML private Label alertSubtitle;
    @FXML private Label weatherIcon;
    @FXML private Label temperatureLabel;
    @FXML private Label conditionLabel;
    @FXML private Label windSpeedLabel;
    @FXML private Label waveHeightLabel;
    @FXML private Label visibilityLabel;
    @FXML private Label humidityLabel;
    
    // Real-time weather components
    private Timeline weatherUpdater;
    private Random random = new Random();
    
    // Weather data for real-time updates
    private double currentTemperature = 28.0;
    private String currentCondition = "Partly Cloudy";
    private double currentWindSpeed = 12.0;
    private double currentWaveHeight = 1.2;
    private double currentVisibility = 10.0;
    private int currentHumidity = 75;
    
    // Weather alert thresholds
    private static final double WAVE_DANGER_THRESHOLD = 3.0;
    private static final double WIND_DANGER_THRESHOLD = 25.0;
    private static final double VISIBILITY_DANGER_THRESHOLD = 2.0;
    
    // Sample weather conditions and alerts
    private final String[] weatherConditions = {
        "Clear", "Partly Cloudy", "Cloudy", "Light Rain", "Heavy Rain", 
        "Thunderstorm", "Windy", "Calm Seas", "Rough Seas"
    };
    
    private final String[] weatherAlerts = {
        "Weather Alert: Strong winds expected in Southern waters",
        "Weather Alert: High waves detected - Exercise caution",
        "Weather Alert: Heavy rainfall expected - Check equipment", 
        "Weather Alert: Thunderstorm approaching - Return to harbor",
        "Weather Alert: Excellent fishing conditions - Clear skies ahead",
        "Weather Alert: Calm seas reported - Perfect for small boats",
        "Weather Alert: Visibility reduced due to fog - Use navigation aids"
    };

    @FXML
    private TableColumn<FishPrice, Integer> colID;
    @FXML
    private TableColumn<FishPrice, String> colFishName;
    @FXML
    private TableColumn<FishPrice, String> colFishDetails;
    @FXML
    private TableColumn<FishPrice, Double> colTodayPrice;
    @FXML
    private TableColumn<FishPrice, String> colPlace;
    @FXML
    private TableColumn<FishPrice, String> colStatus;
    @FXML
    private TableColumn<FishPrice, LocalDate> colDate;

    public void initialize() {
        // Initialize dropdowns
        setupDropdowns();
        
        // Initialize chart with multiple series
        setupEnhancedChart();
        
        addButtonToTable(); // after setting items

        // Table column bindings
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFishName.setCellValueFactory(new PropertyValueFactory<>("fishName"));
        colFishDetails.setCellValueFactory(new PropertyValueFactory<>("fishDetails"));
        colTodayPrice.setCellValueFactory(new PropertyValueFactory<>("todayPrice"));
        colPlace.setCellValueFactory(new PropertyValueFactory<>("place"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Load data
        loadFishPriceData();
        text_field.textProperty().addListener((observable, oldValue, newValue) -> {
            searchData();
        });
        adddata();
        
        // Start real-time weather updates
        startRealTimeWeatherUpdates();
    }
    
    private void setupDropdowns() {
        // Setup fish type dropdown
        ObservableList<String> fishTypes = FXCollections.observableArrayList(
            "All Fish Types", "Salmon", "Tuna", "Mackerel", "Sardine", "Kingfish"
        );
        fishTypeCombo.setItems(fishTypes);
        fishTypeCombo.setValue("All Fish Types");
        
        // Setup time range dropdown
        ObservableList<String> timeRanges = FXCollections.observableArrayList(
            "Last 7 days", "Last 30 days", "Last 3 months", "Last 6 months", "Last year"
        );
        timeRangeCombo.setItems(timeRanges);
        timeRangeCombo.setValue("Last 3 months");
        
        // Add listeners for dropdown changes
        fishTypeCombo.setOnAction(e -> updateChart());
        timeRangeCombo.setOnAction(e -> updateChart());
    }
    
    private void setupEnhancedChart() {
        // Clear existing data
        fishPriceChart.getData().clear();
        
        // Create multiple series for different fish types
        XYChart.Series<String, Number> salmonSeries = new XYChart.Series<>();
        salmonSeries.setName("Salmon (LKR)");
        
        XYChart.Series<String, Number> tunaSeries = new XYChart.Series<>();
        tunaSeries.setName("Tuna (LKR)");
        
        XYChart.Series<String, Number> mackerelSeries = new XYChart.Series<>();
        mackerelSeries.setName("Mackerel (LKR)");
        
        // Sample data for smooth curves like in your image
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        
        // Salmon data (blue line) - higher prices with curves
        double[] salmonPrices = {850, 1200, 1450, 1300, 850, 1250, 1200};
        for (int i = 0; i < days.length; i++) {
            salmonSeries.getData().add(new XYChart.Data<>(days[i], salmonPrices[i]));
        }
        
        // Tuna data (green line) - medium prices, more stable
        double[] tunaPrices = {950, 980, 970, 950, 1000, 1020, 980};
        for (int i = 0; i < days.length; i++) {
            tunaSeries.getData().add(new XYChart.Data<>(days[i], tunaPrices[i]));
        }
        
        // Mackerel data (orange line) - lower prices, stable
        double[] mackerelPrices = {420, 430, 425, 435, 440, 445, 430};
        for (int i = 0; i < days.length; i++) {
            mackerelSeries.getData().add(new XYChart.Data<>(days[i], mackerelPrices[i]));
        }
        
        // Add series to chart
        fishPriceChart.getData().addAll(salmonSeries, tunaSeries, mackerelSeries);
        
        // Add tooltips to data points
        addTooltipsToSeries(salmonSeries, "Salmon");
        addTooltipsToSeries(tunaSeries, "Tuna");
        addTooltipsToSeries(mackerelSeries, "Mackerel");
    }
    
    private void addTooltipsToSeries(XYChart.Series<String, Number> series, String fishType) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            Tooltip tooltip = new Tooltip(fishType + " - " + data.getXValue() + ": LKR " + data.getYValue());
            Tooltip.install(data.getNode(), tooltip);
        }
    }
    
    private void updateChart() {
        String selectedFishType = fishTypeCombo.getValue();
        String selectedTimeRange = timeRangeCombo.getValue();
        
        // Clear and reload chart based on selections
        setupEnhancedChart();
        
        // Filter chart data based on fish type selection
        if (!"All Fish Types".equals(selectedFishType)) {
            fishPriceChart.getData().removeIf(series -> 
                !series.getName().toLowerCase().contains(selectedFishType.toLowerCase()));
        }
        
        // You can add time range filtering logic here based on your database structure
        System.out.println("Chart updated for: " + selectedFishType + " - " + selectedTimeRange);
    }

    @FXML
    public void openFromSidebar(javafx.event.ActionEvent event) {
       // WeatherController.loadWeatherPage((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    private void loadFishPriceData() {
        ObservableList<FishPrice> data = FXCollections.observableArrayList();
        String query = "SELECT * FROM fish_price";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = (ResultSet) stmt.executeQuery()) {

            while (rs.next()) {
                data.add(new FishPrice(
                        rs.getInt("id"),
                        rs.getString("fish_name"),
                        rs.getString("fish_details"),
                        rs.getDouble("today_price"),
                        rs.getString("place"),
                        rs.getString("status"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("image")

                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        agreementTable.setItems(data);
    }


    @FXML
    private TableColumn<FishPrice, Void> colAction;


    private void addButtonToTable() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("View");


            {
                btn.setOnAction(event -> {
                    FishPrice fish = getTableView().getItems().get(getIndex());
                    showFishDetailsPopup(fish);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void showFishDetailsPopup(FishPrice fish) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Fish Details");
        String imagePath = "/images/" + fish.getImage(); // image/ is your folder
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("popup-image");


        VBox layout = new VBox(10);




        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("popup-pane");
        layout.setAlignment(Pos.CENTER);


        Label name = new Label("Name: " + fish.getFishName());
        Label details = new Label("Details: " + fish.getFishDetails());
        Label price = new Label("Price: Rs. " + fish.getTodayPrice());
        Label place = new Label("Place: " + fish.getPlace());
        Label status = new Label("Status: " + fish.getStatus());
        Label date = new Label("Date: " + fish.getDate());


        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> popupStage.close());

        layout.getChildren().addAll(imageView,name, details, price, place, status, date, closeBtn);

        Scene scene = new Scene(layout, 300, 500);
        scene.getStylesheets().add(getClass().getResource("/com/example/demo3/style.css").toExternalForm());
        popupStage.setScene(scene);
        popupStage.showAndWait();

    }

    @FXML
    private void showChatbot() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo3/chatbot.fxml"));
            Parent root = loader.load();


            Scene scene = new Scene(root, 400, 500);
            scene.getStylesheets().add(getClass().getResource("/com/example/demo3/style.css").toExternalForm());


            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Chatbot Assistant");
            popupStage.setScene(scene);
            popupStage.setResizable(false); // Optional
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void searchData() {
        String input = text_field.getText().trim();

        // If the input is empty, load all data
        if (input.isEmpty()) {
            loadFishPriceData(); // This should load all data into the table
            return;
        }

        ObservableList<FishPrice> fishdata = FXCollections.observableArrayList();

        String sql = "SELECT * FROM fish_price WHERE fish_name LIKE ? OR today_price = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set name filter with LIKE for partial match
            stmt.setString(1, "%" + input + "%");

            try {
                double price = Double.parseDouble(input);
                stmt.setDouble(2, price);
            } catch (NumberFormatException e) {
                // If input is not a number, set a dummy price that will never match
                stmt.setDouble(2, -1);
            }

            ResultSet data = stmt.executeQuery();
            while (data.next()) {
                fishdata.add(new FishPrice(
                        data.getInt("id"),
                        data.getString("fish_name"),
                        data.getString("fish_details"),
                        data.getDouble("today_price"),
                        data.getString("place"),
                        data.getString("status"),
                        data.getDate("date").toLocalDate(),
                        data.getString("image")
                ));
            }

            agreementTable.setItems(fishdata);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adddata() {
        try (Connection conn = getConnection()) {
            String fishname = "";
            double fishprice = 0.0;

            // Fix: Replace 'name' and 'price' with your actual column names
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT fish_name, today_price FROM fish_price ORDER BY today_price DESC LIMIT 1"
            );

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                fishname = rs.getString("fish_name");
                fishprice = rs.getDouble("today_price");

                lbelname.setText(fishname);
                lbelprice.setText(String.format("%.2f", fishprice)); // Format price nicely
            } else {
                lbelname.setText("No Data");
                lbelprice.setText("0.00");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Counter methods for Active Boats
    @FXML
    private void increaseBoats() {
        // Find the boats value label and increment it
        updateCardValue("boats", 1);
    }

    @FXML
    private void decreaseBoats() {
        // Find the boats value label and decrement it
        updateCardValue("boats", -1);
    }

    // Counter methods for Fishing Trips
    @FXML
    private void increaseTrips() {
        updateCardValue("trips", 1);
    }

    @FXML
    private void decreaseTrips() {
        updateCardValue("trips", -1);
    }

    // Counter methods for Active Fishermen
    @FXML
    private void increaseFishermen() {
        updateCardValue("fishermen", 1);
    }

    @FXML
    private void decreaseFishermen() {
        updateCardValue("fishermen", -1);
    }

    // Helper method to update card values
    private void updateCardValue(String cardType, int change) {
        try {
            switch (cardType) {
                case "boats":
                    int currentBoats = Integer.parseInt(boatsValueLabel.getText());
                    int newBoats = Math.max(0, currentBoats + change); // Prevent negative values
                    boatsValueLabel.setText(String.valueOf(newBoats));
                    System.out.println("Boats count updated to: " + newBoats);
                    break;
                case "trips":
                    int currentTrips = Integer.parseInt(tripsValueLabel.getText());
                    int newTrips = Math.max(0, currentTrips + change); // Prevent negative values
                    tripsValueLabel.setText(String.valueOf(newTrips));
                    System.out.println("Trips count updated to: " + newTrips);
                    break;
                case "fishermen":
                    // Handle comma-separated numbers (e.g., "1,120")
                    String fishermenText = fishermenValueLabel.getText().replace(",", "");
                    int currentFishermen = Integer.parseInt(fishermenText);
                    int newFishermen = Math.max(0, currentFishermen + change); // Prevent negative values
                    // Format with comma for thousands
                    String formattedFishermen = String.format("%,d", newFishermen);
                    fishermenValueLabel.setText(formattedFishermen);
                    System.out.println("Fishermen count updated to: " + formattedFishermen);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Start real-time weather updates for the dashboard
     */
    private void startRealTimeWeatherUpdates() {
        // Update weather conditions every 2 minutes
        weatherUpdater = new Timeline(
            new KeyFrame(Duration.seconds(120), e -> updateWeatherConditions())
        );
        weatherUpdater.setCycleCount(Timeline.INDEFINITE);
        weatherUpdater.play();
        
        // Check for weather alerts every 45 seconds
        Timeline alertChecker = new Timeline(
            new KeyFrame(Duration.seconds(45), e -> checkAndShowWeatherAlerts())
        );
        alertChecker.setCycleCount(Timeline.INDEFINITE);
        alertChecker.play();
    }
    
    /**
     * Update weather conditions with simulated real-time data
     */
    private void updateWeatherConditions() {
        Platform.runLater(() -> {
            // Simulate weather changes
            currentTemperature += (random.nextDouble() - 0.5) * 4; // ±2°C change
            currentWindSpeed += (random.nextDouble() - 0.5) * 6;   // ±3 km/h change
            currentWaveHeight += (random.nextDouble() - 0.5) * 0.8; // ±0.4m change
            currentVisibility += (random.nextDouble() - 0.5) * 4;   // ±2km change
            currentHumidity += (int)((random.nextDouble() - 0.5) * 20); // ±10% change
            
            // Keep values within realistic ranges
            currentTemperature = Math.max(20, Math.min(35, currentTemperature));
            currentWindSpeed = Math.max(5, Math.min(40, currentWindSpeed));
            currentWaveHeight = Math.max(0.5, Math.min(5.0, currentWaveHeight));
            currentVisibility = Math.max(1, Math.min(15, currentVisibility));
            currentHumidity = Math.max(40, Math.min(95, currentHumidity));
            
            // Update weather condition
            currentCondition = weatherConditions[random.nextInt(weatherConditions.length)];
            
            // Update weather display in the UI (you would need to add fx:id to weather labels in FXML)
            updateWeatherDisplay();
            
            System.out.println("Weather updated: " + currentCondition + " - " + 
                String.format("%.1f°C, Wave: %.1fm, Wind: %.1fkm/h", 
                currentTemperature, currentWaveHeight, currentWindSpeed));
        });
    }
    
    /**
     * Update weather display in the UI
     */
    private void updateWeatherDisplay() {
        // Update weather card with real-time data
        if (temperatureLabel != null) {
            temperatureLabel.setText(String.format("%.0f°C", currentTemperature));
        }
        if (conditionLabel != null) {
            conditionLabel.setText(currentCondition);
        }
        if (windSpeedLabel != null) {
            windSpeedLabel.setText(String.format("%.0f km/h", currentWindSpeed));
        }
        if (waveHeightLabel != null) {
            waveHeightLabel.setText(String.format("%.1f m", currentWaveHeight));
        }
        if (visibilityLabel != null) {
            visibilityLabel.setText(String.format("%.0f km", currentVisibility));
        }
        if (humidityLabel != null) {
            humidityLabel.setText(String.format("%d%%", currentHumidity));
        }
        
        // Update weather icon based on condition
        if (weatherIcon != null) {
            String icon = getWeatherIcon(currentCondition);
            weatherIcon.setText(icon);
        }
        
        System.out.println("Weather display updated in UI: " + currentCondition + " - " + 
            String.format("%.1f°C", currentTemperature));
    }
    
    /**
     * Check for dangerous weather conditions and update alert banner
     */
    private void checkAndShowWeatherAlerts() {
        Platform.runLater(() -> {
            boolean isDangerous = false;
            String alertMessage = "";
            String alertSubtitleText = "";
            String alertIconText = "⚠️";
            
            // Check for dangerous conditions
            if (currentWaveHeight > WAVE_DANGER_THRESHOLD) {
                isDangerous = true;
                alertMessage = "Weather Alert: High waves detected - Exercise extreme caution";
                alertSubtitleText = String.format("Wave height: %.1fm - Fishing boats advised to stay in harbor", currentWaveHeight);
                alertIconText = "🌊";
            } else if (currentWindSpeed > WIND_DANGER_THRESHOLD) {
                isDangerous = true;
                alertMessage = "Weather Alert: Strong winds detected - Consider postponing trips";
                alertSubtitleText = String.format("Wind speed: %.0f km/h - Small boats should avoid open waters", currentWindSpeed);
                alertIconText = "💨";
            } else if (currentVisibility < VISIBILITY_DANGER_THRESHOLD) {
                isDangerous = true;
                alertMessage = "Weather Alert: Low visibility conditions - Use navigation aids";
                alertSubtitleText = String.format("Visibility: %.1fkm - Exercise extreme caution when navigating", currentVisibility);
                alertIconText = "🌫️";
            } else if (random.nextDouble() < 0.15) { // 15% chance for general weather updates
                String randomAlert = weatherAlerts[random.nextInt(weatherAlerts.length)];
                if (randomAlert.contains("Excellent") || randomAlert.contains("Calm")) {
                    alertMessage = randomAlert;
                    alertSubtitleText = "Perfect conditions for fishing - Clear skies and calm seas reported";
                    alertIconText = "☀️";
                } else {
                    alertMessage = randomAlert;
                    alertSubtitleText = "Monitor weather conditions and follow safety guidelines";
                    alertIconText = "⚠️";
                }
            }
            
            // Update alert banner instead of showing popup
            if (isDangerous || random.nextDouble() < 0.15) {
                updateWeatherAlertBanner(alertMessage, alertSubtitleText, alertIconText);
            }
        });
    }
    
    /**
     * Show weather alert dialog
     */
    private void showWeatherAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Weather Alert System");
        alert.setHeaderText("Real-time Weather Update");
        alert.setContentText(message + "\n\nCurrent Conditions:\n" +
            String.format("Temperature: %.1f°C\n", currentTemperature) +
            String.format("Wave Height: %.1fm\n", currentWaveHeight) +
            String.format("Wind Speed: %.1f km/h\n", currentWindSpeed) +
            String.format("Visibility: %.1fkm\n", currentVisibility) +
            String.format("Condition: %s\n", currentCondition) +
            "\nTime: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // Show alert without blocking UI
        alert.show();
        
        // Auto-close after 8 seconds
        Timeline autoClose = new Timeline(new KeyFrame(Duration.seconds(8), e -> alert.close()));
        autoClose.play();
    }
    
    /**
     * Stop real-time weather updates (useful when leaving dashboard)
     */
    public void stopWeatherUpdates() {
        if (weatherUpdater != null) {
            weatherUpdater.stop();
        }
    }
    
    /**
     * Get current weather summary for external use
     */
    public String getCurrentWeatherSummary() {
        return String.format("Current Weather: %s | %.1f°C | Wave: %.1fm | Wind: %.1fkm/h | Visibility: %.1fkm", 
            currentCondition, currentTemperature, currentWaveHeight, currentWindSpeed, currentVisibility);
    }
    
    /**
     * Check if current weather conditions are safe for fishing
     */
    public boolean isSafeForFishing() {
        return currentWaveHeight < WAVE_DANGER_THRESHOLD && 
               currentWindSpeed < WIND_DANGER_THRESHOLD && 
               currentVisibility > VISIBILITY_DANGER_THRESHOLD;
    }
    
    /**
     * Get weather safety level (0=safe, 1=caution, 2=danger)
     */
    public int getWeatherSafetyLevel() {
        if (currentWaveHeight > WAVE_DANGER_THRESHOLD || 
            currentWindSpeed > WIND_DANGER_THRESHOLD || 
            currentVisibility < VISIBILITY_DANGER_THRESHOLD) {
            return 2; // Danger
        } else if (currentWaveHeight > WAVE_DANGER_THRESHOLD * 0.7 || 
                   currentWindSpeed > WIND_DANGER_THRESHOLD * 0.7 || 
                   currentVisibility < VISIBILITY_DANGER_THRESHOLD * 1.5) {
            return 1; // Caution
        }
        return 0; // Safe
    }
    
    /**
     * Get weather icon based on current condition
     */
    private String getWeatherIcon(String condition) {
        switch (condition.toLowerCase()) {
            case "clear":
                return "☀️";
            case "partly cloudy":
                return "🌤️";
            case "cloudy":
                return "☁️";
            case "light rain":
                return "🌦️";
            case "heavy rain":
                return "🌧️";
            case "thunderstorm":
                return "⛈️";
            case "windy":
                return "💨";
            case "calm seas":
                return "🌊";
            case "rough seas":
                return "🌊";
            default:
                return "🌤️";
        }
    }
    
    /**
     * Update the weather alert banner on the homepage
     */
    private void updateWeatherAlertBanner(String alertMessage, String alertSubtitle, String alertIcon) {
        if (alertTitle != null) {
            alertTitle.setText(alertMessage);
        }
        if (alertSubtitle != null) {
            this.alertSubtitle.setText(alertSubtitle);
        }
        if (this.alertIcon != null) {
            this.alertIcon.setText(alertIcon);
        }
        
        // Update alert banner styling based on alert type
        if (weatherAlertBanner != null) {
            weatherAlertBanner.getStyleClass().clear();
            if (alertIcon.equals("☀️")) {
                weatherAlertBanner.getStyleClass().addAll("weather-alert", "weather-alert-good");
            } else {
                weatherAlertBanner.getStyleClass().add("weather-alert");
            }
        }
        
        System.out.println("Weather alert banner updated: " + alertMessage);
    }


}

