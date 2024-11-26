package com.demo.zlib.Controllers.Admin.ManageBorrowings;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.demo.zlib.Source.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class DisplayMemberBorrowingAndReturnController {

    @FXML
    private Button exportDataButton;
    @FXML
    private TextField memberIdField;
    @FXML
    private Button searchMemberBorrowingsButton;
    @FXML
    private TableView<Borrowing> memberBorrowingsTableView;
    @FXML
    private TableColumn<Borrowing, String> borrowingIdColumn;
    @FXML
    private TableColumn<Borrowing, String> isbnColumn;
    @FXML
    private TableColumn<Borrowing, String> titleColumn;
    @FXML
    private TableColumn<Borrowing, Integer> copyIdColumn;
    @FXML
    private TableColumn<Borrowing, Date> borrowDateColumn;
    @FXML
    private TableColumn<Borrowing, Date> dueDateColumn;
    @FXML
    private Button returnBookButton;

    private final Library library = new Library();
    private final LibraryBorrowingManagement libraryBorrowingManagement = new LibraryBorrowingManagement();
    private final ObservableList<Borrowing> borrowingList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupBorrowingColumns();
        loadAllBorrowings();
        searchMemberBorrowingsButton.setOnAction(e -> handleSearchMemberButtonAction());
        returnBookButton.setOnAction(this::onConfirmReturnBookButtonClick);
        exportDataButton.setOnAction(this::onExportDataButtonClick);
    }

    private void setupBorrowingColumns() {
        borrowingIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBorrowingID()));
        isbnColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBookCopy().getBook().getISBN()));
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBookCopy().getBook().getTitle()));
        copyIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBookCopy().getCopyID()));
        borrowDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBorrowDate()));
        dueDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDueDate()));
    }

    private void loadAllBorrowings() {
        borrowingList.setAll(libraryBorrowingManagement.getAllBorrowings());
        borrowingList.removeIf(borrowing -> borrowing.getBookCopy().getStatus().equals("Available"));
        memberBorrowingsTableView.setItems(borrowingList);
    }

    @FXML
    private void handleSearchMemberButtonAction() {
        String memberIdStr = memberIdField.getText().trim();
        if (memberIdStr.isEmpty()) {
            showAlert("Error", "Please enter a valid Member ID.", Alert.AlertType.ERROR);
            return;
        }

        try {
            int memberId = Integer.parseInt(memberIdStr);
            List<Borrowing> borrowingsByMember = libraryBorrowingManagement.getBorrowingsByID(memberId);
            borrowingList.setAll(borrowingsByMember);
            borrowingList.removeIf(borrowing -> borrowing.getBookCopy().getStatus().equals("Available"));
            memberBorrowingsTableView.setItems(borrowingList);
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Member ID format.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onConfirmReturnBookButtonClick(ActionEvent event) {
        Borrowing selectedBorrowing = memberBorrowingsTableView.getSelectionModel().getSelectedItem();
        if (selectedBorrowing == null) {
            showAlert("Error", "Please select a borrowing to return.", Alert.AlertType.ERROR);
        } else {
            BookCopy bookCopy = selectedBorrowing.getBookCopy();
            bookCopy.setStatus("Available");
            library.updateBookCopyField(bookCopy.getCopyID(), "status", "Available");

            // Remove the returned borrowing record from the list
            borrowingList.remove(selectedBorrowing);

            // Refresh the borrowing table
            memberBorrowingsTableView.setItems(borrowingList);
            memberBorrowingsTableView.refresh();

            // Update the database
            libraryBorrowingManagement.deleteBorrowingFromDatabase(selectedBorrowing.getBorrowingID());

            showAlert("Success", "Book returned successfully.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void onExportDataButtonClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Borrowing Data");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"),
                new FileChooser.ExtensionFilter("Word Files", "*.docx"),
                new FileChooser.ExtensionFilter("PNG Files", "*.png")
        );
        File exportFile = fileChooser.showSaveDialog(new Stage());

        if (exportFile != null) {
            String fileExtension = getFileExtension(exportFile.getName());
            if ("txt".equalsIgnoreCase(fileExtension)) {
                exportToTextFile(exportFile);
            } else if ("pdf".equalsIgnoreCase(fileExtension)) {
                exportToPDF(exportFile);
            } else if ("xlsx".equalsIgnoreCase(fileExtension)) {
                exportToExcel(exportFile);
            } else if ("docx".equalsIgnoreCase(fileExtension)) {
                exportToWord(exportFile);
            } else if ("png".equalsIgnoreCase(fileExtension)) {
                exportToPNG(exportFile);
            } else {
                showAlert("Error", "Unsupported file format.", Alert.AlertType.ERROR);
            }
        }
    }

    private void exportToTextFile(File exportFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportFile))) {
            writer.write(String.format("%-20s %-25s %-25s %-10s %-20s %-20s%n", "Borrowing ID", "ISBN", "Title", "Copy ID", "Borrow Date", "Due Date"));
            for (Borrowing borrowing : borrowingList) {
                writer.write(String.format("%-20s %-25s %-25s %-10d %-20s %-20s%n",
                        borrowing.getBorrowingID(),
                        borrowing.getBookCopy().getBook().getISBN(),
                        borrowing.getBookCopy().getBook().getTitle(),
                        borrowing.getBookCopy().getCopyID(),
                        borrowing.getBorrowDate(),
                        borrowing.getDueDate()));
            }
            showAlert("Success", "Data exported successfully to " + exportFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Error", "Failed to export data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void exportToPDF(File exportFile) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(25, 700);
                contentStream.showText(String.format("%-20s %-25s %-25s %-10s %-20s %-20s", "Borrowing ID", "ISBN", "Title", "Copy ID", "Borrow Date", "Due Date"));
                contentStream.newLine();
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                for (Borrowing borrowing : borrowingList) {
                    contentStream.showText(String.format("%-20s %-25s %-25s %-10d %-20s %-20s",
                            borrowing.getBorrowingID(),
                            borrowing.getBookCopy().getBook().getISBN(),
                            borrowing.getBookCopy().getBook().getTitle(),
                            borrowing.getBookCopy().getCopyID(),
                            borrowing.getBorrowDate(),
                            borrowing.getDueDate()));
                    contentStream.newLine();
                }

                contentStream.endText();
            }

            document.save(exportFile);
            showAlert("Success", "Data exported successfully to " + exportFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Error", "Failed to export data to PDF: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void exportToExcel(File exportFile) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Borrowings");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Borrowing ID", "ISBN", "Title", "Copy ID", "Borrow Date", "Due Date"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Borrowing borrowing : borrowingList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(borrowing.getBorrowingID());
                row.createCell(1).setCellValue(borrowing.getBookCopy().getBook().getISBN());
                row.createCell(2).setCellValue(borrowing.getBookCopy().getBook().getTitle());
                row.createCell(3).setCellValue(borrowing.getBookCopy().getCopyID());
                row.createCell(4).setCellValue(borrowing.getBorrowDate().toString());
                row.createCell(5).setCellValue(borrowing.getDueDate().toString());
            }

            try (FileOutputStream fileOut = new FileOutputStream(exportFile)) {
                workbook.write(fileOut);
            }

            showAlert("Success", "Data exported successfully to " + exportFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Error", "Failed to export data to Excel: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void exportToWord(File exportFile) {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setBold(true);
            run.setText(String.format("%-20s %-25s %-25s %-10s %-20s %-20s", "Borrowing ID", "ISBN", "Title", "Copy ID", "Borrow Date", "Due Date"));
            run.addBreak();

            for (Borrowing borrowing : borrowingList) {
                run = document.createParagraph().createRun();
                run.setText(String.format("%-20s %-25s %-25s %-10d %-20s %-20s",
                        borrowing.getBorrowingID(),
                        borrowing.getBookCopy().getBook().getISBN(),
                        borrowing.getBookCopy().getBook().getTitle(),
                        borrowing.getBookCopy().getCopyID(),
                        borrowing.getBorrowDate(),
                        borrowing.getDueDate()));
                run.addBreak();
            }

            try (FileOutputStream out = new FileOutputStream(exportFile)) {
                document.write(out);
            }

            showAlert("Success", "Data exported successfully to " + exportFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Error", "Failed to export data to Word: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void exportToPNG(File exportFile) {
        try {
            int width = 800;
            int height = 600;
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g2d = bufferedImage.createGraphics();

            g2d.drawString(String.format("%-20s %-25s %-25s %-10s %-20s %-20s", "Borrowing ID", "ISBN", "Title", "Copy ID", "Borrow Date", "Due Date"), 10, 20);

            int yPosition = 40;
            for (Borrowing borrowing : borrowingList) {
                g2d.drawString(String.format("%-20s %-25s %-25s %-10d %-20s %-20s",
                        borrowing.getBorrowingID(),
                        borrowing.getBookCopy().getBook().getISBN(),
                        borrowing.getBookCopy().getBook().getTitle(),
                        borrowing.getBookCopy().getCopyID(),
                        borrowing.getBorrowDate(),
                        borrowing.getDueDate()), 10, yPosition);
                yPosition += 20;
            }

            g2d.dispose();
            ImageIO.write(bufferedImage, "png", exportFile);
            showAlert("Success", "Data exported successfully to " + exportFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Error", "Failed to export data to PNG: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex > 0 && lastIndex < fileName.length() - 1) {
            return fileName.substring(lastIndex + 1);
        }
        return "";
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}