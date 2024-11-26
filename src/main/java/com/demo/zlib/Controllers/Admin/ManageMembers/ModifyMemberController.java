package com.demo.zlib.Controllers.Admin.ManageMembers;

import com.demo.zlib.Source.Library;
import com.demo.zlib.Source.Member;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ModifyMemberController {
    @FXML
    private AnchorPane contentPane;
    @FXML
    private TableView<Member> memberTable;
    @FXML
    private TableColumn<Member, Integer> memberID;
    @FXML
    private TableColumn<Member, String> name;
    @FXML
    private TableColumn<Member, String> contactInfo;

    @FXML
    private Button searchButton;
    @FXML
    private TextField keywordField;
    @FXML
    private MenuButton searchMenuButton;

    private final Library library = new Library();
    private final ObservableList<Member> memberObservableList = FXCollections.observableArrayList();
    private String selectedField = "All Field";

    @FXML
    public void initialize() {
        memberID.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactInfo.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));

        loadMembers();

        searchMenuButton.getItems().forEach(item -> {
            item.setOnAction(event -> {
                selectedField = item.getText();
                searchMenuButton.setText(selectedField);
            });
        });

        searchButton.setOnAction(event -> handleSearchButtonAction());
    }

    private void loadMembers() {
        memberObservableList.setAll(library.getMembers());
        memberTable.setItems(memberObservableList);
    }

    @FXML
    private void handleSearchButtonAction() {
        String keyword = keywordField.getText().toLowerCase().trim();
        List<Member> filteredMembers;

        switch (selectedField) {
            case "ID":
                filteredMembers = library.getMembers().stream()
                        .filter(member -> String.valueOf(member.getMemberID()).toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
                break;
            case "Name":
                filteredMembers = library.getMembers().stream()
                        .filter(member -> member.getName().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
                break;
            case "Contact":
                filteredMembers = library.getMembers().stream()
                        .filter(member -> member.getContactInfo().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
                break;
            case "All Field":
            default:
                filteredMembers = library.getMembers().stream()
                        .filter(member -> String.valueOf(member.getMemberID()).toLowerCase().contains(keyword) ||
                                member.getName().toLowerCase().contains(keyword) ||
                                member.getContactInfo().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
                break;
        }

        memberObservableList.setAll(filteredMembers);
    }

    @FXML
    private void handleUpdateMember(ActionEvent event) {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/ManageMembers/ModifyMember1.fxml"));
                Parent modifyMemberRoot = loader.load();

                ModifyMember1Controller controller = loader.getController();
                controller.setLibrary(library);
                controller.setMemberData(selectedMember);
                controller.setParentContentPane(contentPane);

                contentPane.getChildren().setAll(modifyMemberRoot);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Unable to load ModifyMember1 scene.", AlertType.ERROR);
            }
        } else {
            showAlert("Error", "Please select a member to update.", AlertType.WARNING);
        }
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
