package model;

import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CategoryDAO - CRUD untuk kategori tugas
 */
public class CategoryDAO {
    private Connection conn;

    public CategoryDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public boolean create(Category cat) {
        String sql = "INSERT INTO categories (name, description, color) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cat.getName());
            ps.setString(2, cat.getDescription());
            ps.setString(3, cat.getColor());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error create category: " + e.getMessage());
            return false;
        }
    }

    public List<Category> findAll() {
        List<Category> cats = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY name";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) cats.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error findAll categories: " + e.getMessage());
        }
        return cats;
    }

    public Category findById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Error findById category: " + e.getMessage());
        }
        return null;
    }

    public boolean update(Category cat) {
        String sql = "UPDATE categories SET name=?, description=?, color=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cat.getName());
            ps.setString(2, cat.getDescription());
            ps.setString(3, cat.getColor());
            ps.setInt(4, cat.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error update category: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error delete category: " + e.getMessage());
            return false;
        }
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        return new Category(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("color")
        );
    }
}
