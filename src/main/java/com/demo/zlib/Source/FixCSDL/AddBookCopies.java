package com.demo.zlib.Source.FixCSDL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddBookCopies {
    private static int copyID = 1; // copyID bắt đầu từ 1

    public static void main(String[] args) {
        // Sử dụng ClassLoader để lấy đường dẫn thực tế từ resources
        String booksDbUrl = "jdbc:sqlite:" + AddBookCopies.class.getClassLoader().getResource("Books.db").getPath();
        String booksCopiesDbUrl = "jdbc:sqlite:" + AddBookCopies.class.getClassLoader().getResource("BooksCopies.db").getPath();

        System.out.println("Đường dẫn cơ sở dữ liệu Books: " + booksDbUrl);
        System.out.println("Đường dẫn cơ sở dữ liệu BooksCopies: " + booksCopiesDbUrl);

        try (Connection booksConn = DriverManager.getConnection(booksDbUrl);
             Connection booksCopiesConn = DriverManager.getConnection(booksCopiesDbUrl)) {

            System.out.println("Kết nối cơ sở dữ liệu thành công!");

            // Xóa tất cả bản sao trong bảng BooksCopies
            deleteAllCopies(booksCopiesConn);

            // Lấy giá trị copyID lớn nhất từ cơ sở dữ liệu
            String getMaxCopyIDQuery = "SELECT MAX(copyID) AS maxCopyID FROM BooksCopies";
            try (PreparedStatement maxCopyIDStmt = booksCopiesConn.prepareStatement(getMaxCopyIDQuery);
                 ResultSet rs = maxCopyIDStmt.executeQuery()) {
                if (rs.next()) {
                    int maxCopyID = rs.getInt("maxCopyID");
                    copyID = (maxCopyID > 0) ? maxCopyID + 1 : 1; // Nếu có bản ghi, bắt đầu từ maxCopyID + 1
                } else {
                    copyID = 1; // Nếu không có bản ghi nào, bắt đầu từ 1
                }
                System.out.println("Giá trị copyID bắt đầu: " + copyID);
            }

            // Cập nhật trạng thái tất cả các bản sao thành 'Available'
            updateAllCopiesStatus(booksCopiesConn);

            String selectBooksQuery = "SELECT ISBN, copiesQuantity FROM Books";
            try (PreparedStatement selectBooksStmt = booksConn.prepareStatement(selectBooksQuery);
                 ResultSet booksResultSet = selectBooksStmt.executeQuery()) {

                String insertCopiesQuery = "INSERT INTO BooksCopies (ISBN, copyID, Status) VALUES (?, ?, ?)";
                try (PreparedStatement insertCopiesStmt = booksCopiesConn.prepareStatement(insertCopiesQuery)) {

                    while (booksResultSet.next()) {
                        String isbn = booksResultSet.getString("ISBN");
                        int copiesQuantity = booksResultSet.getInt("copiesQuantity");

                        for (int i = 0; i < copiesQuantity; i++) {
                            insertCopiesStmt.setString(1, isbn);  // ISBN
                            insertCopiesStmt.setInt(2, copyID);   // copyID duy nhất
                            insertCopiesStmt.setString(3, "Available"); // Trạng thái mặc định
                            insertCopiesStmt.executeUpdate();

                            copyID++; // Tăng copyID sau mỗi bản sao
                        }
                        System.out.println("Đã thêm bản sao cho sách ISBN: " + isbn);
                    }

                    System.out.println("Cập nhật thành công bảng BooksCopies!");
                }
            }

        } catch (Exception e) {
            System.out.println("Đã xảy ra lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xóa tất cả các bản sao trong bảng BooksCopies.
     */
    private static void deleteAllCopies(Connection booksCopiesConn) {
        String deleteCopiesQuery = "DELETE FROM BooksCopies";
        try (PreparedStatement deleteStmt = booksCopiesConn.prepareStatement(deleteCopiesQuery)) {
            int rowsDeleted = deleteStmt.executeUpdate();
            System.out.println("Đã xóa " + rowsDeleted + " bản sao từ bảng BooksCopies.");
        } catch (Exception e) {
            System.out.println("Đã xảy ra lỗi khi xóa bản sao: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật trạng thái của tất cả các bản sao thành 'Available'.
     */
    private static void updateAllCopiesStatus(Connection booksCopiesConn) {
        String updateStatusQuery = "UPDATE BooksCopies SET Status = 'Available'";
        try (PreparedStatement updateStmt = booksCopiesConn.prepareStatement(updateStatusQuery)) {
            int rowsUpdated = updateStmt.executeUpdate();
            System.out.println("Đã cập nhật trạng thái của " + rowsUpdated + " bản sao thành 'Available'.");
        } catch (Exception e) {
            System.out.println("Đã xảy ra lỗi khi cập nhật trạng thái bản sao: " + e.getMessage());
            e.printStackTrace();
        }
    }
}