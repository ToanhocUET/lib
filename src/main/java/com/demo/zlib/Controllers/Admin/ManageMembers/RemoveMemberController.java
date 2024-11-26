package com.demo.zlib.Controllers.Admin.ManageMembers;

import com.demo.zlib.Source.Book;
import com.demo.zlib.Source.Library;
import com.demo.zlib.Source.Member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class RemoveMemberController {
    @FXML
    private Button remove_btn;
    @FXML
    private TextField keywordField;
    @FXML
    private MenuButton searchMenuButton;
    @FXML
    private MenuItem searchAll;
    @FXML
    private MenuItem searchID;
    @FXML
    private MenuItem searchName;
    @FXML
    private MenuItem searchContact;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<Member> memberTable;
    @FXML
    private TableColumn<Member, Integer> memberID;
    @FXML
    private TableColumn<Member, String> name;
    @FXML
    private TableColumn<Member, String> contactInfo;
    private Library library = new Library();
    private ObservableList<Member> memberObservableList;
    private String selectedField = "All Field";

    @FXML
    private void initialize() {
        memberObservableList = FXCollections.observableArrayList(library.getMembers());
        memberID.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactInfo.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));

        memberTable.setItems(memberObservableList);

        searchMenuButton.getItems().forEach(menuItem -> menuItem.setOnAction(event -> {
            selectedField = menuItem.getText();
            searchMenuButton.setText(selectedField);
        }));

        remove_btn.setOnAction(event -> {handleRemoveMember();});
        searchButton.setOnAction(event -> {handleSearchButtonAction();});
    }

    @FXML
    private void handleSearchButtonAction() {
        String keyword = keywordField.getText().toLowerCase().trim();
        List<Member> filteredMembers = new ArrayList<>();

        switch (selectedField) {
            case "ID":
                filteredMembers = library.getMembers().stream()
                        .filter(member -> String.valueOf(member.getMemberID()).toLowerCase()
                                .contains(keyword)).toList();
                break;
            case "Name":
                filteredMembers = library.getMembers().stream()
                        .filter(member -> member.getName().toLowerCase()
                                .contains(keyword)).toList();
                break;
            case "Contact":
                filteredMembers = library.getMembers().stream()
                        .filter(member -> member.getContactInfo().toLowerCase()
                                .contains(keyword)).toList();
                break;
            default:
                filteredMembers = library.getMembers().stream()
                        .filter(member -> String.valueOf(member.getMemberID()).toLowerCase()
                                .contains(keyword) ||
                                member.getName().toLowerCase().contains(keyword) ||
                                member.getContactInfo().toLowerCase().contains(keyword))
                        .toList();
                break;
        }
        memberObservableList.setAll(filteredMembers);
    }

    @FXML
    private void handleRemoveMember() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();

        if (selectedMember != null) {
            library.removeMember(selectedMember);
            memberObservableList.remove(selectedMember);
            showAlert("Success", "Member removed successfully.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Please select a member to remove.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
