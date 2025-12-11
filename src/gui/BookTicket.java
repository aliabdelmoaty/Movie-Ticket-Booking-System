package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import core.BookingSystem;
import model.Booking;

public class BookTicket extends JFrame {
    private BookingFrame.Movie movie;
    private int movieId;
    private JPanel seatsPanel;
    private JLabel selectedSeatsLabel;
    private JLabel totalPriceLabel;
    private List<SeatButton> selectedSeats;
    private static final double SEAT_PRICE = 15.00;
    private static final int ROWS = 8;
    private static final int COLS = 12;
    
    public BookTicket(BookingFrame.Movie movie) {
        this(movie, 1); // Default movie ID
    }
    
    public BookTicket(BookingFrame.Movie movie, int movieId) {
        this.movie = movie;
        this.movieId = movieId;
        this.selectedSeats = new ArrayList<>();
        
        setTitle("Book Ticket - " + movie.getTitle());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(16, 22, 34));
        add(mainPanel);
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center panel with screen and seats
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(16, 22, 34));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Screen display
        JPanel screenPanel = createScreenPanel();
        centerPanel.add(screenPanel);
        centerPanel.add(Box.createVerticalStrut(30));
        
        // Legend panel
        JPanel legendPanel = createLegendPanel();
        centerPanel.add(legendPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        
        // Seats panel
        seatsPanel = createSeatsPanel();
        centerPanel.add(seatsPanel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer panel with booking info and button
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(28, 31, 39));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(59, 67, 84)),
            BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));
        
        // Left side - Movie info
        JPanel movieInfoPanel = new JPanel();
        movieInfoPanel.setLayout(new BoxLayout(movieInfoPanel, BoxLayout.Y_AXIS));
        movieInfoPanel.setBackground(new Color(28, 31, 39));
        
        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("Spline Sans", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel genreLabel = new JLabel(movie.getGenre() + " • ⭐ " + movie.getRating());
        genreLabel.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        genreLabel.setForeground(new Color(150, 155, 170));
        genreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        movieInfoPanel.add(titleLabel);
        movieInfoPanel.add(Box.createVerticalStrut(5));
        movieInfoPanel.add(genreLabel);
        
        // Right side - Back button
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Spline Sans", Font.BOLD, 14));
        backButton.setBackground(new Color(59, 67, 84));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> dispose());
        
        headerPanel.add(movieInfoPanel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createScreenPanel() {
        JPanel screenPanel = new JPanel();
        screenPanel.setBackground(new Color(16, 22, 34));
        screenPanel.setLayout(new BoxLayout(screenPanel, BoxLayout.Y_AXIS));
        screenPanel.setMaximumSize(new Dimension(800, 60));
        
        // Screen visualization
        JPanel screen = new JPanel();
        screen.setBackground(new Color(200, 200, 200));
        screen.setPreferredSize(new Dimension(600, 8));
        screen.setMaximumSize(new Dimension(600, 8));
        
        JLabel screenLabel = new JLabel("SCREEN");
        screenLabel.setFont(new Font("Spline Sans", Font.BOLD, 14));
        screenLabel.setForeground(new Color(150, 155, 170));
        screenLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        screen.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        screenPanel.add(screen);
        screenPanel.add(Box.createVerticalStrut(10));
        screenPanel.add(screenLabel);
        
        return screenPanel;
    }
    
    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        legendPanel.setBackground(new Color(16, 22, 34));
        legendPanel.setMaximumSize(new Dimension(1000, 40));
        
        // Available
        JPanel availablePanel = createLegendItem("Available", new Color(59, 67, 84));
        // Selected
        JPanel selectedPanel = createLegendItem("Selected", new Color(19, 91, 236));
        // Occupied
        JPanel occupiedPanel = createLegendItem("Occupied", new Color(220, 53, 69));
        
        legendPanel.add(availablePanel);
        legendPanel.add(selectedPanel);
        legendPanel.add(occupiedPanel);
        
        return legendPanel;
    }
    
    private JPanel createLegendItem(String label, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setBackground(new Color(16, 22, 34));
        
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(30, 30));
        colorBox.setBorder(BorderFactory.createLineBorder(new Color(59, 67, 84), 1, true));
        
        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        textLabel.setForeground(Color.WHITE);
        
        panel.add(colorBox);
        panel.add(textLabel);
        
        return panel;
    }
    
    private JPanel createSeatsPanel() {
        JPanel container = new JPanel();
        container.setBackground(new Color(16, 22, 34));
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        
        // Create seats grid
        JPanel seatsGrid = new JPanel(new GridLayout(ROWS, COLS, 8, 8));
        seatsGrid.setBackground(new Color(16, 22, 34));
        seatsGrid.setMaximumSize(new Dimension(800, 400));
        
        // Get occupied seats from database
        List<String> occupiedSeats = Booking.getOccupiedSeats(movieId);
        
        // Generate seats with real occupied status from database
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                String seatLabel = (char)('A' + row) + String.valueOf(col + 1);
                boolean isOccupied = occupiedSeats.contains(seatLabel);
                SeatButton seatButton = new SeatButton(seatLabel, isOccupied, this);
                seatsGrid.add(seatButton);
            }
        }
        
        seatsGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(seatsGrid);
        
        return container;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(28, 31, 39));
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(59, 67, 84)),
            BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));
        
        // Left side - Selection info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(28, 31, 39));
        
        selectedSeatsLabel = new JLabel("Selected Seats: None");
        selectedSeatsLabel.setFont(new Font("Spline Sans", Font.BOLD, 16));
        selectedSeatsLabel.setForeground(Color.WHITE);
        selectedSeatsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        totalPriceLabel = new JLabel("Total: $0.00");
        totalPriceLabel.setFont(new Font("Spline Sans", Font.BOLD, 20));
        totalPriceLabel.setForeground(new Color(19, 91, 236));
        totalPriceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(selectedSeatsLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(totalPriceLabel);
        
        // Right side - Confirm button
        JButton confirmButton = new JButton("Confirm Booking");
        confirmButton.setFont(new Font("Spline Sans", Font.BOLD, 16));
        confirmButton.setBackground(new Color(19, 91, 236));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setPreferredSize(new Dimension(200, 50));
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.addActionListener(e -> confirmBooking());
        
        footerPanel.add(infoPanel, BorderLayout.WEST);
        footerPanel.add(confirmButton, BorderLayout.EAST);
        
        return footerPanel;
    }
    
    public void addSelectedSeat(SeatButton seat) {
        selectedSeats.add(seat);
    }
    
    public void removeSelectedSeat(SeatButton seat) {
        selectedSeats.remove(seat);
    }
    
    public void updateBookingInfo() {
        if (selectedSeats.isEmpty()) {
            selectedSeatsLabel.setText("Selected Seats: None");
            totalPriceLabel.setText("Total: $0.00");
        } else {
            StringBuilder seatsText = new StringBuilder("Selected Seats: ");
            for (int i = 0; i < selectedSeats.size(); i++) {
                seatsText.append(selectedSeats.get(i).getSeatLabel());
                if (i < selectedSeats.size() - 1) {
                    seatsText.append(", ");
                }
            }
            selectedSeatsLabel.setText(seatsText.toString());
            
            double total = selectedSeats.size() * SEAT_PRICE;
            totalPriceLabel.setText(String.format("Total: $%.2f", total));
        }
    }
    
    private void confirmBooking() {
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select at least one seat!",
                "No Seats Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        BookingSystem bookingSystem = BookingSystem.getInstance();
        
        // Check if user is logged in
        if (!bookingSystem.isLoggedIn()) {
            int choice = JOptionPane.showConfirmDialog(this,
                "You need to login to book tickets. Do you want to login now?",
                "Login Required",
                JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
            return;
        }
        
        // Build seats string
        StringBuilder seatsBuilder = new StringBuilder();
        for (int i = 0; i < selectedSeats.size(); i++) {
            seatsBuilder.append(selectedSeats.get(i).getSeatLabel());
            if (i < selectedSeats.size() - 1) {
                seatsBuilder.append(", ");
            }
        }
        String seats = seatsBuilder.toString();
        double totalPrice = selectedSeats.size() * SEAT_PRICE;
        
        // Save booking to database
        if (bookingSystem.createBooking(movieId, seats, totalPrice)) {
            StringBuilder message = new StringBuilder();
            message.append("Movie: ").append(movie.getTitle()).append("\n");
            message.append("Seats: ").append(seats).append("\n");
            message.append(String.format("Total: $%.2f", totalPrice));
            message.append("\n\nBooking confirmed!");
            
            JOptionPane.showMessageDialog(this,
                message.toString(),
                "Booking Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to create booking. Please try again.",
                "Booking Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
