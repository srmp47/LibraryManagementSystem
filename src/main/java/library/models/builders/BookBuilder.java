package library.models.builders;

import library.models.Book;
import library.models.enums.LibraryItemStatus;

import java.time.LocalDate;


public class BookBuilder implements LibraryItemBuilder {
    private Integer id;
    private String title;
    private String author;
    private LocalDate publishDate;
    private LibraryItemStatus status;
    private LocalDate returnDate;
    private String isbn;
    private String genre;
    private int pageCount;

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    @Override
    public void setStatus(LibraryItemStatus status) {
        this.status = status;
    }

    @Override
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public Book getResult() {
        return new Book(id, title, pageCount, author, status, publishDate, isbn, genre, returnDate);
    }
}