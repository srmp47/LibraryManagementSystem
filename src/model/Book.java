package model;

import model.enums.BookStatus;

import java.time.LocalDate;

public class Book {
    private final String title;
    private final String author;
    private BookStatus status;
    private final LocalDate publishDate;

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public BookStatus getStatus() {
        return status;
    }
    public Book(String title, String author, BookStatus status, LocalDate publishDate) {
        this.title = title;
        this.author = author;
        this.status = status;
        this.publishDate = publishDate;
    }
}
