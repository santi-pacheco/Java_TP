package servlet;

import java.io.IOException;
import repository.BlockRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import entity.Review;
import entity.User;
import entity.Movie;
import repository.ReviewRepository;
import repository.UserRepository;
import repository.WatchlistRepository;
import repository.MovieRepository;
import repository.FollowRepository;
import repository.ConfiguracionReglasRepository;
import service.ReviewService;
import service.UserService;
import service.WatchlistService;
import service.MovieService;
import service.ConfiguracionReglasService;

public class UserReviewsServlet extends HttpServlet {
    private ReviewService reviewService;
    private MovieService movieService;

    @Override
    public void init() throws ServletException {
        ReviewRepository reviewRepository = new ReviewRepository();
        UserRepository userRepository = new UserRepository();
        MovieRepository movieRepository = new MovieRepository();
        ConfiguracionReglasRepository configuracionRepository = new ConfiguracionReglasRepository();
        FollowRepository followRepository = new FollowRepository();
        BlockRepository blockRepository = new BlockRepository();
        UserService userService = new UserService(userRepository, new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(), followRepository, blockRepository);
        movieService = new MovieService(movieRepository);
        ConfiguracionReglasService configuracionService = new ConfiguracionReglasService(configuracionRepository);
        WatchlistRepository watchlistRepository = new WatchlistRepository(movieRepository);
        WatchlistService watchlistService = new WatchlistService(watchlistRepository, userService, movieService);
        reviewService = new ReviewService(reviewRepository, userService, movieService, configuracionService, watchlistService);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("usuarioLogueado");
        List<Review> reviews = reviewService.getReviewsByUser(user.getId());
        
        Map<Integer, Movie> moviesMap = reviews.stream()
            .map(Review::getId_movie)
            .distinct()
            .collect(Collectors.toMap(
                movieId -> movieId,
                movieId -> movieService.getMovieById(movieId)
            ));
        
        request.setAttribute("reviews", reviews);
        request.setAttribute("moviesMap", moviesMap);
        request.getRequestDispatcher("/WEB-INF/views/userReviews.jsp").forward(request, response);
    }
}
