package library.models.factories;

import library.models.Reference;
import library.models.enums.LibraryItemStatus;

import java.time.LocalDate;

public class ReferenceFactory implements LibraryItemFactory {
    private final String title;
    private final String author;
    private final LibraryItemStatus status;
    private final LocalDate publishDate;
    private final String referenceType;
    private final String edition;
    private final String subject;

    public ReferenceFactory(String title, String author, LibraryItemStatus status,
                            LocalDate publishDate, String referenceType,
                            String edition, String subject) {
        this.title = title;
        this.author = author;
        this.status = status;
        this.publishDate = publishDate;
        this.referenceType = referenceType;
        this.edition = edition;
        this.subject = subject;
    }

    @Override
    public Reference createLibraryItem() {
        return new Reference(null, title, author, status, publishDate,
                referenceType, edition, subject, null);
    }

}