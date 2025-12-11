package model;

import database.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private int id;
    private int userId;
    private int movieId;
    private String seats;
    private double totalPrice;
    private Timestamp bookingDate;
    
    // For display purposes
    private String userName;
    private String movieTitle;
    
    // Constructors
    public Booking() {}
    
    public Booking(int userId, int movieId, String seats, double totalPrice) {
        this.userId = userId;
        this.movieId = movieId;
        this.seats = seats;
        this.totalPrice = totalPrice;
    }
    
    public Booking(int id, int userId, int movieId, String seats, double totalPrice, Timestamp bookingDate) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
        this.seats = seats;
        this.totalPrice = totalPrice;
        this.bookingDate = bookingDate;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }
    
    public String getSeats() { return seats; }
    public void setSeats(String seats) { this.seats = seats; }
    
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    
    public Timestamp getBookingDate() { return bookingDate; }
    public void setBookingDate(Timestamp bookingDate) { this.bookingDate = bookingDate; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    
    // Database operations
    public boolean save() {
        String sql = "INSERT INTO bookings (user_id, movie_id, seats, total_price) VALUES (?, ?, ?, ?)";
        
        try {
            PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, movieId);
            pstmt.setString(3, seats);
            pstmt.setDouble(4, totalPrice);
            
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
                
                // Mark seats as occupied
                markSeatsAsOccupied();
                
                return true;
            }
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private void markSeatsAsOccupied() {
        String[] seatArray = seats.split(", ");
        String sql = "INSERT OR REPLACE INTO seats (movie_id, seat_label, is_occupied, booking_id) VALUES (?, ?, 1, ?)";
        
        try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            for (String seat : seatArray) {
                pstmt.setInt(1, movieId);
                pstmt.setString(2, seat.trim());
                pstmt.setInt(3, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Booking> getBookingsByUser(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.name as user_name, m.title as movie_title " +
                    "FROM bookings b " +
                    "JOIN users u ON b.user_id = u.id " +
                    "JOIN movies m ON b.movie_id = m.id " +
                    "WHERE b.user_id = ? " +
                    "ORDER BY b.booking_date DESC";
        
        try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = new Booking(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("movie_id"),
                    rs.getString("seats"),
                    rs.getDouble("total_price"),
                    rs.getTimestamp("booking_date")
                );
                booking.setUserName(rs.getString("user_name"));
                booking.setMovieTitle(rs.getString("movie_title"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public static List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.name as user_name, m.title as movie_title " +
                    "FROM bookings b " +
                    "JOIN users u ON b.user_id = u.id " +
                    "JOIN movies m ON b.movie_id = m.id " +
                    "ORDER BY b.booking_date DESC";
        
        try (Statement stmt = DatabaseManager.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Booking booking = new Booking(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("movie_id"),
                    rs.getString("seats"),
                    rs.getDouble("total_price"),
                    rs.getTimestamp("booking_date")
                );
                booking.setUserName(rs.getString("user_name"));
                booking.setMovieTitle(rs.getString("movie_title"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public static boolean isSeatOccupied(int movieId, String seatLabel) {
        String sql = "SELECT is_occupied FROM seats WHERE movie_id = ? AND seat_label = ?";
        
        try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setString(2, seatLabel);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("is_occupied");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static List<String> getOccupiedSeats(int movieId) {
        List<String> occupiedSeats = new ArrayList<>();
        String sql = "SELECT seat_label FROM seats WHERE movie_id = ? AND is_occupied = 1";
        
        try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                occupiedSeats.add(rs.getString("seat_label"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return occupiedSeats;
    }
}

