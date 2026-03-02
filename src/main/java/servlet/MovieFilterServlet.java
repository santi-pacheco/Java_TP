package servlet;

import java.io.IOException;
import java.util.List;

import controller.MovieController;
import entity.Movie;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.MovieRepository;
import service.MovieService;
import exception.ErrorFactory;

@WebServlet("/movie/filter")
public class MovieFilterServlet extends HttpServlet {

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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if (pathInfo != null && !pathInfo.equals("/")) {
			throw ErrorFactory.notFound("La ruta de filtrado no existe.");
		}	
		String genre = request.getParameter("genre");
		String sinceStr = request.getParameter("since");
		String untilStr = request.getParameter("until");
		String name = request.getParameter("name");
		
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
		List<Movie> filteredMovies;
		if (name != null && !name.trim().isEmpty()) {
			filteredMovies = movieController.searchMoviesByName(name.trim());
		} else {
			filteredMovies = movieController.getMovieByFilter(name, genre, since, until);
		}
		request.setAttribute("movies", filteredMovies);
		request.getRequestDispatcher("/WEB-INF/movie-list.jsp").forward(request, response);
	}
}