package library.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static BasicDataSource dataSource;
    private static final Properties properties = new Properties();

    public static final int DEFAULT_USER_ID = 1;

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                logger.error("Unable to find db.properties file");
                throw new RuntimeException("db.properties file not found in classpath");
            }

            properties.load(input);
            initializeDataSource();

        } catch (IOException e) {
            logger.error("Error loading database configuration: {}", e.getMessage());
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    private static void initializeDataSource() {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(properties.getProperty("db.driver"));
        dataSource.setUrl(properties.getProperty("db.url"));
        dataSource.setUsername(properties.getProperty("db.username"));
        dataSource.setPassword(properties.getProperty("db.password"));

        dataSource.setInitialSize(Integer.parseInt(properties.getProperty("db.pool.initialSize", "5")));
        dataSource.setMaxTotal(Integer.parseInt(properties.getProperty("db.pool.maxTotal", "20")));
        dataSource.setMaxIdle(Integer.parseInt(properties.getProperty("db.pool.maxIdle", "10")));
        dataSource.setMinIdle(Integer.parseInt(properties.getProperty("db.pool.minIdle", "5")));
        dataSource.setMaxWaitMillis(Long.parseLong(properties.getProperty("db.pool.maxWaitMillis", "10000")));

        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(true);
        dataSource.setTestWhileIdle(true);

        logger.info("Database connection pool initialized successfully");
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false); // We'll manage transactions manually
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
                connection.close();
            } catch (SQLException e) {
                logger.error("Error closing database connection: {}", e.getMessage());
            }
        }
    }

    public static void commitTransaction(Connection connection) throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.commit();
        }
    }

    public static void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                logger.error("Error rolling back transaction: {}", e.getMessage());
            }
        }
    }

    public static void shutdown() {
        if (dataSource != null) {
            try {
                dataSource.close();
                logger.info("Database connection pool shutdown successfully");
            } catch (SQLException e) {
                logger.error("Error shutting down database connection pool: {}", e.getMessage());
            }
        }
    }

    public static int getDefaultUserId() {
        return DEFAULT_USER_ID;
    }
}