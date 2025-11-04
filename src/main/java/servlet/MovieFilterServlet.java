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

@WebServlet("/movie/filter")
public class MovieFilterServlet extends HttpServlet{


	private MovieController movieController;
	
	@Override
	public void init() {
	
		// Inicialización del servicio y validador si es necesario
		MovieRepository movieRepository = new MovieRepository();
		MovieService movieService = new MovieService(movieRepository);
		this.movieController = new MovieController(movieService);
		
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Lógica para manejar la solicitud de filtrado de películas
		String pathInfo = request.getPathInfo();
		
		if (pathInfo == null || pathInfo.equals("/")) {
			
			// Logica para manejar el filtrado de películas
			
//			
//			String genre = request.getParameter("genre");
//			System.out.println("Peticion recibida" + request);
//			System.out.println("Género recibido: " + request.getAttribute("genre") 
//					+ "Desde recibido: " + request.getParameter("since")
//					+ "Hasta recibido: " + request.getParameter("until") 
//					+ "Rating recibido: " + request.getParameter("rating"));
//			List<Movie> filteredMovies = movieController.getMovieByFilter(request.getParameter("genre"),
//																		  Integer.parseInt(request.getParameter("since")),
//																		  Integer.parseInt(request.getParameter("until")),
//																		  Float.parseFloat(request.getParameter("rating")));
//			request.setAttribute("movies", filteredMovies);
//			request.getRequestDispatcher("/WEB-INF/movie-list.jsp").forward(request, response);
			
			List<Movie> filteredMovies = movieController.getMovieByFilter("Accion", 0, 0, null);
			
		} else {
			// Manejar otros casos si es necesario
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		
	}
	
}
