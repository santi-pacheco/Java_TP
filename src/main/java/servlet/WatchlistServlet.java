package servlet;

import java.io.IOException;
import repository.BlockRepository;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import entity.User;
import entity.Movie;
import controller.WatchlistController;
import controller.MovieController;
import controller.UserController;
import service.WatchlistService;
import service.MovieService;
import service.UserService;
import repository.FollowRepository;
import repository.WatchlistRepository;
import repository.MovieRepository;
import repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import controller.SystemSettingsController;
import service.SystemSettingsService;
import repository.SystemSettingsRepository;
import exception.ErrorFactory;

@WebServlet("/watchlist")
public class WatchlistServlet extends HttpServlet {
    private WatchlistController watchlistController;
    private MovieController movieController;
    private SystemSettingsController configController;
    private UserController userController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        MovieRepository movieRepository = new MovieRepository();
        MovieService movieService = new MovieService(movieRepository);
        this.movieController = new MovieController(movieService);
        
        UserRepository userRepository = new UserRepository();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        FollowRepository followRepository = new FollowRepository();
        BlockRepository blockRepository = new BlockRepository();
        UserService userService = new UserService(userRepository, passwordEncoder, followRepository, blockRepository);
        
        WatchlistRepository watchlistRepository = new WatchlistRepository();
        WatchlistService watchlistService = new WatchlistService(watchlistRepository, userService, movieService);
        this.watchlistController = new WatchlistController(watchlistService);
        
        SystemSettingsRepository configRepo = new SystemSettingsRepository();
        SystemSettingsService configService = new SystemSettingsService(configRepo);
        this.configController = new SystemSettingsController(configService);
        
        this.userController = new UserController(userService);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("usuarioLogueado");
        List<String> movieIds = watchlistController.getMoviesInWatchlist(user.getUserId());
        List<Movie> movies = new ArrayList<>();
        for (String idStr : movieIds) {
            try {
                Movie movie = movieController.getMovieById(Integer.parseInt(idStr));
                if (movie != null) movies.add(movie);
            } catch (Exception e) {
                System.err.println("Error loading movie para watchlist: " + idStr);
            }
        }
        request.setAttribute("movies", movies);
        request.getRequestDispatcher("/WEB-INF/watchlist.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("usuarioLogueado");
        String action = request.getParameter("action");
        String movieId = request.getParameter("movieId");

        if (action == null || movieId == null || movieId.trim().isEmpty()) {
            throw ErrorFactory.badRequest("Acción y ID de película son requeridos.");
        }

        User userAct = userController.getUserById(user.getUserId());
        if ("add".equals(action)) {
            List<String> movieIds = watchlistController.getMoviesInWatchlist(user.getUserId());
            int cantidadPeliculas = movieIds.size();
            int limiteWatchlist = (userAct.getUserLevel() >= 2) 
                    ? configController.getSystemSettings().getActiveWatchlistLimit() 
                    : configController.getSystemSettings().getNormalWatchlistLimit();
            
            if (cantidadPeliculas >= limiteWatchlist) {
                session.setAttribute("watchlistError", "Has alcanzado el límite de películas en tu watchlist");
                response.sendRedirect(request.getHeader("Referer"));
                return;
            }
            watchlistController.addMovie(user.getUserId(), movieId);  
        } else if ("remove".equals(action)) {
            watchlistController.removeMovie(user.getUserId(), movieId);    
        } else {
            throw ErrorFactory.badRequest("Acción desconocida en Watchlist.");
        }
        response.sendRedirect(request.getContextPath() + "/watchlist");
    }
}