package library.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;

import java.time.LocalDate;

public class Book extends LibraryItem {
    private final String isbn;
    private final String genre;
    private final int pageCount;

    @JsonCreator
    public Book(@JsonProperty("id") Integer id,
                @JsonProperty("title") String title,
                @JsonProperty("author") String author,
                @JsonProperty("status") LibraryItemStatus status,
                @JsonProperty("publishDate") LocalDate publishDate,
                @JsonProperty("isbn") String isbn,
                @JsonProperty("genre") String genre,
                @JsonProperty("pageCount") int pageCount) {
        super(id, title, author, status, publishDate, LibraryItemType.BOOK);
        this.isbn = isbn;
        this.genre = genre;
        this.pageCount = pageCount;
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
    public String getIsbn() { return isbn; }
    public String getGenre() { return genre; }
    public int getPageCount() { return pageCount; }
}