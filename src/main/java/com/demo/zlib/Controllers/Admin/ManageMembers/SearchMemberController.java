package com.demo.zlib.Controllers.Admin.ManageMembers;

import com.demo.zlib.Source.Library;
import com.demo.zlib.Source.Member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class SearchMemberController {
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
    private Library library;
    private ObservableList<Member> memberObservableList;
    private String selectedField = "All Field";

    @FXML
    private void initialize() {
        library = new Library();
        memberObservableList = FXCollections.observableArrayList(library.getMembers());
        memberID.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactInfo.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));

        memberTable.setItems(memberObservableList);
        for (Member member : memberObservableList) {
            System.out.println(member.getMemberID());
        }

        searchMenuButton.getItems().forEach(menuItem -> menuItem.setOnAction(event -> {
            selectedField = menuItem.getText();
            searchMenuButton.setText(selectedField);
        }));
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
}
