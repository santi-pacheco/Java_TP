package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entity.ModerationStatus;
import entity.Review;
import exception.ErrorFactory;
import util.DataSourceProvider;
import entity.FeedReviewDTO;
import java.text.SimpleDateFormat;


public class ReviewRepository {

    public Review add(Review review) {
        String sql = "INSERT INTO reviews (id_user, id_movie, review_text, rating, watched_on, moderation_status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, review.getId_user());
            stmt.setInt(2, review.getId_movie());
            stmt.setString(3, review.getReview_text());
            stmt.setDouble(4, review.getRating());
            stmt.setDate(5, java.sql.Date.valueOf(review.getWatched_on()));
            stmt.setString(6, review.getModerationStatus().getValue());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet keyResultSet = stmt.getGeneratedKeys()) {
                    if (keyResultSet.next()) {
                        review.setId(keyResultSet.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            if (e.getMessage().contains("unique_user_movie_review")) throw ErrorFactory.duplicate("Ya tienes una reseña para esta película");
            else if (e.getMessage().contains("id_user")) throw ErrorFactory.notFound("El usuario especificado no existe");
            else if (e.getMessage().contains("id_movie")) throw ErrorFactory.notFound("La película especificada no existe");
            
            else throw ErrorFactory.internal("Error adding review to database");
        }
        return review;
    }

    public Review findOne(int id) {
        Review review = null;
       
        String sql = "SELECT r.*, p.name as movie_title, u.username, u.profile_image FROM reviews r JOIN peliculas p ON r.id_movie = p.id_pelicula LEFT JOIN usuarios u ON r.id_user = u.id_user WHERE r.id_review = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
               if(rs.next()) review = extractReviewFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching review by ID");
        }
        return review;
    }

