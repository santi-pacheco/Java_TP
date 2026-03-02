package entity;

import java.sql.Timestamp;

public class ReviewLike {
    private int userId;
    private int reviewId;
    private Timestamp createdAt;

    public ReviewLike() {}

    public ReviewLike(int userId, int reviewId) {
        this.userId = userId;
        this.reviewId = reviewId;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
