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
        String sql = "INSERT INTO reviews_comments (id_review, id_usuario, comment_text, moderation_status, moderation_reason) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, comment.getIdReview());
            stmt.setInt(2, comment.getIdUsuario());
            stmt.setString(3, comment.getCommentText());
            stmt.setString(4, comment.getModerationStatus() != null ? comment.getModerationStatus().getValue() : "PENDING_MODERATION");
            stmt.setString(5, comment.getModerationReason());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) comment.setIdComment(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error adding comment");
        }
        return comment;
    }

    public ReviewComment findById(int commentId) {
        // MODIFICADO: Se agregó u.profile_image al SELECT
        String sql = "SELECT c.*, u.username, u.profile_image FROM reviews_comments c INNER JOIN usuarios u ON c.id_usuario = u.id_user WHERE c.id_comment = ?";
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

    public List<ReviewComment> findByReviewId(int reviewId) {
        List<ReviewComment> comments = new ArrayList<>();
        // MODIFICADO: Se agregó u.profile_image al SELECT
        String sql = "SELECT c.*, u.username, u.profile_image FROM reviews_comments c INNER JOIN usuarios u ON c.id_usuario = u.id_user WHERE c.id_review = ? AND c.moderation_status IN ('APPROVED', 'SPOILER') ORDER BY c.created_at ASC"; 
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reviewId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) comments.add(mapResultSetToComment(rs));
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching comments for review");
        }
        return comments;
    }

    public void updateModerationStatus(int commentId, String status, String reason) {
        String sql = "UPDATE reviews_comments SET moderation_status = ?, moderation_reason = ? WHERE id_comment = ?";
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
        String sql = "UPDATE reviews_comments SET comment_text = ?, moderation_status = ?, moderation_reason = ? WHERE id_comment = ?";
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
        String sql = "DELETE FROM reviews_comments WHERE id_comment = ?";
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
        comment.setIdComment(rs.getInt("id_comment"));
        comment.setIdReview(rs.getInt("id_review"));
        comment.setIdUsuario(rs.getInt("id_usuario"));
        comment.setCommentText(rs.getString("comment_text"));
        comment.setCreatedAt(rs.getTimestamp("created_at"));
        String statusStr = rs.getString("moderation_status");
        try { if (statusStr != null) comment.setModerationStatus(ModerationStatus.fromString(statusStr)); } catch (Exception e) {}
        comment.setModerationReason(rs.getString("moderation_reason"));
        
        // Traemos los datos cruzados del usuario
        comment.setUsername(rs.getString("username"));
        
        try {
            comment.setProfileImage(rs.getString("profile_image"));
        } catch (Exception e) {
            
        }
        
        return comment;
    }
}