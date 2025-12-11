package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookingFrame extends JFrame {
    private JTextField searchField;
    private List<Movie> movies;
    
    public BookingFrame() {
        setTitle("Movie Booking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Load movies from posters folder
        loadMovies();
        
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(16, 22, 34));
        add(mainPanel);
        
        // Search bar panel at the top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        searchPanel.setBackground(new Color(16, 22, 34));
        
        searchField = new JTextField(30);
        searchField.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        searchField.setForeground(Color.WHITE);
        searchField.setBackground(new Color(28, 31, 39));
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(59, 67, 84), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setPreferredSize(new Dimension(400, 40));
        
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Spline Sans", Font.BOLD, 14));
        searchBtn.setBackground(new Color(19, 91, 236));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setPreferredSize(new Dimension(100, 40));
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> {
            String search = searchField.getText();
            JOptionPane.showMessageDialog(this, "Searching for: " + search);
        });
        
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Movies grid panel with scroll - 4 columns per row
        int columns = 4;
        int rows = (int) Math.ceil((double) movies.size() / columns);
        
        JPanel moviesContainer = new JPanel(new GridLayout(rows, columns, 20, 20));
        moviesContainer.setBackground(new Color(16, 22, 34));
        moviesContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add movie cards from loaded movies
        for (Movie movie : movies) {
            JPanel movieCard = createMovieCard(movie);
            moviesContainer.add(movieCard);
        }
        
        // Fill remaining cells with empty panels if needed
        int remainingCells = (rows * columns) - movies.size();
        for (int i = 0; i < remainingCells; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(new Color(16, 22, 34));
            moviesContainer.add(emptyPanel);
        }
        
        JScrollPane scrollPane = new JScrollPane(moviesContainer);
        scrollPane.setBackground(new Color(16, 22, 34));
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadMovies() {
        movies = new ArrayList<>();
        File postersDir = new File("assets/posters");
        
        if (postersDir.exists() && postersDir.isDirectory()) {
            File[] posterFiles = postersDir.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".jpeg") || 
                name.toLowerCase().endsWith(".jpg") || 
                name.toLowerCase().endsWith(".png")
            );
            
            if (posterFiles != null) {
                String[] genres = {"Action", "Drama", "Sci-Fi", "Thriller", "Comedy", "Adventure", "Crime", "Mystery", "Romance"};
                
                for (int i = 0; i < posterFiles.length; i++) {
                    String title = posterFiles[i].getName().replace(".jpeg", "").replace(".jpg", "").replace(".png", "");
                    title = title.substring(0, 1).toUpperCase() + title.substring(1); // Capitalize first letter
                    
                    String genre = genres[i % genres.length];
                    int hours = 2 + (i % 2);
                    int minutes = (i % 3) * 15;
                    String duration = hours + "h " + minutes + "m";
                    
                    double rating = 7.5 + (i % 15) * 0.1;
                    String ratingStr = String.format("%.1f", rating);
                    
                    movies.add(new Movie(title, genre + " • " + duration, ratingStr, posterFiles[i].getPath()));
                }
            }
        }
        
        // If no posters found, add some default movies
        if (movies.isEmpty()) {
            movies.add(new Movie("Movie 1", "Action • 2h 15m", "8.5", null));
            movies.add(new Movie("Movie 2", "Drama • 2h 30m", "8.2", null));
            movies.add(new Movie("Movie 3", "Sci-Fi • 2h 45m", "8.8", null));
            movies.add(new Movie("Movie 4", "Thriller • 2h 0m", "7.9", null));
        }
    }
    
    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(200, 350));
        card.setBackground(new Color(28, 31, 39));
        card.setBorder(BorderFactory.createLineBorder(new Color(59, 67, 84), 1, true));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Movie poster
        JPanel posterPanel = new JPanel();
        posterPanel.setPreferredSize(new Dimension(200, 280));
        posterPanel.setBackground(new Color(40, 45, 55));
        posterPanel.setLayout(new BorderLayout());
        
        if (movie.getPosterPath() != null) {
            try {
                ImageIcon icon = new ImageIcon(movie.getPosterPath());
                Image image = icon.getImage().getScaledInstance(200, 280, Image.SCALE_SMOOTH);
                JLabel posterLabel = new JLabel(new ImageIcon(image));
                posterPanel.add(posterLabel, BorderLayout.CENTER);
            } catch (Exception e) {
                // If image loading fails, show placeholder
                addPosterPlaceholder(posterPanel);
            }
        } else {
            addPosterPlaceholder(posterPanel);
        }
        
        card.add(posterPanel, BorderLayout.CENTER);
        
        // Movie info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(28, 31, 39));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        // Rating
        JLabel ratingLabel = new JLabel("⭐ " + movie.getRating());
        ratingLabel.setFont(new Font("Spline Sans", Font.BOLD, 12));
        ratingLabel.setForeground(new Color(255, 193, 7));
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Title
        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("Spline Sans", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Genre
        JLabel genreLabel = new JLabel(movie.getGenre());
        genreLabel.setFont(new Font("Spline Sans", Font.PLAIN, 11));
        genreLabel.setForeground(new Color(150, 155, 170));
        genreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(ratingLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(genreLabel);
        
        card.add(infoPanel, BorderLayout.SOUTH);
        
        // Hover effect and click event
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(35, 38, 46));
                infoPanel.setBackground(new Color(35, 38, 46));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(28, 31, 39));
                infoPanel.setBackground(new Color(28, 31, 39));
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Open movie details screen
                MovieDetailsFrame detailsFrame = new MovieDetailsFrame(movie);
                detailsFrame.setVisible(true);
            }
        });
        
        return card;
    }
    
    private void addPosterPlaceholder(JPanel posterPanel) {
        JLabel placeholderLabel = new JLabel("🎬");
        placeholderLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        posterPanel.add(placeholderLabel, BorderLayout.CENTER);
    }
    
    // Inner class for Movie
    class Movie {
        private String title;
        private String genre;
        private String rating;
        private String posterPath;
        
        public Movie(String title, String genre, String rating, String posterPath) {
            this.title = title;
            this.genre = genre;
            this.rating = rating;
            this.posterPath = posterPath;
        }
        
        public String getTitle() { return title; }
        public String getGenre() { return genre; }
        public String getRating() { return rating; }
        public String getPosterPath() { return posterPath; }
    }
}
