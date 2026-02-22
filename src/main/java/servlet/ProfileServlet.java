package servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Review;
import entity.User;
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
import service.ConfiguracionReglasService;
import repository.UserRepository;
import repository.WatchlistRepository;
import repository.ReviewRepository;
import repository.MovieRepository;
import repository.FollowRepository;
import repository.ConfiguracionReglasRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import exception.AppException;
import exception.ErrorFactory;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
	
	private UserController userController;
    private ReviewController reviewController;
    
	@Override
    public void init() throws ServletException {
        super.init();
        
        
        UserRepository userRepository = new UserRepository();
        ReviewRepository reviewRepository = new ReviewRepository();
        MovieRepository movieRepository = new MovieRepository();
        FollowRepository followRepository = new FollowRepository();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        ConfiguracionReglasRepository configuracionReglasRepository = new ConfiguracionReglasRepository();
        ConfiguracionReglasService configuracionReglasService = new ConfiguracionReglasService(configuracionReglasRepository);
        MovieService movieService = new MovieService(movieRepository);
        UserService userService = new UserService(userRepository, encoder, followRepository);
        WatchlistRepository watchlistRepository = new WatchlistRepository(movieRepository);
        WatchlistService watchlistService = new WatchlistService(watchlistRepository, userService, movieService);
        ReviewService reviewService = new ReviewService(reviewRepository, userService, movieService, configuracionReglasService, watchlistService);
        
        this.userController = new UserController(userService);
        this.reviewController = new ReviewController(reviewService);
    }
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = request.getSession(false);
            User loggedUser = (session != null) ? (User) session.getAttribute("usuarioLogueado") : null;
            
            if (loggedUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String idParam = request.getParameter("id");
            User profileUser = null;

            if (idParam != null && !idParam.isEmpty()) {
                try {
                    int idToView = Integer.parseInt(idParam);
                    profileUser = userController.getUserById(idToView);
                } catch (Exception e) {
                    System.out.println("No se encontró usuario con ID: " + idParam + ". Mostrando perfil propio.");
                    profileUser = loggedUser; 
                }
            }
            
            if (profileUser == null) profileUser = loggedUser;

            boolean isMyProfile = (loggedUser.getId() == profileUser.getId());
            boolean isFollowing = false;

            if (!isMyProfile) {
                isFollowing = userController.checkFollowStatus(loggedUser.getId(), profileUser.getId());
            }

            List<Review> userReviews = reviewController.getReviewsByUser(profileUser.getId());
            
            List<User> followersList = userController.getFollowers(profileUser.getId());
            List<User> followingList = userController.getFollowing(profileUser.getId());
            request.setAttribute("followers", followersList);
            request.setAttribute("following", followingList);
            
            Map<String, Integer> ratingDistribution = calculateRatingDistribution(userReviews);

            request.setAttribute("user", profileUser);
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("isMyProfile", isMyProfile);
            request.setAttribute("isFollowing", isFollowing);
            request.setAttribute("totalReviews", userReviews.size());
            request.setAttribute("recentReviews", userReviews.size() > 5 ? userReviews.subList(0, 5) : userReviews);
            request.setAttribute("ratingDistribution", ratingDistribution);
            
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);

        } catch (AppException e) {
            throw e; 
        } catch (Exception e) {
            throw ErrorFactory.internal("Error inesperado cargando el perfil: " + e.getMessage());
        }
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