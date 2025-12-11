import core.BookingSystem;
import java.io.File;

public class ImportMovies {
    public static void main(String[] args) {
        System.out.println("=== Importing Movies from Posters Folder ===\n");
        
        BookingSystem system = BookingSystem.getInstance();
        File postersDir = new File("assets/posters");
        
        System.out.println("Posters directory exists: " + postersDir.exists());
        System.out.println("Is directory: " + postersDir.isDirectory());
        
        if (postersDir.exists() && postersDir.isDirectory()) {
            File[] posterFiles = postersDir.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".jpeg") || 
                name.toLowerCase().endsWith(".jpg") || 
                name.toLowerCase().endsWith(".png")
            );
            
            if (posterFiles != null) {
                System.out.println("Found " + posterFiles.length + " poster files\n");
                
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
                    
                    System.out.println("Adding: " + title + " (" + genre + ", " + ratingStr + "/10)");
                    boolean added = system.addMovie(title, genre, duration, ratingStr, description, posterPath);
                    
                    if (added) {
                        System.out.println("  ✅ Added successfully");
                    } else {
                        System.out.println("  ⚠️ Failed or already exists");
                    }
                }
                
                System.out.println("\n=== Import Complete ===");
                System.out.println("Total movies in database: " + system.getAllMovies().size());
            }
        }
    }
}

