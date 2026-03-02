package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import repository.BlockRepository;
import repository.FollowRepository;
import repository.MovieRepository;
import repository.ReviewRepository;
import repository.SystemSettingsRepository;
import repository.UserRepository;
import repository.WatchlistRepository;

import com.google.gson.Gson;

import entity.FeedReviewDTO;
import service.MovieService;
import service.ReviewService;
import service.SystemSettingsService;
import service.UserService;
import service.WatchlistService;
import entity.User;

@WebServlet("/api/feed")
public class FeedApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReviewService reviewService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        ReviewRepository reviewRepo = new ReviewRepository();
        UserRepository userRepo = new UserRepository();
        MovieRepository movieRepo = new MovieRepository();
        BlockRepository blockRepository = new BlockRepository();
        MovieService movieServ = new MovieService(movieRepo);
        SystemSettingsService configServ = new SystemSettingsService(new SystemSettingsRepository());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        FollowRepository followRepo = new FollowRepository();
        UserService userService = new UserService(userRepo, passwordEncoder, followRepo, blockRepository);
        WatchlistRepository watchlistRepo = new WatchlistRepository();
        WatchlistService watchServ = new WatchlistService(watchlistRepo, userService, movieServ);
        this.reviewService = new ReviewService(reviewRepo, userService, movieServ, configServ, watchServ);
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        User usuarioActual = (User) session.getAttribute("usuarioLogueado");

        int offset = 0;
        String offsetParam = request.getParameter("offset");
        if (offsetParam != null && !offsetParam.isEmpty()) {
            try {
                offset = Integer.parseInt(offsetParam);
            } catch (NumberFormatException e) {
                offset = 0;
            }
        }
        
        int limit = 10;
        List<FeedReviewDTO> feed = reviewService.getGlobalFeedPaginated(usuarioActual.getUserId(), offset, limit);
        
        out.print(gson.toJson(feed));
        out.flush();
    }
}