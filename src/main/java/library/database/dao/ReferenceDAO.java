package library.database.dao;

import library.models.Reference;
import java.sql.Connection;
import java.sql.SQLException;

public interface ReferenceDAO {
    void save(int itemId, Reference reference, Connection connection) throws SQLException;
    void update(Reference reference, Connection connection) throws SQLException;
    Reference findById(int id) throws SQLException;
}