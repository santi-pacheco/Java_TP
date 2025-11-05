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

	
//private Connection connection;
    
public UserRepository() {
    //Ya no se crea la conexión aquí, se obtiene en cada método usando el pool de conexiones
}


public List<User> findAll() {
    List<User> Users = new ArrayList<>();
    String sql = "SELECT id_user ,password, username, role, email, birthdate, esUsuarioActivo FROM usuarios ORDER BY id_user";
    
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
            Users.add(user);
        }
    } catch (SQLException e) {
    	throw ErrorFactory.internal("Error fetching users from database");
    }
    
    return Users;
}

public User findOne(int id) {
	User user = null;
	String sql = "SELECT id_user, password, username, role, email, birthdate, esUsuarioActivo FROM usuarios WHERE id_user = ?";
	
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
			}
		}
	} catch (SQLException e) {
		throw ErrorFactory.internal("Error fetching user by ID");
	}
	return user;
}

public User add(User u) {
	String sql = "INSERT INTO usuarios (password, username, role, email, birthdate) VALUES (?, ?, ?, ?, ?)";
	
	try (Connection conn = DataSourceProvider.getDataSource().getConnection();
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
		if (e.getErrorCode() == 1062) { // Código SQLState para violación de restricción única en PostgreSQL
			throw ErrorFactory.duplicate("Username or email already exists");
		} else {
			throw ErrorFactory.internal("Error adding user to database");
		}
	}
	
	return u;
}

public User update(User u) {
	String sql = "UPDATE usuarios SET username = ?, password = ?, role = ?, email = ?, birthdate = ?, esUsuarioActivo = ? WHERE id_user = ?";
	
	try (Connection connection = DataSourceProvider.getDataSource().getConnection();
		PreparedStatement stmt = connection.prepareStatement(sql)) {
		
		stmt.setString(1, u.getUsername());
		stmt.setString(2, u.getPassword());
		stmt.setString(3, u.getRole());
		stmt.setString(4, u.getEmail());
		stmt.setDate(5, u.getBirthDate());
		stmt.setBoolean(6, u.isEsUsuarioActivo());
		stmt.setInt(7, u.getId());
		
		stmt.executeUpdate();
		
	} catch (SQLException e) {
		if (e.getErrorCode() == 1062) { // Código SQLState para violación de restricción única en PostgreSQL
			throw ErrorFactory.duplicate("Username or email already exists");
		} else {
			throw ErrorFactory.internal("Error updating user in database");
		}
	}
	return u;
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
	String sql = "SELECT id_user, password, username, role, email, birthdate FROM usuarios WHERE username = ?";
	
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
			}
		}
	} catch (SQLException e) {
		throw ErrorFactory.internal("Error fetching user by username from database");
	}
	
	return user;
}
}