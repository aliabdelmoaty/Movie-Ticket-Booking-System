package model;

import database.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Movie {
    private int id;
    private String title;
    private String genre;
    private String duration;
    private String rating;
    private String description;
    private String posterPath;
    private Timestamp createdAt;
    
    // Constructors
    public Movie() {}
    
    public Movie(String title, String genre, String duration, String rating, String description, String posterPath) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.description = description;
        this.posterPath = posterPath;
    }
    
    public Movie(int id, String title, String genre, String duration, String rating, String description, String posterPath, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.description = description;
        this.posterPath = posterPath;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    // Database operations
    public boolean save() {
        String sql = "INSERT INTO movies (title, genre, duration, rating, description, poster_path) VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, genre);
            pstmt.setString(3, duration);
            pstmt.setString(4, rating);
            pstmt.setString(5, description);
            pstmt.setString(6, posterPath);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the last inserted ID using SQLite's last_insert_rowid()
                Statement stmt = DatabaseManager.getInstance().getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()");
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
                rs.close();
                stmt.close();
                pstmt.close();
                return true;
            }
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies";
        
        try (Statement stmt = DatabaseManager.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                movies.add(new Movie(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("genre"),
                    rs.getString("duration"),
                    rs.getString("rating"),
                    rs.getString("description"),
                    rs.getString("poster_path"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
    
    public static Movie findById(int id) {
        String sql = "SELECT * FROM movies WHERE id = ?";
        
        try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Movie(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("genre"),
                    rs.getString("duration"),
                    rs.getString("rating"),
                    rs.getString("description"),
                    rs.getString("poster_path"),
                    rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static List<Movie> searchByTitle(String searchTerm) {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE title LIKE ?";
        
        try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                movies.add(new Movie(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("genre"),
                    rs.getString("duration"),
                    rs.getString("rating"),
                    rs.getString("description"),
                    rs.getString("poster_path"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
}
