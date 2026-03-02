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
        String sql = "INSERT INTO user_blocks (blocker_id, blocked_id) VALUES (?, ?)";
        
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
        String sql = "DELETE FROM user_blocks WHERE blocker_id = ? AND blocked_id = ?";
        
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
        String sql = "SELECT 1 FROM user_blocks WHERE blocker_id = ? AND blocked_id = ?";
        
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
        String sql = "SELECT u.user_id, u.username, u.profile_image FROM users u " +
                     "INNER JOIN user_blocks b ON u.user_id = b.blocked_id " +
                     "WHERE b.blocker_id = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, blockerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
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