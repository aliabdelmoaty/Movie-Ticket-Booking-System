import database.DatabaseManager;
import model.User;
import model.Movie;
import model.Booking;
import core.BookingSystem;

public class TestDatabase {
    public static void main(String[] args) {
        System.out.println("=== Testing Database Connection ===\n");
        
        // Initialize database
        DatabaseManager db = DatabaseManager.getInstance();
        System.out.println("✅ Database initialized successfully!\n");
        
        BookingSystem system = BookingSystem.getInstance();
        
        // Test 1: Register a test user
        System.out.println("--- Test 1: User Registration ---");
        boolean registered = system.register("Test User", "test@example.com", "testuser", "password123");
        if (registered) {
            System.out.println("✅ User registered successfully!");
        } else {
            System.out.println("⚠️ User already exists or registration failed");
        }
        System.out.println();
        
        // Test 2: Login
        System.out.println("--- Test 2: User Login ---");
        boolean loggedIn = system.login("test@example.com", "password123");
        if (loggedIn) {
            System.out.println("✅ Login successful!");
            System.out.println("Current user: " + system.getCurrentUser().getName());
        } else {
            System.out.println("❌ Login failed!");
        }
        System.out.println();
        
        // Test 3: Add a test movie
        System.out.println("--- Test 3: Add Movie ---");
        boolean movieAdded = system.addMovie(
            "The Dark Knight",
            "Action, Crime, Drama",
            "2h 32m",
            "9.0",
            "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
            "assets/posters/tony.jpeg"
        );
        if (movieAdded) {
            System.out.println("✅ Movie added successfully!");
        } else {
            System.out.println("⚠️ Movie already exists or failed to add");
        }
        System.out.println();
        
        // Test 4: Get all movies
        System.out.println("--- Test 4: List All Movies ---");
        java.util.List<Movie> movies = system.getAllMovies();
        System.out.println("Total movies: " + movies.size());
        for (Movie movie : movies) {
            System.out.println("- " + movie.getTitle() + " (" + movie.getRating() + "/10)");
        }
        System.out.println();
        
        // Test 5: Create a booking
        if (loggedIn && !movies.isEmpty()) {
            System.out.println("--- Test 5: Create Booking ---");
            Movie firstMovie = movies.get(0);
            boolean bookingCreated = system.createBooking(
                firstMovie.getId(),
                "A1, A2, A3",
                45.00
            );
            if (bookingCreated) {
                System.out.println("✅ Booking created successfully!");
            } else {
                System.out.println("❌ Booking failed!");
            }
            System.out.println();
        }
        
        // Test 6: Get user bookings
        if (loggedIn) {
            System.out.println("--- Test 6: User Bookings ---");
            java.util.List<Booking> bookings = system.getUserBookings();
            System.out.println("Total bookings: " + bookings.size());
            for (Booking booking : bookings) {
                System.out.println("- Movie: " + booking.getMovieTitle());
                System.out.println("  Seats: " + booking.getSeats());
                System.out.println("  Price: $" + booking.getTotalPrice());
                System.out.println("  Date: " + booking.getBookingDate());
            }
        }
        
        System.out.println("\n=== All Tests Completed ===");
        
        // Cleanup
        db.closeConnection();
    }
}

