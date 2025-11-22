package ledgerly.app.db;

import ledgerly.app.model.Product;
import ledgerly.app.model.Sale;
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

    public static void deleteUser(int userId) {
        String deleteSalesSql = "DELETE FROM sales WHERE user_id = ?";
        String deleteProductsSql = "DELETE FROM products WHERE user_id = ?";
        String deleteUserSql = "DELETE FROM users WHERE id = ?";

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtSales = conn.prepareStatement(deleteSalesSql)) {
                pstmtSales.setInt(1, userId);
                pstmtSales.executeUpdate();
            }

            try (PreparedStatement pstmtProducts = conn.prepareStatement(deleteProductsSql)) {
                pstmtProducts.setInt(1, userId);
                pstmtProducts.executeUpdate();
            }

            try (PreparedStatement pstmtUser = conn.prepareStatement(deleteUserSql)) {
                pstmtUser.setInt(1, userId);
                pstmtUser.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            System.err.println("Transaction failed, rolling back. " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error on rollback: " + ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restore default behavior
                    conn.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }


    public static List<Sale> getSalesForUser(int userId) {
        // Use LEFT JOIN and COALESCE to handle deleted products
        String sql = "SELECT s.sale_id, s.created_at, s.amount, s.description, COALESCE(p.product_name, 'Unknown Product') as product_name " +
                "FROM sales s LEFT JOIN products p ON s.product_id = p.product_id " +
                "WHERE s.user_id = ? ORDER BY s.created_at DESC";
        List<Sale> sales = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sales.add(new Sale(
                        rs.getInt("sale_id"),
                        rs.getString("created_at"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getString("product_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sales: " + e.getMessage());
        }
        return sales;
    }

    public static void addSale(int userId, Integer productId, double amount, String description) {
        String sql = "INSERT INTO sales(user_id, product_id, amount, description) VALUES(?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            if (productId == null) {
                pstmt.setNull(2, Types.INTEGER);
            } else {
                pstmt.setInt(2, productId);
            }
            pstmt.setDouble(3, amount);
            pstmt.setString(4, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding sale: " + e.getMessage());
        }
    }

    public static void updateSale(int saleId, int productId, double amount, String description) {
        String sql = "UPDATE sales SET product_id = ?, amount = ?, description = ? WHERE sale_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, description);
            pstmt.setInt(4, saleId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating sale: " + e.getMessage());
        }
    }

    public static void deleteSale(int saleId) {
        String sql = "DELETE FROM sales WHERE sale_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, saleId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting sale: " + e.getMessage());
        }
    }


    public static List<Product> getProductsForUser(int userId) {
        String sql = "SELECT product_id, product_name FROM products WHERE user_id = ?";
        List<Product> products = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                products.add(new Product(rs.getInt("product_id"), rs.getString("product_name")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
        return products;
    }

    public static void addProduct(int userId, String productName) {
        String sql = "INSERT INTO products(user_id, product_name) VALUES(?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, productName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
        }
    }

    public static void updateProduct(int productId, String newName) {
        String sql = "UPDATE products SET product_name = ? WHERE product_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
        }
    }

    public static void deleteProduct(int productId) {
        // Important: First, update sales to nullify the product_id foreign key
        String updateSalesSql = "UPDATE sales SET product_id = NULL WHERE product_id = ?";
        String deleteProductSql = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = Database.getConnection()) {
            // Disable auto-commit to perform a transaction
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateSalesSql)) {
                pstmtUpdate.setInt(1, productId);
                pstmtUpdate.executeUpdate();
            }

            try (PreparedStatement pstmtDelete = conn.prepareStatement(deleteProductSql)) {
                pstmtDelete.setInt(1, productId);
                pstmtDelete.executeUpdate();
            }

            conn.commit(); // Commit the transaction

        } catch (SQLException e) {
            System.err.println("Error deleting product and updating sales: " + e.getMessage());
        }
    }

    public static int getSalesCountForUser(int userId) {
        String sql = "SELECT COUNT(*) AS cnt FROM sales WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting sales: " + e.getMessage());
        }
        return 0;
    }

    public static List<Sale> getSalesForUserPaged(int userId, int limit, int offset) {
        String sql = "SELECT s.sale_id, s.created_at, s.amount, s.description, COALESCE(p.product_name, 'Unknown Product') as product_name " +
                "FROM sales s LEFT JOIN products p ON s.product_id = p.product_id " +
                "WHERE s.user_id = ? ORDER BY s.created_at DESC LIMIT ? OFFSET ?";
        List<Sale> sales = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sales.add(new Sale(
                            rs.getInt("sale_id"),
                            rs.getString("created_at"),
                            rs.getDouble("amount"),
                            rs.getString("description"),
                            rs.getString("product_name")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching paged sales: " + e.getMessage());
        }
        return sales;
    }

}
