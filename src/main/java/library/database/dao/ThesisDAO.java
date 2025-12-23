package library.database.dao;

import library.models.Thesis;
import java.sql.Connection;
import java.sql.SQLException;

public interface ThesisDAO {
    void save(int itemId, Thesis thesis, Connection connection) throws SQLException;
    void update(Thesis thesis, Connection connection) throws SQLException;
    Thesis findById(int id) throws SQLException;
}