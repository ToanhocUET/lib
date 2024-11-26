package com.demo.zlib.Controllers.Admin.ManageBooks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ManageBooksController {

    @FXML
    public Button addbook_btn;
    @FXML
    public Button removebook_btn;
    @FXML
    public Button searchbook_btn;
    @FXML
    public Button updatebook_btn;
    @FXML
    public Button displaybooks_btn;
    @FXML
    public Button back_btn;
    @FXML
    public AnchorPane contentPane;

    @FXML
    private void handleAddBook(ActionEvent event) {
        loadContent("/Fxml/Admin/ManageBook/AddBook.fxml");
    }

    @FXML
    private void handleRemoveBook(ActionEvent event) {
        loadContent("/Fxml/Admin/ManageBook/RemoveBook.fxml");
    }

    @FXML
    private void handleSearchBook(ActionEvent event) {
        loadContent("/Fxml/Admin/ManageBook/SearchBook.fxml");
    }

    @FXML
    private void handleUpdateBook(ActionEvent event) {
        loadContent("/Fxml/Admin/ManageBook/UpdateBook.fxml");
    }

    @FXML
    private void handleDisplayBooks(ActionEvent event) {
        loadContent("/Fxml/Admin/ManageBook/DisplayBooks.fxml");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/AdminMenu.fxml"));
            Parent adminMenuRoot = loader.load();
            Scene adminMenuScene = new Scene(adminMenuRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(adminMenuScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        loadContent("/Fxml/Admin/ManageBook/ManageBooks.fxml");
    }

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            contentPane.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
