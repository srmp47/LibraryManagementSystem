package library.models;

import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryItemTest {

    private Book book;
    private Magazine magazine;
    private Reference reference;
    private Thesis thesis;

    @BeforeEach
    void setUp() {
        book = TestDataFactory.createSampleBook();
        magazine = TestDataFactory.createSampleMagazine();
        reference = TestDataFactory.createSampleReference();
        thesis = TestDataFactory.createSampleThesis();
    }

    @Test
    void testLibraryItemCreation() {
        assertNotNull(book);
        assertNotNull(magazine);
        assertNotNull(reference);
        assertNotNull(thesis);
    }

    @Test
    void testGetters() {
        assertEquals("The Great Gatsby", book.getTitle());
        assertEquals("F. Scott Fitzgerald", book.getAuthor());
        assertEquals(LibraryItemStatus.EXIST, book.getStatus());
        assertEquals(LocalDate.of(1925, 4, 10), book.getPublishDate());
        assertEquals(1, book.getId());
        assertEquals(LibraryItemType.BOOK, book.getType());
    }

    @Test
    void testSetters() {
        book.setStatus(LibraryItemStatus.BORROWED);
        assertEquals(LibraryItemStatus.BORROWED, book.getStatus());

        magazine.setStatus(LibraryItemStatus.BANNED);
        assertEquals(LibraryItemStatus.BANNED, magazine.getStatus());
    }

    @Test
    void testIdAutoGeneration() {
        LibraryItem.setCounter(100); // Reset counter

        Book newBook = new Book(
                null,
                "New Book",
                "New Author",
                LibraryItemStatus.EXIST,
                LocalDate.now(),
                "1234567890",
                "Test Genre",
                200
        );

        assertEquals(101, newBook.getId());
    }

    @Test
    void testDisplayMethod() {
        // This test verifies that display method doesn't throw exceptions
        assertDoesNotThrow(() -> book.display());
        assertDoesNotThrow(() -> magazine.display());
        assertDoesNotThrow(() -> reference.display());
        assertDoesNotThrow(() -> thesis.display());
    }

    @Test
    void testItemTypeSpecificGetters() {
        assertEquals("978-0-7432-7356-5", book.getIsbn());
        assertEquals("Fiction", book.getGenre());
        assertEquals(180, book.getPageCount());

        assertEquals("Vol. 245 No. 1", magazine.getIssueNumber());
        assertEquals("National Geographic Society", magazine.getPublisher());
        assertEquals("Science & Nature", magazine.getCategory());

        assertEquals("Dictionary", reference.getReferenceType());
        assertEquals("Second Edition", reference.getEdition());
        assertEquals("Linguistics", reference.getSubject());

        assertEquals("Stanford University", thesis.getUniversity());
        assertEquals("Computer Science", thesis.getDepartment());
        assertEquals("Dr. Smith", thesis.getAdvisor());
    }
}