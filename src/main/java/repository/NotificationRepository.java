package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import entity.Notification;
import util.DataSourceProvider;
import exception.ErrorFactory;

public class NotificationRepository {

    public NotificationRepository() {}

    public List<Notification> getNotificationsForUser(int targetUserId, LocalDateTime ultimaRevision) {
        List<Notification> notifications = new ArrayList<>();
        
        String sql = 
            // 1. LIKES (Agrupados por reseña para el efecto "y X más")
            "SELECT 'LIKE' as tipo, " +
            "  (SELECT rl2.id_usuario FROM reviews_likes rl2 WHERE rl2.id_review = rl.id_review ORDER BY rl2.created_at DESC LIMIT 1) as actor_id, " +
            "  (SELECT u2.username FROM usuarios u2 JOIN reviews_likes rl2 ON u2.id_user = rl2.id_usuario WHERE rl2.id_review = rl.id_review ORDER BY rl2.created_at DESC LIMIT 1) as actor_username, " +
            "  (SELECT u2.profile_image FROM usuarios u2 JOIN reviews_likes rl2 ON u2.id_user = rl2.id_usuario WHERE rl2.id_review = rl.id_review ORDER BY rl2.created_at DESC LIMIT 1) as actor_profile_image, " +
            "  rl.id_review as review_id, " +
            "  p.name as movie_title, " +
            "  p.id_pelicula as movie_id, " +
            "  NULL as comment_text, " +
            "  MAX(rl.created_at) as fecha, " +
            "  (COUNT(*) - 1) as extra_count " +
            "FROM reviews_likes rl " +
            "JOIN reviews r ON rl.id_review = r.id_review " +
            "JOIN peliculas p ON r.id_movie = p.id_pelicula " +
            "WHERE r.id_user = ? AND rl.id_usuario != ? " + 
            // 🚨 SOLUCIÓN: Agregamos p.id_pelicula al GROUP BY 🚨
            "GROUP BY rl.id_review, p.name, p.id_pelicula " +
            
            "UNION ALL " +
            
            // 2. COMENTARIOS (Individuales)
            "SELECT 'COMMENT' as tipo, " +
            "  c.id_usuario as actor_id, " +
            "  u.username as actor_username, " +
            "  u.profile_image as actor_profile_image, " +
            "  c.id_review as review_id, " +
            "  p.name as movie_title, " +
            "  p.id_pelicula as movie_id, " +
            "  c.comment_text as comment_text, " +
            "  c.created_at as fecha, " +
            "  0 as extra_count " +
            "FROM reviews_comments c " +
            "JOIN usuarios u ON c.id_usuario = u.id_user " +
            "JOIN reviews r ON c.id_review = r.id_review " +
            "JOIN peliculas p ON r.id_movie = p.id_pelicula " +
            "WHERE r.id_user = ? AND c.id_usuario != ? AND (c.moderation_status IS NULL OR c.moderation_status != 'REJECTED') " +
            
            "UNION ALL " +
            
            // 3. SEGUIDORES
            "SELECT 'FOLLOW' as tipo, " +
            "  s.id_seguidor as actor_id, " +
            "  u.username as actor_username, " +
            "  u.profile_image as actor_profile_image, " +
            "  NULL as review_id, " +
            "  NULL as movie_title, " +
            "  NULL as movie_id, " +
            "  NULL as comment_text, " +
            "  s.fecha_seguimiento as fecha, " +
            "  0 as extra_count " +
            "FROM seguidores s " +
            "JOIN usuarios u ON s.id_seguidor = u.id_user " +
            "WHERE s.id_seguido = ? " +
            
            // ORDENAMOS TODO POR FECHA
            "ORDER BY fecha DESC LIMIT 30";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, targetUserId); 
            stmt.setInt(2, targetUserId); 
            stmt.setInt(3, targetUserId); 
            stmt.setInt(4, targetUserId); 
            stmt.setInt(5, targetUserId); 
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Notification notif = new Notification();
                    notif.setTipo(rs.getString("tipo"));
                    notif.setActorId(rs.getInt("actor_id"));
                    notif.setActorUsername(rs.getString("actor_username"));
                    notif.setActorProfileImage(rs.getString("actor_profile_image"));
                    
                    // Asegúrate de que Notification.java tenga este campo
                    notif.setReviewId(rs.getObject("review_id", Integer.class));
                    notif.setMovieId(rs.getObject("movie_id", Integer.class)); 
                    notif.setMovieTitle(rs.getString("movie_title"));
                    notif.setCommentText(rs.getString("comment_text"));
                    notif.setExtraCount(rs.getInt("extra_count"));
                    
                    java.sql.Timestamp fechaTs = rs.getTimestamp("fecha");
                    if (fechaTs != null) {
                        LocalDateTime notifDate = fechaTs.toLocalDateTime();
                        notif.setFecha(notifDate);
                        
                        if (ultimaRevision == null) {
                            notif.setUnread(true);
                        } else {
                            notif.setUnread(notifDate.isAfter(ultimaRevision));
                        }
                    }
                    
                    notifications.add(notif);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error ejecutando UNION de notificaciones: " + e.getMessage());
            throw ErrorFactory.internal("Error fetching notifications");
        }
        
        return notifications;
    }
}