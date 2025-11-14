package library.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import library.models.data_structures.GenericLinkedList;
import library.models.enums.LibraryItemStatus;

import java.io.File;
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
        this.dataFile = "library_data.json";
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

    public LibraryItem getLibraryItemById(int id){
        return libraryItemHashMap.get(id);
    }

    public boolean isThereLibraryItemWithId(int id){
        return libraryItemHashMap.containsKey(id);
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
                System.out.println("JSON data file not found. Starting with empty library.");
                return new GenericLinkedList<>();
            }

            List<LibraryItem> bookList = objectMapper.readValue(file, new TypeReference<List<LibraryItem>>() {});
            GenericLinkedList<LibraryItem> loadedLibraryItems = new GenericLinkedList<>();
            int maxId = 0;
            for (LibraryItem libraryItem : bookList) {
                loadedLibraryItems.add(libraryItem);
                if (libraryItem.getId() > maxId) {
                    maxId = libraryItem.getId();
                }
                libraryItemHashMap.put(libraryItem.getId(), libraryItem);
            }
            LibraryItem.setCounter(maxId);

            System.out.println("Library items loaded successfully from " + dataFile);
            return loadedLibraryItems;
        } catch (IOException e) {
            System.out.println("Error loading data from JSON file: " + e.getMessage());
            return new GenericLinkedList<>();
        }
    }

    public void writeToFile() {
        try {
            List<LibraryItem> LibraryItemsList = libraryItems.toList();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(dataFile), LibraryItemsList);
            System.out.println("Library items saved successfully to " + dataFile);
        } catch (IOException e) {
            System.out.println("Error saving data to JSON file: " + e.getMessage());
        }
    }
}