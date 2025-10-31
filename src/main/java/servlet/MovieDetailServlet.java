package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import controller.MovieController;
import entity.Movie;
import entity.Person;
import entity.ActorWithCharacter;
import repository.MovieRepository;
import repository.PersonRepository;
import service.MovieService;
import service.PersonService;
import java.util.List;

public class MovieDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private MovieController movieController;
    private PersonService personService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            MovieRepository movieRepository = new MovieRepository();
            MovieService movieService = new MovieService(movieRepository);
            this.movieController = new MovieController(movieService);
            
            PersonRepository personRepository = new PersonRepository();
            this.personService = new PersonService(personRepository);
        } catch (Exception e) {
            throw new ServletException("Failed to initialize MovieDetailServlet", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        try {
            int movieId = Integer.parseInt(pathInfo.substring(1));
            Movie movie = movieController.getMovieById(movieId);
            List<ActorWithCharacter> actors = personService.getActorsByMovieId(movieId);
            List<Person> directors = personService.getDirectorsByMovieId(movieId);
            
            System.out.println("Movie ID: " + movieId);
            System.out.println("Actors found: " + (actors != null ? actors.size() : "null"));
            System.out.println("Directors found: " + (directors != null ? directors.size() : "null"));
            
            request.setAttribute("movie", movie);
            request.setAttribute("actors", actors);
            request.setAttribute("directors", directors);
            request.getRequestDispatcher("/WEB-INF/movie-detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            System.err.println("Invalid movie ID format: " + pathInfo);
            response.sendRedirect(request.getContextPath() + "/");
        } catch (Exception e) {
            System.err.println("Error in MovieDetailServlet: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}