package library.models;

import library.models.enums.LibraryItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MagazineTest {

    private Magazine magazine;

    @BeforeEach
    void setUp() {
        magazine = TestDataFactory.createSampleMagazine();
    }

    @Test
    void testMagazineCreation() {
        assertNotNull(magazine);
        assertEquals("National Geographic", magazine.getTitle());
        assertEquals("Editor in Chief", magazine.getAuthor());
        assertEquals(LibraryItemStatus.BORROWED, magazine.getStatus());
        assertEquals("Vol. 245 No. 1", magazine.getIssueNumber());
        assertEquals("National Geographic Society", magazine.getPublisher());
        assertEquals("Science & Nature", magazine.getCategory());
    }

    @Test
    void testMagazineWithDifferentStatus() {
        Magazine newMagazine = new Magazine(
                5,
                "Tech Magazine",
                "Tech Editor",
                LibraryItemStatus.BANNED,
                LocalDate.of(2023, 12, 1),
                "Issue 50",
                "Tech Publishers",
                "Technology"
        );

        assertEquals(LibraryItemStatus.BANNED, newMagazine.getStatus());
        assertEquals("Tech Magazine", newMagazine.getTitle());
        assertEquals("Issue 50", newMagazine.getIssueNumber());
    }

    @Test
    void testMagazineDisplay() {
        assertDoesNotThrow(() -> magazine.display());
    }

    @Test
    void testMagazineGetters() {
        assertEquals("Vol. 245 No. 1", magazine.getIssueNumber());
        assertEquals("National Geographic Society", magazine.getPublisher());
        assertEquals("Science & Nature", magazine.getCategory());
    }
}