package library.database.dao;

import library.models.LibraryItem;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudDAO<T> {
    int save(T item) throws SQLException;
    boolean update(T item) throws SQLException;
    boolean delete(int id) throws SQLException;
    Optional<T> findById(int id) throws SQLException;
    List<T> findAll() throws SQLException;
}