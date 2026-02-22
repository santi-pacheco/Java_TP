package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DataSourceProvider;
import exception.ErrorFactory;

public class ReviewLikeRepository {

    /**
     * Toggle like: Returns true if like was ADDED, false if REMOVED
     */
    public boolean toggleLike(int userId, int reviewId) {
        if (existsLike(userId, reviewId)) {
            removeLike(userId, reviewId);
            updateLikesCount(reviewId, -1);
            return false; // Like removed
        } else {
            addLike(userId, reviewId);
            updateLikesCount(reviewId, 1);
            return true; // Like added
        }
    }

    public boolean existsLike(int userId, int reviewId) {
        String sql = "SELECT 1 FROM reviews_likes WHERE id_usuario = ? AND id_review = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, reviewId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error checking like existence");
        }
    }

    private void addLike(int userId, int reviewId) {
        String sql = "INSERT INTO reviews_likes (id_usuario, id_review) VALUES (?, ?)";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, reviewId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                // Duplicate entry - ignore, like already exists
                return;
            }
            throw ErrorFactory.internal("Error adding like");
        }
    }

    private void removeLike(int userId, int reviewId) {
        String sql = "DELETE FROM reviews_likes WHERE id_usuario = ? AND id_review = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, reviewId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error removing like");
        }
    }

    private void updateLikesCount(int reviewId, int delta) {
        String sql = "UPDATE reviews SET likes_count = likes_count + ? WHERE id_review = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, delta);
            stmt.setInt(2, reviewId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating likes count");
        }
    }

    public int getLikesCount(int reviewId) {
        String sql = "SELECT likes_count FROM reviews WHERE id_review = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reviewId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("likes_count");
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching likes count");
        }
        return 0;
    }
}
