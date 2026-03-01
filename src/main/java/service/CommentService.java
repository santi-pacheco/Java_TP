package service;

import java.sql.Timestamp;
import java.util.List;

import entity.ModerationStatus;
import entity.Movie;
import entity.Review;
import entity.ReviewComment;
import exception.BanException;
import exception.ErrorFactory;
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

        if (commentText == null || commentText.trim().isEmpty()) {
            throw ErrorFactory.badRequest("El texto del comentario no puede estar vacío.");
        }
        Review review = reviewRepository.findOne(reviewId);
        if (review == null) {
            throw ErrorFactory.notFound("La reseña a la que intentas comentar no existe.");
        }
        Movie movie = movieRepository.findOne(review.getMovieId());
        String movieTitle = movie != null ? movie.getOriginalTitle() : "";
        String moviePlot = movie != null ? movie.getSynopsis() : "";

        ReviewComment comment = new ReviewComment();
        comment.setUserId(userId);
        comment.setReviewId(reviewId);
        comment.setCommentText(commentText.trim());
        comment.setModerationStatus(ModerationStatus.PENDING_MODERATION);
        comment = commentRepository.add(comment);
        moderateAndSaveComment(comment, commentText, review.getReviewText(), moviePlot, movieTitle, userId, false); 
        return comment;
    }

    public ReviewComment editComment(int userId, int commentId, String newText) {
        checkUserBanStatus(userId);
        
        if (newText == null || newText.trim().isEmpty()) {
            throw ErrorFactory.badRequest("El texto no puede estar vacío");
        }
        ReviewComment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw ErrorFactory.notFound("El comentario que intentas editar no existe");
        }
        if (comment.getUserId() != userId) {
            throw ErrorFactory.forbidden("No tienes permiso para editar este comentario");
        }
        Review review = reviewRepository.findOne(comment.getReviewId());
        Movie movie = movieRepository.findOne(review.getMovieId());
        String movieTitle = movie != null ? movie.getOriginalTitle() : "";
        String moviePlot = movie != null ? movie.getSynopsis() : "";

        moderateAndSaveComment(comment, newText.trim(), review.getReviewText(), moviePlot, movieTitle, userId, true); 
        comment.setCommentText(newText.trim());
        return comment;
    }


    public void deleteOwnComment(int userId, int commentId) {
    	ReviewComment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw ErrorFactory.notFound("El comentario que intentas eliminar no existe.");
        }
        if (comment.getUserId() != userId) {
            throw ErrorFactory.forbidden("No tienes permiso para eliminar este comentario.");
        }
        commentRepository.delete(commentId);
    }

    private void moderateAndSaveComment(ReviewComment comment, String text, String reviewText, String moviePlot, String movieTitle, int userId, boolean isEdit) {
        try {
            ModerationResult result = moderationService.moderateComment(text, reviewText, moviePlot, movieTitle);
            String status = "APPROVED";  
            if (result.hasOffensiveContent()) {
                status = "REJECTED";
                userRepository.banUser(userId, BAN_DAYS); // Cuidado: asegúrate de que BAN_DAYS esté definido en tu clase
            } else if (result.hasSpoilers()) {
                status = "SPOILER";
            }
            if (isEdit) {
                commentRepository.updateTextAndStatus(comment.getCommentId(), text, status, result.getReason());
            } else {
                commentRepository.updateModerationStatus(comment.getCommentId(), status, result.getReason());
            } 
            comment.setModerationStatus(ModerationStatus.valueOf(status)); // Usar valueOf suele ser más estándar que fromString
            comment.setModerationReason(result.getReason()); 
        } catch (Exception e) {
            throw ErrorFactory.internal("Ocurrió un error al analizar el comentario con nuestra IA. Por favor, intenta de nuevo.");
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