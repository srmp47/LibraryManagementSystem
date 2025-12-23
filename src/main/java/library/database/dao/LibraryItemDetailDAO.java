package library.database.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface LibraryItemDetailDAO<T> {
    void save(int itemId, T item, Connection connection) throws SQLException;
    void update(T item, Connection connection) throws SQLException;
    T findById(int id) throws SQLException;
}