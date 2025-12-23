package library.database.dao;

import library.models.Magazine;
import java.sql.Connection;
import java.sql.SQLException;

public interface MagazineDAO {
    void save(int itemId, Magazine magazine, Connection connection) throws SQLException;
    void update(Magazine magazine, Connection connection) throws SQLException;
    Magazine findById(int id) throws SQLException;
}