package library.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.protobuf.Timestamp;
import library.controllers.CommandLineController;
import library.models.enums.EventType;
import library.models.enums.LibraryItemStatus;
import library.print.observers.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Library {
    private static Library library = null;
    private final Vector<LibraryItem> libraryItems;
    private final ConcurrentHashMap<Integer, LibraryItem> libraryItemHashMap;
    private final String dataFile;
    private final EventManager eventBus;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(CommandLineController.class);

    public static final Predicate<LibraryItem> IS_AVAILABLE_FOR_BORROWING =
            item -> item.getStatus() == LibraryItemStatus.EXIST;

    private Library() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        this.dataFile = "library_data.pb";
        this.eventBus = EventManager.getEventBus();
        this.libraryItemHashMap = new ConcurrentHashMap<>();
        this.libraryItems = readFromFile(this.dataFile);
    }

    public static synchronized Library getInstance(){
        if(library == null){
            library = new Library();
            logger.info("Created new instance of CommandLineController");
        }
        return library;
    }

    public Vector<LibraryItem> getLibraryItems() {
        return libraryItems;
    }

    public void addLibraryItem(LibraryItem libraryItem) {
        this.libraryItems.add(libraryItem);
        this.libraryItemHashMap.put(libraryItem.getId(), libraryItem);

        var eventType = getAddEventType(libraryItem);
        eventBus.publish(eventType, "Added: " + libraryItem.getTitle());

        logger.debug("Published {} event for item: {}", eventType, libraryItem.getTitle());
    }

    private EventType getAddEventType(LibraryItem item) {
        return switch (item.getType()) {
            case BOOK -> EventType.ADDED_NEW_BOOK;
            case MAGAZINE -> EventType.ADDED_NEW_MAGAZINE;
            case REFERENCE -> EventType.ADDED_NEW_REFERENCE;
            case THESIS -> EventType.ADDED_NEW_THESIS;
        };
    }

    private EventType getReturnEventType(LibraryItem item) {
        return switch (item.getType()) {
            case BOOK -> EventType.RETURNED_BOOK;
            case MAGAZINE -> EventType.RETURNED_MAGAZINE;
            case REFERENCE -> EventType.RETURNED_REFERENCE;
            case THESIS -> EventType.RETURNED_THESIS;
        };
    }

    public void removeLibraryItem(LibraryItem libraryItem) {
        libraryItems.remove(libraryItem);
        libraryItemHashMap.remove(libraryItem.getId());
    }

    public Vector<LibraryItem> sortLibraryItems() {
        return libraryItems.stream()
                .sorted(Comparator.comparing(LibraryItem::getPublishDate).reversed())
                .collect(Collectors.toCollection(Vector::new));
    }

    public Vector<LibraryItem> search(String keyword) {
        var lowerKeyword = keyword.toLowerCase();

        return libraryItems.parallelStream()
                .filter(item ->
                        item.getTitle().toLowerCase().contains(lowerKeyword) ||
                                item.getAuthor().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toCollection(Vector::new));
    }

    public LibraryItem getLibraryItemById(int id) {
        return libraryItemHashMap.get(id);
    }

    public synchronized boolean borrowItem(int itemId, LocalDate expectedReturnDate) {
        var item = libraryItemHashMap.get(itemId);
        if (item == null || item.getStatus() != LibraryItemStatus.EXIST) {
            return false;
        }
        item.setStatus(LibraryItemStatus.BORROWED);
        item.setReturnDate(expectedReturnDate);
        return true;
    }

    public synchronized boolean returnItem(int itemId) {
        var item = libraryItemHashMap.get(itemId);
        if (item == null || item.getStatus() != LibraryItemStatus.BORROWED) {
            return false;
        }
        item.setStatus(LibraryItemStatus.EXIST);
        item.setReturnDate(null);
        var libraryItem = getLibraryItemById(itemId);
        var eventType = getReturnEventType(libraryItem);
        eventBus.publish(eventType, "Returned: " + libraryItem.getTitle());
        logger.debug("Returned {} event for item: {}", eventType, libraryItem.getTitle());
        return true;
    }

    public Vector<LibraryItem> getBorrowedItems() {
        return libraryItems.stream()
                .filter(item -> item.getStatus() == LibraryItemStatus.BORROWED)
                .collect(Collectors.toCollection(Vector::new));
    }

    private Vector<LibraryItem> readFromFile(String dataFile) {
        try {
            var file = new File(dataFile);
            if (!file.exists()) {
                return new Vector<>();
            }

            var collection = LibraryItemProtos.LibraryItemCollection
                    .parseFrom(new FileInputStream(file));

            var loadedLibraryItems = new Vector<LibraryItem>();
            var maxId = 0;

            for (var itemProto : collection.getItemsList()) {
                var libraryItem = convertFromProto(itemProto);
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
            var collectionBuilder = LibraryItemProtos.LibraryItemCollection.newBuilder();

            libraryItems.forEach(item -> {
                var itemProto = convertToProto(item);
                if (itemProto != null) {
                    collectionBuilder.addItems(itemProto);
                }
            });

            var maxId = libraryItems.stream()
                    .mapToInt(LibraryItem::getId)
                    .max()
                    .orElse(0);

            collectionBuilder.setMaxId(maxId);

            var collection = collectionBuilder.build();
            collection.writeTo(new FileOutputStream(dataFile));

        } catch (IOException e) {
            System.out.println("Error saving data to protobuf file: " + e.getMessage());
        }
    }

    private LibraryItemProtos.LibraryItemProto convertToProto(LibraryItem item) {
        if (item instanceof Book book) {
            var bookProto = LibraryItemProtos.BookProto.newBuilder()
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

        } else if (item instanceof Magazine magazine) {
            var magazineProto = LibraryItemProtos.MagazineProto.newBuilder()
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

        } else if (item instanceof Reference reference) {
            var referenceProto = LibraryItemProtos.ReferenceProto.newBuilder()
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

        } else if (item instanceof Thesis thesis) {
            var thesisProto = LibraryItemProtos.ThesisProto.newBuilder()
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
            var bookProto = proto.getBook();
            return new Book(
                    bookProto.getId(),
                    bookProto.getTitle(),
                    bookProto.getPageCount(),
                    bookProto.getAuthor(),
                    convertStatusFromProto(bookProto.getStatus()),
                    convertDateFromProto(bookProto.getPublishDate()),
                    bookProto.getIsbn(),
                    bookProto.getGenre(),
                    convertDateFromProto(bookProto.getReturnDate())
            );
        } else if (proto.hasMagazine()) {
            var magazineProto = proto.getMagazine();
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
            var referenceProto = proto.getReference();
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
            var thesisProto = proto.getThesis();
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

    private Timestamp convertDateToProto(LocalDate date) {
        if (date == null) {
            return Timestamp.getDefaultInstance();
        }
        return Timestamp.newBuilder()
                .setSeconds(date.atStartOfDay(ZoneOffset.UTC).toEpochSecond())
                .build();
    }

    private LocalDate convertDateFromProto(Timestamp timestamp) {
        if (timestamp == null || timestamp.getSeconds() == 0) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds())
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
    }

    private LibraryItemProtos.LibraryItemStatus convertStatusToProto(LibraryItemStatus status) {
        return switch (status) {
            case EXIST -> LibraryItemProtos.LibraryItemStatus.EXIST;
            case BORROWED -> LibraryItemProtos.LibraryItemStatus.BORROWED;
            case BANNED -> LibraryItemProtos.LibraryItemStatus.BANNED;
        };
    }

    private LibraryItemStatus convertStatusFromProto(LibraryItemProtos.LibraryItemStatus status) {
        return switch (status) {
            case EXIST -> LibraryItemStatus.EXIST;
            case BORROWED -> LibraryItemStatus.BORROWED;
            case BANNED -> LibraryItemStatus.BANNED;
            default -> LibraryItemStatus.EXIST;
        };
    }
}