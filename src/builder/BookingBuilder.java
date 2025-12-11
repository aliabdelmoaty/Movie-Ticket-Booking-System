package builder;

import model.Booking;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder Pattern: BookingBuilder
 * Provides a flexible way to construct Booking objects
 * Handles complex booking configurations with multiple options
 */
public class BookingBuilder {
    private int userId;
    private int movieId;
    private List<String> seats;
    private double basePrice;
    private double discount;
    private double serviceFee;
    private double tax;
    private String theaterType;
    private double theaterMultiplier;
    
    public BookingBuilder() {
        this.seats = new ArrayList<>();
        this.basePrice = 10.0; // Default ticket price
        this.discount = 0.0;
        this.serviceFee = 1.5;
        this.tax = 0.0;
        this.theaterType = "Standard";
        this.theaterMultiplier = 1.0;
    }
    
    public BookingBuilder setUserId(int userId) {
        this.userId = userId;
        return this;
    }
    
    public BookingBuilder setMovieId(int movieId) {
        this.movieId = movieId;
        return this;
    }
    
    public BookingBuilder addSeat(String seat) {
        this.seats.add(seat);
        return this;
    }
    
    public BookingBuilder addSeats(List<String> seats) {
        this.seats.addAll(seats);
        return this;
    }
    
    public BookingBuilder setBasePrice(double basePrice) {
        this.basePrice = basePrice;
        return this;
    }
    
    public BookingBuilder setDiscount(double discount) {
        this.discount = discount;
        return this;
    }
    
    public BookingBuilder setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
        return this;
    }
    
    public BookingBuilder setTax(double tax) {
        this.tax = tax;
        return this;
    }
    
    public BookingBuilder setTheaterType(String theaterType, double multiplier) {
        this.theaterType = theaterType;
        this.theaterMultiplier = multiplier;
        return this;
    }
    
    // Convenience methods for special booking types
    public BookingBuilder applyStudentDiscount() {
        this.discount = 0.15; // 15% discount
        return this;
    }
    
    public BookingBuilder applySeniorDiscount() {
        this.discount = 0.20; // 20% discount
        return this;
    }
    
    public BookingBuilder applyGroupDiscount(int numberOfSeats) {
        if (numberOfSeats >= 5) {
            this.discount = 0.10; // 10% discount for groups of 5+
        }
        if (numberOfSeats >= 10) {
            this.discount = 0.15; // 15% discount for groups of 10+
        }
        return this;
    }
    
    public BookingBuilder applyWeekdayDiscount() {
        this.discount = 0.10; // 10% weekday discount
        return this;
    }
    
    // Calculate total price
    private double calculateTotalPrice() {
        int numberOfSeats = seats.size();
        double subtotal = basePrice * numberOfSeats * theaterMultiplier;
        double discountAmount = subtotal * discount;
        double afterDiscount = subtotal - discountAmount;
        double taxAmount = afterDiscount * tax;
        double total = afterDiscount + serviceFee + taxAmount;
        
        return Math.round(total * 100.0) / 100.0; // Round to 2 decimal places
    }
    
    // Build the final Booking object
    public Booking build() {
        if (userId <= 0) {
            throw new IllegalStateException("Valid user ID is required");
        }
        if (movieId <= 0) {
            throw new IllegalStateException("Valid movie ID is required");
        }
        if (seats.isEmpty()) {
            throw new IllegalStateException("At least one seat must be selected");
        }
        
        String seatsString = String.join(", ", seats);
        double totalPrice = calculateTotalPrice();
        
        return new Booking(userId, movieId, seatsString, totalPrice);
    }
    
    // Get booking summary before building
    public String getBookingSummary() {
        int numberOfSeats = seats.size();
        double subtotal = basePrice * numberOfSeats * theaterMultiplier;
        double discountAmount = subtotal * discount;
        double afterDiscount = subtotal - discountAmount;
        double taxAmount = afterDiscount * tax;
        double total = afterDiscount + serviceFee + taxAmount;
        
        StringBuilder summary = new StringBuilder();
        summary.append("Booking Summary:\n");
        summary.append("Theater Type: ").append(theaterType).append("\n");
        summary.append("Number of Seats: ").append(numberOfSeats).append("\n");
        summary.append("Seats: ").append(String.join(", ", seats)).append("\n");
        summary.append("Base Price per Seat: $").append(basePrice).append("\n");
        summary.append("Subtotal: $").append(String.format("%.2f", subtotal)).append("\n");
        
        if (discount > 0) {
            summary.append("Discount (").append((int)(discount * 100)).append("%): -$")
                   .append(String.format("%.2f", discountAmount)).append("\n");
        }
        
        summary.append("Service Fee: $").append(serviceFee).append("\n");
        
        if (tax > 0) {
            summary.append("Tax (").append((int)(tax * 100)).append("%): $")
                   .append(String.format("%.2f", taxAmount)).append("\n");
        }
        
        summary.append("Total: $").append(String.format("%.2f", total));
        
        return summary.toString();
    }
    
    // Static method to create a builder
    public static BookingBuilder newBooking() {
        return new BookingBuilder();
    }
}