    public Review findByUserAndMovie(int userId, int movieId) {
        Review review = null;
        String sql = "SELECT r.*, u.username, u.profile_image, p.name as movie_title " +
                     "FROM reviews r " +
                     "LEFT JOIN usuarios u ON r.id_user = u.id_user " +
                     "LEFT JOIN peliculas p ON r.id_movie = p.id_pelicula " +
                     "WHERE r.id_user = ? AND r.id_movie = ? AND r.moderation_status != 'REJECTED'";
                     
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) review = extractReviewFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching review by user and movie");
        }
        return review;
    }
    public Review findAnyByUserAndMovie(int userId, int movieId) {
        Review review = null;
        String sql = "SELECT r.*, u.username, u.profile_image, p.name as movie_title " +
                     "FROM reviews r " +
                     "LEFT JOIN usuarios u ON r.id_user = u.id_user " +
                     "LEFT JOIN peliculas p ON r.id_movie = p.id_pelicula " +
                     "WHERE r.id_user = ? AND r.id_movie = ?";
                     
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) review = extractReviewFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching any review by user and movie");
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
        String sql = "UPDATE reviews SET review_text = ?, rating = ?, watched_on = ?, moderation_status = ? WHERE id_review = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, review.getReview_text());
            stmt.setDouble(2, review.getRating());
            stmt.setDate(3, java.sql.Date.valueOf(review.getWatched_on()));
            stmt.setString(4, ModerationStatus.PENDING_MODERATION.getValue());
            stmt.setInt(5, review.getId());
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

    public List<Review> findByMovie(int movieId, int idUsuarioLogueado) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.username, u.profile_image, p.name as movie_title, " +
                     "COALESCE((SELECT COUNT(*) FROM reviews_comments rc WHERE rc.id_review = r.id_review AND rc.moderation_status IN ('APPROVED', 'SPOILER')), 0) as comments_count " +
                     "FROM reviews r JOIN usuarios u ON r.id_user = u.id_user JOIN peliculas p ON r.id_movie = p.id_pelicula " +
                     "WHERE r.id_movie = ? AND r.moderation_status IN ('APPROVED', 'SPOILER') " +
                     "AND r.id_user NOT IN (SELECT id_blocked FROM bloqueos WHERE id_blocker = ?) " +
                     "AND r.id_user NOT IN (SELECT id_blocker FROM bloqueos WHERE id_blocked = ?) " +
                     "ORDER BY r.created_at DESC";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
               stmt.setInt(1, movieId);
               stmt.setInt(2, idUsuarioLogueado);
               stmt.setInt(3, idUsuarioLogueado);
               try (ResultSet rs = stmt.executeQuery()) {
                   while (rs.next()) reviews.add(extractReviewFromResultSet(rs));
               }
           } catch (SQLException e) {
               throw ErrorFactory.internal("Error fetching reviews by movie");
           }
           return reviews;
    }

    public List<Review> findAll() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.username, u.profile_image, p.name as movie_title FROM reviews r JOIN usuarios u ON r.id_user = u.id_user JOIN peliculas p ON r.id_movie = p.id_pelicula ORDER BY r.created_at DESC";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) reviews.add(extractReviewFromResultSet(rs));
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching all reviews");
        }
        return reviews;
    }

    public int countReviewsByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE id_user = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error counting user reviews");
        }
        return 0;
    }

    public List<Review> findByUser(int userId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.username, u.profile_image, p.name as movie_title FROM reviews r LEFT JOIN usuarios u ON r.id_user = u.id_user LEFT JOIN peliculas p ON r.id_movie = p.id_pelicula WHERE r.id_user = ? AND r.moderation_status IN ('APPROVED', 'SPOILER') ORDER BY r.created_at DESC";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) reviews.add(extractReviewFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching reviews by user");
        }
        return reviews;
    }

    public boolean updateModerationStatus(int reviewId, ModerationStatus status, String reason){
        String query = "UPDATE reviews SET moderation_status = ?, moderation_reason = ? WHERE id_review = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status.getValue());
            stmt.setString(2, reason);
            stmt.setInt(3, reviewId);
            return stmt.executeUpdate() > 0;
        }catch (SQLException e) {
            throw ErrorFactory.internal("Error updating moderation status");
        }
    }

    public List<Review> getReviewsByModerationStatus(ModerationStatus status){
        String query = "SELECT r.*, u.username, u.profile_image, p.titulo_original as movie_title FROM reviews r JOIN usuarios u ON r.id_user = u.id_user JOIN peliculas p ON r.id_movie = p.id_pelicula WHERE r.moderation_status = ? ORDER BY r.created_at DESC";
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status.getValue());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) reviews.add(extractReviewFromResultSet(rs));
            }
        }catch (SQLException e) {
            throw ErrorFactory.internal("Error fetching reviews by moderation status");
        }
        return reviews;
    }

    private Review extractReviewFromResultSet(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getInt("id_review"));
        review.setId_user(rs.getInt("id_user"));
        review.setId_movie(rs.getInt("id_movie"));
        review.setReview_text(rs.getString("review_text"));
        review.setRating(rs.getDouble("rating"));
        
        java.sql.Date watchedDate = rs.getDate("watched_on");
        if (watchedDate != null) review.setWatched_on(watchedDate.toLocalDate());
        
        java.sql.Date createdDate = rs.getDate("created_at");
        if (createdDate != null) review.setCreated_at(createdDate.toLocalDate());
        
        String statusStr = rs.getString("moderation_status");
        if (statusStr != null) review.setModerationStatus(ModerationStatus.fromString(statusStr));
        
        review.setModerationReason(rs.getString("moderation_reason"));
        review.setUsername(rs.getString("username"));
        review.setMovieTitle(rs.getString("movie_title"));


        try { review.setProfileImage(rs.getString("profile_image")); } catch (Exception e) {}

        try { review.setLikesCount(rs.getInt("likes_count")); } catch (Exception e) {}
        try { review.setCommentsCount(rs.getInt("comments_count")); } catch (Exception e) {}
        
        return review;
    }

    public List<Review> findByMovieSortedByLikes(int movieId, int idUsuarioLogueado) {
        List<Review> reviews = new ArrayList<>();

        String sql = "SELECT r.id_review, r.id_user, r.id_movie, r.review_text, r.rating, " +
                     "r.watched_on, r.created_at, r.moderation_status, r.moderation_reason, " +
                     "u.username, u.profile_image, p.titulo_original as movie_title, " +
                     "COALESCE((SELECT COUNT(*) FROM review_likes rl WHERE rl.id_review = r.id_review), 0) as likes_count " +
                     "FROM reviews r " +
                     "JOIN usuarios u ON r.id_user = u.id_user " +
                     "JOIN peliculas p ON r.id_movie = p.id_pelicula " +
                     "WHERE r.id_movie = ? AND r.moderation_status IN ('APPROVED', 'SPOILER') " +
                     "AND r.id_user NOT IN (SELECT id_blocked FROM bloqueos WHERE id_blocker = ?) " +
                     "AND r.id_user NOT IN (SELECT id_blocker FROM bloqueos WHERE id_blocked = ?) " +
                     "ORDER BY likes_count DESC, r.created_at DESC";
        
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
               stmt.setInt(1, movieId);
               stmt.setInt(2, idUsuarioLogueado);
               stmt.setInt(3, idUsuarioLogueado);
               try (ResultSet rs = stmt.executeQuery()) {
                   while (rs.next()) {
                      reviews.add(extractReviewFromResultSet(rs));
                   }
               }
           } catch (SQLException e) {
               throw ErrorFactory.internal("Error fetching reviews by movie sorted by likes");
           }
           return reviews;
    }

        public List<FeedReviewDTO> getFriendsFeedPaginated(int idUsuarioLogueado, int offset, int limit) {
            List<FeedReviewDTO> feed = new ArrayList<>();
            String sql = "SELECT r.id_review, r.id_movie, r.review_text, r.rating, r.created_at, r.moderation_status, " +
                         "u.id_user, u.username, u.profile_image, u.nivel_usuario, " +
                         "p.poster_path, p.name AS movie_title " +
                         "FROM reviews r " +
                         "INNER JOIN seguidores s ON r.id_user = s.id_seguido " +
                         "INNER JOIN usuarios u ON r.id_user = u.id_user " +
                         "INNER JOIN peliculas p ON r.id_movie = p.id_pelicula " +
                         "WHERE s.id_seguidor = ? " +
                         "AND r.moderation_status IN ('APPROVED', 'SPOILER') " +
                         "ORDER BY r.created_at DESC " +
                         "LIMIT ? OFFSET ?";
                         
            try (Connection conn = DataSourceProvider.getDataSource().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, idUsuarioLogueado);
                stmt.setInt(2, limit);
                stmt.setInt(3, offset);
                
                ResultSet rs = stmt.executeQuery();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                
                while (rs.next()) {
                    entity.FeedReviewDTO dto = new entity.FeedReviewDTO();
                    dto.setReviewId(rs.getInt("id_review"));
                    dto.setMovieId(rs.getInt("id_movie"));
                    dto.setUserId(rs.getInt("id_user"));
                    dto.setPosterPath(rs.getString("poster_path"));
                    dto.setUsername(rs.getString("username"));
                    dto.setUserAvatar(rs.getString("profile_image"));
                    dto.setUserLevel(rs.getInt("nivel_usuario")); // SETEAMOS EL NIVEL ACÁ
                    if (rs.getTimestamp("created_at") != null) {
                        dto.setDateFormatted(sdf.format(rs.getTimestamp("created_at")));
                    } else {
                        dto.setDateFormatted("Reciente");
                    }
                    dto.setRating(rs.getDouble("rating"));
                    dto.setMovieTitle(rs.getString("movie_title"));
                    dto.setText(rs.getString("review_text"));
                    dto.setModeration_status(rs.getString("moderation_status"));
                    dto.setFollowing(true);
                    
                    feed.add(dto);
                }
            } catch (SQLException e) {
                throw ErrorFactory.internal("Error fetching paginated friends' feed");
            }
            return feed;
        }
    

        public int countFriendsReviews(int idUsuarioLogueado) {
            String sql = "SELECT COUNT(*) FROM reviews r INNER JOIN seguidores s ON r.id_user = s.id_seguido WHERE s.id_seguidor = ?";
            try (Connection conn = DataSourceProvider.getDataSource().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idUsuarioLogueado);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            } catch (SQLException e) {
                throw ErrorFactory.internal("Error counting friends' reviews");
            }
            return 0;
        }

        public List<FeedReviewDTO> getPopularFeedPaginated(int idUsuarioLogueado, int offset, int limit) {
            List<FeedReviewDTO> feed = new ArrayList<>();

            String sql = "SELECT r.id_review, r.id_movie, r.review_text, r.rating, r.created_at, r.likes_count, r.moderation_status, " +
                         "u.id_user, u.username, u.profile_image, u.nivel_usuario, p.poster_path, p.name AS movie_title " +
                         "FROM reviews r " +
                         "INNER JOIN usuarios u ON r.id_user = u.id_user " +
                         "INNER JOIN peliculas p ON r.id_movie = p.id_pelicula " +
                         "WHERE r.id_user != ? " + 
                         "AND r.id_user NOT IN (SELECT id_seguido FROM seguidores WHERE id_seguidor = ?) " +
                         "AND r.moderation_status IN ('APPROVED', 'SPOILER') " +
                         "AND r.id_user NOT IN (SELECT id_blocked FROM bloqueos WHERE id_blocker = ?) " +
                         "AND r.id_user NOT IN (SELECT id_blocker FROM bloqueos WHERE id_blocked = ?) " +
                         "ORDER BY r.likes_count DESC, r.created_at DESC " +
                         "LIMIT ? OFFSET ?";
                         
            try (Connection conn = DataSourceProvider.getDataSource().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, idUsuarioLogueado);
                stmt.setInt(2, idUsuarioLogueado);
                stmt.setInt(3, idUsuarioLogueado);
                stmt.setInt(4, idUsuarioLogueado);
                stmt.setInt(5, limit);
                stmt.setInt(6, offset);
                
                ResultSet rs = stmt.executeQuery();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                
                while (rs.next()) {
                    FeedReviewDTO dto = new FeedReviewDTO();
                    dto.setReviewId(rs.getInt("id_review"));
                    dto.setMovieId(rs.getInt("id_movie"));
                    dto.setUserId(rs.getInt("id_user"));
                    dto.setUsername(rs.getString("username"));
                    dto.setUserAvatar(rs.getString("profile_image"));
                    dto.setUserLevel(rs.getInt("nivel_usuario")); // SETEAMOS EL NIVEL ACÁ
                    dto.setPosterPath(rs.getString("poster_path"));
                    dto.setDateFormatted(rs.getTimestamp("created_at") != null ? sdf.format(rs.getTimestamp("created_at")) : "Reciente");
                    dto.setRating(rs.getDouble("rating"));
                    dto.setMovieTitle(rs.getString("movie_title"));
                    dto.setText(rs.getString("review_text"));
                    dto.setModeration_status(rs.getString("moderation_status"));
                    dto.setFollowing(false);
                    feed.add(dto);
                }
            } catch (SQLException e) {
                throw ErrorFactory.internal("Error fetching paginated popular feed");
            }
            return feed;
        }
    
}