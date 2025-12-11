package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import core.BookingSystem;

public class BookingFrame extends JFrame {
    private JTextField searchField;
    private List<model.Movie> movies;
    
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
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        searchPanel.setBackground(new Color(16, 22, 34));
        
        // Search field with clear button
        JPanel searchFieldPanel = new JPanel();
        searchFieldPanel.setLayout(new BorderLayout());
        searchFieldPanel.setBackground(new Color(28, 31, 39));
        searchFieldPanel.setBorder(BorderFactory.createLineBorder(new Color(59, 67, 84), 1, true));
        searchFieldPanel.setPreferredSize(new Dimension(400, 40));
        
        searchField = new JTextField(30);
        searchField.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        searchField.setForeground(Color.WHITE);
        searchField.setBackground(new Color(28, 31, 39));
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 8));
        
        // Clear button (X)
        JButton clearBtn = new JButton("✕");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 16));
        clearBtn.setForeground(new Color(150, 155, 170));
        clearBtn.setBackground(new Color(28, 31, 39));
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setContentAreaFilled(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.setPreferredSize(new Dimension(30, 30));
        clearBtn.setVisible(false);
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            clearBtn.setVisible(false);
            loadMovies();
            refreshMoviesDisplay();
        });
        
        // Show/hide clear button and handle Enter key
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                clearBtn.setVisible(!searchField.getText().isEmpty());
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    String search = searchField.getText().trim();
                    if (!search.isEmpty()) {
                        searchMovies(search);
                    } else {
                        loadMovies();
                        refreshMoviesDisplay();
                    }
                }
            }
        });
        
        searchFieldPanel.add(searchField, BorderLayout.CENTER);
        searchFieldPanel.add(clearBtn, BorderLayout.EAST);
        
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Spline Sans", Font.BOLD, 14));
        searchBtn.setBackground(new Color(19, 91, 236));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setPreferredSize(new Dimension(100, 40));
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> {
            String search = searchField.getText().trim();
            if (!search.isEmpty()) {
                searchMovies(search);
            } else {
                loadMovies();
                refreshMoviesDisplay();
            }
        });
        
        // Add Movie button
        JButton addMovieBtn = new JButton("+ Add Movie");
        addMovieBtn.setFont(new Font("Spline Sans", Font.BOLD, 14));
        addMovieBtn.setBackground(new Color(34, 197, 94));
        addMovieBtn.setForeground(Color.WHITE);
        addMovieBtn.setFocusPainted(false);
        addMovieBtn.setBorderPainted(false);
        addMovieBtn.setPreferredSize(new Dimension(130, 40));
        addMovieBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMovieBtn.addActionListener(e -> openAddMovieDialog());
        
        searchPanel.add(searchFieldPanel);
        searchPanel.add(searchBtn);
        searchPanel.add(addMovieBtn);
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Movies grid panel with scroll - 4 columns per row
        int columns = 4;
        int rows = (int) Math.ceil((double) movies.size() / columns);
        
        JPanel moviesContainer = new JPanel(new GridLayout(rows, columns, 20, 20));
        moviesContainer.setBackground(new Color(16, 22, 34));
        moviesContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add movie cards from loaded movies
        for (model.Movie movie : movies) {
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
        // First, import movies from posters folder to database if not already imported
        importMoviesFromPostersFolder();
        
        // Load all movies from database
        BookingSystem system = BookingSystem.getInstance();
        movies = system.getAllMovies();
        
        // If still empty, add default movies to database
        if (movies.isEmpty()) {
            system.addMovie("The Shawshank Redemption", "Drama", "2h 22m", "9.3", 
                "Two imprisoned men bond over a number of years.", null);
            system.addMovie("The Godfather", "Crime, Drama", "2h 55m", "9.2", 
                "The aging patriarch of an organized crime dynasty transfers control.", null);
            system.addMovie("The Dark Knight", "Action, Crime, Drama", "2h 32m", "9.0", 
                "When the menace known as the Joker wreaks havoc on Gotham.", null);
            
            movies = system.getAllMovies();
        }
    }
    
    private void importMoviesFromPostersFolder() {
        BookingSystem system = BookingSystem.getInstance();
        File postersDir = new File("assets/posters");
        
        if (postersDir.exists() && postersDir.isDirectory()) {
            File[] posterFiles = postersDir.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".jpeg") || 
                name.toLowerCase().endsWith(".jpg") || 
                name.toLowerCase().endsWith(".png")
            );
            
            if (posterFiles != null) {
                String[] genres = {"Action", "Drama", "Sci-Fi", "Thriller", "Comedy", 
                                 "Adventure", "Crime", "Mystery", "Romance"};
                String[] descriptions = {
                    "An epic tale of adventure and excitement.",
                    "A powerful story that will move you.",
                    "Experience the future like never before.",
                    "A heart-pounding thriller that keeps you on edge.",
                    "Laugh out loud with this hilarious comedy.",
                    "Join the adventure of a lifetime.",
                    "A gripping crime drama with unexpected twists.",
                    "Unravel the mystery in this suspenseful tale.",
                    "A beautiful love story that will touch your heart."
                };
                
                for (int i = 0; i < posterFiles.length; i++) {
                    String title = posterFiles[i].getName()
                        .replace(".jpeg", "").replace(".jpg", "").replace(".png", "");
                    title = title.substring(0, 1).toUpperCase() + title.substring(1);
                    
                    String genre = genres[i % genres.length];
                    int hours = 2 + (i % 2);
                    int minutes = (i % 3) * 15;
                    String duration = hours + "h " + minutes + "m";
                    
                    double rating = 7.5 + (i % 15) * 0.1;
                    String ratingStr = String.format("%.1f", rating);
                    
                    String description = descriptions[i % descriptions.length];
                    String posterPath = posterFiles[i].getPath();
                    
                    // Check if movie with same title already exists
                    List<model.Movie> existingMovies = system.searchMovies(title);
                    if (existingMovies.isEmpty()) {
                        system.addMovie(title, genre, duration, ratingStr, description, posterPath);
                    }
                }
            }
        }
    }
    
    private void handleSearch() {
        String search = searchField.getText().trim();
        if (!search.isEmpty()) {
            searchMovies(search);
        } else {
            loadMovies();
            refreshMoviesDisplay();
        }
    }
    
    private void searchMovies(String searchTerm) {
        BookingSystem system = BookingSystem.getInstance();
        movies = system.searchMovies(searchTerm);
        refreshMoviesDisplay();
        
        if (movies.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No movies found for: " + searchTerm,
                "Search Results",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void openAddMovieDialog() {
        AddMovieDialog dialog = new AddMovieDialog(this);
        dialog.setVisible(true);
        
        // Refresh movies list after dialog closes
        if (dialog.isMovieAdded()) {
            loadMovies();
            refreshMoviesDisplay();
        }
    }
    
    private void refreshMoviesDisplay() {
        // Remove old components
        getContentPane().removeAll();
        
        // Recreate the main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(16, 22, 34));
        add(mainPanel);
        
        // Search bar panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        searchPanel.setBackground(new Color(16, 22, 34));
        
        // Search field with clear button
        JPanel searchFieldPanel = new JPanel();
        searchFieldPanel.setLayout(new BorderLayout());
        searchFieldPanel.setBackground(new Color(28, 31, 39));
        searchFieldPanel.setBorder(BorderFactory.createLineBorder(new Color(59, 67, 84), 1, true));
        searchFieldPanel.setPreferredSize(new Dimension(400, 40));
        
        String currentText = searchField != null ? searchField.getText() : "";
        searchField = new JTextField(30);
        searchField.setText(currentText);
        searchField.setFont(new Font("Spline Sans", Font.PLAIN, 14));
        searchField.setForeground(Color.WHITE);
        searchField.setBackground(new Color(28, 31, 39));
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 8));
        
        // Clear button (X)
        JButton clearBtn = new JButton("✕");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 16));
        clearBtn.setForeground(new Color(150, 155, 170));
        clearBtn.setBackground(new Color(28, 31, 39));
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setContentAreaFilled(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.setPreferredSize(new Dimension(30, 30));
        clearBtn.setVisible(!currentText.isEmpty());
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            clearBtn.setVisible(false);
            loadMovies();
            refreshMoviesDisplay();
        });
        
        // Show/hide clear button and handle Enter key
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                clearBtn.setVisible(!searchField.getText().isEmpty());
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    String search = searchField.getText().trim();
                    if (!search.isEmpty()) {
                        searchMovies(search);
                    } else {
                        loadMovies();
                        refreshMoviesDisplay();
                    }
                }
            }
        });
        
        searchFieldPanel.add(searchField, BorderLayout.CENTER);
        searchFieldPanel.add(clearBtn, BorderLayout.EAST);
        
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Spline Sans", Font.BOLD, 14));
        searchBtn.setBackground(new Color(19, 91, 236));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setPreferredSize(new Dimension(100, 40));
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> {
            String search = searchField.getText().trim();
            if (!search.isEmpty()) {
                searchMovies(search);
            } else {
                loadMovies();
                refreshMoviesDisplay();
            }
        });
        
        // Add Movie button
        JButton addMovieBtn = new JButton("+ Add Movie");
        addMovieBtn.setFont(new Font("Spline Sans", Font.BOLD, 14));
        addMovieBtn.setBackground(new Color(34, 197, 94));
        addMovieBtn.setForeground(Color.WHITE);
        addMovieBtn.setFocusPainted(false);
        addMovieBtn.setBorderPainted(false);
        addMovieBtn.setPreferredSize(new Dimension(130, 40));
        addMovieBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMovieBtn.addActionListener(e -> openAddMovieDialog());
        
        searchPanel.add(searchFieldPanel);
        searchPanel.add(searchBtn);
        searchPanel.add(addMovieBtn);
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Movies grid
        int columns = 4;
        int rows = (int) Math.ceil((double) movies.size() / columns);
        
        JPanel moviesContainer = new JPanel(new GridLayout(rows, columns, 20, 20));
        moviesContainer.setBackground(new Color(16, 22, 34));
        moviesContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        for (model.Movie movie : movies) {
            JPanel movieCard = createMovieCard(movie);
            moviesContainer.add(movieCard);
        }
        
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
        
        // Refresh display
        revalidate();
        repaint();
    }
    
    private JPanel createMovieCard(model.Movie movie) {
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
        
        // Genre and Duration
        String genreInfo = movie.getGenre() + " • " + movie.getDuration();
        JLabel genreLabel = new JLabel(genreInfo);
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
                // Convert model.Movie to BookingFrame.Movie for MovieDetailsFrame
                Movie guiMovie = new Movie(
                    movie.getTitle(), 
                    movie.getGenre() + " • " + movie.getDuration(), 
                    movie.getRating(), 
                    movie.getPosterPath()
                );
                MovieDetailsFrame detailsFrame = new MovieDetailsFrame(guiMovie, movie.getId());
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
