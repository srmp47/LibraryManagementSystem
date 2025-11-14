package library.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;

import java.time.LocalDate;

public class Reference extends LibraryItem {
    private final String referenceType;
    private final String edition;
    private final String subject;

    @JsonCreator
    public Reference(@JsonProperty("id") Integer id,
                     @JsonProperty("title") String title,
                     @JsonProperty("author") String author,
                     @JsonProperty("status") LibraryItemStatus status,
                     @JsonProperty("publishDate") LocalDate publishDate,
                     @JsonProperty("referenceType") String referenceType,
                     @JsonProperty("edition") String edition,
                     @JsonProperty("subject") String subject,
                     @JsonProperty("returnDate")LocalDate returnDate) {
        super(id, title, author, status, publishDate, LibraryItemType.REFERENCE, returnDate);
        this.referenceType = referenceType;
        this.edition = edition;
        this.subject = subject;
    }

    @Override
    public void display() {
        System.out.println("Reference: " + getTitle());
        System.out.println("ID: " + getId());
        System.out.println("Author: " + getAuthor());
        System.out.println("Type: " + referenceType);
        System.out.println("Edition: " + edition);
        System.out.println("Subject: " + subject);
        System.out.println("Status: " + getStatus());
        System.out.println("Published: " + getPublishDate());
        System.out.println("------------------------");
    }

    public String getReferenceType() { return referenceType; }
    public String getEdition() { return edition; }
    public String getSubject() { return subject; }
}