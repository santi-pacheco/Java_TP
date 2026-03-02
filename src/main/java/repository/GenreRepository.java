package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entity.Genre;
import util.DataSourceProvider;
import exception.ErrorFactory;

public class GenreRepository {
    
    //Select base Para no repetir tanto
    private static final String BASE_SELECT = "SELECT genre_id, name, api_id FROM genres";

    public GenreRepository() {
    }

    public List<Genre> findAll() {
        List<Genre> genres = new ArrayList<>();
        String sql = BASE_SELECT + " ORDER BY name";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {          
            
            while (rs.next()) {
                genres.add(mapResultSetToGenre(rs));
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching genres from database");
        }
        
        return genres;
    }
    
    public Genre findOne(int id) {
        Genre genre = null;
        String sql = BASE_SELECT + " WHERE genre_id = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    genre = mapResultSetToGenre(rs);
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching genre by ID");
        }
        return genre;
    }
    
    public Genre add(Genre g) {
        String sql = "INSERT INTO genres (name, api_id) VALUES (?, ?)";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, g.getName());
            stmt.setObject(2, g.getApiId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet keyResultSet = stmt.getGeneratedKeys()) {
                    if (keyResultSet.next()) {
                        g.setGenreId(keyResultSet.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                throw ErrorFactory.duplicate("El género con este nombre o API ID ya existe.");
            } else {
                throw ErrorFactory.internal("Error adding genre to database");
            }
        }
        
        return g;
    }
    
    public Genre update(Genre g) {
        String sql = "UPDATE genres SET name = ?, api_id = ? WHERE genre_id = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, g.getName());
            stmt.setObject(2, g.getApiId());
            stmt.setInt(3, g.getGenreId());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw ErrorFactory.duplicate("El género con este nombre o API ID ya existe.");
            } else {
                throw ErrorFactory.internal("Error updating genre in database");
            }
        } 
        return g;
    }
    
    public Genre delete(Genre g) {
        String sql = "DELETE FROM genres WHERE genre_id = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, g.getGenreId());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error deleting genre from database"); 
        }
        return g;
    }
    
    public void saveAll(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) return;
        
        String sql = "INSERT INTO genres (name, api_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name), api_id = VALUES(api_id)";

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            for (Genre genre : genres) {
                stmt.setString(1, genre.getName());
                stmt.setObject(2, genre.getApiId());
                stmt.addBatch();
            }
            stmt.executeBatch();
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error saving genres to database");
        }
    }
    
    public Integer findByIdApi(Integer idApi) {
        Integer genreId = null;
        String sql = "SELECT genre_id FROM genres WHERE api_id = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, idApi);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    genreId = Integer.valueOf(rs.getInt("genre_id"));
                }
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching genre by API ID");
        }
        return genreId;
    } 
    
    // ÚNICO PUNTO DE MAPEO
    private Genre mapResultSetToGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setGenreId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        
        Object apiIdObj = rs.getObject("api_id");
        if (apiIdObj != null) {
            genre.setApiId(((Number) apiIdObj).intValue());
        }
        return genre;
    }
}