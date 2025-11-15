package ledgerly.app.db;

import ledgerly.app.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    public static List<User> getUsers() {
        String sql = "SELECT id, username FROM users";
        List<User> users = new ArrayList<>();

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

    public static void addUser(String username) {
        String sql = "INSERT INTO users(username) VALUES(?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding user to database: " + e.getMessage());
        }
    }
}
