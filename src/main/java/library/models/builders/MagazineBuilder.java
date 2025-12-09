package library.models.builders;

import library.models.Magazine;
import library.models.enums.LibraryItemStatus;

import java.time.LocalDate;

public class MagazineBuilder implements LibraryItemBuilder {
    private Integer id;
    private String title;
    private String author;
    private LocalDate publishDate;
    private LibraryItemStatus status;
    private LocalDate returnDate;
    private String issueNumber;
    private String publisher;
    private String category;

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    @Override
    public void setStatus(LibraryItemStatus status) {
        this.status = status;
    }

    @Override
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Magazine getResult() {
        return new Magazine(id, title, author, status, publishDate, issueNumber, publisher, category, returnDate);
    }
}