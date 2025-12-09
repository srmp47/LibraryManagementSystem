package library.models.builders;

import library.models.LibraryItem;
import library.models.enums.LibraryItemStatus;

import java.time.LocalDate;


public interface LibraryItemBuilder {
    void setId(Integer id);
    void setTitle(String title);
    void setAuthor(String author);
    void setPublishDate(LocalDate publishDate);
    void setStatus(LibraryItemStatus status);
    void setReturnDate(LocalDate returnDate);
}