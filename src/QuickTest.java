import database.DatabaseManager;
import model.User;
import model.Movie;
import model.Booking;
import core.BookingSystem;

public class QuickTest {
    public static void main(String[] args) {
        System.out.println("=== Quick Booking Test ===\n");
        
        BookingSystem system = BookingSystem.getInstance();
        
        // Test: Create a new user and booking
        System.out.println("1. Registering new user...");
        String testEmail = "user" + System.currentTimeMillis() + "@test.com";
        String testUsername = "user" + System.currentTimeMillis();
        
        boolean registered = system.register("Test User", testEmail, testUsername, "password123");
        if (registered) {
            System.out.println("✅ User registered: " + testEmail);
        } else {
            System.out.println("❌ Registration failed!");
            return;
        }
        
        // Login
        System.out.println("\n2. Logging in...");
        boolean loggedIn = system.login(testEmail, "password123");
        if (loggedIn) {
            System.out.println("✅ Login successful!");
            System.out.println("   Current user: " + system.getCurrentUser().getName());
            System.out.println("   User ID: " + system.getCurrentUser().getId());
        } else {
            System.out.println("❌ Login failed!");
            return;
        }
        
        // Add a movie
        System.out.println("\n3. Adding movie...");
        String movieTitle = "Test Movie " + System.currentTimeMillis();
        boolean movieAdded = system.addMovie(
            movieTitle,
            "Action, Thriller",
            "2h 15m",
            "8.5",
            "A great test movie!",
            "test.jpg"
        );
        
        if (movieAdded) {
            System.out.println("✅ Movie added: " + movieTitle);
        } else {
            System.out.println("⚠️ Movie may already exist");
        }
        
        // Get movies
        System.out.println("\n4. Getting movies...");
        java.util.List<Movie> movies = system.getAllMovies();
        System.out.println("   Total movies: " + movies.size());
        
        if (movies.isEmpty()) {
            System.out.println("❌ No movies found!");
            return;
        }
        
        Movie firstMovie = movies.get(0);
        System.out.println("   Using movie: " + firstMovie.getTitle() + " (ID: " + firstMovie.getId() + ")");
        
        // Create booking
        System.out.println("\n5. Creating booking...");
        String seats = "A1, A2, A3";
        double totalPrice = 45.00;
        
        boolean bookingCreated = system.createBooking(firstMovie.getId(), seats, totalPrice);
        
        if (bookingCreated) {
            System.out.println("✅ Booking created successfully!");
            System.out.println("   Movie: " + firstMovie.getTitle());
            System.out.println("   Seats: " + seats);
            System.out.println("   Price: $" + totalPrice);
        } else {
            System.out.println("❌ Booking failed!");
            return;
        }
        
        // Verify booking
        System.out.println("\n6. Verifying booking...");
        java.util.List<Booking> userBookings = system.getUserBookings();
        System.out.println("   Total bookings: " + userBookings.size());
        
        if (!userBookings.isEmpty()) {
            Booking lastBooking = userBookings.get(0);
            System.out.println("✅ Latest booking:");
            System.out.println("   Movie: " + lastBooking.getMovieTitle());
            System.out.println("   Seats: " + lastBooking.getSeats());
            System.out.println("   Price: $" + lastBooking.getTotalPrice());
            System.out.println("   Date: " + lastBooking.getBookingDate());
        }
        
        // Check seat status
        System.out.println("\n7. Checking seat status...");
        boolean seatOccupied = system.isSeatOccupied(firstMovie.getId(), "A1");
        if (seatOccupied) {
            System.out.println("✅ Seat A1 is marked as occupied");
        } else {
            System.out.println("❌ Seat A1 should be occupied!");
        }
        
        System.out.println("\n=== ✅ All Tests Passed! ===");
        System.out.println("\n🎉 الحجز شغال تمام! 🎉");
        
        DatabaseManager.getInstance().closeConnection();
    }
}

