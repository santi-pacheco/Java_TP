package servlet;

import java.io.IOException;
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
import controller.ConfiguracionReglasController;
import service.ConfiguracionReglasService;
import repository.ConfiguracionReglasRepository;


@WebServlet("/watchlist")
public class WatchlistServlet extends HttpServlet {
    private WatchlistController watchlistController;
    private MovieController movieController;
    private ConfiguracionReglasController configController;
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
        UserService userService = new UserService(userRepository, passwordEncoder, followRepository);
        
        WatchlistRepository watchlistRepository = new WatchlistRepository(movieRepository);
        WatchlistService watchlistService = new WatchlistService(watchlistRepository, userService, movieService);
        this.watchlistController = new WatchlistController(watchlistService);
        
        ConfiguracionReglasRepository configRepo = new ConfiguracionReglasRepository();
        ConfiguracionReglasService configService = new ConfiguracionReglasService(configRepo);
        this.configController = new ConfiguracionReglasController(configService);
        
        this.userController = new UserController(userService);
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("usuarioLogueado");
        List<String> movieIds = watchlistController.getMoviesInWatchlist(user.getId());
        
        List<Movie> movies = new ArrayList<>();
        for (String idStr : movieIds) {
            try {
                Movie movie = movieController.getMovieById(Integer.parseInt(idStr));
                if (movie != null) movies.add(movie);
            } catch (Exception e) {
                System.err.println("Error loading movie: " + idStr);
            }
        }
        
        request.setAttribute("movies", movies);
        request.getRequestDispatcher("/WEB-INF/watchlist.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User user = (User) session.getAttribute("usuarioLogueado");
        String action = request.getParameter("action");
        String movieId = request.getParameter("movieId");
        User userAct = userController.getUserById(user.getId());

        if ("add".equals(action)) {
            List<String> movieIds = watchlistController.getMoviesInWatchlist(user.getId());
            int cantidadPeliculas = movieIds.size();
            
            if (userAct.getNivelUsuario() >= 2) {
                int limiteActivo = configController.getConfiguracionReglas().getLimiteWatchlistActivo();
                
                if (cantidadPeliculas >= limiteActivo) {
                    session.setAttribute("watchlistError", "Has alcanzado el límite de películas en tu watchlist");
                    response.sendRedirect(request.getHeader("Referer"));
                    return;
                }
                
            } else {
                int limiteNormal = configController.getConfiguracionReglas().getLimiteWatchlistNormal();
                
                if (cantidadPeliculas >= limiteNormal) {
                    session.setAttribute("watchlistError", "Has alcanzado el límite de películas en tu watchlist");
                    response.sendRedirect(request.getHeader("Referer"));
                    return;
                }
            }    
            watchlistController.addMovie(user.getId(), movieId);
        } else if ("remove".equals(action)) {
            watchlistController.removeMovie(user.getId(), movieId);
        }

        response.sendRedirect(request.getContextPath() + "/watchlist");
    }
}