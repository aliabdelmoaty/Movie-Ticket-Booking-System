package core;

import model.User;
import model.Movie;
import model.Booking;
import database.DatabaseManager;

/**
 * Singleton Pattern: BookingSystem
 * Central system for managing all ticket bookings
 * Ensures only one instance manages all bookings
 */
public class BookingSystem {
    private static BookingSystem instance;
    private User currentUser;
    private SessionManager sessionManager;
    
    private BookingSystem() {
        // Initialize database
        DatabaseManager.getInstance();
        // Initialize session manager
        sessionManager = SessionManager.getInstance();
    }
    
    public static synchronized BookingSystem getInstance() {
        if (instance == null) {
            instance = new BookingSystem();
        }
        return instance;
    }
    
    // User Authentication
    public boolean register(String name, String email, String username, String password) {
        // Check if user already exists
        if (User.findByEmail(email) != null) {
            return false; // Email already exists
        }
        
        if (User.findByUsername(username) != null) {
            return false; // Username already exists
        }
        
        User user = new User(name, email, username, password);
        return user.save();
    }
    
    public boolean login(String email, String password) {
        User user = User.findByEmailAndPassword(email, password);
        if (user != null) {
            currentUser = user;
            sessionManager.setCurrentUser(user);
            return true;
        }
        return false;
    }
    
    public void logout() {
        currentUser = null;
        sessionManager.logout();
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    // Movie Management
    public boolean addMovie(String title, String genre, String duration, String rating, String description, String posterPath) {
        Movie movie = new Movie(title, genre, duration, rating, description, posterPath);
        return movie.save();
    }
    
    public java.util.List<Movie> getAllMovies() {
        return Movie.getAllMovies();
    }
    
    public java.util.List<Movie> searchMovies(String searchTerm) {
        return Movie.searchByTitle(searchTerm);
    }
    
    public Movie getMovieById(int movieId) {
        return Movie.findById(movieId);
    }
    
    // Booking Management
    public boolean createBooking(int movieId, String seats, double totalPrice) {
        if (!isLoggedIn()) {
            return false;
        }
        
        Booking booking = new Booking(currentUser.getId(), movieId, seats, totalPrice);
        boolean success = booking.save();
        
        if (success) {
            sessionManager.addActiveBooking(movieId, "Created");
        }
        
        return success;
    }
    
    public SessionManager getSessionManager() {
        return sessionManager;
    }
    
    public java.util.List<Booking> getUserBookings() {
        if (!isLoggedIn()) {
            return new java.util.ArrayList<>();
        }
        return Booking.getBookingsByUser(currentUser.getId());
    }
    
    public java.util.List<Booking> getAllBookings() {
        return Booking.getAllBookings();
    }
    
    public boolean isSeatOccupied(int movieId, String seatLabel) {
        return Booking.isSeatOccupied(movieId, seatLabel);
    }
    
    public java.util.List<String> getOccupiedSeats(int movieId) {
        return Booking.getOccupiedSeats(movieId);
    }
    
    // Cleanup
    public void cleanup() {
        DatabaseManager.getInstance().closeConnection();
    }
}
