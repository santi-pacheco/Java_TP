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
import repository.ReviewRepository;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("usuarioLogueado") : null;
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        ReviewRepository reviewRepo = new ReviewRepository();
        repository.MovieRepository movieRepo = new repository.MovieRepository();
        List<Review> userReviews = reviewRepo.findByUser(user.getId());
        
        for (Review review : userReviews) {
            entity.Movie movie = movieRepo.findOne(review.getId_movie());
            if (movie != null) {
                review.setMovieTitle(movie.getTitulo());
            }
        }
        
        Map<String, Integer> ratingDistribution = new HashMap<>();
        double[] ratings = {0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0};
        for (double r : ratings) {
            ratingDistribution.put(String.valueOf(r), 0);
        }
        
        for (Review review : userReviews) {
            double rating = Math.round(review.getRating() * 2) / 2.0;
            String key = String.valueOf(rating);
            ratingDistribution.put(key, ratingDistribution.getOrDefault(key, 0) + 1);
        }
        
        request.setAttribute("user", user);
        request.setAttribute("totalReviews", userReviews.size());
        request.setAttribute("recentReviews", userReviews.size() > 5 ? userReviews.subList(0, 5) : userReviews);
        request.setAttribute("ratingDistribution", ratingDistribution);
        
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }
}
