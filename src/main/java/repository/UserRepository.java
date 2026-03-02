package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import entity.User;
import util.DataSourceProvider;
import exception.ErrorFactory;

public class UserRepository {

    // Centralizamos el SELECT y el mapeo para no repetir columnas
    private static final String BASE_SELECT = "SELECT user_id, password, username, role, email, birth_date, " +
                                              "total_kcals, user_level, notified_level, main_dish_movie_id, " +
                                              "profile_image, banned_until, last_notification_check FROM users";

    public UserRepository() {
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = BASE_SELECT + " ORDER BY user_id";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching users from database");
        }
        return users;
    }

    public User findOne(int id) {
        User user = null;
        String sql = BASE_SELECT + " WHERE user_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching user by ID");
        }
        return user;
    }

    public User findByUsername(String username) {
        User user = null;
        String sql = BASE_SELECT + " WHERE username = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching user by username");
        }
        return user;
    }

    public User findByEmail(String email) {
        User user = null;
        String sql = BASE_SELECT + " WHERE email = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching user by email");
        }
        return user;
    }

    public List<User> searchUsersByUsername(String query, int loggedUserId) {
        List<User> users = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE username LIKE ? AND user_id != ? " +
                     "AND user_id NOT IN (SELECT blocked_id FROM user_blocks WHERE blocker_id = ?) " +
                     "AND user_id NOT IN (SELECT blocker_id FROM user_blocks WHERE blocked_id = ?) " +
                     "LIMIT 10";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, "%" + query + "%");
            stmt.setInt(2, loggedUserId);
            stmt.setInt(3, loggedUserId);
            stmt.setInt(4, loggedUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error searching users by username");
        }
        return users;
    }

    public User add(User u) {
        String sql = "INSERT INTO users (password, username, role, email, birth_date, profile_image) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
                        u.setUserId(keyResultSet.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw ErrorFactory.duplicate("El nombre de usuario o email ya están en uso.");
            }
            throw ErrorFactory.internal("Error adding user to database");
        }
        return u;
    }

    public User update(User u) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, email = ?, birth_date = ?, " +
                     "total_kcals = ?, user_level = ?, notified_level = ?, main_dish_movie_id = ?, profile_image = ? " +
                     "WHERE user_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getUsername());
            stmt.setString(2, u.getPassword());
            stmt.setString(3, u.getRole());
            stmt.setString(4, u.getEmail());
            stmt.setDate(5, u.getBirthDate());
            stmt.setInt(6, u.getTotalKcals());
            stmt.setInt(7, u.getUserLevel());
            stmt.setInt(8, u.getNotifiedLevel());

            if (u.getMainDishMovieId() != null) {
                stmt.setInt(9, u.getMainDishMovieId());
            } else {
                stmt.setNull(9, java.sql.Types.INTEGER);
            }

            stmt.setString(10, u.getProfileImage());
            stmt.setInt(11, u.getUserId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw ErrorFactory.duplicate("El nombre de usuario o email ya están en uso.");
            }
            throw ErrorFactory.internal("Error updating user in database");
        }
        return u;
    }

    public User delete(User u) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, u.getUserId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw ErrorFactory.internal("Error deleting user from database");
        }
        return u;
    }

    public void updateProfileImage(int userId, String fileName) {
        String sql = "UPDATE users SET profile_image = ? WHERE user_id = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fileName);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating profile image");
        }
    }

    public void updateUserVolume(int userId, int totalKcals, int userLevel) {
        String sql = "UPDATE users SET total_kcals = ?, user_level = ? WHERE user_id = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, totalKcals);
            stmt.setInt(2, userLevel);
            stmt.setInt(3, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating user volume");
        }
    }

    public void banUser(int userId, int daysToban) {
        String sql = "UPDATE users SET banned_until = DATE_ADD(NOW(), INTERVAL ? DAY) WHERE user_id = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt =prepareStatement(sql)) {

            stmt.setInt(1, daysToban);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error banning user");
        }
    }

    public java.sql.Timestamp getBannedUntil(int userId) {
        String sql = "SELECT banned_until FROM users WHERE user_id = ?";
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

    public void savePasswordResetToken(int userId, String token) {
        String deleteOldSql = "DELETE FROM password_resets WHERE user_id = ?";
        String insertSql = "INSERT INTO password_resets (user_id, token, expires_at) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 15 MINUTE))";

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
            throw ErrorFactory.internal("Error saving password reset token");
        }
    }

    public Integer getUserIdByValidToken(String token) {
        String sql = "SELECT user_id FROM password_resets WHERE token = ? AND expires_at > NOW()";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error validating reset token");
        }
        return null;
    }

    public void updatePasswordAndClearToken(int userId, String hashedPassword, String token) {
        String updateSql = "UPDATE users SET password = ? WHERE user_id = ?";
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
            throw ErrorFactory.internal("Error updating password via token");
        }
    }

    public void updateNotificacionesLeidas(int userId) {
        String sql = "UPDATE users SET last_notification_check = NOW() WHERE user_id = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating notification check time");
        }
    }

    public void markLevelAsNotified(int userId, int level) {
        String sql = "UPDATE users SET notified_level = ? WHERE user_id = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, level);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating notified level");
        }
    }

    public void updatePlatoPrincipal(int userId, Integer movieId) {
        String sql = "UPDATE users SET main_dish_movie_id = ? WHERE user_id = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (movieId != null) {
                stmt.setInt(1, movieId);
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating main dish movie");
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setPassword(rs.getString("password"));
        user.setUsername(rs.getString("username"));
        user.setRole(rs.getString("role"));
        user.setEmail(rs.getString("email"));
        user.setBirthDate(rs.getDate("birth_date"));
        user.setTotalKcals(rs.getInt("total_kcals"));
        user.setUserLevel(rs.getInt("user_level"));
        user.setNotifiedLevel(rs.getInt("notified_level"));

        Object mainDishObj = rs.getObject("main_dish_movie_id");
        if (mainDishObj != null) {
            user.setMainDishMovieId(((Number) mainDishObj).intValue());
        }

        user.setProfileImage(rs.getString("profile_image"));
        user.setBannedUntil(rs.getTimestamp("banned_until"));

        java.sql.Timestamp lastCheckTs = rs.getTimestamp("last_notification_check");
        if (lastCheckTs != null) {
            user.setLastNotificationCheck(lastCheckTs.toLocalDateTime());
        }
        return user;
    }
}