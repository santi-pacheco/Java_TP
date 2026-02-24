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
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            MovieRepository movieRepository = new MovieRepository();
            MovieService movieService = new MovieService(movieRepository);
            this.movieController = new MovieController(movieService);
            
            this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                    @Override
                    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.toString());
                    }
                })
                .registerTypeAdapter(Time.class, new JsonSerializer<Time>() {
                    @Override
                    public JsonElement serialize(Time src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.toString());
                    }
                })
                .create();
                
        } catch (Exception e) {
            throw new ServletException("Failed to initialize SearchApiServlet", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String query = request.getParameter("q");
        
        response.setContentType("application/json;charset=UTF-8");
        
        if (query == null || query.trim().isEmpty()) {
            response.getWriter().write("[]");
            return;
        }
        
        try {
            List<Movie> movies = movieController.searchMoviesByName(query);
            String json = gson.toJson(movies);
            response.getWriter().write(json);
        } catch (Exception e) {
            System.err.println("Error en SearchApiServlet: " + e.getMessage()); 
            e.printStackTrace();
            response.getWriter().write("[]");
        }
    }
}