package servlet;

import java.io.IOException;
import repository.BlockRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import service.WatchlistService;
import service.MovieService;
import repository.WatchlistRepository;
import repository.MovieRepository;
import repository.FollowRepository;
import service.UserService;
import repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@WebServlet("/roulette")
public class RouletteServlet extends HttpServlet {
    private WatchlistController watchlistController;
    private MovieController movieController;
    
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
        
        WatchlistRepository watchlistRepository = new WatchlistRepository(movieRepository);
        WatchlistService watchlistService = new WatchlistService(watchlistRepository, userService, movieService);
        this.watchlistController = new WatchlistController(watchlistService);
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
        
        if (movieIds.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/watchlist");
            return;
        }
        
        List<Movie> movies = new ArrayList<>();
        for (String idStr : movieIds) {
            try {
                Movie movie = movieController.getMovieById(Integer.parseInt(idStr));
                if (movie != null) movies.add(movie);
            } catch (Exception e) {
                System.err.println("Error loading movie: " + idStr);
            }
        }
        
        Random random = new Random(System.nanoTime());
        int selectedIndex = random.nextInt(movies.size());
        Movie selectedMovie = movies.get(selectedIndex);
        int extraSpins = 3 + random.nextInt(4);
        double degreesPerSlice = 360.0 / movies.size();
        double maxOffset = degreesPerSlice * 0.3;
        double randomOffset = -maxOffset + (random.nextDouble() * (maxOffset * 2));
        
        System.out.println("Roulette - Total movies: " + movies.size() + ", Selected index: " + selectedIndex + ", Movie: " + selectedMovie.getTitulo() + ", Extra spins: " + extraSpins + ", DegreesPerSlice: " + degreesPerSlice + ", Offset: " + randomOffset + ", Max offset: " + maxOffset);
        
        request.setAttribute("movies", movies);
        request.setAttribute("selectedMovie", selectedMovie);
        request.setAttribute("selectedIndex", selectedIndex);
        request.setAttribute("totalMovies", movies.size());
        request.setAttribute("extraSpins", extraSpins);
        request.setAttribute("randomOffset", randomOffset);
        request.getRequestDispatcher("/WEB-INF/roulette.jsp").forward(request, response);
    }
}
