package controller;

import java.util.List;

import entity.Review;
import service.ReviewService;

public class ReviewController {

    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public Review createReview(Review review) {
        return reviewService.createReview(review);
    }

    public Review getReviewById(int id) {
        return reviewService.getReviewById(id);
    }

    public Review getReviewByUserAndMovie(int userId, int movieId) {
        return reviewService.getReviewByUserAndMovie(userId, movieId);
    }

    public Review updateReview(Review review) {
        return reviewService.updateReview(review);
    }

    public void deleteReview(int reviewId) {
        reviewService.deleteReview(reviewId);
    }

    public List<Review> getReviewsByMovie(int movieId) {
        return reviewService.getReviewsByMovie(movieId);
    }

    public boolean hasUserReviewedMovie(int userId, int movieId) {
        return reviewService.hasUserReviewedMovie(userId, movieId);
    }

    // Método principal para el caso de uso
    public Review createOrUpdateReview(Review review) {
        return reviewService.createOrUpdateReview(review);
    }

    // Métodos para administradores
    public List<Review> getPendingSpoilerReviews() {
        return reviewService.getPendingSpoilerReviews();
    }

    public void updateSpoilerStatus(int reviewId, boolean containsSpoiler) {
        reviewService.updateSpoilerStatus(reviewId, containsSpoiler);
    }
}
