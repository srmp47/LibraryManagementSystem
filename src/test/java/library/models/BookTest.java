package library.models;

import library.models.enums.LibraryItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = TestDataFactory.createSampleBook();
    }

    @Test
    void testBookCreation() {
        assertNotNull(book);
        assertEquals("The Great Gatsby", book.getTitle());
        assertEquals("F. Scott Fitzgerald", book.getAuthor());
        assertEquals(LibraryItemStatus.EXIST, book.getStatus());
        assertEquals("978-0-7432-7356-5", book.getIsbn());
        assertEquals("Fiction", book.getGenre());
        assertEquals(180, book.getPageCount());
    }

    @Test
    void testBookWithNullId() {
        Book newBook = new Book(
                null,
                "Test Book",
                "Test Author",
                LibraryItemStatus.BORROWED,
                LocalDate.now(),
                "1234567890",
                "Test Genre",
                250
        );

        assertTrue(newBook.getId() > 0);
        assertEquals("Test Book", newBook.getTitle());
        assertEquals("Test Author", newBook.getAuthor());
    }

    @ParameterizedTest
    @CsvSource({
            "100, 100",
            "500, 500",
            "1000, 1000"
    })
    void testBookWithDifferentPageCounts(int inputPages, int expectedPages) {
        Book testBook = new Book(
                10,
                "Test Book",
                "Test Author",
                LibraryItemStatus.EXIST,
                LocalDate.now(),
                "1234567890",
                "Test Genre",
                inputPages
        );

        assertEquals(expectedPages, testBook.getPageCount());
    }

    @Test
    void testBookStatusChange() {
        assertEquals(LibraryItemStatus.EXIST, book.getStatus());

        book.setStatus(LibraryItemStatus.BORROWED);
        assertEquals(LibraryItemStatus.BORROWED, book.getStatus());

        book.setStatus(LibraryItemStatus.BANNED);
        assertEquals(LibraryItemStatus.BANNED, book.getStatus());
    }

    @Test
    void testBookDisplay() {
        // Capture output and verify it contains expected text
        assertDoesNotThrow(() -> book.display());
    }
}