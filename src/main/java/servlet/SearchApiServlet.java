package servlet;

import java.io.IOException;
import java.util.List;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.sql.Time;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import controller.MovieController;
import entity.Movie;
import repository.MovieRepository;
import service.MovieService;

public class SearchApiServlet extends HttpServlet {
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
        
        String query = request.getParameter("q");

        if (query == null || query.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        List<Movie> movies = movieController.searchMoviesByName(query);
        request.setAttribute("movies", movies);
        request.getRequestDispatcher("/WEB-INF/search-results.jsp").forward(request, response);
    }
}