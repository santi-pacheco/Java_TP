package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DataSourceProvider;
import exception.ErrorFactory;
import java.util.ArrayList;
import java.util.List;
import entity.User;

public class FollowRepository {

    public void addFollow(int followerId, int followedId) {
        String sql = "INSERT INTO followers (follower_id, followed_id) VALUES (?, ?)";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, followerId);
            stmt.setInt(2, followedId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw ErrorFactory.duplicate("Ya sigues a este usuario.");
            } else {
                throw ErrorFactory.internal("Error al guardar el seguimiento en la base de datos");
            }
        }
    }

    public void removeFollow(int followerId, int followedId) {
        String sql = "DELETE FROM followers WHERE follower_id = ? AND followed_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, followerId);
            stmt.setInt(2, followedId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw ErrorFactory.internal("Error al eliminar el seguimiento");
        }
    }

    public boolean isFollowing(int followerId, int followedId) {
        String sql = "SELECT COUNT(*) FROM followers WHERE follower_id = ? AND followed_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, followerId);
            stmt.setInt(2, followedId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error al verificar el estado del seguimiento");
        }
        return false;
    }
    
    public List<User> findFollowers(int userId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.email " +
                     "FROM users u " +
                     "INNER JOIN followers s ON u.user_id = s.follower_id " +
                     "WHERE s.followed_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error al obtener seguidores");
        }
        return users;
    }

    public List<User> findFollowing(int userId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.email " +
                     "FROM users u " +
                     "INNER JOIN followers s ON u.user_id = s.followed_id " +
                     "WHERE s.follower_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error al obtener seguidos");
        }
        return users;
    }
}