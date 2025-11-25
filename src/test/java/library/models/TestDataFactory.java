package library.models;

import library.models.*;
import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;

import java.time.LocalDate;

public class TestDataFactory {

    public static Book createSampleBook() {
        return new Book(
                1,
                "The Great Gatsby",
                "F. Scott Fitzgerald",
                LibraryItemStatus.EXIST,
                LocalDate.of(1925, 4, 10),
                "978-0-7432-7356-5",
                "Fiction",
                180
        );
    }

    public static Book createSampleBook(int id) {
        return new Book(
                id,
                "Test Book " + id,
                "Author " + id,
                LibraryItemStatus.EXIST,
                LocalDate.now().minusYears(id),
                "ISBN-" + id,
                "Genre " + id,
                100 + id
        );
    }

    public static Magazine createSampleMagazine() {
        return new Magazine(
                2,
                "National Geographic",
                "Editor in Chief",
                LibraryItemStatus.BORROWED,
                LocalDate.of(2023, 1, 15),
                "Vol. 245 No. 1",
                "National Geographic Society",
                "Science & Nature"
        );
    }

    public static Reference createSampleReference() {
        return new Reference(
                3,
                "Oxford English Dictionary",
                "Oxford University Press",
                LibraryItemStatus.EXIST,
                LocalDate.of(2020, 3, 20),
                "Dictionary",
                "Second Edition",
                "Linguistics"
        );
    }

    public static Thesis createSampleThesis() {
        return new Thesis(
                4,
                "Machine Learning Applications",
                "John Doe",
                LibraryItemStatus.BANNED,
                LocalDate.of(2022, 6, 15),
                "Stanford University",
                "Computer Science",
                "Dr. Smith"
        );
    }
}