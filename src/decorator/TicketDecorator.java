package decorator;

/**
 * Decorator Pattern: TicketDecorator
 * Allows adding additional features and services to tickets dynamically
 * Each decorator adds extra functionality without modifying the base ticket
 */

// Component interface
interface Ticket {
    String getDescription();
    double getCost();
}

// Concrete Component - Base Ticket
class BaseTicket implements Ticket {
    private String movieTitle;
    private String seatNumber;
    private double basePrice;
    
    public BaseTicket(String movieTitle, String seatNumber, double basePrice) {
        this.movieTitle = movieTitle;
        this.seatNumber = seatNumber;
        this.basePrice = basePrice;
    }
    
    @Override
    public String getDescription() {
        return "Movie: " + movieTitle + " | Seat: " + seatNumber;
    }
    
    @Override
    public double getCost() {
        return basePrice;
    }
}

// Abstract Decorator
abstract class TicketDecorator implements Ticket {
    protected Ticket ticket;
    
    public TicketDecorator(Ticket ticket) {
        this.ticket = ticket;
    }
    
    @Override
    public String getDescription() {
        return ticket.getDescription();
    }
    
    @Override
    public double getCost() {
        return ticket.getCost();
    }
}

// Concrete Decorator 1: Popcorn and Drink Combo
class PopcornDrinkDecorator extends TicketDecorator {
    private String comboSize;
    
    public PopcornDrinkDecorator(Ticket ticket, String comboSize) {
        super(ticket);
        this.comboSize = comboSize;
    }
    
    @Override
    public String getDescription() {
        return ticket.getDescription() + " + Popcorn & Drink Combo (" + comboSize + ")";
    }
    
    @Override
    public double getCost() {
        double comboCost = 0;
        switch (comboSize.toLowerCase()) {
            case "small":
                comboCost = 5.99;
                break;
            case "medium":
                comboCost = 7.99;
                break;
            case "large":
                comboCost = 9.99;
                break;
            default:
                comboCost = 7.99;
        }
        return ticket.getCost() + comboCost;
    }
}

// Concrete Decorator 2: 3D Glasses
class ThreeDGlassesDecorator extends TicketDecorator {
    
    public ThreeDGlassesDecorator(Ticket ticket) {
        super(ticket);
    }
    
    @Override
    public String getDescription() {
        return ticket.getDescription() + " + 3D Glasses";
    }
    
    @Override
    public double getCost() {
        return ticket.getCost() + 3.50;
    }
}

// Concrete Decorator 3: Premium Seat Upgrade
class PremiumSeatDecorator extends TicketDecorator {
    
    public PremiumSeatDecorator(Ticket ticket) {
        super(ticket);
    }
    
    @Override
    public String getDescription() {
        return ticket.getDescription() + " + Premium Reclining Seat";
    }
    
    @Override
    public double getCost() {
        return ticket.getCost() + 5.00;
    }
}

// Concrete Decorator 4: VIP Lounge Access
class VIPLoungeDecorator extends TicketDecorator {
    
    public VIPLoungeDecorator(Ticket ticket) {
        super(ticket);
    }
    
    @Override
    public String getDescription() {
        return ticket.getDescription() + " + VIP Lounge Access";
    }
    
    @Override
    public double getCost() {
        return ticket.getCost() + 15.00;
    }
}

// Concrete Decorator 5: Reserved Parking
class ReservedParkingDecorator extends TicketDecorator {
    
    public ReservedParkingDecorator(Ticket ticket) {
        super(ticket);
    }
    
    @Override
    public String getDescription() {
        return ticket.getDescription() + " + Reserved Parking";
    }
    
    @Override
    public double getCost() {
        return ticket.getCost() + 5.00;
    }
}

// Concrete Decorator 6: Meal Voucher
class MealVoucherDecorator extends TicketDecorator {
    private String mealType;
    
    public MealVoucherDecorator(Ticket ticket, String mealType) {
        super(ticket);
        this.mealType = mealType;
    }
    
    @Override
    public String getDescription() {
        return ticket.getDescription() + " + " + mealType + " Meal Voucher";
    }
    
    @Override
    public double getCost() {
        double mealCost = 0;
        switch (mealType.toLowerCase()) {
            case "snack":
                mealCost = 8.99;
                break;
            case "dinner":
                mealCost = 15.99;
                break;
            case "deluxe":
                mealCost = 22.99;
                break;
            default:
                mealCost = 8.99;
        }
        return ticket.getCost() + mealCost;
    }
}

// Concrete Decorator 7: Insurance (for cancellation/rescheduling)
class TicketInsuranceDecorator extends TicketDecorator {
    
    public TicketInsuranceDecorator(Ticket ticket) {
        super(ticket);
    }
    
    @Override
    public String getDescription() {
        return ticket.getDescription() + " + Cancellation Insurance";
    }
    
    @Override
    public double getCost() {
        return ticket.getCost() + 2.50;
    }
}

// Helper class to build tickets with decorators
class TicketBuilder {
    
    public static Ticket createBasicTicket(String movieTitle, String seatNumber, double basePrice) {
        return new BaseTicket(movieTitle, seatNumber, basePrice);
    }
    
    public static Ticket createStandardPackage(String movieTitle, String seatNumber, double basePrice) {
        Ticket ticket = new BaseTicket(movieTitle, seatNumber, basePrice);
        ticket = new PopcornDrinkDecorator(ticket, "Medium");
        return ticket;
    }
    
    public static Ticket createPremiumPackage(String movieTitle, String seatNumber, double basePrice) {
        Ticket ticket = new BaseTicket(movieTitle, seatNumber, basePrice);
        ticket = new PremiumSeatDecorator(ticket);
        ticket = new PopcornDrinkDecorator(ticket, "Large");
        ticket = new ReservedParkingDecorator(ticket);
        return ticket;
    }
    
    public static Ticket createVIPPackage(String movieTitle, String seatNumber, double basePrice) {
        Ticket ticket = new BaseTicket(movieTitle, seatNumber, basePrice);
        ticket = new PremiumSeatDecorator(ticket);
        ticket = new VIPLoungeDecorator(ticket);
        ticket = new MealVoucherDecorator(ticket, "Deluxe");
        ticket = new ReservedParkingDecorator(ticket);
        ticket = new TicketInsuranceDecorator(ticket);
        return ticket;
    }
    
    public static Ticket create3DPackage(String movieTitle, String seatNumber, double basePrice) {
        Ticket ticket = new BaseTicket(movieTitle, seatNumber, basePrice);
        ticket = new ThreeDGlassesDecorator(ticket);
        ticket = new PopcornDrinkDecorator(ticket, "Medium");
        return ticket;
    }
}
