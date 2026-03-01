package entity;

import java.time.LocalDate;
import jakarta.validation.constraints.*;

public class Review {
    private int reviewId;

    @NotNull(message = "El ID del usuario es requerido")
    @Positive(message = "El ID del usuario debe ser positivo")
    private int userId;

    @NotNull(message = "El ID de la película es requerido")
    @Positive(message = "El ID de la película debe ser positivo")
    private int movieId;

    @NotBlank(message = "El texto de la reseña es requerido")
    @Size(min = 10, max = 1000, message = "La reseña debe tener entre 10 y 1000 caracteres")
    private String reviewText;

    @NotNull(message = "El rating es requerido")
    @DecimalMin(value = "0.0", message = "El rating mínimo es 0.0")
    @DecimalMax(value = "5.0", message = "El rating máximo es 5.0")
    private Double rating;

    @NotNull(message = "La fecha en que viste la película es requerida")
    @PastOrPresent(message = "La fecha de visualización no puede ser futura")
    private LocalDate watchedOn;

    private LocalDate createdAt;

    private ModerationStatus moderationStatus;
    private String moderationReason;

    private String username;
    private String movieTitle;
    private String profileImage;

    private int likesCount;
    private int commentsCount;

    // Constructors
    public Review() {
        this.watchedOn = LocalDate.now();
        this.createdAt = LocalDate.now();
        this.moderationStatus = ModerationStatus.PENDING_MODERATION;
    }

    public Review(int userId, int movieId, String reviewText, Double rating, LocalDate watchedOn) {
        this.userId = userId;
        this.movieId = movieId;
        this.reviewText = reviewText;
        this.rating = rating;
        this.watchedOn = watchedOn != null ? watchedOn : LocalDate.now();
        this.createdAt = LocalDate.now();
        this.moderationStatus = ModerationStatus.PENDING_MODERATION;
    }

    // Getters and Setters
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public LocalDate getWatchedOn() { return watchedOn; }
    public void setWatchedOn(LocalDate watchedOn) { this.watchedOn = watchedOn; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

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
                "reviewId=" + reviewId +
                ", userId=" + userId +
                ", movieId=" + movieId +
                ", reviewText='" + reviewText + '\'' +
                ", rating=" + rating +
                ", watchedOn=" + watchedOn +
                ", createdAt=" + createdAt +
                ", moderationStatus=" + moderationStatus +
                '}';
    }
}
