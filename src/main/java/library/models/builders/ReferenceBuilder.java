package library.models.builders;

import library.models.Reference;
import library.models.enums.LibraryItemStatus;

import java.time.LocalDate;


public class ReferenceBuilder implements LibraryItemBuilder {
    private Integer id;
    private String title;
    private String author;
    private LocalDate publishDate;
    private LibraryItemStatus status;
    private LocalDate returnDate;
    private String referenceType;
    private String edition;
    private String subject;

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

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Reference getResult() {
        return new Reference(id, title, author, status, publishDate, referenceType, edition, subject, returnDate);
    }
}