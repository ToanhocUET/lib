package com.demo.zlib.Controllers.Admin.ManageBooks;

import com.demo.zlib.Source.Book;
import com.demo.zlib.Source.Library;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchBookController {

    @FXML
    private TextField keywordField;

    @FXML
    private MenuButton searchMenuButton;

    @FXML
    private MenuButton inDatabase;

    @FXML
    private Button searchButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox resultsContainer;

    private Library library;
    private String selectedField = "All Field";
    private String searchSource = "Database";

    @FXML
    public void initialize() {
        library = new Library();

        searchMenuButton.getItems().forEach(item -> item.setOnAction(event -> {
            selectedField = item.getText();
            searchMenuButton.setText(selectedField);
        }));

        inDatabase.getItems().forEach(item -> item.setOnAction(event -> {
            searchSource = item.getText();
            inDatabase.setText(searchSource);
        }));
    }

    @FXML
    private void handleSearchButtonAction() {
        String keyword = keywordField.getText().toLowerCase().trim();

        if (searchSource.equals("Database")) {
            searchInDatabase(keyword);
        } else if (searchSource.equals("API")) {
            try {
                searchInAPI(keyword);
            } catch (org.apache.hc.core5.http.ParseException e) {
                showAlert("Error", "Failed to parse the API response: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void searchInDatabase(String keyword) {
        List<Book> filteredBooks = library.getBooks();

        switch (selectedField) {
            case "ISBN":
                filteredBooks = filteredBooks.stream()
                        .filter(book -> book.getISBN().toLowerCase().contains(keyword))
                        .toList();
                break;
            case "Title":
                filteredBooks = filteredBooks.stream()
                        .filter(book -> book.getTitle().toLowerCase().contains(keyword))
                        .toList();
                break;
            case "Author":
                filteredBooks = filteredBooks.stream()
                        .filter(book -> book.getAuthor().toLowerCase().contains(keyword))
                        .toList();
                break;
            case "Category":
                filteredBooks = filteredBooks.stream()
                        .filter(book -> book.getCategory().toLowerCase().contains(keyword))
                        .toList();
                break;
            case "All Field":
            default:
                filteredBooks = filteredBooks.stream()
                        .filter(book -> book.getISBN().toLowerCase().contains(keyword) ||
                                book.getTitle().toLowerCase().contains(keyword) ||
                                book.getAuthor().toLowerCase().contains(keyword) ||
                                book.getCategory().toLowerCase().contains(keyword) ||
                                book.getPublisher().toLowerCase().contains(keyword) ||
                                book.getYear().toLowerCase().contains(keyword))
                        .toList();
                break;
        }
        updateUIWithBooks(filteredBooks);
    }

    private void searchInAPI(String keyword) throws org.apache.hc.core5.http.ParseException {
        String apiKey = "AIzaSyDHCFLDbjiJW6iZaWg3V3JNY5YqTkS2p-g"; // Thay bằng API Key của bạn
        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + keyword + "&key=" + apiKey;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            request.setConfig(RequestConfig.custom()
                    .setConnectTimeout(Timeout.ofSeconds(10))
                    .setResponseTimeout(Timeout.ofSeconds(10))
                    .build());

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonArray itemsArray = jsonResponse.getAsJsonArray("items");

                    List<Book> booksFromAPI = new ArrayList<>();
                    if (itemsArray != null) {
                        for (var itemElement : itemsArray) {
                            JsonObject itemJson = itemElement.getAsJsonObject();
                            JsonObject volumeInfo = itemJson.getAsJsonObject("volumeInfo");

                            String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown";
                            String authors = "Unknown";
                            if (volumeInfo.has("authors")) {
                                JsonArray authorsArray = volumeInfo.getAsJsonArray("authors");
                                List<String> authorsList = new ArrayList<>();
                                for (int i = 0; i < authorsArray.size(); i++) {
                                    authorsList.add(authorsArray.get(i).getAsString());
                                }
                                authors = String.join(", ", authorsList);
                            }
                            String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : "Unknown";
                            String year = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "Unknown";
                            String category = volumeInfo.has("categories") ? volumeInfo.getAsJsonArray("categories").get(0).getAsString() : "Unknown";
                            String coverUrl = volumeInfo.has("imageLinks") && volumeInfo.getAsJsonObject("imageLinks").has("thumbnail")
                                    ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
                                    : null;

                            String isbn = "Unknown";
                            if (volumeInfo.has("industryIdentifiers")) {
                                JsonArray industryIdentifiers = volumeInfo.getAsJsonArray("industryIdentifiers");
                                if (industryIdentifiers.size() > 0) {
                                    isbn = industryIdentifiers.get(0).getAsJsonObject().get("identifier").getAsString();
                                }
                            }

                            Book book = new Book(
                                    title,
                                    authors,
                                    publisher,
                                    isbn,      // ISBN
                                    category,  // Category
                                    0,         // CopiesQuantity
                                    "Available",
                                    year       // PublishedDate
                            );
                            book.setCoverUrl(coverUrl);
                            booksFromAPI.add(book);
                        }
                    }

                    updateUIWithBooks(booksFromAPI);
                } else {
                    showAlert("Error", "API responded with status code: " + response.getCode(), Alert.AlertType.ERROR);
                }
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to fetch data from API: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void updateUIWithBooks(List<Book> books) {
        resultsContainer.getChildren().clear();

        for (Book book : books) {
            HBox bookBox = new HBox();
            bookBox.setSpacing(10);
            bookBox.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-background-color: #ffffff;");
            bookBox.setPrefHeight(160);

            VBox imageAndButtonContainer = new VBox();
            imageAndButtonContainer.setSpacing(5);
            imageAndButtonContainer.setPrefWidth(80);

            ImageView bookCover = new ImageView();
            bookCover.setFitHeight(100);
            bookCover.setFitWidth(80);

            String coverUrl = book.getCoverUrl();
            try {
                if (coverUrl != null && !coverUrl.isEmpty()) {
                    bookCover.setImage(new Image(coverUrl));
                } else {
                    bookCover.setImage(new Image(getClass().getResource("/Images/ZLIB.png").toExternalForm()));
                }
            } catch (Exception e) {
                bookCover.setImage(new Image(getClass().getResource("/Images/ZLIB.png").toExternalForm()));
            }

            Button addButton = new Button("Add Book");
            addButton.setStyle("-fx-background-color: white; -fx-font-weight: bold;");
            addButton.setOnAction(event -> addBookToDatabase(book));

            imageAndButtonContainer.getChildren().addAll(bookCover, addButton);

            // Hiển thị thông tin sách
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




    private void addBookToDatabase(Book book) {
        try {
            // Nếu sách có URL ảnh bìa
            if (book.getCoverUrl() != null) {
                // Lấy thư mục lưu ảnh từ ClassLoader
                String saveDirectory = getClass().getClassLoader()
                        .getResource("Images")
                        .getPath();

                // Tên file ảnh là ISBN của sách (hoặc tên khác nếu cần)
                String fileName = book.getISBN() + ".jpg";

                // Gọi phương thức để tải và lưu ảnh
                String savedImagePath = ImageDownloader.downloadImage(book.getCoverUrl(), saveDirectory, fileName);

                if (savedImagePath != null) {
                    // Cập nhật URL ảnh bìa của sách để sử dụng từ file cục bộ
                    book.setCoverUrl("file:" + savedImagePath);
                }
            }

            // Thêm sách vào cơ sở dữ liệu
            library.addBook(book);
            showAlert("Success", "The book has been added to the database.", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while adding the book: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleDatabaseSelection() {
        searchSource = "Database";
        inDatabase.setText("Database");
    }

    @FXML
    private void handleAPISelection() {
        searchSource = "API";
        inDatabase.setText("API");
    }
}
