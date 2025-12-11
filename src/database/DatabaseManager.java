package database;

import java.sql.*;

/**
 * Singleton Pattern: DatabaseManager
 * Manages database connection and ensures only one connection exists
 * Thread-safe implementation for database operations
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:moviebooking.db";
    private static DatabaseManager instance;
    private Connection connection;
    
    private DatabaseManager() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            System.out.println("DatabaseManager initialized successfully (Singleton Pattern)");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    private void createTables() {
        try {
            Statement stmt = connection.createStatement();
            
            // Users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "email TEXT UNIQUE NOT NULL," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createUsersTable);
            
            // Movies table
            String createMoviesTable = "CREATE TABLE IF NOT EXISTS movies (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT NOT NULL," +
                    "genre TEXT NOT NULL," +
                    "duration TEXT NOT NULL," +
                    "rating TEXT NOT NULL," +
                    "description TEXT," +
                    "poster_path TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createMoviesTable);
            
            // Bookings table
            String createBookingsTable = "CREATE TABLE IF NOT EXISTS bookings (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "movie_id INTEGER NOT NULL," +
                    "seats TEXT NOT NULL," +
                    "total_price REAL NOT NULL," +
                    "booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)," +
                    "FOREIGN KEY (movie_id) REFERENCES movies(id)" +
                    ")";
            stmt.execute(createBookingsTable);
            
            // Seats table (to track occupied seats)
            String createSeatsTable = "CREATE TABLE IF NOT EXISTS seats (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "movie_id INTEGER NOT NULL," +
                    "seat_label TEXT NOT NULL," +
                    "is_occupied BOOLEAN DEFAULT 0," +
                    "booking_id INTEGER," +
                    "FOREIGN KEY (movie_id) REFERENCES movies(id)," +
                    "FOREIGN KEY (booking_id) REFERENCES bookings(id)," +
                    "UNIQUE(movie_id, seat_label)" +
                    ")";
            stmt.execute(createSeatsTable);
            
            stmt.close();
            System.out.println("Database tables created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

