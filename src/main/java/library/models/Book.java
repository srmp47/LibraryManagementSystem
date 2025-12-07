package library.models;


import library.models.enums.EventType;
import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;
import library.observers.EventManager;
import library.observers.listeners.PrinterListener;

import java.time.LocalDate;

public class Book extends LibraryItem {
    private final String isbn;
    private final String genre;
    private final int pageCount;

    public Book( Integer id, String title, int pageCount, String author, LibraryItemStatus status, LocalDate publishDate,
                 String isbn, String genre, LocalDate returnDate) {
        super(id, title, author, status, publishDate, LibraryItemType.BOOK, returnDate);
        this.isbn = isbn;
        this.genre = genre;
        this.pageCount = pageCount;
    }

    @Override
    public void setStatus(LibraryItemStatus status) {
        if(this.status == LibraryItemStatus.BORROWED && status == LibraryItemStatus.EXIST)
            sendNotification(EventType.RETURNED_BOOK);
        this.status = status;
        this.returnDate = null;
    }

    @Override
    public void display() {
        System.out.println("Book: " + getTitle());
        System.out.println("ID: " + getId());
        System.out.println("Author: " + getAuthor());
        System.out.println("ISBN: " + isbn);
        System.out.println("Genre: " + genre);
        System.out.println("Pages: " + pageCount);
        System.out.println("Status: " + getStatus());
        System.out.println("Published: " + getPublishDate());
        System.out.println("------------------------");
    }

    @Override
    protected EventManager createEventManager() {
        EventManager eventManager = new EventManager(EventType.ADDED_NEW_BOOK, EventType.RETURNED_BOOK);
        eventManager.subscribe(EventType.ADDED_NEW_BOOK, new PrinterListener());
        eventManager.subscribe(EventType.RETURNED_BOOK, new PrinterListener());
        return eventManager;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getGenre() {
        return genre;
    }

    public int getPageCount() {
        return pageCount;
    }
}