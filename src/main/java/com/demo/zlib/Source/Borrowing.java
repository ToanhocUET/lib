package com.demo.zlib.Source;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Borrowing {
    private Member member;
    private String borrowingID;
    private Integer copyID;
    private Date borrowDate;
    private Date dueDate;
    private BookCopy bookCopy;

    public Borrowing(BookCopy bookCopy, Member member) {
        this.member = member;
        this.bookCopy = bookCopy;
        this.copyID = bookCopy.getCopyID();
        this.borrowDate = new Date(); // Khởi tạo borrowDate trước khi sử dụng nó
        this.borrowingID = borrowIDGenerator(); // Gọi borrowIDGenerator sau khi borrowDate được khởi tạo
        this.dueDate = this.calculateDueDate();
    }

    // Constructor
    public Borrowing(String borrowingID, Member member, Date borrowDate, Date dueDate, BookCopy bookCopy) {
        this.borrowingID = borrowingID;
        this.member = member;
        this.copyID = bookCopy.getCopyID();
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.bookCopy = bookCopy;
    }

    public Member getMember() {
        return member;
    }

    public String getBorrowingID() {
        return borrowingID;
    }

    public Integer getCopyID() {
        return copyID;
    }

    public void setCopyID(Integer copyID) {
        this.copyID = copyID;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public BookCopy getBookCopy() {
        return bookCopy;
    }

    // Tạo bộ sinh mã mượn sách
    private String borrowIDGenerator() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(this.borrowDate); // Đảm bảo rằng borrowDate không null
    }

    // Lấy về ngày đáo hạn
    private Date calculateDueDate() {
        long MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000L;
        long dueDateInMillis = System.currentTimeMillis() + (30 * MILLISECONDS_IN_A_DAY);
        return new Date(dueDateInMillis);
    }

    @Override
    public String toString() {
        return String.format("%-20s %-20s %-25s %-60s %-40s %-40s%n", borrowingID, this.bookCopy.getCopyID(), this.bookCopy.getBook().getISBN(),
                this.bookCopy.getBook().getTitle(), borrowDate, dueDate);
    }

    public void displayBorrowingDetails() {
        Book book = bookCopy.getBook();
        System.out.println("Borrowing ID :" + this.getBorrowingID());
        System.out.println("Copy ID: " + this.getBookCopy().getCopyID());
        System.out.println("ISBN : " + book.getISBN());
        System.out.println("Book : " + book.getTitle());
        System.out.println("Author : " + book.getAuthor());
        System.out.println("Publisher : " + book.getPublisher());
        System.out.println("Borrowing Date : " + this.getBorrowDate());
        System.out.println("Due Date : " + this.getDueDate());
    }
}
