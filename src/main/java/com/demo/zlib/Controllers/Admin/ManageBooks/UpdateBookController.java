package com.demo.zlib.Controllers.Admin.ManageBooks;

import com.demo.zlib.Source.Book;
import com.demo.zlib.Source.Library;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateBookController {

    @FXML
    private AnchorPane contentPane;
    @FXML
    private VBox resultsContainer;
    @FXML
    private TextField keywordField;
    @FXML
    private MenuButton searchMenuButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button updateButton;

    private final Library library = new Library();
    private String selectedField = "All Field";

    @FXML
    public void initialize() {
        // Tải dữ liệu sách
        loadBooks();

        searchMenuButton.getItems().forEach(item -> {
            item.setOnAction(event -> {
                selectedField = item.getText();
                searchMenuButton.setText(selectedField);
            });
        });

        searchButton.setOnAction(event -> handleSearchButtonAction());

        updateButton.setOnAction(event -> handleUpdateBook());
    }

    private void loadBooks() {
        displayBooks(library.getBooks());
    }

    @FXML
    private void handleSearchButtonAction() {
        String keyword = keywordField.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            loadBooks();
            return;
        }

        List<Book> filteredBooks = filterBooksByKeyword(keyword);

        if (filteredBooks.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Results", "No books found for the given keyword.");
        } else {
            displayBooks(filteredBooks);
        }
    }

    private List<Book> filterBooksByKeyword(String keyword) {
        return library.getBooks().stream()
                .filter(book -> switch (selectedField) {
                    case "ISBN" -> book.getISBN().toLowerCase().contains(keyword);
                    case "Title" -> book.getTitle().toLowerCase().contains(keyword);
                    case "Author" -> book.getAuthor().toLowerCase().contains(keyword);
                    case "Category" -> book.getCategory().toLowerCase().contains(keyword);
                    case "All Field" -> book.getISBN().toLowerCase().contains(keyword)
                            || book.getTitle().toLowerCase().contains(keyword)
                            || book.getAuthor().toLowerCase().contains(keyword)
                            || book.getCategory().toLowerCase().contains(keyword)
                            || book.getPublisher().toLowerCase().contains(keyword);
                    default -> false;
                })
                .collect(Collectors.toList());
    }

    private void displayBooks(List<Book> books) {
        resultsContainer.getChildren().clear();

        for (Book book : books) {
            HBox bookBox = new HBox();
            bookBox.setSpacing(15);
            bookBox.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-background-color: #ffffff;");
            bookBox.setPrefHeight(150);

            // Container for image and button
            VBox imageAndButtonContainer = new VBox();
            imageAndButtonContainer.setSpacing(10);
            imageAndButtonContainer.setPrefWidth(100);

            // Book cover image
            ImageView bookCover = new ImageView();
            bookCover.setFitHeight(120);
            bookCover.setFitWidth(90);

            if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
                try {
                    bookCover.setImage(new Image(book.getCoverUrl()));
                } catch (Exception e) {
                    bookCover.setImage(new Image(getClass().getResource("/Images/ZLIB.png").toExternalForm()));
                }
            } else {
                bookCover.setImage(new Image(getClass().getResource("/Images/ZLIB.png").toExternalForm()));
            }

            // Update button
            Button updateButton = new Button("Update");
            updateButton.setStyle("-fx-background-color: white; -fx-font-weight: bold; -fx-text-fill: black;");
            updateButton.setOnAction(event -> openUpdateBookView(book));

            imageAndButtonContainer.getChildren().addAll(bookCover, updateButton);

            // Book information
            VBox bookInfo = new VBox();
            bookInfo.setSpacing(5);
            Text isbn = new Text("ISBN: " + book.getISBN());
            Text title = new Text("Title: " + book.getTitle());
            Text author = new Text("Author: " + book.getAuthor());
            Text publisher = new Text("Publisher: " + book.getPublisher());
            Text year = new Text("Year: " + book.getYear());
            Text category = new Text("Category: " + book.getCategory());
            Text copies = new Text("Copies: " + book.getCopiesQuantity()); // Display copies

            bookInfo.getChildren().addAll(isbn, title, author, publisher, year, category, copies);

            // Add components to the HBox
            bookBox.getChildren().addAll(imageAndButtonContainer, bookInfo);

            // Add the book box to the results container
            resultsContainer.getChildren().add(bookBox);
        }
    }



    @FXML
    private void handleUpdateBook() {
        String keyword = keywordField.getText().toLowerCase().trim();
        List<Book> matchedBooks = filterBooksByKeyword(keyword);

        if (!matchedBooks.isEmpty()) {
            openUpdateBookView(matchedBooks.get(0));
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please enter the correct ISBN or select a book to update.");
        }
    }

    private void openUpdateBookView(Book selectedBook) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/ManageBook/UpdateBook1.fxml"));
            Parent updateBook1Root = loader.load();

            Updatebook1Controller controller = loader.getController();
            controller.setLibrary(library);
            controller.setBookData(selectedBook);
            controller.setParentContentPane(contentPane);

            contentPane.getChildren().setAll(updateBook1Root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load update book view.");
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
