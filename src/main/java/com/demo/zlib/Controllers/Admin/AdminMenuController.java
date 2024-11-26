package com.demo.zlib.Controllers.Admin;

import com.demo.zlib.Source.Library;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable {
    @FXML
    private Button managebooks_btn;
    @FXML
    private Button managemembers_btn;
    @FXML
    private Button manageborrowings_btn;
    @FXML
    private Button logout_btn;
    @FXML
    private Button report_btn;
    @FXML
    private BarChart<String, Number> libraryBarChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private Library library;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        library = new Library();
        int totalBooks = library.getBooks().size();
        int activeMembers = library.getMembers().size();
        int borrowingsToday = getBorrowingsTodayCount();
        populateBarChart(totalBooks, activeMembers, borrowingsToday);
    }

    private void populateBarChart(int totalBooks, int activeMembers, int borrowingsToday) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Library Data");
        series.getData().add(new XYChart.Data<>("Total Books", totalBooks));
        series.getData().add(new XYChart.Data<>("Active Members", activeMembers));
        series.getData().add(new XYChart.Data<>("Borrowings Today", borrowingsToday));
        libraryBarChart.getData().clear();
        libraryBarChart.getData().add(series);
    }

    private int getBorrowingsTodayCount() {
        String url = "jdbc:sqlite:" + getClass().getClassLoader().getResource("BooksCopies.db").getPath();
        String sql = "SELECT COUNT(*) AS totalBorrowings FROM BooksCopies WHERE Status = 'Borrowed'";
        int totalBorrowings = 0;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                totalBorrowings = rs.getInt("totalBorrowings");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalBorrowings;
    }

    @FXML
    private void handleManageBooksButton(ActionEvent event) {
        switchScene(event, "/Fxml/Admin/ManageBook/ManageBooks.fxml");
    }

    @FXML
    private void handleManageMembersButton(ActionEvent event) {
        switchScene(event, "/Fxml/Admin/ManageMembers/ManageMembers.fxml");
    }

    @FXML
    private void handleManageBorrowingsButton(ActionEvent event) {
        switchScene(event, "/Fxml/Admin/ManageBorrowing/ManageBorrowings.fxml");
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) {
        switchScene(event, "/Fxml/Login.fxml");
    }

    @FXML
    private void handleReportButton(ActionEvent event) {
        System.out.println("Report Button Clicked");
    }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
