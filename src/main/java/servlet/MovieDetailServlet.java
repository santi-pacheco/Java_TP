package servlet;

import java.io.IOException;
import repository.BlockRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import controller.MovieController;
import controller.WatchlistController;
import controller.ReviewController;
import entity.Movie;
import entity.Person;
import entity.ActorWithCharacter;
import entity.Country;
import entity.User;
import entity.Review;
import repository.MovieRepository;
import repository.PersonRepository;
import repository.WatchlistRepository;
import repository.UserRepository;
import repository.ReviewRepository;
import service.MovieService;
import service.PersonService;
import service.WatchlistService;
import service.UserService;
import service.ReviewService;
import service.LikeService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import repository.FollowRepository;

public class MovieDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private MovieController movieController;
    private PersonService personService;
    private WatchlistController watchlistController;
    private ReviewController reviewController;
    private controller.SystemSettingsController configController;
    private LikeService likeService;
    private UserService userService; 
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            MovieRepository movieRepository = new MovieRepository();
            MovieService movieService = new MovieService(movieRepository);
            this.movieController = new MovieController(movieService);
            
            PersonRepository personRepository = new PersonRepository();
            this.personService = new PersonService(personRepository);
            
            UserRepository userRepository = new UserRepository();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            FollowRepository followRepository = new FollowRepository();
            BlockRepository blockRepository = new BlockRepository();
            this.userService = new UserService(userRepository, passwordEncoder, followRepository, blockRepository);
            
            WatchlistRepository watchlistRepository = new WatchlistRepository(movieRepository);
            WatchlistService watchlistService = new WatchlistService(watchlistRepository, this.userService, movieService);
            this.watchlistController = new WatchlistController(watchlistService);
            
            ReviewRepository reviewRepository = new ReviewRepository();
            repository.SystemSettingsRepository configuracionRepository = new repository.SystemSettingsRepository();
            service.SystemSettingsService configuracionService = new service.SystemSettingsService(configuracionRepository);
            ReviewService reviewService = new ReviewService(reviewRepository, this.userService, movieService, configuracionService, watchlistService);
            this.reviewController = new ReviewController(reviewService);
            this.configController = new controller.SystemSettingsController(configuracionService);
            this.likeService = new LikeService();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize MovieDetailServlet", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        int movieId;
        try {
            movieId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            throw exception.ErrorFactory.notFound("La película solicitada no existe o la URL es inválida.");
        }  
        Movie movie = movieController.getMovieById(movieId);
        if (movie == null) {
            throw exception.ErrorFactory.notFound("No se encontró ninguna película con el ID especificado.");
        }
        
        List<ActorWithCharacter> actors = new ArrayList<>();
        List<Person> directors = new ArrayList<>();
        List<Country> countries = new ArrayList<>();

        try { actors = personService.getActorsByMovieId(movieId); } 
        catch (Exception e) { System.err.println("Error fetching actors: " + e.getMessage()); }
        try { directors = personService.getDirectorsByMovieId(movieId); } 
        catch (Exception e) { System.err.println("Error fetching directors: " + e.getMessage()); }
        try { countries = movieController.getCountriesByMovieId(movieId); } 
        catch (Exception e) { System.err.println("Error fetching countries: " + e.getMessage()); }
        
        HttpSession session = request.getSession(false);
        boolean isInWatchlist = false;
        boolean canAddToWatchlist = true;
        Review userReview = null;
        Set<Integer> followingIds = new HashSet<>();
        int idLector = -1;
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            User user = (User) session.getAttribute("usuarioLogueado");
            idLector = user.getUserId();
            User updatedUser = userService.getUserById(user.getUserId()); 
            
            List<String> watchlistMovies = watchlistController.getMoviesInWatchlist(user.getUserId());
            isInWatchlist = watchlistMovies.contains(String.valueOf(movieId));
            userReview = reviewController.getReviewByUserAndMovie(user.getUserId(), movieId);
            
            int cantidadPeliculas = watchlistMovies.size();
            int limite = updatedUser.getUserLevel() >= 2 
                ? configController.getSystemSettings().getActiveWatchlistLimit()
                : configController.getSystemSettings().getNormalWatchlistLimit();
            canAddToWatchlist = cantidadPeliculas < limite;
            
            List<User> followingList = userService.getFollowing(user.getUserId());
            if (followingList != null) {
                for (User u : followingList) {
                    followingIds.add(u.getUserId());
                }
            }
        }
        
        String sortBy = request.getParameter("sortBy");
        List<Review> reviews;
        if ("likes".equals(sortBy)) {
            reviews = reviewController.getReviewsByMovieSortedByLikes(movieId, idLector);
        } else {
            reviews = reviewController.getReviewsByMovie(movieId, idLector);
        }
        
        Map<Integer, Integer> likesCountMap = new HashMap<>();
        Map<Integer, Boolean> userLikesMap = new HashMap<>();
        Map<Integer, Boolean> followedUsersMap = new HashMap<>(); 
        Map<Integer, Integer> userLevelsMap = new HashMap<>();
        
        for (Review review : reviews) {
            int likesCount = likeService.getLikesCount(review.getReviewId());
            likesCountMap.put(review.getReviewId(), likesCount);
            if (idLector != -1) {
                boolean hasLiked = likeService.hasUserLiked(idLector, review.getReviewId());
                userLikesMap.put(review.getReviewId(), hasLiked);
            } 
            if (!followedUsersMap.containsKey(review.getUserId())) {
                followedUsersMap.put(review.getUserId(), followingIds.contains(review.getUserId()));
            }  
            if (!userLevelsMap.containsKey(review.getUserId())) {
                User reviewAuthor = userService.getUserById(review.getUserId());
                if (reviewAuthor != null) {
                    userLevelsMap.put(review.getUserId(), reviewAuthor.getUserLevel());
                } else {
                    userLevelsMap.put(review.getUserId(), 1); 
                }
            }
        }
        
        request.setAttribute("movie", movie);
        request.setAttribute("likesCountMap", likesCountMap);
        request.setAttribute("userLikesMap", userLikesMap);
        request.setAttribute("followedUsersMap", followedUsersMap); 
        request.setAttribute("userLevelsMap", userLevelsMap);
        request.setAttribute("currentSort", sortBy != null ? sortBy : "date");
        request.setAttribute("actors", actors);
        request.setAttribute("directors", directors);
        request.setAttribute("countries", countries);
        request.setAttribute("isInWatchlist", isInWatchlist);
        request.setAttribute("canAddToWatchlist", canAddToWatchlist);
        request.setAttribute("reviews", reviews);
        request.setAttribute("userReview", userReview);
        
        request.getRequestDispatcher("/WEB-INF/movie-detail.jsp").forward(request, response);
    }
}