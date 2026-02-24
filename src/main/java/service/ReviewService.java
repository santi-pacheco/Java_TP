package service;

import java.util.List;

import entity.Review;
import entity.User;
import entity.Movie;
import entity.ConfiguracionReglas;
import entity.ModerationStatus;
import repository.ReviewRepository;
import repository.UserRepository;
import service.GeminiModerationService.ModerationResult;
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
        // 0. Checkear si el usuario esta baneado
        checkUserBanStatus(review.getId_user());
        
        // 1. Verificar que la película existe
        Movie movie = movieService.getMovieById(review.getId_movie());

        // 2. Verificar que no existe una reseña previa del mismo usuario para la misma película
        if (reviewRepository.existsByUserAndMovie(review.getId_user(), review.getId_movie())) {
            throw ErrorFactory.duplicate("Ya tienes una reseña para esta película. Puedes editarla en lugar de crear una nueva");
        }

        // 3. Validaciones de negocio adicionales
        if (review.getRating() < 0.0 || review.getRating() > 5.0) {
            throw ErrorFactory.validation("El rating debe estar entre 0.0 y 5.0");
        }

        if (review.getReview_text() == null || review.getReview_text().trim().isEmpty()) {
            throw ErrorFactory.validation("El texto de la reseña es requerido");
        }

        if (review.getReview_text().trim().length() < 10) {
            throw ErrorFactory.validation("La reseña debe tener al menos 10 caracteres");
        }

        // 4. Asegurar que el moderationStatus esté en PENDING_MODERATION al crear
        review.setModerationStatus(ModerationStatus.PENDING_MODERATION);

        // 5. Guardar la reseña
        Review savedReview = reviewRepository.add(review);
        
        // 6. Actualizar estadísticas de la película
        movieService.updateReviewStats(review.getId_movie());
        
        // 7. Verificar y actualizar estado activo del usuario
        checkUserActiveStatus(review.getId_user());
        
        //8. Si el usuario tiene la película en su watchlist, removerla
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
    	// 0. Checkear si el usuario esta baneado
        checkUserBanStatus(review.getId_user());
        
        // 1. Verificar que la reseña existe
        Review existingReview = reviewRepository.findOne(review.getId());
        if (existingReview == null) {
            throw ErrorFactory.notFound("No se puede actualizar. Reseña con ID " + review.getId() + " no encontrada");
        }

        // 2. Validaciones de negocio
        if (review.getRating() < 0.0 || review.getRating() > 5.0) {
            throw ErrorFactory.validation("El rating debe estar entre 0.0 y 5.0");
        }

        if (review.getReview_text() == null || review.getReview_text().trim().isEmpty()) {
            throw ErrorFactory.validation("El texto de la reseña es requerido");
        }

        if (review.getReview_text().trim().length() < 10) {
            throw ErrorFactory.validation("La reseña debe tener al menos 10 caracteres");
        }

        // 3. Al actualizar, resetear el estado de moderacion para nueva revisión
        review.setModerationStatus(ModerationStatus.PENDING_MODERATION);

        // 4. Actualizar
        Review updatedReview = reviewRepository.update(review);
        
        // 5. Actualizar estadísticas de la película
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
        validateUserActiveStatusOnDelete(existingReview.getId_user());
    }

    public List<Review> getReviewsByMovie(int movieId) {
        return reviewRepository.findByMovie(movieId);
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
    
    private void checkUserActiveStatus(int userId) {
        ConfiguracionReglas config = configuracionService.getConfiguracionReglas();
        if (config != null) {
            User user = userService.getUserById(userId);
            if (!user.isEsUsuarioActivo()) {
                int reviewCount = reviewRepository.countReviewsByUser(userId);
                if (reviewCount >= config.getUmbralResenasActivo()) {
                    userService.updateUser(createActiveUser(user));
                }
            }
        }
    }
    
    private void validateUserActiveStatusOnDelete(int userId) {
        ConfiguracionReglas config = configuracionService.getConfiguracionReglas();
        if (config != null) {
            User user = userService.getUserById(userId);
            if (user.isEsUsuarioActivo()) {
                int reviewCount = reviewRepository.countReviewsByUser(userId);
                if (reviewCount < config.getUmbralResenasActivo()) {
                    userService.updateUser(createInactiveUser(user));
                }
            }
        }
    }
    
    private User createActiveUser(User user) {
        user.setEsUsuarioActivo(true);
        return user;
    }
    
    private User createInactiveUser(User user) {
        user.setEsUsuarioActivo(false);
        return user;
    }
    
    private void checkUserBanStatus(int userId) {
        java.sql.Timestamp bannedUntil = userRepository.getBannedUntil(userId);
        if (bannedUntil != null && bannedUntil.after(new java.sql.Timestamp(System.currentTimeMillis()))) {
            throw new BanException(bannedUntil);
        }
    }
    
    
    public List<Review> getReviewsByMovieSortedByLikes(int movieId) {
        return reviewRepository.findByMovieSortedByLikes(movieId);
    }
}
