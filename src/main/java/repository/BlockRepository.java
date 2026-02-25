package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.User;
import exception.ErrorFactory;
import util.DataSourceProvider;

public class BlockRepository {

    public BlockRepository() {
    }

    public void addBlock(int blockerId, int blockedId) {
        String sql = "INSERT INTO bloqueos (id_blocker, id_blocked) VALUES (?, ?)";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, blockerId);
            stmt.setInt(2, blockedId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            if (e.getErrorCode() != 1062) {
                throw ErrorFactory.internal("Error al bloquear al usuario en la base de datos.");
            }
        }
    }

    public void removeBlock(int blockerId, int blockedId) {
        String sql = "DELETE FROM bloqueos WHERE id_blocker = ? AND id_blocked = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, blockerId);
            stmt.setInt(2, blockedId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error al desbloquear al usuario en la base de datos.");
        }
    }

    public boolean isBlocking(int blockerId, int blockedId) {
        String sql = "SELECT 1 FROM bloqueos WHERE id_blocker = ? AND id_blocked = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, blockerId);
            stmt.setInt(2, blockedId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error al verificar estado de bloqueo.");
        }
    }

    public List<User> getBlockedUsers(int blockerId) {
        List<User> blockedUsers = new ArrayList<>();
        String sql = "SELECT u.id_user, u.username, u.profile_image FROM usuarios u " +
                     "INNER JOIN bloqueos b ON u.id_user = b.id_blocked " +
                     "WHERE b.id_blocker = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, blockerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id_user"));
                    user.setUsername(rs.getString("username"));
                    user.setProfileImage(rs.getString("profile_image"));
                    blockedUsers.add(user);
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error obteniendo la lista de usuarios bloqueados.");
        }
        return blockedUsers;
    }
}