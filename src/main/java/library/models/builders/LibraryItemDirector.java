package library.models.builders;

import library.models.enums.LibraryItemStatus;

import java.time.LocalDate;


public class LibraryItemDirector {


    public void constructStandardBook(LibraryItemBuilder builder,
                                      String title,
                                      String author,
                                      LocalDate publishDate,
                                      String isbn,
                                      String genre,
                                      int pageCount) {
        builder.setTitle(title);
        builder.setAuthor(author);
        builder.setPublishDate(publishDate);
        builder.setStatus(LibraryItemStatus.EXIST);

        if (builder instanceof BookBuilder bookBuilder) {
            bookBuilder.setIsbn(isbn);
            bookBuilder.setGenre(genre);
            bookBuilder.setPageCount(pageCount);
        }
    }



    public void constructStandardMagazine(LibraryItemBuilder builder,
                                          String title,
                                          String editor,
                                          LocalDate publishDate,
                                          String issueNumber,
                                          String publisher,
                                          String category) {
        builder.setTitle(title);
        builder.setAuthor(editor);
        builder.setPublishDate(publishDate);
        builder.setStatus(LibraryItemStatus.EXIST);

        if (builder instanceof MagazineBuilder magazineBuilder) {
            magazineBuilder.setIssueNumber(issueNumber);
            magazineBuilder.setPublisher(publisher);
            magazineBuilder.setCategory(category);
        }
    }


    public void constructStandardReference(LibraryItemBuilder builder,
                                           String title,
                                           String author,
                                           LocalDate publishDate,
                                           String referenceType,
                                           String edition,
                                           String subject) {
        builder.setTitle(title);
        builder.setAuthor(author);
        builder.setPublishDate(publishDate);
        builder.setStatus(LibraryItemStatus.EXIST);

        if (builder instanceof ReferenceBuilder referenceBuilder) {
            referenceBuilder.setReferenceType(referenceType);
            referenceBuilder.setEdition(edition);
            referenceBuilder.setSubject(subject);
        }
    }


    public void constructStandardThesis(LibraryItemBuilder builder,
                                        String title,
                                        String author,
                                        LocalDate publishDate,
                                        String university,
                                        String department,
                                        String advisor) {
        builder.setTitle(title);
        builder.setAuthor(author);
        builder.setPublishDate(publishDate);
        builder.setStatus(LibraryItemStatus.EXIST);

        if (builder instanceof ThesisBuilder thesisBuilder) {
            thesisBuilder.setUniversity(university);
            thesisBuilder.setDepartment(department);
            thesisBuilder.setAdvisor(advisor);
        }
    }

}