package library.models;

import library.models.enums.EventType;
import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;
import library.observers.EventManager;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class LibraryItem {
    private static final AtomicInteger numberOfItems = new AtomicInteger(0);
    protected final int id;
    protected final String title;
    protected final String author;
    protected final LocalDate publishDate;
    protected LibraryItemStatus status;
    protected final LibraryItemType type;
    protected LocalDate returnDate;
    protected final EventManager events ;
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public LibraryItemType getType() {
        return type;
    }


    public LibraryItem(Integer id, String title, String author, LibraryItemStatus status, LocalDate publishDate,
                       LibraryItemType type, LocalDate returnDate) {
        this.title = title;
        this.author = author;
        this.status = status;
        this.publishDate = publishDate;
        this.type = type;
        this.returnDate = returnDate;
        this.events = createEventManager();
        if (id != null) {
            this.id = id;
            numberOfItems.updateAndGet(current -> Math.max(current, id));
        } else {
            this.id = numberOfItems.incrementAndGet();
        }
    }

    public abstract void setStatus(LibraryItemStatus status);


    public void sendNotification(EventType eventType) {
        events.notify(eventType, getTitle());
    }

    public abstract void display();
    protected abstract EventManager createEventManager();

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public LibraryItemStatus getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public static void setCounter(int value) {
        numberOfItems.set(value);
    }

}