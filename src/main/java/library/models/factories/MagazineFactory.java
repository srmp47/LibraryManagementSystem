package library.models.factories;

import library.models.Magazine;
import library.models.enums.LibraryItemStatus;

import java.time.LocalDate;

public class MagazineFactory implements LibraryItemFactory {
    private final String title;
    private final String author;
    private final LocalDate publishDate;
    private final LibraryItemStatus status;
    private final String issueNumber;
    private final String publisher;
    private final String category;
    public MagazineFactory(String title, String author, LocalDate publishDate,
                           LibraryItemStatus status,
                           String issueNumber, String publisher, String category) {
        this.title = title;
        this.author = author;
        this.publishDate = publishDate;
        this.status = status;
        this.issueNumber = issueNumber;
        this.publisher = publisher;
        this.category = category;

    }

    @Override
    public Magazine createLibraryItem() {
        return new Magazine(null, title, author, status, publishDate, issueNumber, publisher, category, null);
    }
}
