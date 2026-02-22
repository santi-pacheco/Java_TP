package entity;

import java.sql.Timestamp;

public class ReviewComment {
    private int idComment;
    private int idReview;
    private int idUsuario;
    private String commentText;
    private Timestamp createdAt;
    private ModerationStatus moderationStatus;
    private String moderationReason;
    private String username;

    public ReviewComment() {
        this.moderationStatus = ModerationStatus.PENDING_MODERATION;
    }

    public int getIdComment() { return idComment; }
    public void setIdComment(int idComment) { this.idComment = idComment; }

    public int getIdReview() { return idReview; }
    public void setIdReview(int idReview) { this.idReview = idReview; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    // Alias para compatibilidad con JSON
    public int getUserId() { return idUsuario; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public ModerationStatus getModerationStatus() { return moderationStatus; }
    public void setModerationStatus(ModerationStatus moderationStatus) { this.moderationStatus = moderationStatus; }

    public String getModerationReason() { return moderationReason; }
    public void setModerationReason(String moderationReason) { this.moderationReason = moderationReason; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
