package service;

import java.util.ArrayList;
import java.util.List;

import entity.Review;
import entity.User;
import entity.Movie;
import entity.ConfiguracionReglas;
import entity.FeedReviewDTO;
import entity.ModerationStatus;
import repository.ReviewRepository;
import repository.UserRepository;
import service.WatchlistService;
import exception.BanException;
import exception.ErrorFactory;

public class ReviewService {

    private ReviewRepository reviewRepository;
    private UserService userService;
    private MovieService movieService;
    private WatchlistService watchlistService;
    private ConfiguracionReglasService configuracionService;
    private final UserRepository userRepository;
    private static final int BAN_DAYS = 7;
    
    public ReviewService(ReviewRepository reviewRepository, UserService userService, MovieService movieService, ConfiguracionReglasService configuracionService, WatchlistService watchlistService) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.movieService = movieService;
        this.configuracionService = configuracionService;
        this.watchlistService = watchlistService;
        this.userRepository = new UserRepository();
    }

    public Review createReview(Review review) {
        checkUserBanStatus(review.getId_user());
        
        Movie movie = movieService.getMovieById(review.getId_movie());

        if (reviewRepository.existsByUserAndMovie(review.getId_user(), review.getId_movie())) {
            throw ErrorFactory.duplicate("Ya tienes una reseña para esta película. Puedes editarla en lugar de crear una nueva");
        }

        if (review.getRating() < 0.0 || review.getRating() > 5.0) {
            throw ErrorFactory.validation("El rating debe estar entre 0.0 y 5.0");
        }

        if (review.getReview_text() == null || review.getReview_text().trim().isEmpty()) {
            throw ErrorFactory.validation("El texto de la reseña es requerido");
        }

        if (review.getReview_text().trim().length() < 10) {
            throw ErrorFactory.validation("La reseña debe tener al menos 10 caracteres");
        }

        review.setModerationStatus(ModerationStatus.PENDING_MODERATION);

        Review savedReview = reviewRepository.add(review);
        
        movieService.updateReviewStats(review.getId_movie());
        
        if (watchlistService.getWatchlist(review.getId_user()).getMovies().contains(String.valueOf(movie.getId()))) {
            watchlistService.removeMovie(review.getId_user(), String.valueOf(movie.getId()));
        }
        
        return savedReview;
    }

    public Review getReviewById(int id) {
        Review review = reviewRepository.findOne(id);
        if (review == null) {
            throw ErrorFactory.notFound("Reseña no encontrada con ID: " + id);
        }
        return review;
    }

    public Review getReviewByUserAndMovie(int userId, int movieId) {
        return reviewRepository.findByUserAndMovie(userId, movieId);
    }

    public Review updateReview(Review review) {
        checkUserBanStatus(review.getId_user());
        
        Review existingReview = reviewRepository.findOne(review.getId());
        if (existingReview == null) {
            throw ErrorFactory.notFound("No se puede actualizar. Reseña con ID " + review.getId() + " no encontrada");
        }

        if (review.getRating() < 0.0 || review.getRating() > 5.0) {
            throw ErrorFactory.validation("El rating debe estar entre 0.0 y 5.0");
        }

        if (review.getReview_text() == null || review.getReview_text().trim().isEmpty()) {
            throw ErrorFactory.validation("El texto de la reseña es requerido");
        }

        if (review.getReview_text().trim().length() < 10) {
            throw ErrorFactory.validation("La reseña debe tener al menos 10 caracteres");
        }

        review.setModerationStatus(ModerationStatus.PENDING_MODERATION);

        Review updatedReview = reviewRepository.update(review);
        
        movieService.updateReviewStats(review.getId_movie());
        
        return updatedReview;
    }

    public void deleteReview(int reviewId) {
        Review existingReview = reviewRepository.findOne(reviewId);
        if (existingReview == null) {
            throw ErrorFactory.notFound("No se puede eliminar. Reseña con ID " + reviewId + " no encontrada");
        }

        reviewRepository.delete(existingReview);
        movieService.updateReviewStats(existingReview.getId_movie());
    }

    public List<Review> getReviewsByMovie(int movieId, int idLector) {
        return reviewRepository.findByMovie(movieId, idLector);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getReviewsByUser(int userId) {
        return reviewRepository.findByUser(userId);
    }

    public boolean hasUserReviewedMovie(int userId, int movieId) {
        return reviewRepository.existsByUserAndMovie(userId, movieId);
    }

    public Review createOrUpdateReview(Review review) {

        Review existingReview = reviewRepository.findAnyByUserAndMovie(review.getId_user(), review.getId_movie());
        
        if (existingReview != null) {
            review.setId(existingReview.getId());
            return updateReview(review); 
        } else {
            return createReview(review);
        }
    }
    
    public boolean updateModerationStatus(int reviewId, ModerationStatus status, String reason) {
        Review review = reviewRepository.findOne(reviewId);
        if (review == null) {
            throw ErrorFactory.notFound("Reseña no encontrada con ID: " + reviewId);
        }
        
        try {
            boolean updated = reviewRepository.updateModerationStatus(reviewId, status, reason);
            if (updated) {
                movieService.updateReviewStats(review.getId_movie());
            }
            return updated;
        } catch (Exception e) {
            throw ErrorFactory.internal("Error al actualizar estado de moderación");
        }
    }
    
    private void checkUserBanStatus(int userId) {
        java.sql.Timestamp bannedUntil = userRepository.getBannedUntil(userId);
        if (bannedUntil != null && bannedUntil.after(new java.sql.Timestamp(System.currentTimeMillis()))) {
            throw new BanException(bannedUntil);
        }
    }
    
    
    public List<Review> getReviewsByMovieSortedByLikes(int movieId, int idLector) {
        return reviewRepository.findByMovieSortedByLikes(movieId, idLector);
    }
    
    public List<FeedReviewDTO> getGlobalFeedPaginated(int userId, int offset, int limit) {
        List<FeedReviewDTO> result = new ArrayList<>();
        int friendsCount = reviewRepository.countFriendsReviews(userId);

        if (offset < friendsCount) {
            List<FeedReviewDTO> friendsReviews = reviewRepository.getFriendsFeedPaginated(userId, offset, limit);
            result.addAll(friendsReviews);
            if (result.size() < limit) {
                int needed = limit - result.size();
                List<FeedReviewDTO> popularReviews = reviewRepository.getPopularFeedPaginated(userId, 0, needed);
                result.addAll(popularReviews);
            }
        } else {
            int popularOffset = offset - friendsCount;
            List<FeedReviewDTO> popularReviews = reviewRepository.getPopularFeedPaginated(userId, popularOffset, limit);
            result.addAll(popularReviews);
        }
        
        return result;
    }
}