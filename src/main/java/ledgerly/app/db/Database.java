package ledgerly.app.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    // Path to your SQLite file
    private static final String DB_FILE = "ledgerly_db.sqlite";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;

    private Database() {}

    public static Connection getConnection() throws SQLException {
        // Check if the file exists
        File file = new File(DB_FILE);
        if (!file.exists()) {
            throw new SQLException("Database file not found: " + DB_FILE);
        }

        Connection conn = DriverManager.getConnection(URL);

        System.out.println("Connected to database successfully");

        // Enable foreign key constraints
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }

        return conn;
    }
}
