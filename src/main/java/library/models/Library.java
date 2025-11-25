package library.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import library.models.data_structures.GenericLinkedList;
import library.models.enums.LibraryItemStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Library {
    private final GenericLinkedList<LibraryItem> libraryItems;
    private final HashMap<Integer, LibraryItem> libraryItemHashMap;
    private final String dataFile;
    private final ObjectMapper objectMapper;

    public Library() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        this.dataFile = "library_data.pb";
        this.libraryItemHashMap = new HashMap<>();
        this.libraryItems = readFromFile(this.dataFile);
    }

    public Library(String dataFile) {
        this.dataFile = dataFile;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        this.libraryItemHashMap = new HashMap<>();
        this.libraryItems = readFromFile(dataFile);
    }

    public GenericLinkedList<LibraryItem> getLibraryItems() {
        return libraryItems;
    }

    public void addLibraryItem(LibraryItem libraryItem) {
        this.libraryItems.add(libraryItem);
        this.libraryItemHashMap.put(libraryItem.getId(), libraryItem);
    }

    public void removeLibraryItem(LibraryItem libraryItem) {
        libraryItems.remove(libraryItem);
        libraryItemHashMap.remove(libraryItem.getId());
    }

    public GenericLinkedList<LibraryItem> sortLibraryItems() {
        GenericLinkedList<LibraryItem> libraryItemsCopy = new GenericLinkedList<>();
        for (LibraryItem item : libraryItems) {
            libraryItemsCopy.add(item);
        }
        libraryItemsCopy.sort(Comparator.comparing(LibraryItem::getPublishDate).reversed());
        return libraryItemsCopy;
    }

    public GenericLinkedList<LibraryItem> search(String keyword) {
        return libraryItems.filter(libraryItem ->
                libraryItem.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        libraryItem.getAuthor().toLowerCase().contains(keyword.toLowerCase())
        );
    }

    public LibraryItem getLibraryItemById(int id) {
        return libraryItemHashMap.get(id);
    }

    public boolean borrowItem(int itemId, LocalDate expectedReturnDate) {
        LibraryItem item = libraryItemHashMap.get(itemId);
        if (item == null) {
            System.out.println("❌ Item not found with ID: " + itemId);
            return false;
        }

        if (item.getStatus() != LibraryItemStatus.EXIST) {
            System.out.println("❌ Item is not available for borrowing. Current status: " + item.getStatus());
            return false;
        }

        item.setStatus(LibraryItemStatus.BORROWED);
        item.setReturnDate(expectedReturnDate);
        System.out.println("✅ Item borrowed successfully: " + item.getTitle());
        return true;
    }

    public boolean returnItem(int itemId) {
        LibraryItem item = libraryItemHashMap.get(itemId);
        if (item == null) {
            System.out.println("❌ Item not found with ID: " + itemId);
            return false;
        }

        if (item.getStatus() != LibraryItemStatus.BORROWED) {
            System.out.println("❌ Item is not currently borrowed. Current status: " + item.getStatus());
            return false;
        }

        item.setStatus(LibraryItemStatus.EXIST);
        item.setReturnDate(null);

        System.out.println("✅ Item returned successfully: " + item.getTitle());
        return true;
    }

    public GenericLinkedList<LibraryItem> getBorrowedItems() {
        GenericLinkedList<LibraryItem> borrowedItems = new GenericLinkedList<>();
        for (LibraryItem item : libraryItems) {
            if (item.getStatus() == LibraryItemStatus.BORROWED) {
                borrowedItems.add(item);
            }
        }
        return borrowedItems;
    }


    private GenericLinkedList<LibraryItem> readFromFile(String dataFile) {
        try {
            File file = new File(dataFile);
            if (!file.exists()) {
                System.out.println("Protobuf data file not found. Starting with empty library.");
                return new GenericLinkedList<>();
            }

            LibraryItemProtos.LibraryItemCollection collection =
                    LibraryItemProtos.LibraryItemCollection.parseFrom(new FileInputStream(file));

            GenericLinkedList<LibraryItem> loadedLibraryItems = new GenericLinkedList<>();
            int maxId = 0;

            for (LibraryItemProtos.LibraryItemProto itemProto : collection.getItemsList()) {
                LibraryItem libraryItem = convertFromProto(itemProto);
                if (libraryItem != null) {
                    loadedLibraryItems.add(libraryItem);
                    if (libraryItem.getId() > maxId) {
                        maxId = libraryItem.getId();
                    }
                    libraryItemHashMap.put(libraryItem.getId(), libraryItem);
                }
            }

            LibraryItem.setCounter(maxId);
            System.out.println("Library items loaded successfully from " + dataFile);
            return loadedLibraryItems;

        } catch (IOException e) {
            System.out.println("Error loading data from protobuf file: " + e.getMessage());
            return new GenericLinkedList<>();
        }
    }

    public void writeToFile() {
        try {
            LibraryItemProtos.LibraryItemCollection.Builder collectionBuilder =
                    LibraryItemProtos.LibraryItemCollection.newBuilder();

            for (LibraryItem item : libraryItems) {
                LibraryItemProtos.LibraryItemProto itemProto = convertToProto(item);
                if (itemProto != null) {
                    collectionBuilder.addItems(itemProto);
                }
            }

            int maxId = 0;
            for (LibraryItem item : libraryItems) {
                if (item.getId() > maxId) {
                    maxId = item.getId();
                }
            }
            collectionBuilder.setMaxId(maxId);

            LibraryItemProtos.LibraryItemCollection collection = collectionBuilder.build();
            collection.writeTo(new FileOutputStream(dataFile));
            System.out.println("Library items saved successfully to " + dataFile);

        } catch (IOException e) {
            System.out.println("Error saving data to protobuf file: " + e.getMessage());
        }
    }

    private LibraryItemProtos.LibraryItemProto convertToProto(LibraryItem item) {
        if (item instanceof Book) {
            Book book = (Book) item;
            LibraryItemProtos.BookProto bookProto = LibraryItemProtos.BookProto.newBuilder()
                    .setId(book.getId())
                    .setTitle(book.getTitle())
                    .setAuthor(book.getAuthor())
                    .setStatus(convertStatusToProto(book.getStatus()))
                    .setPublishDate(convertDateToProto(book.getPublishDate()))
                    .setIsbn(book.getIsbn() != null ? book.getIsbn() : "")
                    .setGenre(book.getGenre() != null ? book.getGenre() : "")
                    .setPageCount(book.getPageCount())
                    .setReturnDate(convertDateToProto(book.getReturnDate()))
                    .build();

            return LibraryItemProtos.LibraryItemProto.newBuilder()
                    .setBook(bookProto)
                    .build();

        } else if (item instanceof Magazine) {
            Magazine magazine = (Magazine) item;
            LibraryItemProtos.MagazineProto magazineProto = LibraryItemProtos.MagazineProto.newBuilder()
                    .setId(magazine.getId())
                    .setTitle(magazine.getTitle())
                    .setAuthor(magazine.getAuthor())
                    .setStatus(convertStatusToProto(magazine.getStatus()))
                    .setPublishDate(convertDateToProto(magazine.getPublishDate()))
                    .setIssueNumber(magazine.getIssueNumber() != null ? magazine.getIssueNumber() : "")
                    .setPublisher(magazine.getPublisher() != null ? magazine.getPublisher() : "")
                    .setCategory(magazine.getCategory() != null ? magazine.getCategory() : "")
                    .setReturnDate(convertDateToProto(magazine.getReturnDate()))
                    .build();

            return LibraryItemProtos.LibraryItemProto.newBuilder()
                    .setMagazine(magazineProto)
                    .build();

        } else if (item instanceof Reference) {
            Reference reference = (Reference) item;
            LibraryItemProtos.ReferenceProto referenceProto = LibraryItemProtos.ReferenceProto.newBuilder()
                    .setId(reference.getId())
                    .setTitle(reference.getTitle())
                    .setAuthor(reference.getAuthor())
                    .setStatus(convertStatusToProto(reference.getStatus()))
                    .setPublishDate(convertDateToProto(reference.getPublishDate()))
                    .setReferenceType(reference.getReferenceType() != null ? reference.getReferenceType() : "")
                    .setEdition(reference.getEdition() != null ? reference.getEdition() : "")
                    .setSubject(reference.getSubject() != null ? reference.getSubject() : "")
                    .setReturnDate(convertDateToProto(reference.getReturnDate()))
                    .build();

            return LibraryItemProtos.LibraryItemProto.newBuilder()
                    .setReference(referenceProto)
                    .build();

        } else if (item instanceof Thesis) {
            Thesis thesis = (Thesis) item;
            LibraryItemProtos.ThesisProto thesisProto = LibraryItemProtos.ThesisProto.newBuilder()
                    .setId(thesis.getId())
                    .setTitle(thesis.getTitle())
                    .setAuthor(thesis.getAuthor())
                    .setStatus(convertStatusToProto(thesis.getStatus()))
                    .setPublishDate(convertDateToProto(thesis.getPublishDate()))
                    .setUniversity(thesis.getUniversity() != null ? thesis.getUniversity() : "")
                    .setDepartment(thesis.getDepartment() != null ? thesis.getDepartment() : "")
                    .setAdvisor(thesis.getAdvisor() != null ? thesis.getAdvisor() : "")
                    .setReturnDate(convertDateToProto(thesis.getReturnDate()))
                    .build();

            return LibraryItemProtos.LibraryItemProto.newBuilder()
                    .setThesis(thesisProto)
                    .build();
        }

        return null;
    }

    private LibraryItem convertFromProto(LibraryItemProtos.LibraryItemProto proto) {
        if (proto.hasBook()) {
            LibraryItemProtos.BookProto bookProto = proto.getBook();
            return new Book(
                    bookProto.getId(),
                    bookProto.getTitle(),
                    bookProto.getAuthor(),
                    convertStatusFromProto(bookProto.getStatus()),
                    convertDateFromProto(bookProto.getPublishDate()),
                    bookProto.getIsbn(),
                    bookProto.getGenre(),
                    bookProto.getPageCount(),
                    convertDateFromProto(bookProto.getReturnDate())
            );
        } else if (proto.hasMagazine()) {
            LibraryItemProtos.MagazineProto magazineProto = proto.getMagazine();
            return new Magazine(
                    magazineProto.getId(),
                    magazineProto.getTitle(),
                    magazineProto.getAuthor(),
                    convertStatusFromProto(magazineProto.getStatus()),
                    convertDateFromProto(magazineProto.getPublishDate()),
                    magazineProto.getIssueNumber(),
                    magazineProto.getPublisher(),
                    magazineProto.getCategory(),
                    convertDateFromProto(magazineProto.getReturnDate())
            );
        } else if (proto.hasReference()) {
            LibraryItemProtos.ReferenceProto referenceProto = proto.getReference();
            return new Reference(
                    referenceProto.getId(),
                    referenceProto.getTitle(),
                    referenceProto.getAuthor(),
                    convertStatusFromProto(referenceProto.getStatus()),
                    convertDateFromProto(referenceProto.getPublishDate()),
                    referenceProto.getReferenceType(),
                    referenceProto.getEdition(),
                    referenceProto.getSubject(),
                    convertDateFromProto(referenceProto.getReturnDate())
            );
        } else if (proto.hasThesis()) {
            LibraryItemProtos.ThesisProto thesisProto = proto.getThesis();
            return new Thesis(
                    thesisProto.getId(),
                    thesisProto.getTitle(),
                    thesisProto.getAuthor(),
                    convertStatusFromProto(thesisProto.getStatus()),
                    convertDateFromProto(thesisProto.getPublishDate()),
                    thesisProto.getUniversity(),
                    thesisProto.getDepartment(),
                    thesisProto.getAdvisor(),
                    convertDateFromProto(thesisProto.getReturnDate())
            );
        }

        return null;
    }

    private com.google.protobuf.Timestamp convertDateToProto(LocalDate date) {
        if (date == null) {
            return com.google.protobuf.Timestamp.getDefaultInstance();
        }
        return com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(date.atStartOfDay(java.time.ZoneOffset.UTC).toEpochSecond())
                .build();
    }

    private LocalDate convertDateFromProto(com.google.protobuf.Timestamp timestamp) {
        if (timestamp == null || timestamp.getSeconds() == 0) {
            return null;
        }
        return java.time.Instant.ofEpochSecond(timestamp.getSeconds())
                .atZone(java.time.ZoneOffset.UTC)
                .toLocalDate();
    }

    private LibraryItemProtos.LibraryItemStatus convertStatusToProto(LibraryItemStatus status) {
        switch (status) {
            case EXIST:
                return LibraryItemProtos.LibraryItemStatus.EXIST;
            case BORROWED:
                return LibraryItemProtos.LibraryItemStatus.BORROWED;
            case BANNED:
                return LibraryItemProtos.LibraryItemStatus.BANNED;
            default:
                return LibraryItemProtos.LibraryItemStatus.EXIST;
        }
    }

    private LibraryItemStatus convertStatusFromProto(LibraryItemProtos.LibraryItemStatus status) {
        switch (status) {
            case EXIST:
                return LibraryItemStatus.EXIST;
            case BORROWED:
                return LibraryItemStatus.BORROWED;
            case BANNED:
                return LibraryItemStatus.BANNED;
            default:
                return LibraryItemStatus.EXIST;
        }
    }
}