import model.Booking;
import core.BookingSystem;
import java.util.List;

public class TestSeats {
    public static void main(String[] args) {
        System.out.println("=== Testing Occupied Seats ===\n");
        
        BookingSystem system = BookingSystem.getInstance();
        
        // Test for movie ID 4 (Tony sopranos)
        int movieId = 4;
        System.out.println("Checking occupied seats for Movie ID: " + movieId);
        
        List<String> occupiedSeats = Booking.getOccupiedSeats(movieId);
        
        System.out.println("Total occupied seats: " + occupiedSeats.size());
        System.out.println("Occupied seats:");
        for (String seat : occupiedSeats) {
            System.out.println("  - " + seat);
        }
        
        // Test specific seats
        System.out.println("\nTesting specific seats:");
        System.out.println("  A1 occupied? " + Booking.isSeatOccupied(movieId, "A1"));
        System.out.println("  A2 occupied? " + Booking.isSeatOccupied(movieId, "A2"));
        System.out.println("  A3 occupied? " + Booking.isSeatOccupied(movieId, "A3"));
        System.out.println("  B1 occupied? " + Booking.isSeatOccupied(movieId, "B1"));
        System.out.println("  C5 occupied? " + Booking.isSeatOccupied(movieId, "C5"));
        
        System.out.println("\n✅ Test Complete!");
    }
}

