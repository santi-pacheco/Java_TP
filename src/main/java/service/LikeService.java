package service;

import repository.ReviewLikeRepository;
import repository.UserRepository;
import repository.ReviewRepository;
import repository.ConfiguracionReglasRepository;
import entity.Review;
import entity.ConfiguracionReglas;
import entity.User;

public class LikeService {
    private final ReviewLikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ConfiguracionReglasService configuracionService;

    public LikeService() {
        this.likeRepository = new ReviewLikeRepository();
        this.userRepository = new UserRepository();
        this.reviewRepository = new ReviewRepository();
        this.configuracionService = new ConfiguracionReglasService(new ConfiguracionReglasRepository());
    }

    public LikeResponse toggleLike(int userId, int reviewId) {
        
        boolean liked = likeRepository.toggleLike(userId, reviewId);
        int currentCount = likeRepository.getLikesCount(reviewId);
        
        updateAuthorVolume(userId, reviewId, liked);
        
        return new LikeResponse(liked, currentCount);
    }

    public boolean hasUserLiked(int userId, int reviewId) {
        return likeRepository.existsLike(userId, reviewId);
    }

    public int getLikesCount(int reviewId) {
        return likeRepository.getLikesCount(reviewId);
    }

    private void updateAuthorVolume(int actorId, int reviewId, boolean isAddingLike) {
        Review review = reviewRepository.findOne(reviewId);
        if (review == null) return;

        int authorId = review.getId_user();

        if (actorId != authorId) {
            int kcalsModifier = isAddingLike ? 500 : -500;
            
            User author = userRepository.findOne(authorId);
            if (author == null) return;

            int newKcals = author.getTotalKcals() + kcalsModifier;
            if (newKcals < 0) newKcals = 0;

            ConfiguracionReglas config = configuracionService.getConfiguracionReglas();
            int newLevel = 1;

            if (config != null) {
                if (newKcals >= config.getUmbralKcalsNivel4()) {
                    newLevel = 4;
                } else if (newKcals >= config.getUmbralKcalsNivel3()) {
                    newLevel = 3;
                } else if (newKcals >= config.getUmbralKcalsNivel2()) {
                    newLevel = 2;
                }
            }

            userRepository.updateUserVolume(authorId, newKcals, newLevel);
        }
    }

    public static class LikeResponse {
        private final boolean liked;
        private final int likesCount;

        public LikeResponse(boolean liked, int likesCount) {
            this.liked = liked;
            this.likesCount = likesCount;
        }

        public boolean isLiked() { return liked; }
        public int getLikesCount() { return likesCount; }
    }
}