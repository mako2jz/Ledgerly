package ledgerly.app.db;

import ledgerly.app.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    public static List<User> getUsers() {
        String sql = "SELECT id, username FROM users";
        List<User> users = new ArrayList<>();

        // Use the centralized getConnection method from the Database class
        try (Connection conn = Database.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users from database: " + e.getMessage());
        }
        return users;
    }
}
