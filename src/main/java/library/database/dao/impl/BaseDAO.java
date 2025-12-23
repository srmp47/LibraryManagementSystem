package library.database.dao.impl;

import library.database.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDAO {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Connection getConnection() throws SQLException {
        return DBUtil.getConnection();
    }

    protected void closeConnection(Connection conn) {
        DBUtil.closeConnection(conn);
    }

    protected void commitTransaction(Connection conn) throws SQLException {
        DBUtil.commitTransaction(conn);
    }

    protected void rollbackTransaction(Connection conn) {
        DBUtil.rollbackTransaction(conn);
    }

    protected int getDefaultUserId() {
        return DBUtil.getDefaultUserId();
    }
}