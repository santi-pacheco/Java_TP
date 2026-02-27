package entity;

import java.time.LocalDateTime;

public class Notification {
    private String tipo; // "LIKE", "COMMENT", "FOLLOW"
    private int actorId; 
    private Integer movieId;
    private String actorUsername;
    private String actorProfileImage;
    
    // Datos opcionales según el tipo
    private Integer reviewId; 
    private String movieTitle;
    private String commentText; 
    
    private int extraCount; 
    
    private LocalDateTime fecha;
    private boolean unread;

    public Notification() {}

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getActorId() { return actorId; }
    public void setActorId(int actorId) { this.actorId = actorId; }

    public String getActorUsername() { return actorUsername; }
    public void setActorUsername(String actorUsername) { this.actorUsername = actorUsername; }

    public String getActorProfileImage() { return actorProfileImage; }
    public void setActorProfileImage(String actorProfileImage) { this.actorProfileImage = actorProfileImage; }

    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public int getExtraCount() { return extraCount; }
    public void setExtraCount(int extraCount) { this.extraCount = extraCount; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public boolean isUnread() { return unread; }
    public void setUnread(boolean unread) { this.unread = unread; }

	public Integer getMovieId() {
		return movieId;
	}

	public void setMovieId(Integer movieId) {
		this.movieId = movieId;
	}

	
}