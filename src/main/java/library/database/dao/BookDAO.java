package library.database.dao;

import library.models.Book;
import java.sql.Connection;
import java.sql.SQLException;

public interface BookDAO {
    void save(int itemId, Book book, Connection connection) throws SQLException;
    void update(Book book, Connection connection) throws SQLException;
    Book findById(int id) throws SQLException;
}