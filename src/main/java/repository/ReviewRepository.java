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
        String sql = "INSERT INTO reviews (user_id, movie_id, review_text, rating, watched_on, moderation_status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, review.getUserId());
            stmt.setInt(2, review.getMovieId());
            stmt.setString(3, review.getReviewText());
            stmt.setDouble(4, review.getRating());
            stmt.setDate(5, java.sql.Date.valueOf(review.getWatchedOn()));
            stmt.setString(6, review.getModerationStatus().getValue());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet keyResultSet = stmt.getGeneratedKeys()) {
                    if (keyResultSet.next()) {
                        review.setReviewId(keyResultSet.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            if (e.getMessage().contains("unique_user_movie_review")) throw ErrorFactory.duplicate("Ya tienes una reseña para esta película");
            else if (e.getMessage().contains("user_id")) throw ErrorFactory.notFound("El usuario especificado no existe");
            else if (e.getMessage().contains("movie_id")) throw ErrorFactory.notFound("La película especificada no existe");
            
            else throw ErrorFactory.internal("Error adding review to database");
        }
        return review;
    }

    public Review findOne(int id) {
        Review review = null;
       
        String sql = "SELECT r.*, p.title as movie_title, u.username, u.profile_image FROM reviews r JOIN movies p ON r.movie_id = p.movie_id LEFT JOIN users u ON r.user_id = u.user_id WHERE r.review_id = ?";
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
        String sql = "SELECT r.*, u.username, u.profile_image, p.title as movie_title " +
                     "FROM reviews r " +
                     "LEFT JOIN users u ON r.user_id = u.user_id " +
                     "LEFT JOIN movies p ON r.movie_id = p.movie_id " +
                     "WHERE r.user_id = ? AND r.movie_id = ? AND r.moderation_status != 'REJECTED'";
                     
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
        String sql = "SELECT r.*, u.username, u.profile_image, p.title as movie_title " +
                     "FROM reviews r " +
                     "LEFT JOIN users u ON r.user_id = u.user_id " +
                     "LEFT JOIN movies p ON r.movie_id = p.movie_id " +
                     "WHERE r.user_id = ? AND r.movie_id = ?";
                     
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
        String sql = "SELECT 1 FROM reviews WHERE user_id = ? AND movie_id = ? LIMIT 1";
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
        String sql = "UPDATE reviews SET review_text = ?, rating = ?, watched_on = ?, moderation_status = ? WHERE review_id = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, review.getReviewText());
            stmt.setDouble(2, review.getRating());
            stmt.setDate(3, java.sql.Date.valueOf(review.getWatchedOn()));
            stmt.setString(4, ModerationStatus.PENDING_MODERATION.getValue());
            stmt.setInt(5, review.getReviewId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error updating review in database");
        }
        return review;
    }

    public Review delete(Review review) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, review.getReviewId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ErrorFactory.internal("Error deleting review from database");
        }
        return review;
    }

    public List<Review> findByMovie(int movieId, int idUsuarioLogueado) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.username, u.profile_image, p.title as movie_title, " +
                     "COALESCE((SELECT COUNT(*) FROM review_comments rc WHERE rc.review_id = r.review_id AND rc.moderation_status IN ('APPROVED', 'SPOILER')), 0) as comments_count " +
                     "FROM reviews r JOIN users u ON r.user_id = u.user_id JOIN movies p ON r.movie_id = p.movie_id " +
                     "WHERE r.movie_id = ? AND r.moderation_status IN ('APPROVED', 'SPOILER') " +
                     "AND r.user_id NOT IN (SELECT blocked_id FROM user_blocks WHERE blocker_id = ?) " +
                     "AND r.user_id NOT IN (SELECT blocker_id FROM user_blocks WHERE blocked_id = ?) " +
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
        String sql = "SELECT r.*, u.username, u.profile_image, p.title as movie_title FROM reviews r JOIN users u ON r.user_id = u.user_id JOIN movies p ON r.movie_id = p.movie_id ORDER BY r.created_at DESC";
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
        String sql = "SELECT COUNT(*) FROM reviews WHERE user_id = ?";
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
        String sql = "SELECT r.*, u.username, u.profile_image, p.title as movie_title FROM reviews r LEFT JOIN users u ON r.user_id = u.user_id LEFT JOIN movies p ON r.movie_id = p.movie_id WHERE r.user_id = ? AND r.moderation_status IN ('APPROVED', 'SPOILER') ORDER BY r.created_at DESC";
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
        String query = "UPDATE reviews SET moderation_status = ?, moderation_reason = ? WHERE review_id = ?";
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
        String query = "SELECT r.*, u.username, u.profile_image, p.title as movie_title FROM reviews r JOIN users u ON r.user_id = u.user_id JOIN movies p ON r.movie_id = p.movie_id WHERE r.moderation_status = ? ORDER BY r.created_at DESC";
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
        review.setReviewId(rs.getInt("review_id"));
        review.setUserId(rs.getInt("user_id"));
        review.setMovieId(rs.getInt("movie_id"));
        review.setReviewText(rs.getString("review_text"));
        review.setRating(rs.getDouble("rating"));

        java.sql.Date watchedDate = rs.getDate("watched_on");
        if (watchedDate != null) review.setWatchedOn(watchedDate.toLocalDate());

        java.sql.Date createdDate = rs.getDate("created_at");
        if (createdDate != null) review.setCreatedAt(createdDate.toLocalDate());

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

        String sql = "SELECT r.review_id, r.user_id, r.movie_id, r.review_text, r.rating, " +
                     "r.watched_on, r.created_at, r.moderation_status, r.moderation_reason, " +
                     "u.username, u.profile_image, p.title as movie_title, " +
                     "COALESCE((SELECT COUNT(*) FROM review_likes rl WHERE rl.review_id = r.review_id), 0) as likes_count " +
                     "FROM reviews r " +
                     "JOIN users u ON r.user_id = u.user_id " +
                     "JOIN movies p ON r.movie_id = p.movie_id " +
                     "WHERE r.movie_id = ? AND r.moderation_status IN ('APPROVED', 'SPOILER') " +
                     "AND r.user_id NOT IN (SELECT blocked_id FROM user_blocks WHERE blocker_id = ?) " +
                     "AND r.user_id NOT IN (SELECT blocker_id FROM user_blocks WHERE blocked_id = ?) " +
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
            String sql = "SELECT r.review_id, r.movie_id, r.review_text, r.rating, r.created_at, r.moderation_status, " +
                         "u.user_id, u.username, u.profile_image, u.user_level, " +
                         "p.poster_path, p.title AS movie_title " +
                         "FROM reviews r " +
                         "INNER JOIN followers s ON r.user_id = s.followed_id " +
                         "INNER JOIN users u ON r.user_id = u.user_id " +
                         "INNER JOIN movies p ON r.movie_id = p.movie_id " +
                         "WHERE s.follower_id = ? " +
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
                    dto.setReviewId(rs.getInt("review_id"));
                    dto.setMovieId(rs.getInt("movie_id"));
                    dto.setUserId(rs.getInt("user_id"));
                    dto.setPosterPath(rs.getString("poster_path"));
                    dto.setUsername(rs.getString("username"));
                    dto.setUserAvatar(rs.getString("profile_image"));
                    dto.setUserLevel(rs.getInt("user_level"));
                    if (rs.getTimestamp("created_at") != null) {
                        dto.setDateFormatted(sdf.format(rs.getTimestamp("created_at")));
                    } else {
                        dto.setDateFormatted("Reciente");
                    }
                    dto.setRating(rs.getDouble("rating"));
                    dto.setMovieTitle(rs.getString("movie_title"));
                    dto.setText(rs.getString("review_text"));
                    dto.setModerationStatus(rs.getString("moderation_status"));
                    dto.setFollowing(true);
                    
                    feed.add(dto);
                }
            } catch (SQLException e) {
                throw ErrorFactory.internal("Error fetching paginated friends' feed");
            }
            return feed;
        }
    

        public int countFriendsReviews(int idUsuarioLogueado) {
            String sql = "SELECT COUNT(*) FROM reviews r INNER JOIN followers s ON r.user_id = s.followed_id WHERE s.follower_id = ?";
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

            String sql = "SELECT r.review_id, r.movie_id, r.review_text, r.rating, r.created_at, r.likes_count, r.moderation_status, " +
                         "u.user_id, u.username, u.profile_image, u.user_level, p.poster_path, p.title AS movie_title " +
                         "FROM reviews r " +
                         "INNER JOIN users u ON r.user_id = u.user_id " +
                         "INNER JOIN movies p ON r.movie_id = p.movie_id " +
                         "WHERE r.user_id != ? " + 
                         "AND r.user_id NOT IN (SELECT followed_id FROM followers WHERE follower_id = ?) " +
                         "AND r.moderation_status IN ('APPROVED', 'SPOILER') " +
                         "AND r.user_id NOT IN (SELECT blocked_id FROM user_blocks WHERE blocker_id = ?) " +
                         "AND r.user_id NOT IN (SELECT blocker_id FROM user_blocks WHERE blocked_id = ?) " +
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
                    dto.setReviewId(rs.getInt("review_id"));
                    dto.setMovieId(rs.getInt("movie_id"));
                    dto.setUserId(rs.getInt("user_id"));
                    dto.setUsername(rs.getString("username"));
                    dto.setUserAvatar(rs.getString("profile_image"));
                    dto.setUserLevel(rs.getInt("user_level"));
                    dto.setPosterPath(rs.getString("poster_path"));
                    dto.setDateFormatted(rs.getTimestamp("created_at") != null ? sdf.format(rs.getTimestamp("created_at")) : "Reciente");
                    dto.setRating(rs.getDouble("rating"));
                    dto.setMovieTitle(rs.getString("movie_title"));
                    dto.setText(rs.getString("review_text"));
                    dto.setModerationStatus(rs.getString("moderation_status"));
                    dto.setFollowing(false);
                    feed.add(dto);
                }
            } catch (SQLException e) {
                throw ErrorFactory.internal("Error fetching paginated popular feed");
            }
            return feed;
        }
    
}