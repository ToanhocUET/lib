package com.demo.zlib.Controllers.Admin.ManageMembers;

import com.demo.zlib.Source.Library;
import com.demo.zlib.Source.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ModifyMember1Controller {
    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField memberIDField;
    @FXML
    private TextField memberNameField;
    @FXML
    private TextField memberContactField;

    private Library library;
    private Member currentMember;
    private AnchorPane parentContentPane;

    private String originalID;
    private String originalName;
    private String originalContact;

    public void setLibrary(Library library) {
        this.library = library;
    }

    public void setParentContentPane(AnchorPane contentPane) {
        this.parentContentPane = contentPane;
    }

    public void setMemberData(Member member) {
        this.currentMember = member;
        this.originalID = String.valueOf(member.getMemberID());
        this.originalName = member.getName();
        this.originalContact = member.getContactInfo();

        memberIDField.setText(originalID);
        memberNameField.setText(originalName);
        memberContactField.setText(originalContact);
    }

    @FXML
    private void handleConfirmButtonAction(ActionEvent event) {
        String memberID = memberIDField.getText().trim();
        String memberName = memberNameField.getText().trim();
        String memberContact = memberContactField.getText().trim();

        if (memberID.isEmpty() || memberName.isEmpty() || memberContact.isEmpty()) {
            showAlert("Error", "Please fill all fields.", Alert.AlertType.ERROR);
            return;
        }

        try {
            int memberIdInt = Integer.parseInt(memberID);
            if (!memberID.equals(originalID) && library.findMemberByID(memberIdInt) != null) {
                showAlert("Duplicate Member ID", "A member with this ID already exists.", Alert.AlertType.ERROR);
                return;
            }

            // Update member information
            currentMember.setMemberID(memberIdInt);  // Updated to use setMemberID
            currentMember.setName(memberName);
            currentMember.setContactInfo(memberContact);

            // Update member in the library
            library.updateMember(currentMember);
            showAlert("Success", "Member updated successfully.", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Error", "Member ID must be a valid number.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        memberIDField.setText(originalID);
        memberNameField.setText(originalName);
        memberContactField.setText(originalContact);
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/ManageMembers/ModifyMember.fxml"));
            Parent manageMembersRoot = loader.load();

            ModifyMemberController modifyMemberController = loader.getController();
            parentContentPane.getChildren().setAll(manageMembersRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleCancel(ActionEvent event) {
        // Restore the original values of the member fields
        memberIDField.setText(originalID);
        memberNameField.setText(originalName);
        memberContactField.setText(originalContact);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
