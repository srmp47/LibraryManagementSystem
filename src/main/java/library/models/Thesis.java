package library.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;

import java.time.LocalDate;

public class Thesis extends LibraryItem {
    private final String university;
    private final String department;
    private final String advisor;

    @JsonCreator
    public Thesis(@JsonProperty("id") Integer id,
                  @JsonProperty("title") String title,
                  @JsonProperty("author") String author,
                  @JsonProperty("status") LibraryItemStatus status,
                  @JsonProperty("publishDate") LocalDate publishDate,
                  @JsonProperty("university") String university,
                  @JsonProperty("department") String department,
                  @JsonProperty("advisor") String advisor,
                  @JsonProperty("returnDate") LocalDate returnDate) {
        super(id, title, author, status, publishDate, LibraryItemType.THESIS, returnDate);
        this.university = university;
        this.department = department;
        this.advisor = advisor;
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