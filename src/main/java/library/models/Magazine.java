package library.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;

import java.time.LocalDate;

public class Magazine extends LibraryItem {
    private final String issueNumber;
    private final String publisher;
    private final String category;

    @JsonCreator
    public Magazine(@JsonProperty("id") Integer id,
                    @JsonProperty("title") String title,
                    @JsonProperty("author") String author,
                    @JsonProperty("status") LibraryItemStatus status,
                    @JsonProperty("publishDate") LocalDate publishDate,
                    @JsonProperty("issueNumber") String issueNumber,
                    @JsonProperty("publisher") String publisher,
                    @JsonProperty("category") String category,
                    @JsonProperty("returnDate") LocalDate returnDate) {
        super(id, title, author, status, publishDate, LibraryItemType.MAGAZINE, returnDate);
        this.issueNumber = issueNumber;
        this.publisher = publisher;
        this.category = category;
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