package library.models.builders;

import library.models.Thesis;
import library.models.enums.LibraryItemStatus;

import java.time.LocalDate;


public class ThesisBuilder implements LibraryItemBuilder {
    private Integer id;
    private String title;
    private String author;
    private LocalDate publishDate;
    private LibraryItemStatus status;
    private LocalDate returnDate;
    private String university;
    private String department;
    private String advisor;

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    @Override
    public void setStatus(LibraryItemStatus status) {
        this.status = status;
    }

    @Override
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setAdvisor(String advisor) {
        this.advisor = advisor;
    }

    public Thesis getResult() {
        return new Thesis(id, title, author, status, publishDate, university, department, advisor, returnDate);
    }
}