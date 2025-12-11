package factory;

/**
 * Factory Pattern: TheaterFactory
 * Creates different types of theater locations and configurations
 */
public class TheaterFactory {
    
    public enum TheaterType {
        STANDARD, IMAX, VIP, DOLBY_ATMOS, FOUR_DX
    }
    
    public static Theater createTheater(TheaterType type, int capacity) {
        switch (type) {
            case STANDARD:
                return new StandardTheater(capacity);
            case IMAX:
                return new IMAXTheater(capacity);
            case VIP:
                return new VIPTheater(capacity);
            case DOLBY_ATMOS:
                return new DolbyAtmosTheater(capacity);
            case FOUR_DX:
                return new FourDXTheater(capacity);
            default:
                return new StandardTheater(capacity);
        }
    }
    
    // Theater Interface
    public interface Theater {
        String getName();
        String getDescription();
        double getPriceMultiplier();
        int getCapacity();
        String[] getFeatures();
    }
    
    // Standard Theater
    static class StandardTheater implements Theater {
        private int capacity;
        
        public StandardTheater(int capacity) {
            this.capacity = capacity;
        }
        
        @Override
        public String getName() {
            return "Standard Cinema Hall";
        }
        
        @Override
        public String getDescription() {
            return "Regular cinema hall with comfortable seating";
        }
        
        @Override
        public double getPriceMultiplier() {
            return 1.0;
        }
        
        @Override
        public int getCapacity() {
            return capacity;
        }
        
        @Override
        public String[] getFeatures() {
            return new String[]{"Comfortable Seating", "Digital Sound", "HD Screen"};
        }
    }
    
    // IMAX Theater
    static class IMAXTheater implements Theater {
        private int capacity;
        
        public IMAXTheater(int capacity) {
            this.capacity = capacity;
        }
        
        @Override
        public String getName() {
            return "IMAX Theater";
        }
        
        @Override
        public String getDescription() {
            return "Premium IMAX experience with massive screen and immersive sound";
        }
        
        @Override
        public double getPriceMultiplier() {
            return 1.8;
        }
        
        @Override
        public int getCapacity() {
            return capacity;
        }
        
        @Override
        public String[] getFeatures() {
            return new String[]{"Giant IMAX Screen", "12-Channel Sound", "Laser Projection", "Premium Seating"};
        }
    }
    
    // VIP Theater
    static class VIPTheater implements Theater {
        private int capacity;
        
        public VIPTheater(int capacity) {
            this.capacity = capacity;
        }
        
        @Override
        public String getName() {
            return "VIP Luxury Theater";
        }
        
        @Override
        public String getDescription() {
            return "Luxury VIP experience with reclining seats and waiter service";
        }
        
        @Override
        public double getPriceMultiplier() {
            return 2.5;
        }
        
        @Override
        public int getCapacity() {
            return capacity;
        }
        
        @Override
        public String[] getFeatures() {
            return new String[]{"Reclining Leather Seats", "Waiter Service", "Premium Sound", "Extra Legroom"};
        }
    }
    
    // Dolby Atmos Theater
    static class DolbyAtmosTheater implements Theater {
        private int capacity;
        
        public DolbyAtmosTheater(int capacity) {
            this.capacity = capacity;
        }
        
        @Override
        public String getName() {
            return "Dolby Atmos Theater";
        }
        
        @Override
        public String getDescription() {
            return "Immersive audio experience with Dolby Atmos technology";
        }
        
        @Override
        public double getPriceMultiplier() {
            return 1.5;
        }
        
        @Override
        public int getCapacity() {
            return capacity;
        }
        
        @Override
        public String[] getFeatures() {
            return new String[]{"Dolby Atmos Sound", "Enhanced Visuals", "Comfortable Seating", "Object-Based Audio"};
        }
    }
    
    // 4DX Theater
    static class FourDXTheater implements Theater {
        private int capacity;
        
        public FourDXTheater(int capacity) {
            this.capacity = capacity;
        }
        
        @Override
        public String getName() {
            return "4DX Theater";
        }
        
        @Override
        public String getDescription() {
            return "4D experience with motion seats, wind, and environmental effects";
        }
        
        @Override
        public double getPriceMultiplier() {
            return 2.0;
        }
        
        @Override
        public int getCapacity() {
            return capacity;
        }
        
        @Override
        public String[] getFeatures() {
            return new String[]{"Motion Seats", "Wind Effects", "Water Spray", "Scent Effects", "Lighting Effects"};
        }
    }
}
