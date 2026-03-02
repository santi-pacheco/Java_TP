package service;

import entity.Movie;
import entity.ModerationStatus;
import entity.Review;
import repository.MovieRepository;
import repository.ReviewRepository;
import repository.UserRepository;
import service.GeminiModerationService.ModerationResult;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ReviewModerationService {
    private static ReviewModerationService instance;
    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository; 
    private final GeminiModerationService geminiService;
    private final ExecutorService executorService;
    
    private static final int BAN_DAYS = 7;

    private ReviewModerationService() {
        this.reviewRepository = new ReviewRepository();
        this.movieRepository = new MovieRepository();
        this.userRepository = new UserRepository(); 
        this.geminiService = new GeminiModerationService();
        this.executorService = Executors.newFixedThreadPool(3);
    }

    public static synchronized ReviewModerationService getInstance() {
        if (instance == null) {
            instance = new ReviewModerationService();
        }
        return instance;
    }

    public void moderateReviewAsync(int reviewId) {
        executorService.submit(() -> {
            try {
                moderateReview(reviewId);
            } catch (Exception e) {
                // Fallo silencioso en el hilo asíncrono
            }
        });
    }

    private void moderateReview(int reviewId) throws SQLException {
        Review review = reviewRepository.findOne(reviewId);
        if (review == null) return;

        Movie movie = movieRepository.findOne(review.getMovieId());
        if (movie == null) return;

        ModerationResult result = geminiService.moderateReview(
            review.getReviewText(),
            movie.getSynopsis(),
            movie.getTitle()
        );

        ModerationStatus newStatus = determineStatus(result);
       
        if (result.getReason() != null && result.getReason().toUpperCase().contains("REJECT")) {
            newStatus = ModerationStatus.REJECTED;
        }

        boolean updated = reviewRepository.updateModerationStatus(reviewId, newStatus, result.getReason());

        if (updated && newStatus == ModerationStatus.REJECTED) {
            try {
                userRepository.banUser(review.getUserId(), BAN_DAYS);
            } catch (Exception e) {
                // Evitamos que un error al banear rompa el flujo
            }
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