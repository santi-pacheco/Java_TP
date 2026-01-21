package service;

import entity.Movie;
import entity.ModerationStatus;
import entity.Review;
import repository.MovieRepository;
import repository.ReviewRepository;
import service.GeminiModerationService.ModerationResult;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ReviewModerationService {
    private static ReviewModerationService instance;
    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final GeminiModerationService geminiService;
    private final ExecutorService executorService;

    private ReviewModerationService() {
        this.reviewRepository = new ReviewRepository();
        this.movieRepository = new MovieRepository();
        this.geminiService = new GeminiModerationService();
        this.executorService = Executors.newFixedThreadPool(3);
    }

    public static synchronized ReviewModerationService getInstance() {
        if (instance == null) {
            instance = new ReviewModerationService();
        }
        return instance;
    }

    // Método principal: ejecuta moderación en segundo plano
    public void moderateReviewAsync(int reviewId) {
        executorService.submit(() -> {
            try {
                moderateReview(reviewId);
            } catch (Exception e) {
                System.err.println("Error al moderar reseña " + reviewId + ": " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void moderateReview(int reviewId) throws SQLException {
        Review review = reviewRepository.findOne(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Reseña no encontrada: " + reviewId);
        }

        Movie movie = movieRepository.findOne(review.getId_movie());
        if (movie == null) {
            throw new IllegalArgumentException("Película no encontrada: " + review.getId_movie());
        }

        ModerationResult result = geminiService.moderateReview(
            review.getReview_text(),
            movie.getSinopsis(),
            movie.getTitulo()
        );

        ModerationStatus newStatus = determineStatus(result);
        boolean updated = reviewRepository.updateModerationStatus(reviewId, newStatus, result.getReason());

        if (updated) {
            System.out.println("[MODERACIÓN] Reseña " + reviewId + " → " + newStatus + " | Razón: " + result.getReason());
        }
    }

    private ModerationStatus determineStatus(ModerationResult result) {
        if (result.hasOffensiveContent()) {
            return ModerationStatus.REJECTED;
        }
        if (result.hasSpoilers()) {
            return ModerationStatus.SPOILER;
        }
        return ModerationStatus.APPROVED;
    }

    public void shutdown() {
        System.out.println("Deteniendo ExecutorService de moderación...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
