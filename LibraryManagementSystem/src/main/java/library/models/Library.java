package library.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import library.models.data_structures.GenericLinkedList;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class Library {
    private GenericLinkedList<LibraryItem> libraryItems;
    private final String dataFile = "library_data.json";
    private final ObjectMapper objectMapper;

    public Library() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.libraryItems = readFromFile();
    }

    public GenericLinkedList<LibraryItem> getLibraryItems() {
        return libraryItems;
    }

    public void addLibraryItem(LibraryItem libraryItem) {
        this.libraryItems.add(libraryItem);
    }

    public void removeLibraryItem(LibraryItem libraryItem) {

        libraryItems.remove(libraryItem);
    }

    public GenericLinkedList<LibraryItem> sortLibraryItems() {
        GenericLinkedList<LibraryItem> libraryItemsCopy = libraryItems;
        libraryItemsCopy.sort(Comparator.comparing(LibraryItem::getPublishDate).reversed());
        return libraryItemsCopy;
    }

    public GenericLinkedList<LibraryItem> search(String keyword) {
        return libraryItems.filter(libraryItem ->
                libraryItem.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        libraryItem.getAuthor().toLowerCase().contains(keyword.toLowerCase())
        );
    }


    private GenericLinkedList<LibraryItem> readFromFile() {
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