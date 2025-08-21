package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import entity.User;
import util.DatabaseConnection;

public class UserRepository {

	
private Connection connection;
    
public UserRepository() {
    try {
        // Obtener una nueva conexión cada vez que se crea el repositorio
        this.connection = DatabaseConnection.getConnection();
    } catch (SQLException e) {
        throw new RuntimeException("Error al conectar con la base de datos", e);
    }
}


public List<User> findAll() {
    List<User> Users = new ArrayList<>();
    String sql = "SELECT id,password,username,role,email,birthdate FROM users ORDER BY id";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setPassword(rs.getString("password"));
            user.setUsername(rs.getString("username"));
            user.setRole(rs.getString("role"));
            user.setEmail(rs.getString("email"));
            user.setBirthDate(rs.getDate("birthdate"));
            Users.add(user);
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error fetching users from database", e);
    }
    
    return Users;
}

public User getbyID(int id) {
	User user = null;
	String sql = "SELECT id, password, username, role, email, birthdate FROM users WHERE id = ?";
	
	try (Connection conn = DatabaseConnection.getConnection();
	     PreparedStatement stmt = conn.prepareStatement(sql)) {
		
		stmt.setInt(1, id);
		try (ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				user = new User();
				user.setId(rs.getInt("id"));
				user.setPassword(rs.getString("password"));
				user.setUsername(rs.getString("username"));
				user.setRole(rs.getString("role"));
				user.setEmail(rs.getString("email"));
				user.setBirthDate(rs.getDate("birthdate"));
			}
		}
	} catch (SQLException e) {
		throw new RuntimeException("Error fetching user by ID", e);
	}
	
	return user;
}

public User add(User u) {
	String sql = "INSERT INTO users (password, username, role, email, birthdate) VALUES (?, ?, ?, ?, ?)";
	
	try (Connection conn = DatabaseConnection.getConnection();
	     PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
		
		stmt.setString(1, u.getPassword());
		stmt.setString(2, u.getUsername());
		stmt.setString(3, u.getRole());
		stmt.setString(4, u.getEmail());
		stmt.setDate(5, u.getBirthDate());
		
		int affectedRows = stmt.executeUpdate();
		if (affectedRows > 0) {
			try (ResultSet keyResultSet = stmt.getGeneratedKeys()) {
				if (keyResultSet.next()) {
					u.setId(keyResultSet.getInt(1));
				}
			}
		}
	} catch (SQLException e) {
		throw new RuntimeException("Error adding user to database", e);
	}
	
	return u;
}

}