package service;

import java.util.List;

import entity.Review;
import entity.User;
import entity.Movie;
import entity.ConfiguracionReglas;
import repository.ReviewRepository;
import service.UserService;
import service.MovieService;
import service.ConfiguracionReglasService;
import exception.ErrorFactory;

public class ReviewService {

    private ReviewRepository reviewRepository;
    private UserService userService;
    private MovieService movieService;
    private ConfiguracionReglasService configuracionService;

    public ReviewService(ReviewRepository reviewRepository, UserService userService, MovieService movieService, ConfiguracionReglasService configuracionService) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.movieService = movieService;
        this.configuracionService = configuracionService;
    }

    public Review createReview(Review review) {
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

        // 4. Asegurar que contieneSpoiler sea null al crear (para revisión posterior)
        review.setContieneSpoiler(null);

        // 5. Guardar la reseña
        Review savedReview = reviewRepository.add(review);
        
        // 6. Actualizar estadísticas de la película
        movieService.updateReviewStats(review.getId_movie());
        
        // 7. Verificar y actualizar estado activo del usuario
        checkUserActiveStatus(review.getId_user());
        
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

        // 3. Al actualizar, resetear el estado de spoiler para nueva revisión
        review.setContieneSpoiler(null);

        // 4. Actualizar
        Review updatedReview = reviewRepository.update(review);
        
        // 5. Actualizar estadísticas de la película
        movieService.updateReviewStats(review.getId_movie());
        
        return updatedReview;
    }

    public void deleteReview(int reviewId) {
        // 1. Verificar que la reseña existe
        Review existingReview = reviewRepository.findOne(reviewId);
        if (existingReview == null) {
            throw ErrorFactory.notFound("No se puede eliminar. Reseña con ID " + reviewId + " no encontrada");
        }

        // 2. Eliminar
        reviewRepository.delete(existingReview);
        
        // 3. Actualizar estadísticas de la película
        movieService.updateReviewStats(existingReview.getId_movie());
        
        // 4. Verificar y actualizar estado activo del usuario
        validateUserActiveStatusOnDelete(existingReview.getId_user());
    }

    public List<Review> getReviewsByMovie(int movieId) {
        return reviewRepository.findByMovie(movieId);
    }

    public boolean hasUserReviewedMovie(int userId, int movieId) {
        return reviewRepository.existsByUserAndMovie(userId, movieId);
    }

    // Métodos para Validacion de Spoilers
    public List<Review> getPendingSpoilerReviews() {
        return reviewRepository.findPendingSpoilerReviews();
    }

    public void updateSpoilerStatus(int reviewId, boolean containsSpoiler) {
        // Verificar que la reseña existe
        Review existingReview = reviewRepository.findOne(reviewId);
        if (existingReview == null) {
            throw ErrorFactory.notFound("Reseña con ID " + reviewId + " no encontrada");
        }

        reviewRepository.updateSpoilerStatus(reviewId, containsSpoiler);
    }

    // Método para el caso de uso principal: crear o actualizar reseña
    public Review createOrUpdateReview(Review review) {
        // Verificar si ya existe una reseña del usuario para esta película
        Review existingReview = reviewRepository.findByUserAndMovie(review.getId_user(), review.getId_movie());
        
        if (existingReview != null) {
            // Si existe, actualizar la reseña existente
            review.setId(existingReview.getId());
            return updateReview(review);
        } else {
            // Si no existe, crear nueva reseña
            return createReview(review);
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
}
