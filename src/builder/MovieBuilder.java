package builder;

import model.Movie;

/**
 * Builder Pattern: MovieBuilder
 * Provides a flexible way to construct Movie objects step by step
 * Useful for creating movies with many optional parameters
 */
public class MovieBuilder {
    private String title;
    private String genre;
    private String duration;
    private String rating;
    private String description;
    private String posterPath;
    
    public MovieBuilder() {
        // Set default values
        this.genre = "General";
        this.duration = "120 min";
        this.rating = "PG-13";
        this.description = "";
        this.posterPath = "";
    }
    
    public MovieBuilder setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public MovieBuilder setGenre(String genre) {
        this.genre = genre;
        return this;
    }
    
    public MovieBuilder setDuration(String duration) {
        this.duration = duration;
        return this;
    }
    
    public MovieBuilder setRating(String rating) {
        this.rating = rating;
        return this;
    }
    
    public MovieBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public MovieBuilder setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }
    
    // Convenience method for common rating types
    public MovieBuilder setFamilyFriendly() {
        this.rating = "G";
        return this;
    }
    
    public MovieBuilder setTeenRating() {
        this.rating = "PG-13";
        return this;
    }
    
    public MovieBuilder setMatureRating() {
        this.rating = "R";
        return this;
    }
    
    // Build the final Movie object
    public Movie build() {
        if (title == null || title.isEmpty()) {
            throw new IllegalStateException("Title is required");
        }
        
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setDuration(duration);
        movie.setRating(rating);
        movie.setDescription(description);
        movie.setPosterPath(posterPath);
        
        return movie;
    }
    
    // Static method to create a builder
    public static MovieBuilder newMovie() {
        return new MovieBuilder();
    }
}
