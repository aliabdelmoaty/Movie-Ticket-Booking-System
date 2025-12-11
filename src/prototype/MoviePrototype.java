package prototype;

import model.Movie;

/**
 * Prototype Pattern: MoviePrototype
 * Allows cloning of Movie objects for creating similar movies
 * Useful for creating movie series or similar showings
 */
public class MoviePrototype implements Cloneable {
    private Movie movie;
    
    public MoviePrototype(Movie movie) {
        this.movie = movie;
    }
    
    public Movie getMovie() {
        return movie;
    }
    
    public void setMovie(Movie movie) {
        this.movie = movie;
    }
    
    @Override
    public MoviePrototype clone() {
        try {
            MoviePrototype cloned = (MoviePrototype) super.clone();
            // Deep copy of the movie object
            Movie clonedMovie = new Movie();
            clonedMovie.setTitle(this.movie.getTitle());
            clonedMovie.setGenre(this.movie.getGenre());
            clonedMovie.setDuration(this.movie.getDuration());
            clonedMovie.setRating(this.movie.getRating());
            clonedMovie.setDescription(this.movie.getDescription());
            clonedMovie.setPosterPath(this.movie.getPosterPath());
            
            cloned.setMovie(clonedMovie);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    // Clone with modifications
    public MoviePrototype cloneWithTitle(String newTitle) {
        MoviePrototype cloned = this.clone();
        cloned.getMovie().setTitle(newTitle);
        return cloned;
    }
    
    public MoviePrototype cloneWithGenre(String newGenre) {
        MoviePrototype cloned = this.clone();
        cloned.getMovie().setGenre(newGenre);
        return cloned;
    }
    
    public MoviePrototype cloneWithPoster(String newPosterPath) {
        MoviePrototype cloned = this.clone();
        cloned.getMovie().setPosterPath(newPosterPath);
        return cloned;
    }
    
    // Clone for creating sequels
    public MoviePrototype cloneAsSequel(String sequelNumber) {
        MoviePrototype cloned = this.clone();
        String originalTitle = this.movie.getTitle();
        cloned.getMovie().setTitle(originalTitle + " " + sequelNumber);
        return cloned;
    }
    
    // Clone for different showtime/theater
    public MoviePrototype cloneForDifferentShowing() {
        return this.clone();
    }
}
