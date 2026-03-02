package servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import entity.Movie;
import repository.MovieRepository;
import service.MovieService;
import controller.MovieController;

@WebServlet("/home")
public class LandingServlet extends HttpServlet {
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
        List<Movie> movies = movieController.getRandomMovies(72);
        request.setAttribute("movies", movies);
        request.getRequestDispatcher("/landing.jsp").forward(request, response);
    }
}