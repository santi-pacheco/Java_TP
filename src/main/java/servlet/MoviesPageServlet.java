package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import controller.MovieController;
import entity.Movie;
import repository.MovieRepository;
import service.MovieService;

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
        
        try {
            List<Movie> popularMovies = movieController.getMostPopularMovies(12);
            List<Movie> topRatedMovies = movieController.getTopRatedMovies(12);
            List<Movie> recentMovies = movieController.getRecentMovies(12);
            
            request.setAttribute("popularMovies", popularMovies);
            request.setAttribute("topRatedMovies", topRatedMovies);
            request.setAttribute("recentMovies", recentMovies);
            
            request.getRequestDispatcher("/WEB-INF/movies-page.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error in MoviesPageServlet: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}