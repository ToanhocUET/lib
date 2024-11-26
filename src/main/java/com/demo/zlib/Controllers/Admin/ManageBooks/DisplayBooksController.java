package com.demo.zlib.Controllers.Admin.ManageBooks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.demo.zlib.Source.Library;
import com.demo.zlib.Source.Book;

public class DisplayBooksController {

    @FXML
    private TableView<Book> tableView;

    @FXML
    private TableColumn<Book, Integer> orderColumn;

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
    private TableColumn<Book, Integer> copiesColumn;

    @FXML
    private TableColumn<Book, String> statusColumn;

    private final Library library = new Library();
    private final ObservableList<Book> booksList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        orderColumn.setCellValueFactory(new PropertyValueFactory<>("order"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        copiesColumn.setCellValueFactory(new PropertyValueFactory<>("copiesQuantity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadBooks();
    }

    private void loadBooks() {
        int order = 1;
        for (Book book : library.getBooks()) {
            book.setOrder(order++);
            booksList.add(book);
        }

        tableView.setItems(booksList);
    }
}
