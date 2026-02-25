package service;

import java.sql.Timestamp;
import java.util.List;

import entity.ModerationStatus;
import entity.Movie;
import entity.Review;
import entity.ReviewComment;
import exception.BanException;
import repository.CommentRepository;
import repository.MovieRepository;
import repository.ReviewRepository;
import repository.UserRepository;
import service.GeminiModerationService.ModerationResult;

public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final GeminiModerationService moderationService;

    private static final int BAN_DAYS = 7;

    public CommentService() {
        this.commentRepository = new CommentRepository();
        this.userRepository = new UserRepository();
        this.reviewRepository = new ReviewRepository();
        this.movieRepository = new MovieRepository();
        this.moderationService = new GeminiModerationService();
    }

    public ReviewComment createComment(int userId, int reviewId, String commentText) {
        checkUserBanStatus(userId);
        if (commentText == null || commentText.trim().isEmpty()) throw new IllegalArgumentException("El texto del comentario es requerido");
        
        Review review = reviewRepository.findOne(reviewId);
        if (review == null) throw new IllegalArgumentException("Review no encontrada");

        Movie movie = movieRepository.findOne(review.getId_movie());
        String movieTitle = movie != null ? movie.getTituloOriginal() : "";
        String moviePlot = movie != null ? movie.getSinopsis() : "";

        ReviewComment comment = new ReviewComment();
        comment.setIdUsuario(userId);
        comment.setIdReview(reviewId);
        comment.setCommentText(commentText.trim());
        comment.setModerationStatus(ModerationStatus.PENDING_MODERATION);

        comment = commentRepository.add(comment);
        moderateAndSaveComment(comment, commentText, review.getReview_text(), moviePlot, movieTitle, userId, false);
        return comment;
    }


    public ReviewComment editComment(int userId, int commentId, String newText) {
        checkUserBanStatus(userId);
        if (newText == null || newText.trim().isEmpty()) throw new IllegalArgumentException("El texto no puede estar vacío");

        ReviewComment comment = commentRepository.findById(commentId);
        if (comment == null || comment.getIdUsuario() != userId) throw new IllegalArgumentException("No tienes permiso para editar este comentario");

        Review review = reviewRepository.findOne(comment.getIdReview());
        Movie movie = movieRepository.findOne(review.getId_movie());
        String movieTitle = movie != null ? movie.getTituloOriginal() : "";
        String moviePlot = movie != null ? movie.getSinopsis() : "";

        moderateAndSaveComment(comment, newText.trim(), review.getReview_text(), moviePlot, movieTitle, userId, true);
        comment.setCommentText(newText.trim());
        return comment;
    }

    // NUEVO: Eliminar propio comentario
    public void deleteOwnComment(int userId, int commentId) {
        ReviewComment comment = commentRepository.findById(commentId);
        if (comment != null && comment.getIdUsuario() == userId) {
            commentRepository.delete(commentId);
        } else {
            throw new IllegalArgumentException("No tienes permiso para eliminar este comentario");
        }
    }

    private void moderateAndSaveComment(ReviewComment comment, String text, String reviewText, String moviePlot, String movieTitle, int userId, boolean isEdit) {
        try {
            ModerationResult result = moderationService.moderateComment(text, reviewText, moviePlot, movieTitle);
            String status = "APPROVED";
            
            if (result.hasOffensiveContent()) {
                status = "REJECTED";
                userRepository.banUser(userId, BAN_DAYS);
            } else if (result.hasSpoilers()) {
                status = "SPOILER";
            }

            if (isEdit) {
                commentRepository.updateTextAndStatus(comment.getIdComment(), text, status, result.getReason());
            } else {
                commentRepository.updateModerationStatus(comment.getIdComment(), status, result.getReason());
            }
            comment.setModerationStatus(ModerationStatus.fromString(status));
            comment.setModerationReason(result.getReason());
            
        } catch (Exception e) {
            System.err.println("Moderation failed: " + e.getMessage());
        }
    }

    public List<ReviewComment> getCommentsByReview(int reviewId, int loggedUserId) {
        return commentRepository.findByReviewId(reviewId, loggedUserId);
    }

    private void checkUserBanStatus(int userId) {
        Timestamp bannedUntil = userRepository.getBannedUntil(userId);
        if (bannedUntil != null && bannedUntil.after(new Timestamp(System.currentTimeMillis()))) {
            throw new BanException(bannedUntil);
        }
    }
}