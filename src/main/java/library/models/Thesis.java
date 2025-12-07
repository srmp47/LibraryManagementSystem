package library.models;


import library.models.enums.EventType;
import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;
import library.observers.EventManager;
import library.observers.listeners.PrinterListener;

import java.time.LocalDate;

public class Thesis extends LibraryItem {
    private final String university;
    private final String department;
    private final String advisor;


    public Thesis(Integer id, String title, String author, LibraryItemStatus status, LocalDate publishDate, String university,
                   String department, String advisor, LocalDate returnDate) {
        super(id, title, author, status, publishDate, LibraryItemType.THESIS, returnDate);
        this.university = university;
        this.department = department;
        this.advisor = advisor;
    }

    @Override
    public void setStatus(LibraryItemStatus status) {
        if(this.status == LibraryItemStatus.BORROWED && status == LibraryItemStatus.EXIST)
            sendNotification(EventType.RETURNED_THESIS);
        this.status = status;
        this.returnDate = null;
    }

    @Override
    public void display() {
        System.out.println("Thesis: " + getTitle());
        System.out.println("ID: " + getId());
        System.out.println("Author: " + getAuthor());
        System.out.println("University: " + university);
        System.out.println("Department: " + department);
        System.out.println("Advisor: " + advisor);
        System.out.println("Status: " + getStatus());
        System.out.println("Published: " + getPublishDate());
        System.out.println("------------------------");
    }

    @Override
    protected EventManager createEventManager() {
        EventManager eventManager = new EventManager(EventType.ADDED_NEW_THESIS, EventType.RETURNED_THESIS);
        eventManager.subscribe(EventType.ADDED_NEW_THESIS, new PrinterListener());
        eventManager.subscribe(EventType.RETURNED_THESIS, new PrinterListener());
        return eventManager;
    }

    public String getUniversity() {
        return university;
    }

    public String getDepartment() {
        return department;
    }

    public String getAdvisor() {
        return advisor;
    }
}