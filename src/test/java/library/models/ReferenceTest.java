package library.models;

import library.models.enums.LibraryItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ReferenceTest {

    private Reference reference;

    @BeforeEach
    void setUp() {
        reference = TestDataFactory.createSampleReference();
    }

    @Test
    void testReferenceCreation() {
        assertNotNull(reference);
        assertEquals("Oxford English Dictionary", reference.getTitle());
        assertEquals("Oxford University Press", reference.getAuthor());
        assertEquals(LibraryItemStatus.EXIST, reference.getStatus());
        assertEquals("Dictionary", reference.getReferenceType());
        assertEquals("Second Edition", reference.getEdition());
        assertEquals("Linguistics", reference.getSubject());
    }

    @Test
    void testReferenceWithDifferentTypes() {
        Reference encyclopedia = new Reference(
                6,
                "World Encyclopedia",
                "Various Authors",
                LibraryItemStatus.BORROWED,
                LocalDate.of(2021, 1, 1),
                "Encyclopedia",
                "First Edition",
                "General Knowledge"
        );

        assertEquals("Encyclopedia", encyclopedia.getReferenceType());
        assertEquals("First Edition", encyclopedia.getEdition());
        assertEquals("General Knowledge", encyclopedia.getSubject());
    }

    @Test
    void testReferenceDisplay() {
        assertDoesNotThrow(() -> reference.display());
    }

    @Test
    void testReferenceStatusUpdate() {
        reference.setStatus(LibraryItemStatus.BORROWED);
        assertEquals(LibraryItemStatus.BORROWED, reference.getStatus());
    }
}