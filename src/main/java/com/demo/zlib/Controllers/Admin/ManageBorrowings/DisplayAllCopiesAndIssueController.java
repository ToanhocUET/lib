package com.demo.zlib.Controllers.Admin.ManageBorrowings;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import com.demo.zlib.Source.*;
import javafx.scene.layout.AnchorPane;

import java.util.List;
import java.util.Random;

public class DisplayAllCopiesAndIssueController {

    public Button checkCopiesButton;
    public TableColumn<Book, Integer> copiesQuantityColumn;
    public Button confirmIssueBookButton;
    public AnchorPane memberInputPane;
    public TextField memberIdField;
    @FXML
    private TextField keywordField;
    @FXML
    private MenuButton searchMenuButton;
    @FXML
    private MenuItem searchAll;
    @FXML
    private MenuItem searchISBN;
    @FXML
    private MenuItem searchTitle;
    @FXML
    private Button searchBooksButton;
    @FXML
    private Button issueBookButton;
    @FXML
    private TableView<Book> bookTableView;
    @FXML
    private TableColumn<Book, String> isbnColumn;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> publisherColumn;
    @FXML
    private TableColumn<Book, String> categoryColumn;
    @FXML
    private TableColumn<Book, String> yearColumn;
    @FXML
    private TableView<BookCopy> copiesTableView;
    @FXML
    private TableColumn<BookCopy, Integer> orderColumn;
    @FXML
    private TableColumn<BookCopy, Integer> copyIDColumn;
    @FXML
    private TableColumn<BookCopy, String> copyISBNColumn;
    @FXML
    private TableColumn<BookCopy, String> statusColumn;

    private final Library library = new Library();
    private final LibraryBorrowingManagement borrowingManagement = new LibraryBorrowingManagement();
    private final ObservableList<Book> bookList = FXCollections.observableArrayList();
    private final ObservableList<BookCopy> copyList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupBookColumns();
        setupCopyColumns();
        loadBooks();
        setupSearchMenuItems();
        searchBooksButton.setOnAction(e -> handleSearchButtonAction());
        issueBookButton.setOnAction(e -> showMemberInputPane());
        confirmIssueBookButton.setOnAction(this::onConfirmIssueBookButtonClick);
        checkCopiesButton.setOnAction(this::onCheckCopiesButtonClick);
    }

    private void setupBookColumns() {
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        copiesQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("copiesQuantity"));
    }

    private void setupCopyColumns() {
        orderColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(copiesTableView.getItems().indexOf(cellData.getValue()) + 1));
        copyIDColumn.setCellValueFactory(new PropertyValueFactory<>("copyID"));
        copyISBNColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getBook().getISBN()));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadBooks() {
        bookList.setAll(library.getBooks());
        bookTableView.setItems(bookList);
    }

    @FXML
    private void handleSearchButtonAction() {
        String keyword = keywordField.getText().trim().toLowerCase();
        ObservableList<Book> filteredList = FXCollections.observableArrayList();

        switch (searchMenuButton.getText().trim()) {
            case "All":
                filteredList.setAll(bookList.stream()
                        .filter(book -> book.getTitle().toLowerCase().contains(keyword) ||
                                book.getISBN().toLowerCase().contains(keyword))
                        .toList());
                break;
            case "ISBN":
                filteredList.setAll(bookList.stream()
                        .filter(book -> book.getISBN().toLowerCase().contains(keyword))
                        .toList());
                break;
            case "Title":
                filteredList.setAll(bookList.stream()
                        .filter(book -> book.getTitle().toLowerCase().contains(keyword))
                        .toList());
                break;
        }

        bookTableView.setItems(filteredList);
    }

    @FXML
    private void setupSearchMenuItems() {
        searchAll.setOnAction(e -> searchMenuButton.setText("All"));
        searchISBN.setOnAction(e -> searchMenuButton.setText("ISBN"));
        searchTitle.setOnAction(e -> searchMenuButton.setText("Title"));
    }

    @FXML
    private void showMemberInputPane() {
        memberInputPane.setVisible(true);
    }

    @FXML
    void onConfirmIssueBookButtonClick(ActionEvent event) {
        String memberIdStr = memberIdField.getText().trim();
        if (memberIdStr.isEmpty()) {
            showAlert("Error", "Please enter a valid Member ID.", Alert.AlertType.ERROR);
            return;
        }

        try {
            int memberId = Integer.parseInt(memberIdStr);
            Member member = library.findMemberByID(memberId);
            BookCopy selectedCopy = copiesTableView.getSelectionModel().getSelectedItem();

            if (selectedCopy == null) {
                showAlert("Error", "Please select a copy to issue.", Alert.AlertType.ERROR);
            } else if (!"Available".equalsIgnoreCase(selectedCopy.getStatus())) {
                showAlert("Error", "Selected copy is not available for issuing.", Alert.AlertType.ERROR);
            } else if (member == null) {
                showAlert("Error", "Member not found.", Alert.AlertType.ERROR);
            } else {
                selectedCopy.setStatus("Borrowed");
                library.updateBookCopyField(selectedCopy.getCopyID(), "Status", "Borrowed");

                // Tạo đối tượng Borrowing và thêm vào cơ sở dữ liệu
                Borrowing borrowing = new Borrowing(selectedCopy, member);
                borrowingManagement.addBorrowingToDatabase(borrowing);

                showAlert("Success", "Book copy issued successfully.", Alert.AlertType.INFORMATION);
                copiesTableView.refresh();
                memberInputPane.setVisible(false);
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Member ID format.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onCheckCopiesButtonClick(ActionEvent event) {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Error", "Please select a book to view its copies.", Alert.AlertType.ERROR);
        } else {
            copyList.setAll(selectedBook.getCopies());
            copiesTableView.setItems(copyList);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
//    public static void main(String[] args) {
//        Library library = new Library();
//        LibraryBorrowingManagement borrowingManagement = new LibraryBorrowingManagement();
//        Random random = new Random();
//
//        // Create random borrowings for each member
//        List<Member> members = library.getMembers();
//        List<Book> books = library.getBooks();
//
//        for (Member member : members) {
//            int numCopiesToBorrow = random.nextInt(3) + 2; // Random number from 2 to 4
//
//            for (int i = 0; i < numCopiesToBorrow; i++) {
//                Book book = books.get(random.nextInt(books.size()));
//                List<BookCopy> availableCopies = book.getAvailableCopies();
//
//                if (!availableCopies.isEmpty()) {
//                    BookCopy bookCopy = availableCopies.get(0); // Borrow the first available copy
//                    bookCopy.setStatus("Borrowed");
//                    library.updateBookCopyField(bookCopy.getCopyID(), "Status", "Borrowed");
//
//                    Borrowing borrowing = new Borrowing(bookCopy, member);
//                    borrowingManagement.addBorrowingToDatabase(borrowing);
//                }
//            }
//        }
//
//        System.out.println("Database has been updated with random borrowings for each member.");
//    }
}
