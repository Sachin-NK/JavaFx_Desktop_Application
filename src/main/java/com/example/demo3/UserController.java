package com.example.demo3;




import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static Connection.DBConnection.getConnection;

public class UserController {

    public Button admin;
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> nameCol;
    @FXML
    private TableColumn<User, Integer> ageCol;
    @FXML
    private TableColumn<User, String> fishingDayCol;
    @FXML
    private TableColumn<User, String> addressCol;
    @FXML
    private TableColumn<User, String> contactCol;
    @FXML
    private Label totalUsersLabel;

    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        fishingDayCol.setCellValueFactory(new PropertyValueFactory<>("fishingDay"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        getUsers();
        countusers();

        // Dummy data

    }

    public void getUsers() {
        ObservableList<User> data = FXCollections.observableArrayList();

        try (Connection conn = getConnection();
             PreparedStatement user = conn.prepareStatement("Select * from fishman");
             ResultSet resultSet = user.executeQuery()) {

            while (resultSet.next()) {
                data.add(new User(
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getString("fishingDay"),
                        resultSet.getString("address"),
                        resultSet.getString("contact")

                ));

            }

            userTable.setItems(data);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }


    }


    @FXML
    private void addUser() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Add Fisherman");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.getStyleClass().add("popup-pane");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField ageField = new TextField();
        ageField.setPromptText("Age");

        TextField fishingDayField = new TextField();
        fishingDayField.setPromptText("Fishing Day");

        TextField addressField = new TextField();
        addressField.setPromptText("Address");

        TextField contactField = new TextField();
        contactField.setPromptText("Contact");

        Button saveBtn = new Button("Save");
        Button closeBtn = new Button("Cancel");

        HBox btnBox = new HBox(10, saveBtn, closeBtn);
        btnBox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(nameField, ageField, fishingDayField, addressField, contactField, btnBox);

        Scene scene = new Scene(vbox, 300, 300);
        popupStage.setScene(scene);
        popupStage.show();

        saveBtn.setOnAction(e -> {
            try (Connection connection = getConnection()) {
                String sql = "INSERT INTO fishman (name, age, fishingDay, address, contact) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);

                stmt.setString(1, nameField.getText());
                stmt.setInt(2, Integer.parseInt(ageField.getText()));
                stmt.setString(3, fishingDayField.getText());
                stmt.setString(4, addressField.getText());
                stmt.setString(5, contactField.getText());

                stmt.executeUpdate();

                // Update TableView
                getUsers();

                popupStage.close();

            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                // You can show a dialog here for error handling
            }
        });

        closeBtn.setOnAction(e -> popupStage.close());
    }

    @FXML
    private void deleteSelectedUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            System.out.println("No user selected!");
            return;
        }

        try (Connection connection = getConnection()) {
            String sql = "DELETE FROM fishman WHERE name = ? AND contact = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, selectedUser.getName());
            stmt.setString(2, selectedUser.getContact());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                userList.remove(selectedUser);
                System.out.println("User deleted.");
                getUsers();
            } else {
                System.out.println("Delete failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateSelectedUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            System.out.println("No user selected!");
            return;
        }

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Update User");

        TextField nameField = new TextField(selectedUser.getName());
        TextField ageField = new TextField(String.valueOf(selectedUser.getAge()));
        TextField fishingDayField = new TextField(selectedUser.getFishingDay());
        TextField addressField = new TextField(selectedUser.getAddress());
        TextField contactField = new TextField(selectedUser.getContact());

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");

        HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(10, nameField, ageField, fishingDayField, addressField, contactField, buttonBox);
        vbox.setPadding(new Insets(20));
        vbox.getStyleClass().add("popup-pane");

        popupStage.setScene(new Scene(vbox, 350, 300));
        popupStage.show();

        saveBtn.setOnAction(e -> {
            try (Connection connection = getConnection()) {
                String sql = "UPDATE fishman SET name = ?, age = ?, fishingDay = ?, address = ?, contact = ? WHERE name = ? AND contact = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);

                stmt.setString(1, nameField.getText());
                stmt.setInt(2, Integer.parseInt(ageField.getText()));
                stmt.setString(3, fishingDayField.getText());
                stmt.setString(4, addressField.getText());
                stmt.setString(5, contactField.getText());

                // WHERE clause – original identifiers
                stmt.setString(6, selectedUser.getName());
                stmt.setString(7, selectedUser.getContact());

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    // update local data too
                    selectedUser.setName(nameField.getText());
                    selectedUser.setAge(Integer.parseInt(ageField.getText()));
                    selectedUser.setFishingDay(fishingDayField.getText());
                    selectedUser.setAddress(addressField.getText());
                    selectedUser.setContact(contactField.getText());

                    userTable.refresh(); // refresh UI
                    popupStage.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        cancelBtn.setOnAction(e -> popupStage.close());
    }

    @FXML
    private void countusers() {
        try (Connection conn = getConnection()) {
            String usercount = "";
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(name) from fishman");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                usercount = rs.getString(1); // or: rs.getString("COUNT(name)")
            }
            totalUsersLabel.setText(usercount);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void moveToAdminPage() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
        Parent adminRoot = loader.load();

        Stage stage = (Stage) admin.getScene().getWindow();

        // Set the new scene
        stage.setScene(new Scene(adminRoot));

        stage.show();
    }
}




