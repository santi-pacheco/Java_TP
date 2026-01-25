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
    @Size(min = 10, max = 1000, message = "La reseña debe tener entre 10 y 1000 caracteres")//EN un futuro puede haber mas caracteres
    private String review_text;
    
    @NotNull(message = "El rating es requerido")
    @DecimalMin(value = "0.0", message = "El rating mínimo es 0.0")
    @DecimalMax(value = "5.0", message = "El rating máximo es 5.0")
    private Double rating;
    
    @NotNull(message = "La fecha en que viste la película es requerida")
    @PastOrPresent(message = "La fecha de visualización no puede ser futura")
    private LocalDate watched_on; // Fecha en que el usuario vio la película
    
    private LocalDate created_at; // Fecha en que se creó la reseña (automática)
    
    private ModerationStatus moderationStatus;
    private String moderationReason;
    
    private String username; // Para mostrar el nombre del usuario
    private String movieTitle; // Para mostrar el título de la película
    // Constructors
    public Review() {
        this.watched_on = LocalDate.now(); // Por defecto: hoy
        this.created_at = LocalDate.now(); // Fecha de creación automática
        this.moderationStatus = ModerationStatus.PENDING_MODERATION; // Estado por defecto
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
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_movie() {
        return id_movie;
    }

    public void setId_movie(int id_movie) {
        this.id_movie = id_movie;
    }

    public String getReview_text() {
        return review_text;
    }

    public void setReview_text(String review_text) {
        this.review_text = review_text;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public LocalDate getWatched_on() {
        return watched_on;
    }

    public void setWatched_on(LocalDate watched_on) {
        this.watched_on = watched_on;
    }

    public LocalDate getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDate created_at) {
        this.created_at = created_at;
    }
    
    public ModerationStatus getModerationStatus() {
		return moderationStatus;
	}
    
    public void setModerationStatus(ModerationStatus moderationStatus) {
    	this.moderationStatus = moderationStatus;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
    
    public String getModerationReason() {
        return moderationReason;
    }

    public void setModerationReason(String moderationReason) {
        this.moderationReason = moderationReason;
    }
    
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
