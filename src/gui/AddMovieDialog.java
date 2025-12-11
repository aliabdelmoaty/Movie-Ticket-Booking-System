package gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import core.BookingSystem;

public class AddMovieDialog extends JDialog {
    private JTextField titleField;
    private JTextField genreField;
    private JTextField durationField;
    private JTextField ratingField;
    private JTextArea descriptionArea;
    private JTextField posterPathField;
    private JButton browseButton;
    private boolean movieAdded = false;
    
    public AddMovieDialog(Frame parent) {
        super(parent, "Add New Movie", true);
        setSize(600, 700);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(16, 22, 34));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Title label
        JLabel titleLabel = new JLabel("Add New Movie");
        titleLabel.setFont(new Font("Spline Sans", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Fill in the movie details below");
        subtitleLabel.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(150, 155, 170));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        
        // Movie Title
        mainPanel.add(createLabel("Movie Title *"));
        titleField = createTextField();
        mainPanel.add(titleField);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Genre
        mainPanel.add(createLabel("Genre *"));
        genreField = createTextField();
        mainPanel.add(genreField);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Duration
        mainPanel.add(createLabel("Duration (e.g., 2h 30m) *"));
        durationField = createTextField();
        mainPanel.add(durationField);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Rating
        mainPanel.add(createLabel("Rating (0.0 - 10.0) *"));
        ratingField = createTextField();
        mainPanel.add(ratingField);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Description
        mainPanel.add(createLabel("Description"));
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        descriptionArea.setForeground(Color.WHITE);
        descriptionArea.setBackground(new Color(28, 31, 39));
        descriptionArea.setCaretColor(Color.WHITE);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(59, 67, 84), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(500, 100));
        scrollPane.setMaximumSize(new Dimension(500, 100));
        scrollPane.setBorder(null);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Poster Path
        mainPanel.add(createLabel("Poster Image (Optional)"));
        JPanel posterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        posterPanel.setBackground(new Color(16, 22, 34));
        posterPanel.setMaximumSize(new Dimension(500, 40));
        posterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        posterPathField = createTextField();
        posterPathField.setEditable(false);
        posterPathField.setPreferredSize(new Dimension(370, 40));
        
        browseButton = new JButton("Browse");
        browseButton.setFont(new Font("Spline Sans", Font.BOLD, 14));
        browseButton.setBackground(new Color(59, 67, 84));
        browseButton.setForeground(Color.WHITE);
        browseButton.setFocusPainted(false);
        browseButton.setBorderPainted(false);
        browseButton.setPreferredSize(new Dimension(100, 40));
        browseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        browseButton.addActionListener(e -> browsePosterImage());
        
        posterPanel.add(posterPathField);
        posterPanel.add(Box.createHorizontalStrut(10));
        posterPanel.add(browseButton);
        mainPanel.add(posterPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBackground(new Color(16, 22, 34));
        buttonsPanel.setMaximumSize(new Dimension(500, 50));
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton addButton = new JButton("Add Movie");
        addButton.setFont(new Font("Spline Sans", Font.BOLD, 16));
        addButton.setBackground(new Color(34, 197, 94));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setPreferredSize(new Dimension(150, 45));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> addMovie());
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Spline Sans", Font.BOLD, 16));
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setPreferredSize(new Dimension(100, 45));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dispose());
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(cancelButton);
        mainPanel.add(buttonsPanel);
        
        // Add main panel to dialog
        add(new JScrollPane(mainPanel));
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Spline Sans", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(28, 31, 39));
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(59, 67, 84), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(500, 40));
        field.setMaximumSize(new Dimension(500, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }
    
    private void browsePosterImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Poster Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));
        
        // Set initial directory to assets/posters
        File postersDir = new File("assets/posters");
        if (postersDir.exists() && postersDir.isDirectory()) {
            fileChooser.setCurrentDirectory(postersDir);
        }
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            posterPathField.setText(selectedFile.getAbsolutePath());
        }
    }
    
    private void addMovie() {
        // Validate inputs
        String title = titleField.getText().trim();
        String genre = genreField.getText().trim();
        String duration = durationField.getText().trim();
        String rating = ratingField.getText().trim();
        String description = descriptionArea.getText().trim();
        String posterPath = posterPathField.getText().trim();
        
        if (title.isEmpty() || genre.isEmpty() || duration.isEmpty() || rating.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields (*)",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate rating
        try {
            double ratingValue = Double.parseDouble(rating);
            if (ratingValue < 0 || ratingValue > 10) {
                JOptionPane.showMessageDialog(this,
                    "Rating must be between 0.0 and 10.0",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Rating must be a valid number",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Add movie to database
        BookingSystem system = BookingSystem.getInstance();
        boolean added = system.addMovie(title, genre, duration, rating, 
            description.isEmpty() ? "No description available." : description,
            posterPath.isEmpty() ? null : posterPath);
        
        if (added) {
            JOptionPane.showMessageDialog(this,
                "Movie added successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            movieAdded = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to add movie. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isMovieAdded() {
        return movieAdded;
    }
}

