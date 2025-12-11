package factory;

import model.Movie;

/**
 * Factory Pattern: MovieFactory
 * Creates different types of movies based on genre
 * Handles specific configuration for each movie type
 */
public class MovieFactory {
    
    public enum MovieType {
        ACTION, COMEDY, DRAMA, HORROR, SCIFI, ROMANCE, THRILLER
    }
    
    public static Movie createMovie(MovieType type, String title, String duration, String rating, String description, String posterPath) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDuration(duration);
        movie.setRating(rating);
        movie.setDescription(description);
        movie.setPosterPath(posterPath);
        
        // Set genre-specific properties
        switch (type) {
            case ACTION:
                movie.setGenre("Action");
                if (description == null || description.isEmpty()) {
                    movie.setDescription("An action-packed thriller with intense sequences and stunts.");
                }
                break;
                
            case COMEDY:
                movie.setGenre("Comedy");
                if (description == null || description.isEmpty()) {
                    movie.setDescription("A hilarious comedy that will make you laugh out loud.");
                }
                break;
                
            case DRAMA:
                movie.setGenre("Drama");
                if (description == null || description.isEmpty()) {
                    movie.setDescription("A compelling drama with emotional depth and character development.");
                }
                break;
                
            case HORROR:
                movie.setGenre("Horror");
                if (description == null || description.isEmpty()) {
                    movie.setDescription("A terrifying horror experience that will keep you on the edge of your seat.");
                }
                break;
                
            case SCIFI:
                movie.setGenre("Sci-Fi");
                if (description == null || description.isEmpty()) {
                    movie.setDescription("A futuristic science fiction adventure exploring new worlds.");
                }
                break;
                
            case ROMANCE:
                movie.setGenre("Romance");
                if (description == null || description.isEmpty()) {
                    movie.setDescription("A heartwarming romantic story about love and relationships.");
                }
                break;
                
            case THRILLER:
                movie.setGenre("Thriller");
                if (description == null || description.isEmpty()) {
                    movie.setDescription("A suspenseful thriller with unexpected twists and turns.");
                }
                break;
                
            default:
                movie.setGenre("General");
                break;
        }
        
        return movie;
    }
    
    public static MovieType getMovieType(String genre) {
        if (genre == null) return MovieType.DRAMA;
        
        switch (genre.toUpperCase()) {
            case "ACTION":
                return MovieType.ACTION;
            case "COMEDY":
                return MovieType.COMEDY;
            case "DRAMA":
                return MovieType.DRAMA;
            case "HORROR":
                return MovieType.HORROR;
            case "SCI-FI":
            case "SCIFI":
                return MovieType.SCIFI;
            case "ROMANCE":
                return MovieType.ROMANCE;
            case "THRILLER":
                return MovieType.THRILLER;
            default:
                return MovieType.DRAMA;
        }
    }
}
