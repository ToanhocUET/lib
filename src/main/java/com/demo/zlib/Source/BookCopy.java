package com.demo.zlib.Source;

public class BookCopy {
    private static int iDGenerator = 0;
    private int copyID;
    private String status;
    private String ISBN;
    private Book book;

    public BookCopy(Book book) {
        this.copyID = iDGenerator++;
        this.status = "Available";
        this.book = book;
        this.ISBN = book.getISBN();
    }

    public int getCopyID() {
        return copyID;
    }

    public void setCopyID(int copyID) {
        this.copyID = copyID;
    }

    public String getStatus() {

        return status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public Book getBook() {
        return book;
    }

    @Override
    public String toString() {
        return String.format("%-15s %-25s %-25s%n", copyID, status, this.getBook().getISBN());
    }
}
