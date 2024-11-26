package com.demo.zlib.Controllers.Admin.ManageMembers;

import com.demo.zlib.Source.Library;
import com.demo.zlib.Source.Member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class AddMemberController {
    @FXML
    private TextField memberIDField;
    @FXML
    private TextField memberNameField;
    @FXML
    private TextField memberContactField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;
    @FXML
    private TableColumn<Member, Integer> memberID;
    @FXML
    private TableColumn<Member, String> name;
    @FXML
    private TableColumn<Member, String> contactInfo;
    @FXML
    private TableView<Member> memberTable;

    private final Library library = new Library();
    private final ObservableList<Member> members = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        memberID.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactInfo.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));

        loadMembers();
    }

    private void loadMembers() {
        members.clear();
        if (library.getMembers().isEmpty()) {
            System.out.println("No members found");
        } else {
            System.out.println("Found " + library.getMembers().size() + " members");
            members.addAll(library.getMembers());
            memberTable.setItems(members);
        }
    }

    @FXML
    private void handleConfirmButtonAction(ActionEvent event) {
        int memberID = 0;
        while (true) {
            try {
                memberID = Integer.parseInt(memberIDField.getText());
                for (Member member : members) {
                    if (member.getMemberID() == memberID) {
                        System.out.println("Member already exists. Please choose another one.");
                        showAlert("Duplicate Member ID", "The member with this ID is already exist.", Alert.AlertType.ERROR);
                        clearFields();
                        return;
                    }
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid member ID");
            }
        }
        String name = memberNameField.getText();
        String contactInfo = memberContactField.getText();

        if (memberID == 0 || name.isEmpty() || contactInfo.isEmpty()) {
            System.out.println("Please enter all fields.");
            return;
        }

        Member member = new Member(memberID, name, contactInfo);
        library.addMember(member);
        loadMembers();
        clearFields();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        memberIDField.clear();
        memberNameField.clear();
        memberContactField.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
