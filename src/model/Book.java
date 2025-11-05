package model;

import model.enums.BookStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class Book {
    private final String title;
    private final String author;
    private BookStatus status;
    private final LocalDate publishDate;

    @JsonCreator
    public Book(@JsonProperty("title") String title,
                @JsonProperty("author") String author,
                @JsonProperty("status") BookStatus status,
                @JsonProperty("publishDate") LocalDate publishDate) {
        this.title = title;
        this.author = author;
        this.status = status;
        this.publishDate = publishDate;
    }

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

    @Override
    public String toString() {
        return String.format("Book{title='%s', author='%s', status=%s, publishDate=%s}",
                title, author, status, publishDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return title.equals(book.title) &&
                author.equals(book.author) &&
                publishDate.equals(book.publishDate);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(title, author, publishDate);
    }
}