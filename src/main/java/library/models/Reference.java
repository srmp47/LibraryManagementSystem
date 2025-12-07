package library.models;


import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;


import java.time.LocalDate;

public class Reference extends LibraryItem {
    private final String referenceType;
    private final String edition;
    private final String subject;


    public Reference( Integer id, String title, String author, LibraryItemStatus status, LocalDate publishDate,
                      String referenceType, String edition, String subject, LocalDate returnDate) {
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



    public String getReferenceType() {
        return referenceType;
    }

    public String getEdition() {
        return edition;
    }

    public String getSubject() {
        return subject;
    }
}