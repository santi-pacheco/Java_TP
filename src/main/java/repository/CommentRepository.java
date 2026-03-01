package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.ModerationStatus;
import entity.ReviewComment;
import util.DataSourceProvider;
import exception.ErrorFactory;

public class CommentRepository {

    public ReviewComment add(ReviewComment comment) {
        String sql = "INSERT INTO review_comments (review_id, user_id, comment_text, moderation_status, moderation_reason) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, comment.getReviewId());
            stmt.setInt(2, comment.getUserId());
            stmt.setString(3, comment.getCommentText());
            stmt.setString(4, comment.getModerationStatus() != null ? comment.getModerationStatus().getValue() : "PENDING_MODERATION");
            stmt.setString(5, comment.getModerationReason());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) comment.setCommentId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error adding comment");
        }
        return comment;
    }

    public ReviewComment findById(int commentId) {
        String sql = "SELECT c.*, u.username, u.profile_image FROM review_comments c INNER JOIN users u ON c.user_id = u.user_id WHERE c.comment_id = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToComment(rs);
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching comment");
        }
        return null;
    }

    public List<ReviewComment> findByReviewId(int reviewId, int loggedUserId) {
        List<ReviewComment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.username, u.profile_image FROM review_comments c " +
                     "INNER JOIN users u ON c.user_id = u.user_id " +
                     "WHERE c.review_id = ? AND c.moderation_status IN ('APPROVED', 'SPOILER') " +
                     "AND c.user_id NOT IN (SELECT blocked_id FROM user_blocks WHERE blocker_id = ?) " +
                     "AND c.user_id NOT IN (SELECT blocker_id FROM user_blocks WHERE blocked_id = ?) " +
                     "ORDER BY c.created_at ASC";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reviewId);
            stmt.setInt(2, loggedUserId);
            stmt.setInt(3, loggedUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) comments.add(mapResultSetToComment(rs));
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching comments for review");
        }
        return comments;
    }

    public void updateModerationStatus(int commentId, String status, String reason) {
        String sql = "UPDATE review_comments SET moderation_status = ?, moderation_reason = ? WHERE comment_id = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, reason);
            stmt.setInt(3, commentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating comment moderation status");
        }
    }

    public void updateTextAndStatus(int commentId, String newText, String status, String reason) {
        String sql = "UPDATE review_comments SET comment_text = ?, moderation_status = ?, moderation_reason = ? WHERE comment_id = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newText);
            stmt.setString(2, status);
            stmt.setString(3, reason);
            stmt.setInt(4, commentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating comment text");
        }
    }

    public void delete(int commentId) {
        String sql = "DELETE FROM review_comments WHERE comment_id = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error deleting comment");
        }
    }

    private ReviewComment mapResultSetToComment(ResultSet rs) throws SQLException {
        ReviewComment comment = new ReviewComment();
        comment.setCommentId(rs.getInt("comment_id"));
        comment.setReviewId(rs.getInt("review_id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setCommentText(rs.getString("comment_text"));
        comment.setCreatedAt(rs.getTimestamp("created_at"));
        String statusStr = rs.getString("moderation_status");
        try { if (statusStr != null) comment.setModerationStatus(ModerationStatus.fromString(statusStr)); } catch (Exception e) {}
        comment.setModerationReason(rs.getString("moderation_reason"));
        comment.setUsername(rs.getString("username"));
        try {
            comment.setProfileImage(rs.getString("profile_image"));
        } catch (Exception e) {}
        return comment;
    }
}
