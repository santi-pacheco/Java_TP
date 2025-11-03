package service;

import java.util.List;

import entity.Review;
import entity.User;
import entity.Movie;
import repository.ReviewRepository;
import service.UserService;
import service.MovieService;
import exception.ErrorFactory;

public class ReviewService {

    private ReviewRepository reviewRepository;
    private UserService userService;
    private MovieService movieService;

    public ReviewService(ReviewRepository reviewRepository, UserService userService, MovieService movieService) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.movieService = movieService;
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
        return reviewRepository.add(review);
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
        return reviewRepository.update(review);
    }

    public void deleteReview(int reviewId) {
        // 1. Verificar que la reseña existe
        Review existingReview = reviewRepository.findOne(reviewId);
        if (existingReview == null) {
            throw ErrorFactory.notFound("No se puede eliminar. Reseña con ID " + reviewId + " no encontrada");
        }

        // 2. Eliminar
        reviewRepository.delete(existingReview);
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
}
