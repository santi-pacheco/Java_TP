package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entity.ModerationStatus;
import entity.ReviewComment;
import util.DataSourceProvider;
import exception.ErrorFactory;

public class CommentRepository {

    // Select base con join de usuarios
    private static final String BASE_SELECT = 
        "SELECT c.comment_id, c.review_id, c.user_id, c.comment_text, c.created_at, " +
        "c.moderation_status, c.moderation_reason, u.username, u.profile_image " +
        "FROM review_comments c " +
        "INNER JOIN users u ON c.user_id = u.user_id ";

    public ReviewComment add(ReviewComment comment) {
        String sql = "INSERT INTO review_comments (review_id, user_id, comment_text, moderation_status, moderation_reason) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            stmt.setInt(1, comment.getReviewId());
            stmt.setInt(2, comment.getUserId());
            stmt.setString(3, comment.getCommentText());
            stmt.setString(4, comment.getModerationStatus() != null ? comment.getModerationStatus().getValue() : "PENDING_MODERATION");
            stmt.setString(5, comment.getModerationReason());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        comment.setCommentId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error al guardar el comentario en la base de datos.");
        }
        return comment;
    }

    public ReviewComment findById(int commentId) {
        String sql = BASE_SELECT + "WHERE c.comment_id = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComment(rs);
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error al buscar el comentario por ID.");
        }
        return null;
    }

    public List<ReviewComment> findByReviewId(int reviewId, int loggedUserId) {
        List<ReviewComment> comments = new ArrayList<>();
        String sql = BASE_SELECT + 
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
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs));
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error al obtener los comentarios de la reseña.");
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
            throw ErrorFactory.internal("Error al actualizar el estado de moderación del comentario.");
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
            throw ErrorFactory.internal("Error al actualizar el texto y el estado del comentario.");
        }
    }

    public void delete(int commentId) {
        String sql = "DELETE FROM review_comments WHERE comment_id = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, commentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error al eliminar el comentario.");
        }
    }

    // ÚNICO PUNTO DE MAPEO
    private ReviewComment mapResultSetToComment(ResultSet rs) throws SQLException {
        ReviewComment comment = new ReviewComment();
        comment.setCommentId(rs.getInt("comment_id"));
        comment.setReviewId(rs.getInt("review_id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setCommentText(rs.getString("comment_text"));
        comment.setCreatedAt(rs.getTimestamp("created_at"));
        
        String statusStr = rs.getString("moderation_status");
        if (statusStr != null) {
            try {
                comment.setModerationStatus(ModerationStatus.fromString(statusStr));
            } catch (IllegalArgumentException e) {
                // Fallback seguro en caso de que haya basura en la DB
                comment.setModerationStatus(ModerationStatus.PENDING_MODERATION);
            }
        }
        
        comment.setModerationReason(rs.getString("moderation_reason"));
        comment.setUsername(rs.getString("username"));
        comment.setProfileImage(rs.getString("profile_image"));
        
        return comment;
    }
}