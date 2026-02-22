package servlet;

import java.io.IOException;

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
import repository.FollowRepository;

public class MovieDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private MovieController movieController;
    private PersonService personService;
    private WatchlistController watchlistController;
    private ReviewController reviewController;
    private controller.ConfiguracionReglasController configController;
    private LikeService likeService;
    
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
            UserService userService = new UserService(userRepository, passwordEncoder, followRepository);
            WatchlistRepository watchlistRepository = new WatchlistRepository(movieRepository);
            WatchlistService watchlistService = new WatchlistService(watchlistRepository, userService, movieService);
            this.watchlistController = new WatchlistController(watchlistService);
            
            ReviewRepository reviewRepository = new ReviewRepository();
            repository.ConfiguracionReglasRepository configuracionRepository = new repository.ConfiguracionReglasRepository();
            service.ConfiguracionReglasService configuracionService = new service.ConfiguracionReglasService(configuracionRepository);
            ReviewService reviewService = new ReviewService(reviewRepository, userService, movieService, configuracionService, watchlistService);
            this.reviewController = new ReviewController(reviewService);
            this.configController = new controller.ConfiguracionReglasController(configuracionService);
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
        
        try {
            int movieId = Integer.parseInt(pathInfo.substring(1));
            Movie movie = movieController.getMovieById(movieId);
            
            List<ActorWithCharacter> actors = new ArrayList<>();
            List<Person> directors = new ArrayList<>();
            List<Country> countries = new ArrayList<>();
            try {
                actors = personService.getActorsByMovieId(movieId);
            } catch (Exception e) {
                System.err.println("Error fetching actors: " + e.getMessage());
            }
            
            try {
                directors = personService.getDirectorsByMovieId(movieId);
            } catch (Exception e) {
                System.err.println("Error fetching directors: " + e.getMessage());
            }
            try {
                countries = movieController.getCountriesByMovieId(movieId);
            } catch (Exception e) {
                System.err.println("Error fetching countries: " + e.getMessage());
            }
            
            HttpSession session = request.getSession(false);
            boolean isInWatchlist = false;
            boolean canAddToWatchlist = true;
            Review userReview = null;
            if (session != null && session.getAttribute("usuarioLogueado") != null) {
                User user = (User) session.getAttribute("usuarioLogueado");
                List<String> watchlistMovies = watchlistController.getMoviesInWatchlist(user.getId());
                isInWatchlist = watchlistMovies.contains(String.valueOf(movieId));
                userReview = reviewController.getReviewByUserAndMovie(user.getId(), movieId);
                
                int cantidadPeliculas = watchlistMovies.size();
                int limite = user.isEsUsuarioActivo() 
                    ? configController.getConfiguracionReglas().getLimiteWatchlistActivo()
                    : configController.getConfiguracionReglas().getLimiteWatchlistNormal();
                canAddToWatchlist = cantidadPeliculas < limite;
            }
            
            String sortBy = request.getParameter("sortBy");
            List<Review> reviews;
            if ("likes".equals(sortBy)) {
                reviews = reviewController.getReviewsByMovieSortedByLikes(movieId);
            } else {
                reviews = reviewController.getReviewsByMovie(movieId);
            }
            
            java.util.Map<Integer, Integer> likesCountMap = new java.util.HashMap<>();
            java.util.Map<Integer, Boolean> userLikesMap = new java.util.HashMap<>();
            
            for (Review review : reviews) {
                int likesCount = likeService.getLikesCount(review.getId());
                likesCountMap.put(review.getId(), likesCount);
                
                if (session != null && session.getAttribute("usuarioLogueado") != null) {
                    User user = (User) session.getAttribute("usuarioLogueado");
                    boolean hasLiked = likeService.hasUserLiked(user.getId(), review.getId());
                    userLikesMap.put(review.getId(), hasLiked);
                }
            }
            
            request.setAttribute("movie", movie);
            request.setAttribute("likesCountMap", likesCountMap);
            request.setAttribute("userLikesMap", userLikesMap);
            request.setAttribute("currentSort", sortBy != null ? sortBy : "date");
            request.setAttribute("actors", actors);
            request.setAttribute("directors", directors);
            request.setAttribute("countries", countries);
            request.setAttribute("isInWatchlist", isInWatchlist);
            request.setAttribute("canAddToWatchlist", canAddToWatchlist);
            request.setAttribute("reviews", reviews);
            request.setAttribute("userReview", userReview);
            request.getRequestDispatcher("/WEB-INF/movie-detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            System.err.println("Invalid movie ID format: " + pathInfo);
            response.sendRedirect(request.getContextPath() + "/");
        } catch (Exception e) {
            System.err.println("Error in MovieDetailServlet: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}