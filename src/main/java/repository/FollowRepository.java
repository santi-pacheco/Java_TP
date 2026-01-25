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
        String sql = "INSERT INTO seguidores (id_seguidor, id_seguido) VALUES (?, ?)";

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
        String sql = "DELETE FROM seguidores WHERE id_seguidor = ? AND id_seguido = ?";

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
        String sql = "SELECT COUNT(*) FROM seguidores WHERE id_seguidor = ? AND id_seguido = ?";

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
        String sql = "SELECT u.id_user, u.username, u.email " +
                     "FROM usuarios u " +
                     "INNER JOIN seguidores s ON u.id_user = s.id_seguidor " +
                     "WHERE s.id_seguido = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id_user"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    // user.setProfilePath(rs.getString("profile_path"));
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
        String sql = "SELECT u.id_user, u.username, u.email " +
                     "FROM usuarios u " +
                     "INNER JOIN seguidores s ON u.id_user = s.id_seguido " +
                     "WHERE s.id_seguidor = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id_user"));
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