package stockwise.com.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        String[] queries = {
                """
            CREATE TABLE IF NOT EXISTS Users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                full_name TEXT NOT NULL,
                role TEXT NOT NULL,
                status TEXT DEFAULT 'PENDING'
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS Categories (
                category_id INTEGER PRIMARY KEY AUTOINCREMENT,
                category_name TEXT NOT NULL,
                description TEXT
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS Suppliers (
                supplier_id INTEGER PRIMARY KEY AUTOINCREMENT,
                supplier_name TEXT NOT NULL,
                contact_person TEXT,
                phone TEXT,
                email TEXT,
                status TEXT DEFAULT 'Active'
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS Products (
                product_id INTEGER PRIMARY KEY AUTOINCREMENT,
                product_name TEXT NOT NULL,
                category TEXT,
                price REAL NOT NULL,
                quantity INTEGER NOT NULL,
                reorder_level INTEGER DEFAULT 10
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS Inventory_Transactions (
                transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
                product_id INTEGER,
                user_id INTEGER,
                transaction_type TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (product_id) REFERENCES Products(product_id),
                FOREIGN KEY (user_id) REFERENCES Users(user_id)
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS Purchase_Orders (
                po_id INTEGER PRIMARY KEY AUTOINCREMENT,
                supplier_id INTEGER,
                user_id INTEGER,
                order_date TEXT,
                status TEXT DEFAULT 'Pending',
                total_amount REAL,
                FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id),
                FOREIGN KEY (user_id) REFERENCES Users(user_id)
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS Activity_Logs (
                log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                action_type TEXT NOT NULL,
                action_description TEXT,
                timestamp TEXT,
                affected_record_id INTEGER,
                FOREIGN KEY (user_id) REFERENCES Users(user_id)
            )
            """
        };

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            for (String sql : queries) {
                stmt.execute(sql);
            }
            System.out.println("Database and tables initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}