package com.demo.zlib.Controllers.Admin.ManageBorrowings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import com.demo.zlib.Source.Book;
import com.demo.zlib.Source.Library;
import com.demo.zlib.Source.Member;
import com.demo.zlib.Source.BookCopy;
import com.demo.zlib.Source.Borrowing;
import com.demo.zlib.Source.LibraryBorrowingManagement;

import java.io.IOException;
import java.util.List;

public class ManageBorrowingsController {

    public Button displayMemberBorrowings_btn;
    public Button back_btn;
    public Button displayAllCopies_btn;
    @FXML
    private TextField memberIdField;

    @FXML
    private TextField isbnField;

    @FXML
    private TextField borrowingIdField;

    @FXML
    private AnchorPane contentPane;

    private final Library library = new Library();
    private final LibraryBorrowingManagement borrowingManagement = new LibraryBorrowingManagement();

    @FXML
    public void handleDisplayAllCopies() {
        loadView("/Fxml/Admin/ManageBorrowing/DisplayAllCopiesAndIssue.fxml");
    }

    @FXML
    public void handleDisplayMemberBorrowings() {
        loadView("/Fxml/Admin/ManageBorrowing/DisplayMemberBorrowingAndReturn.fxml");
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/AdminMenu.fxml"));
            Parent adminMenuRoot = loader.load();
            Scene adminMenuScene = new Scene(adminMenuRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(adminMenuScene);
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể tải giao diện Admin Menu.");
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) {
        try {
            // Kiểm tra nếu đường dẫn là null hoặc rỗng
            if (fxmlFile == null || fxmlFile.trim().isEmpty()) {
                throw new IOException("Đường dẫn FXML không hợp lệ: " + fxmlFile);
            }

            // In ra log để kiểm tra đường dẫn FXML
            System.out.println("Loading FXML: " + fxmlFile);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node node = loader.load();

            // Xóa tất cả các node hiện tại trước khi thêm node mới
            contentPane.getChildren().clear();
            contentPane.getChildren().add(node);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể tải giao diện: " + fxmlFile);
            e.printStackTrace();
        }
    }

    @FXML
    public void issueBook() {
        String memberIdStr = memberIdField.getText();
        String isbn = isbnField.getText();

        try {
            int memberId = Integer.parseInt(memberIdStr);
            Member member = library.findMemberByID(memberId);
            Book book = library.findBookByISBN(isbn);

            if (member == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Member not found.");
                return;
            }

            if (book == null || book.getCopiesQuantity() <= 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Book is not available or doesn't exist.");
                return;
            }

            for (BookCopy copy : book.getCopies()) {
                if (copy.getStatus().equals("Available")) {
                    copy.setStatus("Borrowed");
                    Borrowing borrowing = new Borrowing(copy, member);
                    borrowingManagement.addBorrowingToDatabase(borrowing);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Book issued successfully.");
                    return;
                }
            }

            showAlert(Alert.AlertType.ERROR, "Error", "No available copies to issue.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid member ID.");
        }
    }

    @FXML
    public void returnBook() {
        String borrowingId = borrowingIdField.getText();

        try {
            List<Borrowing> allBorrowings = borrowingManagement.getBorrowingsByID(Integer.parseInt(borrowingId));
            for (Borrowing borrowing : allBorrowings) {
                if (borrowing.getBorrowingID().equals(borrowingId)) {
                    BookCopy bookCopy = borrowing.getBookCopy();
                    bookCopy.setStatus("Available");
                    library.updateBookCopyField(bookCopy.getCopyID(), "Status", "Available");
                    borrowingManagement.deleteBorrowingFromDatabase(borrowingId);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully.");
                    return;
                }
            }
            showAlert(Alert.AlertType.ERROR, "Error", "Borrowing ID not found.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid borrowing ID.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
