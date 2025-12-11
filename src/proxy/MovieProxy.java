package proxy;

import model.Movie;
import model.User;

/**
 * Proxy Pattern: MovieProxy
 * Controls access to movie resources based on user permissions
 * Provides lazy loading and access control
 */

// Subject interface
interface MovieAccess {
    Movie getMovie();
    boolean canEdit();
    boolean canDelete();
    boolean canView();
}

// Real Subject
class RealMovie implements MovieAccess {
    private Movie movie;
    private User user;
    
    public RealMovie(Movie movie, User user) {
        this.movie = movie;
        this.user = user;
        loadMovieData();
    }
    
    private void loadMovieData() {
        // Simulate expensive operation of loading movie data
        System.out.println("Loading movie data: " + movie.getTitle());
    }
    
    @Override
    public Movie getMovie() {
        return movie;
    }
    
    @Override
    public boolean canEdit() {
        // Only admins can edit (for this example, check if user email contains "admin")
        return user != null && user.getEmail().contains("admin");
    }
    
    @Override
    public boolean canDelete() {
        // Only admins can delete
        return user != null && user.getEmail().contains("admin");
    }
    
    @Override
    public boolean canView() {
        // All logged-in users can view
        return user != null;
    }
}

// Proxy
public class MovieProxy implements MovieAccess {
    private Movie movie;
    private User user;
    private RealMovie realMovie;
    private String ageRating;
    private int userAge;
    
    public MovieProxy(Movie movie, User user) {
        this.movie = movie;
        this.user = user;
        this.ageRating = movie.getRating();
        this.userAge = 18; // Default age, could be retrieved from user profile
    }
    
    public MovieProxy(Movie movie, User user, int userAge) {
        this.movie = movie;
        this.user = user;
        this.ageRating = movie.getRating();
        this.userAge = userAge;
    }
    
    private void loadRealMovie() {
        if (realMovie == null) {
            realMovie = new RealMovie(movie, user);
        }
    }
    
    @Override
    public Movie getMovie() {
        // Check age restrictions before allowing access
        if (!checkAgeRestriction()) {
            System.out.println("Access Denied: Age restriction not met for " + movie.getTitle());
            return null;
        }
        
        // Lazy loading: only load the real movie if needed
        loadRealMovie();
        return realMovie.getMovie();
    }
    
    @Override
    public boolean canEdit() {
        loadRealMovie();
        return realMovie.canEdit();
    }
    
    @Override
    public boolean canDelete() {
        loadRealMovie();
        return realMovie.canDelete();
    }
    
    @Override
    public boolean canView() {
        if (!checkAgeRestriction()) {
            return false;
        }
        loadRealMovie();
        return realMovie.canView();
    }
    
    // Age restriction checking
    private boolean checkAgeRestriction() {
        switch (ageRating) {
            case "G":
                return true; // General audience
            case "PG":
                return userAge >= 8; // Parental guidance suggested
            case "PG-13":
                return userAge >= 13;
            case "R":
                return userAge >= 17;
            case "NC-17":
                return userAge >= 18;
            default:
                return true;
        }
    }
    
    // Additional proxy functionality - logging
    public void logAccess() {
        System.out.println("User " + (user != null ? user.getUsername() : "Unknown") + 
                         " accessed movie: " + movie.getTitle());
    }
    
    // Additional proxy functionality - caching
    public boolean isMovieLoaded() {
        return realMovie != null;
    }
}

// Protection Proxy for Admin Operations
class AdminMovieProxy {
    private Movie movie;
    private User user;
    
    public AdminMovieProxy(Movie movie, User user) {
        this.movie = movie;
        this.user = user;
    }
    
    public boolean updateMovie(String title, String genre, String duration, String rating, String description) {
        if (!isAdmin()) {
            System.out.println("Access Denied: Only administrators can update movies");
            return false;
        }
        
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setDuration(duration);
        movie.setRating(rating);
        movie.setDescription(description);
        
        System.out.println("Movie updated by admin: " + user.getUsername());
        return movie.save();
    }
    
    public boolean deleteMovie() {
        if (!isAdmin()) {
            System.out.println("Access Denied: Only administrators can delete movies");
            return false;
        }
        
        System.out.println("Movie deleted by admin: " + user.getUsername());
        // Implementation would delete from database
        return true;
    }
    
    private boolean isAdmin() {
        return user != null && user.getEmail().contains("admin");
    }
}
