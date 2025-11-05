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


@WebServlet("/movies")
public class MoviesPageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private MovieController movieController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            MovieRepository movieRepository = new MovieRepository();
            MovieService movieService = new MovieService(movieRepository);
            this.movieController = new MovieController(movieService);
        } catch (Exception e) {
            throw new ServletException("Failed to initialize MoviesPageServlet", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String genre = request.getParameter("genre");
            String sinceStr = request.getParameter("since");
            String untilStr = request.getParameter("until");
            String name = request.getParameter("name");
            
            List<Movie> filteredMovies = null;
            
            // Check if any filter is applied
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
                    System.err.println("Error parsing year parameters: " + e.getMessage());
                }
                
                // Use combined filter method
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
            
        } catch (Exception e) {
            System.err.println("Error in MoviesPageServlet: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}