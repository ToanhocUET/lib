package com.demo.zlib.Controllers.Admin.ManageMembers;

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

public class ManageMembersController {

    @FXML
    public Button addmember_btn;
    @FXML
    public Button removemember_btn;
    @FXML
    public Button searchmember_btn;
    @FXML
    public Button displaymembers_btn;
    @FXML
    public Button back_btn;
    @FXML
    public AnchorPane contentPane;
    @FXML
    public Button modifymember_btn;

    @FXML
    private void handleAddMember(ActionEvent event) {
        loadContent("/Fxml/Admin/ManageMembers/AddMember.fxml");
    }

    @FXML
    private void handleRemoveMember(ActionEvent event) {
        loadContent("/Fxml/Admin/ManageMembers/RemoveMember.fxml");
    }

    @FXML
    private void handleModifyMember(ActionEvent event) {
        loadContent("/Fxml/Admin/ManageMembers/ModifyMember.fxml");
    }

    @FXML
    private void handleSearchMember(ActionEvent event) {
        loadContent("/Fxml/Admin/ManageMembers/SearchMember.fxml");
    }

    @FXML
    private void handleDisplayAllMembers(ActionEvent event) {
        loadContent("/Fxml/Admin/ManageMembers/DisplayMembers.fxml");
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
