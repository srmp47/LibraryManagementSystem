package library.database.util;

import library.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DBUtil {
    private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);


    public static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public static void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            DatabaseConnection.closeConnection(conn);
        } catch (SQLException e) {
            logger.error("Error closing database resources: {}", e.getMessage());
        }
    }

    public static void closeResources(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            logger.error("Error closing database resources: {}", e.getMessage());
        }
    }

    public static void closeConnection(Connection conn) {
        DatabaseConnection.closeConnection(conn);
    }

    public static void commitTransaction(Connection conn) throws SQLException {
        DatabaseConnection.commitTransaction(conn);
    }

    public static void rollbackTransaction(Connection conn) {
        DatabaseConnection.rollbackTransaction(conn);
    }

    public static int getDefaultUserId() {
        return DatabaseConnection.getDefaultUserId();
    }

    public static int executeUpdate(Connection conn, String sql, Object... params) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }

    public static void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                stmt.setNull(i + 1, Types.NULL);
            } else if (params[i] instanceof Integer) {
                stmt.setInt(i + 1, (Integer) params[i]);
            } else if (params[i] instanceof String) {
                stmt.setString(i + 1, (String) params[i]);
            } else if (params[i] instanceof java.sql.Date) {
                stmt.setDate(i + 1, (java.sql.Date) params[i]);
            } else if (params[i] instanceof java.time.LocalDate) {
                stmt.setDate(i + 1, java.sql.Date.valueOf((java.time.LocalDate) params[i]));
            } else if (params[i] instanceof Enum) {
                stmt.setString(i + 1, ((Enum<?>) params[i]).name());
            } else {
                throw new SQLException("Unsupported parameter type: " + params[i].getClass().getName());
            }
        }
    }

    public static ResultSet executeQuery(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        setParameters(stmt, params);
        return stmt.executeQuery();
    }

    public static <T> T executeQueryAndMap(String sql, ResultSetMapper<T> mapper, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            setParameters(stmt, params);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapper.map(rs);
            }
            return null;

        } finally {
            closeResources(rs, stmt, conn);
        }
    }

    public static <T> java.util.List<T> executeQueryAndMapList(String sql, ResultSetMapper<T> mapper, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        java.util.List<T> results = new java.util.ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            setParameters(stmt, params);
            rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapper.map(rs));
            }
            return results;

        } finally {
            closeResources(rs, stmt, conn);
        }
    }

    public interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}