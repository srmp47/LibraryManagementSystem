package library.models;


import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;
import java.time.LocalDate;

public class Magazine extends LibraryItem {
    private final String issueNumber;
    private final String publisher;
    private final String category;

    public Magazine(Integer id, String title, String author, LibraryItemStatus status, LocalDate publishDate, String issueNumber,
                    String publisher, String category, LocalDate returnDate) {
        super(id, title, author, status, publishDate, LibraryItemType.MAGAZINE, returnDate);
        this.issueNumber = issueNumber;
        this.publisher = publisher;
        this.category = category;
    }
    @Override
    public void setStatus(LibraryItemStatus status) {
        this.status = status;
        this.returnDate = null;
    }

    @Override
    public void display() {
        System.out.println("Magazine: " + getTitle());
        System.out.println("ID: " + getId());
        System.out.println("Editor: " + getAuthor());
        System.out.println("Issue: " + issueNumber);
        System.out.println("Publisher: " + publisher);
        System.out.println("Category: " + category);
        System.out.println("Status: " + getStatus());
        System.out.println("Published: " + getPublishDate());
        System.out.println("------------------------");
    }



    public String getIssueNumber() {
        return issueNumber;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getCategory() {
        return category;
    }
}