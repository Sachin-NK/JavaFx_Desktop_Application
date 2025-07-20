package com.example.demo3;

import Model.Admin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static Connection.DBConnection.getConnection;

public class AdminController {

    public Button back;
    @FXML
    private TableView<Admin> AdminTable;

    @FXML
    private TableColumn<Admin, String> fnameCol;

    @FXML
    private TableColumn<Admin, String> lnameCol;

    @FXML
    private TableColumn<Admin, String> pwdCol;

    @FXML
    private TableColumn<Admin, String> rollCol;

    @FXML
    private TableColumn<Admin, java.sql.Date> dateCol;

    @FXML
    public void initialize() {
        fnameCol.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lnameCol.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        pwdCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        rollCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("logindate"));

        getdata();
    }

    public void getdata() {
        ObservableList<Admin> data = FXCollections.observableArrayList();
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM admin");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.add(new Admin(
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getDate("logindate")
                ));
            }
            AdminTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();  // Better than throwing a RuntimeException directly
        }
    }

    public  void backTouser() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent adminRoot = loader.load();

        Stage stage = (Stage) back.getScene().getWindow();

        // Set the new scene
        stage.setScene(new Scene(adminRoot));
        stage.setMaximized(true);
        stage.show();
    }
}
