package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.google.gson.Gson;

import entity.FeedReviewDTO;
import service.ReviewService;
import entity.User;

@WebServlet("/api/feed")
public class FeedApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReviewService reviewService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        repository.ReviewRepository reviewRepo = new repository.ReviewRepository();
        repository.UserRepository userRepo = new repository.UserRepository();
        repository.MovieRepository movieRepo = new repository.MovieRepository();
        service.MovieService movieServ = new service.MovieService(movieRepo);
        service.ConfiguracionReglasService configServ = new service.ConfiguracionReglasService(new repository.ConfiguracionReglasRepository());
        service.WatchlistService watchServ = new service.WatchlistService(new repository.WatchlistRepository(movieRepo), new service.UserService(userRepo, new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(), new repository.FollowRepository()), movieServ);
        
        this.reviewService = new service.ReviewService(reviewRepo, null, movieServ, configServ, watchServ);
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        User usuarioActual = (session != null) ? (User) session.getAttribute("usuarioLogueado") : null;
        
        if (usuarioActual == null) {
            out.print("[]");
            out.flush();
            return;
        }

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
        List<FeedReviewDTO> feed = reviewService.getGlobalFeedPaginated(usuarioActual.getId(), offset, limit);
        
        String json = gson.toJson(feed);
        out.print(json);
        out.flush();
    }
}