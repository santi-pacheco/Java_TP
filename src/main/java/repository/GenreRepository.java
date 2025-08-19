package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.Genre;

public class GenreRepository {

    private Connection connection;
    
    public GenreRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Genre> findAll() {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT id, name FROM genres ORDER BY name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Genre genre = new Genre();
                genre.setId(rs.getInt("id"));
                genre.setName(rs.getString("name"));
                genres.add(genre);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching genres from database", e);
        }
        
        return genres;
    }
    
    public void saveAll(List<Genre> genres) {
        String sql = "INSERT INTO genres (id, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (Genre genre : genres) {
                stmt.setInt(1, genre.getId());
                stmt.setString(2, genre.getName());
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving genres to database", e);
        }
    }
}