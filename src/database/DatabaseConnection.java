package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - Singleton pattern untuk koneksi JDBC ke MySQL
 */
public class DatabaseConnection {
    // Enkapsulasi: semua atribut private
    private static DatabaseConnection instance;
    private Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/databasepbo";
//    private static final String DATABASE = "database_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // Ganti sesuai password MySQL Anda

    // Enkapsulasi: private constructor
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Koneksi database berhasil!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Gagal koneksi database: " + e.getMessage());
        }
    }

    // Singleton getInstance
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            System.err.println("Error cek koneksi: " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi database ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("Error menutup koneksi: " + e.getMessage());
        }
    }
}
