package com.demo.zlib.Controllers.Admin.ManageBooks;

import com.demo.zlib.Source.Book;
import com.demo.zlib.Source.Library;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;

public class AddBookController {

    @FXML
    private TextField isbnField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField publisherField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField copiesField1; // Field for entering the number of book copies
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private AnchorPane contentPane;

    private Library library = new Library();

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
        String isbn = isbnField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String publisher = publisherField.getText().trim();
        String category = categoryField.getText().trim();
        String year = yearField.getText().trim();
        String copiesText = copiesField1.getText().trim();

        if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || publisher.isEmpty() ||
                category.isEmpty() || year.isEmpty() || copiesText.isEmpty()) {
            showAlert("Error", "Please fill all fields.", Alert.AlertType.ERROR);
            return;
        }

        int copies;
        try {
            copies = Integer.parseInt(copiesText);
            if (copies < 0) {
                showAlert("Error", "Number of copies must be at least 0.", Alert.AlertType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number for copies.", Alert.AlertType.ERROR);
            return;
        }

        if (library.findBookByISBN(isbn) != null) {
            showAlert("Duplicate ISBN", "A book with this ISBN already exists.", Alert.AlertType.ERROR);
            return;
        }

        Book newBook;
        if (copies == 0) {
            newBook = new Book(title, author, publisher, isbn, category, copies, "Not Available", year);
        } else {
            newBook = new Book(title, author, publisher, isbn, category, copies, "Available", year);
        }

        library.addBook(newBook);
        library.addCopies(newBook, copies);

        showAlert("Success", "Book and copies added successfully!", Alert.AlertType.INFORMATION);

        clearFields();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        isbnField.clear();
        titleField.clear();
        authorField.clear();
        publisherField.clear();
        categoryField.clear();
        yearField.clear();
        copiesField1.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
