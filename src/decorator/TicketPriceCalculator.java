package decorator;

/**
 * Public helper class to calculate ticket prices using Decorator Pattern
 * This class provides a public interface to use the decorator pattern
 * for calculating ticket prices with various extras
 */
public class TicketPriceCalculator {
    
    /**
     * Calculate total price using Decorator Pattern
     * @param movieTitle Movie title
     * @param basePricePerSeat Base price per seat
     * @param numberOfSeats Number of seats selected
     * @param hasPopcorn Whether popcorn combo is selected
     * @param has3DGlasses Whether 3D glasses are selected
     * @param hasPremiumSeat Whether premium seat upgrade is selected
     * @return Total price with decorators applied
     */
    public static double calculateTotalPrice(String movieTitle, double basePricePerSeat, 
                                            int numberOfSeats, boolean hasPopcorn, 
                                            boolean has3DGlasses, boolean hasPremiumSeat) {
        if (numberOfSeats == 0) {
            return 0.0;
        }
        
        // Create base ticket (Decorator Pattern)
        String representativeSeat = "A1"; // Representative seat
        Ticket ticket = new BaseTicket(movieTitle, representativeSeat, basePricePerSeat);
        
        // Apply decorators dynamically (Decorator Pattern)
        if (hasPopcorn) {
            ticket = new PopcornDrinkDecorator(ticket, "Medium");
        }
        if (has3DGlasses) {
            ticket = new ThreeDGlassesDecorator(ticket);
        }
        if (hasPremiumSeat) {
            ticket = new PremiumSeatDecorator(ticket);
        }
        
        // Calculate total: decorator-enhanced price per seat * number of seats
        double enhancedPricePerSeat = ticket.getCost();
        double total = enhancedPricePerSeat * numberOfSeats;
        
        // Adjust for items that are per-booking (popcorn is typically one per booking)
        if (hasPopcorn) {
            double popcornCost = 7.99; // Medium size
            total = total - (popcornCost * (numberOfSeats - 1));
        }
        
        return total;
    }
}
