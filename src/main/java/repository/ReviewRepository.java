package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entity.Review;
import exception.ErrorFactory;
import util.DataSourceProvider;

public class ReviewRepository {

    public Review add(Review review) {
        String sql = "INSERT INTO reviews (id_user, id_movie, review_text, rating, watched_on, contiene_spoiler) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, review.getId_user());
            stmt.setInt(2, review.getId_movie());
            stmt.setString(3, review.getReview_text());
            stmt.setDouble(4, review.getRating());
            stmt.setDate(5, java.sql.Date.valueOf(review.getWatched_on()));
            
            // contiene_spoiler siempre null al crear
            if (review.getContieneSpoiler() != null) {
                stmt.setBoolean(6, review.getContieneSpoiler());
            } else {
                stmt.setNull(6, java.sql.Types.BOOLEAN);
            }
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet keyResultSet = stmt.getGeneratedKeys()) {
                    if (keyResultSet.next()) {
                        review.setId(keyResultSet.getInt(1));
                    }
                }
            }
            
        } catch (SQLException e) {
            if (e.getMessage().contains("unique_user_movie_review")) {
                throw ErrorFactory.duplicate("Ya tienes una reseña para esta película");
            } else if (e.getMessage().contains("id_user")) {
                throw ErrorFactory.notFound("El usuario especificado no existe");
            } else if (e.getMessage().contains("id_movie")) {
                throw ErrorFactory.notFound("La película especificada no existe");
            } else {
                throw ErrorFactory.internal("Error adding review to database");
            }
        }
        
        return review;
    }

    public Review findOne(int id) {
        Review review = null;
        String sql = "SELECT id_review, id_user, id_movie, review_text, rating, watched_on, created_at, contiene_spoiler FROM reviews WHERE id_review = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    review = new Review();
                    review.setId(rs.getInt("id_review"));
                    review.setId_user(rs.getInt("id_user"));
                    review.setId_movie(rs.getInt("id_movie"));
                    review.setReview_text(rs.getString("review_text"));
                    review.setRating(rs.getDouble("rating"));
                    review.setWatched_on(rs.getDate("watched_on").toLocalDate());
                    
                    // Manejar created_at que puede ser null
                    java.sql.Date createdDate = rs.getDate("created_at");
                    if (createdDate != null) {
                        review.setCreated_at(createdDate.toLocalDate());
                    }
                    
                    // Manejar contiene_spoiler nullable
                    Boolean spoiler = rs.getObject("contiene_spoiler", Boolean.class);
                    review.setContieneSpoiler(spoiler);
                }
            }
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching review by ID");
        }
        
        return review;
    }

    public Review findByUserAndMovie(int userId, int movieId) {
        Review review = null;
        String sql = "SELECT id_review, id_user, id_movie, review_text, rating, watched_on, created_at, contiene_spoiler FROM reviews WHERE id_user = ? AND id_movie = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    review = new Review();
                    review.setId(rs.getInt("id_review"));
                    review.setId_user(rs.getInt("id_user"));
                    review.setId_movie(rs.getInt("id_movie"));
                    review.setReview_text(rs.getString("review_text"));
                    review.setRating(rs.getDouble("rating"));
                    review.setWatched_on(rs.getDate("watched_on").toLocalDate());
                    
                    java.sql.Date createdDate = rs.getDate("created_at");
                    if (createdDate != null) {
                        review.setCreated_at(createdDate.toLocalDate());
                    }
                    
                    Boolean spoiler = rs.getObject("contiene_spoiler", Boolean.class);
                    review.setContieneSpoiler(spoiler);
                }
            }
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching review by user and movie");
        }
        
        return review;
    }

    public boolean existsByUserAndMovie(int userId, int movieId) {
        String sql = "SELECT 1 FROM reviews WHERE id_user = ? AND id_movie = ? LIMIT 1";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error checking review existence");
        }
    }

    public Review update(Review review) {
        String sql = "UPDATE reviews SET review_text = ?, rating = ?, watched_on = ? WHERE id_review = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, review.getReview_text());
            stmt.setDouble(2, review.getRating());
            stmt.setDate(3, java.sql.Date.valueOf(review.getWatched_on()));
            stmt.setInt(4, review.getId());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating review in database");
        }
        
        return review;
    }

    public Review delete(Review review) {
        String sql = "DELETE FROM reviews WHERE id_review = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, review.getId());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error deleting review from database");
        }
        
        return review;
    }

    public List<Review> findByMovie(int movieId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.id_review, r.id_user, r.id_movie, r.review_text, r.rating, r.watched_on, r.created_at, r.contiene_spoiler, u.username FROM reviews r JOIN usuarios u ON r.id_user = u.id_user WHERE r.id_movie = ? ORDER BY r.created_at DESC";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, movieId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review();
                    review.setId(rs.getInt("id_review"));
                    review.setId_user(rs.getInt("id_user"));
                    review.setId_movie(rs.getInt("id_movie"));
                    review.setReview_text(rs.getString("review_text"));
                    review.setRating(rs.getDouble("rating"));
                    review.setWatched_on(rs.getDate("watched_on").toLocalDate());
                    review.setUsername(rs.getString("username"));
                    
                    java.sql.Date createdDate = rs.getDate("created_at");
                    if (createdDate != null) {
                        review.setCreated_at(createdDate.toLocalDate());
                    }
                    
                    Boolean spoiler = rs.getObject("contiene_spoiler", Boolean.class);
                    review.setContieneSpoiler(spoiler);
                    
                    reviews.add(review);
                }
            }
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching reviews by movie");
        }
        
        return reviews;
    }

    public List<Review> findPendingSpoilerReviews() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT id_review, id_user, id_movie, review_text, rating, watched_on, created_at, contiene_spoiler FROM reviews WHERE contiene_spoiler IS NULL ORDER BY created_at ASC";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Review review = new Review();
                review.setId(rs.getInt("id_review"));
                review.setId_user(rs.getInt("id_user"));
                review.setId_movie(rs.getInt("id_movie"));
                review.setReview_text(rs.getString("review_text"));
                review.setRating(rs.getDouble("rating"));
                review.setWatched_on(rs.getDate("watched_on").toLocalDate());
                
                java.sql.Date createdDate = rs.getDate("created_at");
                if (createdDate != null) {
                    review.setCreated_at(createdDate.toLocalDate());
                }
                
                review.setContieneSpoiler(null);
                reviews.add(review);
            }
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching pending spoiler reviews");
        }
        
        return reviews;
    }

    public void updateSpoilerStatus(int reviewId, boolean containsSpoiler) {
        String sql = "UPDATE reviews SET contiene_spoiler = ? WHERE id_review = ?";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, containsSpoiler);
            stmt.setInt(2, reviewId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating spoiler status");
        }
    }
}
