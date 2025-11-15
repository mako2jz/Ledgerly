package ledgerly.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private static final String URL = "jdbc:sqlite:ledgerly_db.sqlite";

    private Database() {}

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);

        System.out.println("Connected to database successfully");

        // Enable foreign key constraints
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }

        return conn;
    }
}
