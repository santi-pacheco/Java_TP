package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import controller.MovieController;
import entity.Movie;
import repository.MovieRepository;
import service.MovieService;

@WebServlet("/movies-page")
public class MoviesPageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private MovieController movieController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        MovieRepository movieRepository = new MovieRepository();
        MovieService movieService = new MovieService(movieRepository);
        this.movieController = new MovieController(movieService);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String genre = request.getParameter("genre");
        String sinceStr = request.getParameter("since");
        String untilStr = request.getParameter("until");
        String name = request.getParameter("name");
        
        List<Movie> filteredMovies = null;
        
        boolean hasFilters = (name != null && !name.trim().isEmpty()) || 
                           (genre != null && !genre.trim().isEmpty()) || 
                           (sinceStr != null && !sinceStr.trim().isEmpty()) || 
                           (untilStr != null && !untilStr.trim().isEmpty());
        if (hasFilters) {
            int since = 0;
            int until = 0;
            try {
                if (sinceStr != null && !sinceStr.isEmpty()) {
                    since = Integer.parseInt(sinceStr);
                }
                if (untilStr != null && !untilStr.isEmpty()) {
                    until = Integer.parseInt(untilStr);
                }
            } catch (NumberFormatException e) {
            }
            filteredMovies = movieController.getMovieByFilter(name, genre, since, until);
        }
        
        if (filteredMovies != null) {
            request.setAttribute("filteredMovies", filteredMovies);
            request.setAttribute("currentName", name);
            request.setAttribute("currentGenre", genre);
            request.setAttribute("currentSince", sinceStr);
            request.setAttribute("currentUntil", untilStr);
        } else {
            List<Movie> popularMovies = movieController.getMostPopularMovies(12);
            List<Movie> topRatedMovies = movieController.getTopRatedMovies(12);
            List<Movie> recentMovies = movieController.getRecentMovies(12);
            request.setAttribute("popularMovies", popularMovies);
            request.setAttribute("topRatedMovies", topRatedMovies);
            request.setAttribute("recentMovies", recentMovies);
        }
        request.getRequestDispatcher("/WEB-INF/movies-page.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}