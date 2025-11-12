package library.models;

import library.models.enums.LibraryItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ThesisTest {

    private Thesis thesis;

    @BeforeEach
    void setUp() {
        thesis = TestDataFactory.createSampleThesis();
    }

    @Test
    void testThesisCreation() {
        assertNotNull(thesis);
        assertEquals("Machine Learning Applications", thesis.getTitle());
        assertEquals("John Doe", thesis.getAuthor());
        assertEquals(LibraryItemStatus.BANNED, thesis.getStatus());
        assertEquals("Stanford University", thesis.getUniversity());
        assertEquals("Computer Science", thesis.getDepartment());
        assertEquals("Dr. Smith", thesis.getAdvisor());
    }

    @Test
    void testThesisWithDifferentUniversity() {
        Thesis newThesis = new Thesis(
                7,
                "Advanced Algorithms",
                "Jane Smith",
                LibraryItemStatus.EXIST,
                LocalDate.of(2023, 5, 20),
                "MIT",
                "Electrical Engineering",
                "Dr. Johnson"
        );

        assertEquals("MIT", newThesis.getUniversity());
        assertEquals("Electrical Engineering", newThesis.getDepartment());
        assertEquals("Dr. Johnson", newThesis.getAdvisor());
    }

    @Test
    void testThesisDisplay() {
        assertDoesNotThrow(() -> thesis.display());
    }

    @Test
    void testThesisStatusChange() {
        thesis.setStatus(LibraryItemStatus.EXIST);
        assertEquals(LibraryItemStatus.EXIST, thesis.getStatus());
    }
}