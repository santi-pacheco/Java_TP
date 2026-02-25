package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.User;
import util.DataSourceProvider;
import exception.ErrorFactory;

public class UserRepository {
    
    public UserRepository() {
    }

    public List<User> findAll() {
        List<User> Users = new ArrayList<>();
        String sql = "SELECT id_user, password, username, role, email, birthdate, esUsuarioActivo, profile_image, banned_until FROM usuarios ORDER BY id_user";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id_user"));
                user.setPassword(rs.getString("password"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setEmail(rs.getString("email"));
                user.setBirthDate(rs.getDate("birthdate"));
                user.setEsUsuarioActivo(rs.getBoolean("esUsuarioActivo"));
                user.setProfileImage(rs.getString("profile_image"));
                user.setBannedUntil(rs.getTimestamp("banned_until")); 
                Users.add(user);
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching users from database");
        }
        
        return Users;
    }

    public User findOne(int id) {
        User user = null;
        String sql = "SELECT id_user, password, username, role, email, birthdate, esUsuarioActivo, profile_image, banned_until FROM usuarios WHERE id_user = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id_user"));
                    user.setPassword(rs.getString("password"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(rs.getString("role"));
                    user.setEmail(rs.getString("email"));
                    user.setBirthDate(rs.getDate("birthdate"));
                    user.setEsUsuarioActivo(rs.getBoolean("esUsuarioActivo"));
                    user.setProfileImage(rs.getString("profile_image"));
                    user.setBannedUntil(rs.getTimestamp("banned_until")); // AGREGADO
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching user by ID");
        }
        return user;
    }

    public User add(User u) {
        String sql = "INSERT INTO usuarios (password, username, role, email, birthdate, profile_image) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, u.getPassword());
            stmt.setString(2, u.getUsername());
            stmt.setString(3, u.getRole());
            stmt.setString(4, u.getEmail());
            stmt.setDate(5, u.getBirthDate());
            stmt.setString(6, u.getProfileImage());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet keyResultSet = stmt.getGeneratedKeys()) {
                    if (keyResultSet.next()) {
                        u.setId(keyResultSet.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                throw ErrorFactory.duplicate("Username or email already exists");
            } else {
                throw ErrorFactory.internal("Error adding user to database");
            }
        }
        
        return u;
    }

