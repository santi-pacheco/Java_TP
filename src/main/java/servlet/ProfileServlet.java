package servlet;

import java.io.IOException;
import repository.BlockRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Review;
import entity.User;
import entity.Movie;
import entity.SystemSettings;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import controller.UserController;
import controller.ReviewController;
import service.UserService;
import service.WatchlistService;
import service.ReviewService;
import service.MovieService;
import service.SystemSettingsService;
import repository.UserRepository;
import repository.WatchlistRepository;
import repository.ReviewRepository;
import repository.MovieRepository;
import repository.FollowRepository;
import repository.SystemSettingsRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import exception.AppException;
import exception.ErrorFactory;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    
    private UserController userController;
    private ReviewController reviewController;
    private SystemSettingsService configuracionReglasService;
    private MovieService movieService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        UserRepository userRepository = new UserRepository();
        ReviewRepository reviewRepository = new ReviewRepository();
        MovieRepository movieRepository = new MovieRepository();
        FollowRepository followRepository = new FollowRepository();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        SystemSettingsRepository configuracionReglasRepository = new SystemSettingsRepository();
        this.configuracionReglasService = new SystemSettingsService(configuracionReglasRepository);
        this.movieService = new MovieService(movieRepository);
        BlockRepository blockRepository = new BlockRepository();
        UserService userService = new UserService(userRepository, encoder, followRepository, blockRepository);
        WatchlistRepository watchlistRepository = new WatchlistRepository(movieRepository);
        WatchlistService watchlistService = new WatchlistService(watchlistRepository, userService, movieService);
        ReviewService reviewService = new ReviewService(reviewRepository, userService, movieService, configuracionReglasService, watchlistService);
        
        this.userController = new UserController(userService);
        this.reviewController = new ReviewController(reviewService);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedUser = (User) session.getAttribute("usuarioLogueado");
        String idParam = request.getParameter("id");
        User profileUser = null;
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int idToView = Integer.parseInt(idParam);
                profileUser = userController.getUserById(idToView);
            } catch (NumberFormatException | AppException e) {
                System.out.println("ID inválido o usuario no encontrado: " + idParam + ". Mostrando perfil propio.");
            }
        }
        if (profileUser == null) {
            profileUser = loggedUser;
        }
        boolean isMyProfile = (loggedUser.getUserId() == profileUser.getUserId());
        boolean isFollowing = false;

        if (!isMyProfile) {
            boolean loBloquee = userController.isBlocking(loggedUser.getUserId(), profileUser.getUserId());
            boolean meBloqueo = userController.isBlocking(profileUser.getUserId(), loggedUser.getUserId());
            if (loBloquee || meBloqueo) {
                throw ErrorFactory.notFound("Usuario no encontrado");
            }
            isFollowing = userController.checkFollowStatus(loggedUser.getUserId(), profileUser.getUserId());
        }
        List<Review> userReviews = reviewController.getReviewsByUser(profileUser.getUserId());  
        List<User> followersList = userController.getFollowers(profileUser.getUserId());
        List<User> followingList = userController.getFollowing(profileUser.getUserId());
        int realFollowersCount = followersList.size();
        int realFollowingCount = followingList.size();
        
        if (loggedUser != null) {
            followersList.removeIf(u -> 
                userController.isBlocking(loggedUser.getUserId(), u.getUserId()) || 
                userController.isBlocking(u.getUserId(), loggedUser.getUserId())
            );
            followingList.removeIf(u -> 
                userController.isBlocking(loggedUser.getUserId(), u.getUserId()) || 
                userController.isBlocking(u.getUserId(), loggedUser.getUserId())
            );
        }
        request.setAttribute("followers", followersList);
        request.setAttribute("following", followingList);
        request.setAttribute("realFollowersCount", realFollowersCount);
        request.setAttribute("realFollowingCount", realFollowingCount);
        if (isMyProfile) {
            List<User> blockedList = userController.getBlockedUsers(loggedUser.getUserId());
            request.setAttribute("blockedUsers", blockedList);
        } 
        Map<String, Integer> ratingDistribution = calculateRatingDistribution(userReviews);
        SystemSettings config = configuracionReglasService.getSystemSettings();
        int userKcals = profileUser.getTotalKcals();
        int userLevel = profileUser.getUserLevel();
        int nextLevelMax = config.getKcalsToLevel2();
        int currentLevelMin = 0;
        if (userLevel == 2) {
            currentLevelMin = config.getKcalsToLevel2();
            nextLevelMax = config.getKcalsToLevel3();
        } else if (userLevel == 3) {
            currentLevelMin = config.getKcalsToLevel3();
            nextLevelMax = config.getKcalsToLevel4();
        } else if (userLevel >= 4) {
            currentLevelMin = config.getKcalsToLevel4();
            nextLevelMax = config.getKcalsToLevel4();
        }
        int progressPercentage = 100;
        if (userLevel < 4) {
            progressPercentage = (int) (((float) (userKcals - currentLevelMin) / (nextLevelMax - currentLevelMin)) * 100);
        }
        progressPercentage = Math.max(0, Math.min(100, progressPercentage));

        Movie platoPrincipalMovie = null;
        if (profileUser.getMainDishMovieId() != null) {
            try {
                platoPrincipalMovie = movieService.getMovieById(profileUser.getMainDishMovieId());
            } catch (Exception e) {
                System.err.println("Error fetching plato principal: " + e.getMessage());
            }
        }
        request.setAttribute("userLevel", userLevel);
        request.setAttribute("userKcals", userKcals);
        request.setAttribute("nextLevelMax", nextLevelMax);
        request.setAttribute("progressPercentage", progressPercentage);
        request.setAttribute("platoPrincipalMovie", platoPrincipalMovie);
        request.setAttribute("user", profileUser);
        request.setAttribute("loggedUser", loggedUser);
        request.setAttribute("isMyProfile", isMyProfile);
        request.setAttribute("isFollowing", isFollowing);
        request.setAttribute("totalReviews", userReviews.size());
        request.setAttribute("recentReviews", userReviews.size() > 5 ? userReviews.subList(0, 5) : userReviews);
        request.setAttribute("ratingDistribution", ratingDistribution); 
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }

    private Map<String, Integer> calculateRatingDistribution(List<Review> reviews) {
        Map<String, Integer> distribution = new HashMap<>();
        double[] ratings = {0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0};
        for (double r : ratings) distribution.put(String.valueOf(r), 0);
        
        for (Review r : reviews) {
            double rating = Math.round(r.getRating() * 2) / 2.0;
            String key = String.valueOf(rating);
            distribution.put(key, distribution.getOrDefault(key, 0) + 1);
        }
        return distribution;
    }
}