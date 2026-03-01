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
            "SELECT 'LIKE' as tipo, " +
            "  (SELECT rl2.user_id FROM review_likes rl2 WHERE rl2.review_id = rl.review_id ORDER BY rl2.created_at DESC LIMIT 1) as actor_id, " +
            "  (SELECT u2.username FROM users u2 JOIN review_likes rl2 ON u2.user_id = rl2.user_id WHERE rl2.review_id = rl.review_id ORDER BY rl2.created_at DESC LIMIT 1) as actor_username, " +
            "  (SELECT u2.profile_image FROM users u2 JOIN review_likes rl2 ON u2.user_id = rl2.user_id WHERE rl2.review_id = rl.review_id ORDER BY rl2.created_at DESC LIMIT 1) as actor_profile_image, " +
            "  (SELECT u2.user_level FROM users u2 JOIN review_likes rl2 ON u2.user_id = rl2.user_id WHERE rl2.review_id = rl.review_id ORDER BY rl2.created_at DESC LIMIT 1) as user_level, " +
            "  rl.review_id as review_id, " +
            "  m.title as movie_title, " +
            "  m.movie_id as movie_id, " +
            "  NULL as comment_text, " +
            "  MAX(rl.created_at) as fecha, " +
            "  (COUNT(*) - 1) as extra_count " +
            "FROM review_likes rl " +
            "JOIN reviews r ON rl.review_id = r.review_id " +
            "JOIN movies m ON r.movie_id = m.movie_id " +
            "WHERE r.user_id = ? AND rl.user_id != ? " +
            "GROUP BY rl.review_id, m.title, m.movie_id " +

            "UNION ALL " +

            "SELECT 'COMMENT' as tipo, " +
            "  c.user_id as actor_id, " +
            "  u.username as actor_username, " +
            "  u.profile_image as actor_profile_image, " +
            "  u.user_level as user_level, " +
            "  c.review_id as review_id, " +
            "  m.title as movie_title, " +
            "  m.movie_id as movie_id, " +
            "  c.comment_text as comment_text, " +
            "  c.created_at as fecha, " +
            "  0 as extra_count " +
            "FROM review_comments c " +
            "JOIN users u ON c.user_id = u.user_id " +
            "JOIN reviews r ON c.review_id = r.review_id " +
            "JOIN movies m ON r.movie_id = m.movie_id " +
            "WHERE r.user_id = ? AND c.user_id != ? AND (c.moderation_status IS NULL OR c.moderation_status != 'REJECTED') " +

            "UNION ALL " +

            "SELECT 'FOLLOW' as tipo, " +
            "  f.follower_id as actor_id, " +
            "  u.username as actor_username, " +
            "  u.profile_image as actor_profile_image, " +
            "  u.user_level as user_level, " +
            "  NULL as review_id, " +
            "  NULL as movie_title, " +
            "  NULL as movie_id, " +
            "  NULL as comment_text, " +
            "  f.followed_at as fecha, " +
            "  0 as extra_count " +
            "FROM followers f " +
            "JOIN users u ON f.follower_id = u.user_id " +
            "WHERE f.followed_id = ? " +

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
                    notif.setType(rs.getString("tipo"));
                    notif.setActorId(rs.getInt("actor_id"));
                    notif.setActorUsername(rs.getString("actor_username"));
                    notif.setActorProfileImage(rs.getString("actor_profile_image"));

                    notif.setReviewId(rs.getObject("review_id", Integer.class));
                    notif.setMovieId(rs.getObject("movie_id", Integer.class));
                    notif.setMovieTitle(rs.getString("movie_title"));
                    notif.setCommentText(rs.getString("comment_text"));
                    notif.setExtraCount(rs.getInt("extra_count"));

                    notif.setUserLevel(rs.getInt("user_level"));

                    java.sql.Timestamp fechaTs = rs.getTimestamp("fecha");
                    if (fechaTs != null) {
                        LocalDateTime notifDate = fechaTs.toLocalDateTime();
                        notif.setCreatedAt(notifDate);

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
