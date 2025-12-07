package library.models;


import library.models.enums.EventType;
import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;
import library.observers.EventManager;
import library.observers.listeners.PrinterListener;

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
        if(this.status == LibraryItemStatus.BORROWED && status == LibraryItemStatus.EXIST)
            sendNotification(EventType.RETURNED_MAGAZINE);
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

    @Override
    protected EventManager createEventManager() {
        EventManager eventManager =  new EventManager(EventType.ADDED_NEW_MAGAZINE, EventType.RETURNED_MAGAZINE);
        eventManager.subscribe(EventType.ADDED_NEW_MAGAZINE, new PrinterListener());
        eventManager.subscribe(EventType.RETURNED_MAGAZINE, new PrinterListener());
        return eventManager;
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