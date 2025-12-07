package library.models.factories;

import library.models.Thesis;
import library.models.enums.LibraryItemStatus;

import java.time.LocalDate;

public class ThesisFactory implements LibraryItemFactory {
    private final String title;
    private final String author;
    private final LibraryItemStatus status;
    private final LocalDate publishDate;
    private final String university;
    private final String department;
    private final String advisor;

    public ThesisFactory(String title, String author, LibraryItemStatus status,
                         LocalDate publishDate, String university,
                         String department, String advisor) {
        this.title = title;
        this.author = author;
        this.status = status;
        this.publishDate = publishDate;
        this.university = university;
        this.department = department;
        this.advisor = advisor;
    }

    @Override
    public Thesis createLibraryItem() {
        return new Thesis(null, title, author, status, publishDate,
                university, department, advisor, null);
    }
}