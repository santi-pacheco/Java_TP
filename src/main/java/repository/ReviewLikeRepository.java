package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DataSourceProvider;
import exception.ErrorFactory;

public class ReviewLikeRepository {


    public boolean toggleLike(int userId, int reviewId) {
        if (existsLike(userId, reviewId)) {
            removeLike(userId, reviewId);
            updateLikesCount(reviewId, -1);
            return false; // Like removido
        } else {
            addLike(userId, reviewId);
            updateLikesCount(reviewId, 1);
            return true; // Like agregado
        }
    }

    public boolean existsLike(int userId, int reviewId) {
        String sql = "SELECT 1 FROM review_likes WHERE user_id = ? AND review_id = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, reviewId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error al verificar la existencia del like.");
        }
    }

    private void addLike(int userId, int reviewId) {
        String sql = "INSERT INTO review_likes (user_id, review_id) VALUES (?, ?)";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, reviewId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                //lo ignoramos porque el like ya existe
                return;
            }
            throw ErrorFactory.internal("Error al agregar el like en la base de datos.");
        }
    }

    private void removeLike(int userId, int reviewId) {
        String sql = "DELETE FROM review_likes WHERE user_id = ? AND review_id = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, reviewId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error al remover el like en la base de datos.");
        }
    }

    private void updateLikesCount(int reviewId, int delta) {
        String sql = "UPDATE reviews SET likes_count = likes_count + ? WHERE review_id = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, delta);
            stmt.setInt(2, reviewId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error al actualizar el contador de likes de la reseña.");
        }
    }

    public int getLikesCount(int reviewId) {
        String sql = "SELECT likes_count FROM reviews WHERE review_id = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reviewId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("likes_count");
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error al obtener la cantidad total de likes.");
        }
        return 0;
    }
}