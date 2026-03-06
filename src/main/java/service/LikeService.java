package service;

import repository.ReviewLikeRepository;
import repository.UserRepository;
import repository.ReviewRepository;
import repository.SystemSettingsRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import entity.Review;
import entity.SystemSettings;
import entity.User;
import exception.ErrorFactory;

public class LikeService {
    private final ReviewLikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final SystemSettingsService configuracionService;
    private final EmailService emailService;
    
    public LikeService() {
        this.likeRepository = new ReviewLikeRepository();
        this.userRepository = new UserRepository();
        this.reviewRepository = new ReviewRepository();
        this.configuracionService = new SystemSettingsService(new SystemSettingsRepository());
        this.emailService = new EmailService();
    }

    public LikeResponse toggleLike(int userId, int reviewId) {
        if (userId <= 0 || reviewId <= 0) {
            throw ErrorFactory.badRequest("IDs de usuario o reseña inválidos.");
        }
        
        boolean liked = likeRepository.toggleLike(userId, reviewId);
        int currentCount = likeRepository.getLikesCount(reviewId);
        
        updateAuthorVolume(userId, reviewId, liked);
        
        return new LikeResponse(liked, currentCount);
    }

    public boolean hasUserLiked(int userId, int reviewId) {
        if (userId <= 0 || reviewId <= 0) return false;
        return likeRepository.existsLike(userId, reviewId);
    }

    public int getLikesCount(int reviewId) {
        if (reviewId <= 0) return 0;
        return likeRepository.getLikesCount(reviewId);
    }

    private void updateAuthorVolume(int actorId, int reviewId, boolean isAddingLike) {
        Review review = reviewRepository.findOne(reviewId);
        if (review == null) return;

        int authorId = review.getUserId();

        // El autor no gana/pierde kcals por darse like a sí mismo
        if (actorId != authorId) {
            int kcalsModifier = isAddingLike ? 500 : -500;
            
            User author = userRepository.findOne(authorId);
            if (author == null) return;

            int newKcals = author.getTotalKcals() + kcalsModifier;
            int notifiedLevel = author.getNotifiedLevel();
            if (newKcals < 0) newKcals = 0;

            SystemSettings config = configuracionService.getSystemSettings();
            int newLevel = 1;

            if (config != null) {
                if (newKcals >= config.getKcalsToLevel4()) {
                    newLevel = 4;
                } else if (newKcals >= config.getKcalsToLevel3()) {
                    newLevel = 3;
                } else if (newKcals >= config.getKcalsToLevel2()) {
                    newLevel = 2;
                }
            }

            userRepository.updateUserVolume(authorId, newKcals, newLevel);
            
            if (newLevel > notifiedLevel) {
                enviarEmailSubidaNivel(author, newLevel);
            }
        }
    }
    
    private void enviarEmailSubidaNivel(User author, int newLevel) {
        CompletableFuture.runAsync(() -> {
            try {
            	String htmlBody = leerPlantillaHtml("/templates/level-up.html");	
                String baseUrl = "http://localhost:8080/FatMovies";
                
                htmlBody = htmlBody.replace("{{BASE_URL}}", baseUrl);
                htmlBody = htmlBody.replace("{{USERNAME}}", author.getUsername());
                htmlBody = htmlBody.replace("{{NEW_LEVEL}}", String.valueOf(newLevel)); 
                String titulo = "¡Haz subido de nivel!";
                if (newLevel == 4) titulo = "👑 ¡Llegaste a Crítico Michelin!";
                
                String subject = "¡Felicidades " + author.getUsername() + "! " + titulo + " 🍿";
                emailService.sendEmail(author.getEmail(), subject, htmlBody);
                
                System.out.println("Email de Level Up enviado exitosamente a: " + author.getEmail());
            } catch (Exception e) {
            	System.err.println("Error enviando email de subida de nivel: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private String leerPlantillaHtml(String ruta) throws IOException {
        InputStream is = getClass().getResourceAsStream(ruta);
        
        if (is == null) {
            throw new IOException("No se encontró la plantilla en: " + ruta);
        }
        try (java.util.Scanner scanner = new java.util.Scanner(is, java.nio.charset.StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        }
    }
    
    //Clase interna para devolver la respuesta
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