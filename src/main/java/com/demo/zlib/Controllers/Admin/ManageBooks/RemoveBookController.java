package com.demo.zlib.Controllers.Admin.ManageBooks;

import com.demo.zlib.Source.Book;
import com.demo.zlib.Source.Library;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.stream.Collectors;

public class RemoveBookController {

    @FXML
    private Button remove_btn;

    @FXML
    private Button search_btn;

    @FXML
    private TextField keywordField;

    @FXML
    private MenuButton searchMenuButton;

    @FXML
    private ScrollPane resultsScrollPane;

    @FXML
    private VBox resultsContainer;

    private final Library library = new Library();
    private String selectedField = "All Field";

    @FXML
    public void initialize() {
        if (remove_btn == null) {
            System.out.println("Error: remove_btn is not initialized. Check fx:id in FXML.");
            return;
        }

        loadBooks();

        remove_btn.setOnAction(event -> showRemoveInstructions());

        searchMenuButton.getItems().forEach(item -> {
            item.setOnAction(event -> {
                selectedField = item.getText();
                searchMenuButton.setText(selectedField);
            });
        });

        search_btn.setOnAction(event -> handleSearchButtonAction());
    }

    private void loadBooks() {
        List<Book> books = library.getBooks();
        displayBooks(books);
    }

    @FXML
    private void handleSearchButtonAction() {
        String keyword = keywordField.getText().toLowerCase().trim();
        List<Book> filteredBooks;

        switch (selectedField) {
            case "ISBN":
                filteredBooks = library.getBooks().stream()
                        .filter(book -> book.getISBN().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
                break;
            case "Title":
                filteredBooks = library.getBooks().stream()
                        .filter(book -> book.getTitle().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
                break;
            case "Author":
                filteredBooks = library.getBooks().stream()
                        .filter(book -> book.getAuthor().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
                break;
            case "Category":
                filteredBooks = library.getBooks().stream()
                        .filter(book -> book.getCategory().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
                break;
            case "All Field":
            default:
                filteredBooks = library.getBooks().stream()
                        .filter(book -> book.getISBN().toLowerCase().contains(keyword) ||
                                book.getTitle().toLowerCase().contains(keyword) ||
                                book.getAuthor().toLowerCase().contains(keyword) ||
                                book.getCategory().toLowerCase().contains(keyword) ||
                                book.getPublisher().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
                break;
        }

        displayBooks(filteredBooks);
    }

    private void displayBooks(List<Book> books) {
        resultsContainer.getChildren().clear();

        for (Book book : books) {
            HBox bookBox = new HBox();
            bookBox.setSpacing(10);
            bookBox.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-background-color: #ffffff;");
            bookBox.setPrefHeight(120);

            VBox imageAndButtonContainer = new VBox();
            imageAndButtonContainer.setSpacing(10); // Khoảng cách giữa ảnh và nút
            ImageView bookCover = new ImageView();
            bookCover.setFitHeight(80);
            bookCover.setFitWidth(60);

            if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
                try {
                    bookCover.setImage(new Image(book.getCoverUrl()));
                } catch (Exception e) {
                    bookCover.setImage(new Image(getClass().getResource("/Images/ZLIB.png").toExternalForm()));
                }
            } else {
                bookCover.setImage(new Image(getClass().getResource("/Images/ZLIB.png").toExternalForm()));
            }

            Button removeButton = new Button("Remove");
            removeButton.setStyle("-fx-background-color: white; -fx-font-weight: bold; -fx-text-fill: black;");
            removeButton.setOnAction(event -> removeBook(book));
            imageAndButtonContainer.getChildren().addAll(bookCover, removeButton);

            VBox bookInfo = new VBox();
            bookInfo.setSpacing(5);
            Text isbn = new Text("ISBN: " + book.getISBN());
            Text title = new Text("Title: " + book.getTitle());
            Text author = new Text("Author: " + book.getAuthor());
            Text publisher = new Text("Publisher: " + book.getPublisher());
            Text year = new Text("Year: " + book.getYear());
            Text category = new Text("Category: " + book.getCategory());
            Text status = new Text("Status: " + book.getStatus());

            bookInfo.getChildren().addAll(isbn, title, author, publisher, year, category, status);

            bookBox.getChildren().addAll(imageAndButtonContainer, bookInfo);

            resultsContainer.getChildren().add(bookBox);
        }
    }

    private void removeBook(Book book) {
        library.removeBook(book);
        handleSearchButtonAction();
        showAlert("Success", "Book removed successfully.", Alert.AlertType.INFORMATION);
    }

    private void showRemoveInstructions() {
        showAlert("Info", "Please use the 'Remove' button next to each book to remove it.", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
