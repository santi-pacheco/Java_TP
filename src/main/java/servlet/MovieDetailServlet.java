package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import controller.MovieController;
import controller.WatchlistController;
import entity.Movie;
import entity.Person;
import entity.ActorWithCharacter;
import entity.User;
import repository.MovieRepository;
import repository.PersonRepository;
import repository.WatchlistRepository;
import repository.UserRepository;
import service.MovieService;
import service.PersonService;
import service.WatchlistService;
import service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private MovieController movieController;
    private PersonService personService;
    private WatchlistController watchlistController;
    
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
            UserService userService = new UserService(userRepository, passwordEncoder);
            WatchlistRepository watchlistRepository = new WatchlistRepository(movieRepository);
            WatchlistService watchlistService = new WatchlistService(watchlistRepository, userService, movieService);
            this.watchlistController = new WatchlistController(watchlistService);
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
            
            HttpSession session = request.getSession(false);
            boolean isInWatchlist = false;
            if (session != null && session.getAttribute("usuarioLogueado") != null) {
                User user = (User) session.getAttribute("usuarioLogueado");
                List<String> watchlistMovies = watchlistController.getMoviesInWatchlist(user.getId());
                isInWatchlist = watchlistMovies.contains(String.valueOf(movieId));
            }
            
            request.setAttribute("movie", movie);
            request.setAttribute("actors", actors);
            request.setAttribute("directors", directors);
            request.setAttribute("isInWatchlist", isInWatchlist);
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