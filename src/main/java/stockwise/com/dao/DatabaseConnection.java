package stockwise.com.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    // Dynamically points to your project root folder so the database file is always unified
    private static final String DB_PATH = System.getProperty("user.dir") + File.separator + "stockwise.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    public static Connection connect() throws SQLException {
        // Uncomment the line below to verify your exact database path in the console log if needed:
        // System.out.println("Connecting to database at: " + DB_PATH);

        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }
}