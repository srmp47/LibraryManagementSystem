package library.models.factories;

import library.models.Book;
import library.models.enums.LibraryItemStatus;

import java.time.LocalDate;

public class BookFactory implements LibraryItemFactory {
    private final String title;
    private final String author;
    private final LibraryItemStatus status;
    private final LocalDate publishDate;
    private final String isbn;
    private final String genre;
    private final int pageCount;
    public BookFactory(String title,  String author,LibraryItemStatus status, LocalDate publishDate,
                       String isbn,  String genre, int pageCount) {
        this.genre = genre;
        this.title = title;
        this.author = author;
        this.status = status;
        this.publishDate = publishDate;
        this.isbn = isbn;
        this.pageCount = pageCount;
    }

    @Override
    public Book createLibraryItem() {
        return new Book(null, title, pageCount, author, status, publishDate, isbn, genre, null) {
        };
    }
}
