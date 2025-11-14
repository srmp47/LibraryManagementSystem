package library.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Book.class, name = "BOOK"),
        @JsonSubTypes.Type(value = Magazine.class, name = "MAGAZINE"),
        @JsonSubTypes.Type(value = Thesis.class, name = "THESIS"),
        @JsonSubTypes.Type(value = Reference.class, name = "REFERENCE")
})
public abstract class LibraryItem {
    private static final AtomicInteger numberOfItems = new AtomicInteger(0);
    protected final int id;
    protected final String title;
    protected final String author;
    protected final LocalDate publishDate;
    protected LibraryItemStatus status;
    protected final LibraryItemType type;
    protected LocalDate returnDate;

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public LibraryItemType getType() {
        return type;
    }

    @JsonCreator
    public LibraryItem(@JsonProperty("id") Integer id,
                       @JsonProperty("title") String title,
                       @JsonProperty("author") String author,
                       @JsonProperty("status") LibraryItemStatus status,
                       @JsonProperty("publishDate") LocalDate publishDate,
                       @JsonProperty("type") LibraryItemType type,
                       @JsonProperty("returnDate")LocalDate returnDate) {
        this.title = title;
        this.author = author;
        this.status = status;
        this.publishDate = publishDate;
        this.type = type;
        this.returnDate = returnDate;
        if (id != null) {
            this.id = id;
            numberOfItems.updateAndGet(current -> Math.max(current, id));
        } else {
            this.id = numberOfItems.incrementAndGet();
        }
    }

    public void setStatus(LibraryItemStatus status) {
        this.status = status;
    }

    public abstract void display();
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public LocalDate getPublishDate() { return publishDate; }
    public LibraryItemStatus getStatus(){ return status; }
    public int getId() { return id; }
    public static void setCounter(int value) {numberOfItems.set(value);}

}