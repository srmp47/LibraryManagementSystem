package library.models;

import library.models.enums.RequestType;
import java.time.LocalDate;

public class LibraryRequest {
    private final RequestType requestType;
    private final LibraryItem item;
    private final Integer itemId;
    private final LocalDate returnDate;
    private final String newStatus;

    // Constructor for CREATE
    public LibraryRequest(RequestType requestType, LibraryItem item) {
        this.requestType = requestType;
        this.item = item;
        this.itemId = null;
        this.returnDate = null;
        this.newStatus = null;
    }

    // Constructor for BORROW
    public LibraryRequest(RequestType requestType, int itemId, LocalDate returnDate) {
        this.requestType = requestType;
        this.item = null;
        this.itemId = itemId;
        this.returnDate = returnDate;
        this.newStatus = null;
    }

    // Constructor for RETURN and DELETE
    public LibraryRequest(RequestType requestType, int itemId) {
        this.requestType = requestType;
        this.item = null;
        this.itemId = itemId;
        this.returnDate = null;
        this.newStatus = null;
    }

    // Constructor for UPDATE_STATUS
    public LibraryRequest(RequestType requestType, int itemId, String newStatus) {
        this.requestType = requestType;
        this.item = null;
        this.itemId = itemId;
        this.returnDate = null;
        this.newStatus = newStatus;
    }

    public RequestType getRequestType() { return requestType; }
    public LibraryItem getItem() { return item; }
    public Integer getItemId() { return itemId; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getNewStatus() { return newStatus; }
}