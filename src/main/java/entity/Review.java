package entity;

import java.time.LocalDate;
import jakarta.validation.constraints.*;

public class Review {
    private int id;
    
    @NotNull(message = "El ID del usuario es requerido")
    @Positive(message = "El ID del usuario debe ser positivo")
    private int id_user;
    
    @NotNull(message = "El ID de la película es requerido")
    @Positive(message = "El ID de la película debe ser positivo")
    private int id_movie;
    
    @NotBlank(message = "El texto de la reseña es requerido")
    @Size(min = 10, max = 1000, message = "La reseña debe tener entre 10 y 1000 caracteres")
    private String review_text;
    
    @NotNull(message = "El rating es requerido")
    @DecimalMin(value = "0.0", message = "El rating mínimo es 0.0")
    @DecimalMax(value = "5.0", message = "El rating máximo es 5.0")
    private Double rating;
    
    @NotNull(message = "La fecha en que viste la película es requerida")
    @PastOrPresent(message = "La fecha de visualización no puede ser futura")
    private LocalDate watched_on; 
    
    private LocalDate created_at; 
    
    private ModerationStatus moderationStatus;
    private String moderationReason;
    
    private String username; 
    private String movieTitle; 
    private String profileImage; 
    
    private int likesCount; 
    private int commentsCount; 
    
    // Constructors
    public Review() {
        this.watched_on = LocalDate.now(); 
        this.created_at = LocalDate.now(); 
        this.moderationStatus = ModerationStatus.PENDING_MODERATION; 
    }
    
    public Review(int id_user, int id_movie, String review_text, Double rating, LocalDate watched_on) {
        this.id_user = id_user;
        this.id_movie = id_movie;
        this.review_text = review_text;
        this.rating = rating;
        this.watched_on = watched_on != null ? watched_on : LocalDate.now();
        this.created_at = LocalDate.now();
        this.moderationStatus = ModerationStatus.PENDING_MODERATION;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getId_user() { return id_user; }
    public void setId_user(int id_user) { this.id_user = id_user; }

    public int getId_movie() { return id_movie; }
    public void setId_movie(int id_movie) { this.id_movie = id_movie; }

    public String getReview_text() { return review_text; }
    public void setReview_text(String review_text) { this.review_text = review_text; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public LocalDate getWatched_on() { return watched_on; }
    public void setWatched_on(LocalDate watched_on) { this.watched_on = watched_on; }

    public LocalDate getCreated_at() { return created_at; }
    public void setCreated_at(LocalDate created_at) { this.created_at = created_at; }
    
    public ModerationStatus getModerationStatus() { return moderationStatus; }
    public void setModerationStatus(ModerationStatus moderationStatus) { this.moderationStatus = moderationStatus; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    
    public String getModerationReason() { return moderationReason; }
    public void setModerationReason(String moderationReason) { this.moderationReason = moderationReason; }
    
    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    
    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", id_user=" + id_user +
                ", id_movie=" + id_movie +
                ", review_text='" + review_text + '\'' +
                ", rating=" + rating +
                ", watched_on=" + watched_on +
                ", created_at=" + created_at +
                ", moderationStatus=" + moderationStatus +
                '}';
    }
}