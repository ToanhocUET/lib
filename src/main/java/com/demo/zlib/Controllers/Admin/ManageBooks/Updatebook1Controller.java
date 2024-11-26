package com.demo.zlib.Controllers.Admin.ManageBooks;

import com.demo.zlib.Source.Book;
import com.demo.zlib.Source.BookCopy;
import com.demo.zlib.Source.Library;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Updatebook1Controller {
    @FXML
    private Button cancel_btn1;
    @FXML
    private Button okButton_btn;
    @FXML
    private Button back_btn;
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
    private TextField copiesField1;

    private Library library;
    private Book currentBook;
    private AnchorPane parentContentPane;

    private String originalISBN;
    private String originalTitle;
    private String originalAuthor;
    private String originalPublisher;
    private String originalCategory;
    private String originalYear;
    private int originalCopies;

    public void setLibrary(Library library) {
        this.library = library;
    }

    public void setParentContentPane(AnchorPane contentPane) {
        this.parentContentPane = contentPane;
    }

    public void setBookData(Book book) {
        this.currentBook = book;
        this.originalISBN = book.getISBN();
        this.originalTitle = book.getTitle();
        this.originalAuthor = book.getAuthor();
        this.originalPublisher = book.getPublisher();
        this.originalCategory = book.getCategory();
        this.originalYear = book.getYear();
        this.originalCopies = book.getCopiesQuantity();

        isbnField.setText(originalISBN);
        titleField.setText(originalTitle);
        authorField.setText(originalAuthor);
        publisherField.setText(originalPublisher);
        categoryField.setText(originalCategory);
        yearField.setText(originalYear);
        copiesField1.setText(String.valueOf(originalCopies));
    }

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
                showAlert("Error", "Number of copies must be positive.", Alert.AlertType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Copies must be a valid number.", Alert.AlertType.ERROR);
            return;
        }

        if (!isbn.equals(originalISBN) && library.findBookByISBN(isbn) != null) {
            showAlert("Duplicate ISBN", "A book with this ISBN already exists.", Alert.AlertType.ERROR);
            return;
        }

        currentBook.setISBN(isbn);
        currentBook.setTitle(title);
        currentBook.setAuthor(author);
        currentBook.setPublisher(publisher);
        currentBook.setCategory(category);
        currentBook.setYear(year);
        currentBook.setCopiesQuantity(copies);
        currentBook.setStatus(copies > 0 ? "Available" : "Not Available");

        if (library != null) {
            library.updateBook(currentBook);

            int currentCopiesCount = currentBook.getCopies().size();

            if (copies > currentCopiesCount) {
                for (int i = 0; i < copies - currentCopiesCount; i++) {
                    BookCopy newCopy = new BookCopy(currentBook);
                    library.insertCopy(newCopy);
                    currentBook.addNewCopy(newCopy);
                }
            } else if (copies < currentCopiesCount) {
                for (int i = 0; i < currentCopiesCount - copies; i++) {
                    BookCopy copyToRemove = currentBook.getCopies().get(currentBook.getCopies().size() - 1);
                    library.updateBookCopyField(copyToRemove.getCopyID(), "Status", "Removed");
                    currentBook.getCopies().remove(copyToRemove);
                }
            }

            showAlert("Success", "Book updated successfully with updated copies.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Library not initialized.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        // Khôi phục dữ liệu ban đầu của sách
        isbnField.setText(originalISBN);
        titleField.setText(originalTitle);
        authorField.setText(originalAuthor);
        publisherField.setText(originalPublisher);
        categoryField.setText(originalCategory);
        yearField.setText(originalYear);
        copiesField1.setText(String.valueOf(originalCopies));
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            if (parentContentPane == null) {
                showAlert("Error", "Parent content pane is not initialized.", Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/ManageBook/UpdateBook.fxml"));
            Parent updateBookRoot = loader.load();

            UpdateBookController updateBookController = loader.getController();

            parentContentPane.getChildren().setAll(updateBookRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
