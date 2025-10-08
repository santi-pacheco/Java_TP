package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.Genre;
import util.DataSourceProvider;

public class GenreRepository {

    //private Connection connection;
    
    public GenreRepository() {
    	//Ya no se crea la conexión aquí, se obtiene en cada método usando el pool de conexiones
    }

    public List<Genre> findAll() {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT id_genero, name, id_api FROM generos ORDER BY name";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
        		PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {          
        	
            while (rs.next()) {
                Genre genre = new Genre();
                genre.setId(rs.getInt("id_genero"));
                genre.setName(rs.getString("name"));
                genre.setId_api(rs.getInt("id_api"));
                genres.add(genre);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching genres from database", e);
        }
        
        return genres;
    }
    
    public Genre findOne(int id) {
    	Genre genre = null;
    	String sql = "SELECT id_genero, name, id_api FROM generos WHERE id_genero = ?";
    	
    	try (Connection conn = DataSourceProvider.getDataSource().getConnection();
    	     PreparedStatement stmt = conn.prepareStatement(sql)) {
    		
    		stmt.setInt(1, id);
    		try (ResultSet rs = stmt.executeQuery()) {
    			if (rs.next()) {
    				genre = new Genre();
    				genre.setId(rs.getInt("id_genero"));
    				genre.setName(rs.getString("name"));
    				genre.setId_api(rs.getInt("id_api"));
    			}
    		}
    	} catch (SQLException e) {
    		throw new RuntimeException("Error fetching user by ID", e);
    	}
    	
    	return genre;
    }
    
    public Genre add(Genre g) {
    	String sql = "INSERT INTO generos (name, id_api) VALUES (?, ?)";
    	
    	try (Connection conn = DataSourceProvider.getDataSource().getConnection();
    	     PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
    		
    		stmt.setString(1, g.getName());
    		stmt.setInt(2, g.getId_api());
    		
    		int affectedRows = stmt.executeUpdate();
    		if (affectedRows > 0) {
    			try (ResultSet keyResultSet = stmt.getGeneratedKeys()) {
    				if (keyResultSet.next()) {
    					g.setId(keyResultSet.getInt(1));
    				}
    			}
    		}
    	} catch (SQLException e) {
    		throw new RuntimeException("Error adding user to database", e);
    	}
    	
    	return g;
    }
    
    public Genre update(Genre g) {
		String sql = "UPDATE generos SET name = ?, id_api = ? WHERE id_genero = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setString(1, g.getName());
			stmt.setInt(2, g.getId_api());
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new RuntimeException("Error preparing update statement for genre", e);
		} 
		return g;
	}
	
	public Genre delete(Genre g) {
		String sql = "DELETE FROM generos WHERE id_genero = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, g.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Error deleting person from database", e);
		}
		return g;
	}
    
    public void saveAll(List<Genre> genres) {
        String sql = "INSERT INTO generos (id_genero, name, id_api) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
       	     PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (Genre genre : genres) {
                stmt.setInt(1, genre.getId());
                stmt.setString(2, genre.getName());
                stmt.setInt(3, genre.getId_api());
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving genres to database", e);
        }
    }
}