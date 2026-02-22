package service;

import repository.ReviewLikeRepository;
import repository.UserRepository;
import exception.BanException;
import java.sql.Timestamp;

public class LikeService {
    private final ReviewLikeRepository likeRepository;
    private final UserRepository userRepository;

    public LikeService() {
        this.likeRepository = new ReviewLikeRepository();
        this.userRepository = new UserRepository();
    }

    public LikeResponse toggleLike(int userId, int reviewId) {
        
        boolean liked = likeRepository.toggleLike(userId, reviewId);
        int currentCount = likeRepository.getLikesCount(reviewId);
        
        return new LikeResponse(liked, currentCount);
    }

    public boolean hasUserLiked(int userId, int reviewId) {
        return likeRepository.existsLike(userId, reviewId);
    }

    public int getLikesCount(int reviewId) {
        return likeRepository.getLikesCount(reviewId);
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
