package gui;

import javax.swing.*;
import java.awt.*;

public class MovieDetailsFrame extends JFrame {
    private BookingFrame.Movie movie;
    
    public MovieDetailsFrame(BookingFrame.Movie movie) {
        this.movie = movie;
        
        setTitle("Movie Details - " + movie.getTitle());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(16, 22, 34));
        add(mainPanel);
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        contentPanel.setBackground(new Color(16, 22, 34));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Left side - Movie Poster
        JPanel posterPanel = new JPanel(new BorderLayout());
        posterPanel.setPreferredSize(new Dimension(300, 450));
        posterPanel.setMaximumSize(new Dimension(300, 450));
        posterPanel.setBackground(new Color(40, 45, 55));
        posterPanel.setBorder(BorderFactory.createLineBorder(new Color(59, 67, 84), 2, true));
        
        if (movie.getPosterPath() != null) {
            try {
                ImageIcon icon = new ImageIcon(movie.getPosterPath());
                Image image = icon.getImage().getScaledInstance(300, 450, Image.SCALE_SMOOTH);
                JLabel posterLabel = new JLabel(new ImageIcon(image));
                posterPanel.add(posterLabel, BorderLayout.CENTER);
            } catch (Exception e) {
                addPosterPlaceholder(posterPanel);
            }
        } else {
            addPosterPlaceholder(posterPanel);
        }
        
        contentPanel.add(posterPanel);
        contentPanel.add(Box.createHorizontalStrut(40));
        
        // Right side - Movie Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(16, 22, 34));
        
        // Title
        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("Spline Sans", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Rating
        JLabel ratingLabel = new JLabel("⭐ " + movie.getRating() + " / 10");
        ratingLabel.setFont(new Font("Spline Sans", Font.BOLD, 18));
        ratingLabel.setForeground(new Color(255, 193, 7));
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Genre
        JLabel genreLabel = new JLabel(movie.getGenre());
        genreLabel.setFont(new Font("Spline Sans", Font.PLAIN, 16));
        genreLabel.setForeground(new Color(150, 155, 170));
        genreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Description
        JTextArea descriptionArea = new JTextArea(
            "Experience this amazing movie in our state-of-the-art cinema. " +
            "Book your tickets now and enjoy the best viewing experience with " +
            "premium sound and picture quality."
        );
        descriptionArea.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        descriptionArea.setForeground(new Color(200, 205, 220));
        descriptionArea.setBackground(new Color(16, 22, 34));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionArea.setMaximumSize(new Dimension(450, 150));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBackground(new Color(16, 22, 34));
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton bookButton = new JButton("Book Tickets");
        bookButton.setFont(new Font("Spline Sans", Font.BOLD, 16));
        bookButton.setBackground(new Color(19, 91, 236));
        bookButton.setForeground(Color.WHITE);
        bookButton.setFocusPainted(false);
        bookButton.setBorderPainted(false);
        bookButton.setPreferredSize(new Dimension(150, 45));
        bookButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookButton.addActionListener(e -> {
            BookTicket bookTicketFrame = new BookTicket(movie);
            bookTicketFrame.setVisible(true);
        });
        
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Spline Sans", Font.BOLD, 16));
        backButton.setBackground(new Color(59, 67, 84));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setPreferredSize(new Dimension(100, 45));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> dispose());
        
        buttonsPanel.add(bookButton);
        buttonsPanel.add(backButton);
        
        // Add all components to info panel
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(ratingLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(genreLabel);
        infoPanel.add(Box.createVerticalStrut(25));
        infoPanel.add(descriptionArea);
        infoPanel.add(Box.createVerticalStrut(30));
        infoPanel.add(buttonsPanel);
        infoPanel.add(Box.createVerticalGlue());
        
        contentPanel.add(infoPanel);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }
    
    private void addPosterPlaceholder(JPanel posterPanel) {
        JLabel placeholderLabel = new JLabel("🎬", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
        posterPanel.add(placeholderLabel, BorderLayout.CENTER);
    }
}

