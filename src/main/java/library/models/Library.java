package library.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import library.models.structures.GenericLinkedList;
import library.models.enums.LibraryItemStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Library {
    private final Vector<LibraryItem> libraryItems;
    private final ConcurrentHashMap<Integer, LibraryItem> libraryItemHashMap;
    private final String dataFile;
    private final ObjectMapper objectMapper;

    public Library() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        this.dataFile = "library_data.pb";
        this.libraryItemHashMap = new ConcurrentHashMap<>();
        this.libraryItems = readFromFile(this.dataFile);
    }

    public Library(String dataFile) {
        this.dataFile = dataFile;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        this.libraryItemHashMap = new ConcurrentHashMap<>();
        this.libraryItems = readFromFile(dataFile);
    }

    public Vector<LibraryItem> getLibraryItems() {
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

    public Vector<LibraryItem> sortLibraryItems() {
        libraryItems.sort(Comparator.comparing(LibraryItem::getPublishDate).reversed());
        return libraryItems;
    }

    public Vector<LibraryItem> search(String keyword) {
        Vector<LibraryItem> results = new Vector<>();
        String lowerKeyword = keyword.toLowerCase();
        synchronized (libraryItems) {
            for (LibraryItem item : libraryItems) {
                if (item.getTitle().toLowerCase().contains(lowerKeyword) ||
                        item.getAuthor().toLowerCase().contains(lowerKeyword)) {
                    results.add(item);
                }
            }
        }
        return results;
    }

    public LibraryItem getLibraryItemById(int id) {
        return libraryItemHashMap.get(id);
    }

    public synchronized boolean borrowItem(int itemId, LocalDate expectedReturnDate) {
        LibraryItem item = libraryItemHashMap.get(itemId);
        if (item == null || item.getStatus() != LibraryItemStatus.EXIST) {
            return false;
        }
        item.setStatus(LibraryItemStatus.BORROWED);
        item.setReturnDate(expectedReturnDate);
        return true;
    }

    public synchronized boolean returnItem(int itemId)  {
        LibraryItem item = libraryItemHashMap.get(itemId);
        if (item == null || item.getStatus() != LibraryItemStatus.BORROWED) {
            return false;
        }
        item.setStatus(LibraryItemStatus.EXIST);
        item.setReturnDate(null);
        return true;
    }

    public Vector<LibraryItem> getBorrowedItems() {
        Vector<LibraryItem> borrowedItems = new Vector<>();
        synchronized (libraryItems) {
            for (LibraryItem item : libraryItems) {
                if (item.getStatus() == LibraryItemStatus.BORROWED) {
                    borrowedItems.add(item);
                }
            }
        }
        return borrowedItems;
    }


    private Vector<LibraryItem> readFromFile(String dataFile) {
        try {
            File file = new File(dataFile);
            if (!file.exists()) {
                return new Vector<>();
            }

            LibraryItemProtos.LibraryItemCollection collection =
                    LibraryItemProtos.LibraryItemCollection.parseFrom(new FileInputStream(file));

            Vector<LibraryItem> loadedLibraryItems = new Vector<>();
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
            return loadedLibraryItems;

        } catch (IOException e) {
            return new Vector<>();
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