    public User update(User u) {
        String sql = "UPDATE usuarios SET username = ?, password = ?, role = ?, email = ?, birthdate = ?, esUsuarioActivo = ?, profile_image = ? WHERE id_user = ?";
        
        try (Connection connection = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, u.getUsername());
            stmt.setString(2, u.getPassword());
            stmt.setString(3, u.getRole());
            stmt.setString(4, u.getEmail());
            stmt.setDate(5, u.getBirthDate());
            stmt.setBoolean(6, u.isEsUsuarioActivo());
            stmt.setString(7, u.getProfileImage());
            stmt.setInt(8, u.getId());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw ErrorFactory.duplicate("Username or email already exists");
            } else {
                throw ErrorFactory.internal("Error updating user in database");
            }
        }
        return u;
    }

    public void updateProfileImage(int userId, String fileName) {
        String sql = "UPDATE usuarios SET profile_image = ? WHERE id_user = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, fileName);
            stmt.setInt(2, userId);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating profile image");
        }
    }
    
    public User delete(User u) {
        String sql = "DELETE FROM usuarios WHERE id_user = ?";
        
        try ( Connection connection = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, u.getId());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error deleting user from database");
        }
        return u;
    }

    public User findByUsername(String username) {
        User user = null;
        String sql = "SELECT id_user, password, username, role, email, birthdate, esUsuarioActivo, profile_image, banned_until FROM usuarios WHERE username = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id_user"));
                    user.setPassword(rs.getString("password"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(rs.getString("role"));
                    user.setEmail(rs.getString("email"));
                    user.setBirthDate(rs.getDate("birthdate"));
                    user.setEsUsuarioActivo(rs.getBoolean("esUsuarioActivo"));
                    user.setProfileImage(rs.getString("profile_image"));
                    user.setBannedUntil(rs.getTimestamp("banned_until")); 
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching user by username from database");
        }
        
        return user;
    }
    
	public void updateActiveStatus(int userId, boolean isActive) {
		String sql = "UPDATE usuarios SET esUsuarioActivo = ? WHERE id_user = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setBoolean(1, isActive);
			stmt.setInt(2, userId);
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error updating user active status");
		}
	}

	public void banUser(int userId, int daysToban) {
	    String sql = "UPDATE usuarios SET banned_until = DATE_ADD(NOW(), INTERVAL ? DAY) WHERE id_user = ?";
	    
	    try (Connection conn = DataSourceProvider.getDataSource().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, daysToban);
	        stmt.setInt(2, userId);
	        stmt.executeUpdate();
	        
	    } catch (SQLException e) {
	        throw ErrorFactory.internal("Error banning user");
	    }
	}

	public java.sql.Timestamp getBannedUntil(int userId) {
	    String sql = "SELECT banned_until FROM usuarios WHERE id_user = ?";
	    
	    try (Connection conn = DataSourceProvider.getDataSource().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, userId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getTimestamp("banned_until");
	            }
	        }
	    } catch (SQLException e) {
	        throw ErrorFactory.internal("Error checking user ban status");
	    }
	    return null;
	}

	public boolean isUserBanned(int userId) {
	    java.sql.Timestamp bannedUntil = getBannedUntil(userId);
	    if (bannedUntil == null) return false;
	    return bannedUntil.after(new java.sql.Timestamp(System.currentTimeMillis()));
	}

	public List<User> searchUsersByUsername(String query, int loggedUserId) {
	    List<User> users = new ArrayList<>();
	    String sql = "SELECT id_user, username, profile_image FROM usuarios " +
	                 "WHERE username LIKE ? AND id_user != ? " +
	                 "AND id_user NOT IN (SELECT id_blocked FROM bloqueos WHERE id_blocker = ?) " +
	                 "AND id_user NOT IN (SELECT id_blocker FROM bloqueos WHERE id_blocked = ?) " +
	                 "LIMIT 10";
	    try (Connection conn = DataSourceProvider.getDataSource().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, "%" + query + "%");
	        stmt.setInt(2, loggedUserId);
	        stmt.setInt(3, loggedUserId);
	        stmt.setInt(4, loggedUserId);
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                User user = new User();
	                user.setId(rs.getInt("id_user"));
	                user.setUsername(rs.getString("username"));
	                user.setProfileImage(rs.getString("profile_image"));
	                users.add(user);
	            }
	        }
	    } catch (SQLException e) {
	        throw ErrorFactory.internal("Error buscando usuarios por nombre");
	    }
	    return users;
	}
	
	public User findByEmail(String email) {
        User user = null;
        String sql = "SELECT id_user, password, username, role, email, birthdate, esUsuarioActivo, profile_image, banned_until FROM usuarios WHERE email = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id_user"));
                    user.setPassword(rs.getString("password"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(rs.getString("role"));
                    user.setEmail(rs.getString("email"));
                    user.setBirthDate(rs.getDate("birthdate"));
                    user.setEsUsuarioActivo(rs.getBoolean("esUsuarioActivo"));
                    user.setProfileImage(rs.getString("profile_image"));
                    user.setBannedUntil(rs.getTimestamp("banned_until")); 
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error buscando usuario por email");
        }
        return user;
    }
	
	public void savePasswordResetToken(int userId, String token) {
        String deleteOldSql = "DELETE FROM password_resets WHERE id_user = ?";
        String insertSql = "INSERT INTO password_resets (id_user, token, expiry_date) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 15 MINUTE))";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection()) {
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteOldSql)) {
                deleteStmt.setInt(1, userId);
                deleteStmt.executeUpdate();
            }
            
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, userId);
                insertStmt.setString(2, token);
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error guardando el token de recuperación");
        }
    }
	
	public Integer getUserIdByValidToken(String token) {
        String sql = "SELECT id_user FROM password_resets WHERE token = ? AND expiry_date > NOW()";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_user");
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error validando el token");
        }
        return null;
    }

	public void updatePasswordAndClearToken(int userId, String hashedPassword, String token) {
        String updateSql = "UPDATE usuarios SET password = ? WHERE id_user = ?";
        String deleteTokenSql = "DELETE FROM password_resets WHERE token = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteTokenSql)) {
                
                updateStmt.setString(1, hashedPassword);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();
                
                deleteStmt.setString(1, token);
                deleteStmt.executeUpdate();
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error actualizando la contraseña");
        }
    }
	
}