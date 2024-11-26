package com.demo.zlib.Controllers.Admin.ManageMembers;

import com.demo.zlib.Source.Library;
import com.demo.zlib.Source.Member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class DisplayMemberController {
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
        members.clear(); // Ensure the list is clear before adding items
        if (library.getMembers().isEmpty()) {
            System.out.println("No members found");
        } else {
            System.out.println("Found " + library.getMembers().size() + " members");
            members.addAll(library.getMembers());
            memberTable.setItems(members);
        }
    }

}
