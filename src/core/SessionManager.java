package core;

import model.User;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Pattern: SessionManager
 * Manages user sessions and booking status
 * Ensures only one instance exists throughout the application
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private Map<Integer, String> activeBookings; // movieId -> booking status
    
    private SessionManager() {
        this.activeBookings = new HashMap<>();
    }
    
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    // User Session Management
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isUserLoggedIn() {
        return currentUser != null;
    }
    
    public void logout() {
        currentUser = null;
        activeBookings.clear();
    }
    
    // Booking Status Management
    public void addActiveBooking(int movieId, String status) {
        activeBookings.put(movieId, status);
    }
    
    public String getBookingStatus(int movieId) {
        return activeBookings.getOrDefault(movieId, "Not Started");
    }
    
    public void removeActiveBooking(int movieId) {
        activeBookings.remove(movieId);
    }
    
    public Map<Integer, String> getAllActiveBookings() {
        return new HashMap<>(activeBookings);
    }
    
    public void clearAllBookings() {
        activeBookings.clear();
    }
}
