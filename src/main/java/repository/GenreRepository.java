package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.Genre;
import util.DataSourceProvider;
import exception.ErrorFactory;

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
            throw ErrorFactory.internal("Error fetching genres from database");
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
    		throw ErrorFactory.internal("Error fetching user by ID");
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
    		if (e.getSQLState().equals("23505")) { // Código de error para clave duplicada en MySQL
				throw ErrorFactory.duplicate("Genre with the same API ID already exists");
			}
			throw ErrorFactory.internal("Error adding genre to database");
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
			throw ErrorFactory.internal("Error preparing update statement for genre");
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
			throw ErrorFactory.internal("Error deleting person from database");
		}
		return g;
	}
    
    public void saveAll(List<Genre> genres) {
        String sql = "INSERT INTO generos (id_genero, name, id_api) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name), id_api = VALUES(id_api)";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
       	     PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (Genre genre : genres) {
                stmt.setInt(1, genre.getId());
                stmt.setString(2, genre.getName());
                stmt.setInt(3, genre.getId_api());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error saving genres to database");
        }
    }
    
    public Integer findByIdApi(Integer idApi) {
		//Cambia el metodo... Ahora busca por idApi, y devuelve el id. (La PK de mi BD)
		Integer generoId = null;
		String sql = "SELECT id_genero FROM generos WHERE id_api = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, idApi);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					generoId = rs.getInt("id_genero");
				}
				//Pasar a Integer
				generoId = Integer.valueOf(rs.getInt("id_genero"));
			}
		} catch (SQLException e) {
				throw ErrorFactory.internal("Error fetching genre by API ID");
		}
		return generoId;
	} 
}