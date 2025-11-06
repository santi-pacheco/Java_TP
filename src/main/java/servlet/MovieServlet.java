package servlet;

import java.io.IOException;
import java.sql.Time;
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
import com.google.gson.Gson;

@WebServlet("/movies")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MovieController movieController;
	private Gson gson = new Gson();

	@Override
	public void init() throws ServletException {
		super.init();
		MovieRepository movieRepository = new MovieRepository();
		MovieService movieService = new MovieService(movieRepository);
		this.movieController = new MovieController(movieService);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String accion = request.getParameter("accion");
		if (accion == null) accion = "listar";
		
		switch (accion) {
			case "listar":
				List<Movie> movies = movieController.getMovies();
				request.setAttribute("movies", movies);
				request.getRequestDispatcher("/movieCrud.jsp").forward(request, response);
				break;
			case "mostrarFormEditar":
				int idEditar = Integer.parseInt(request.getParameter("id"));
				Movie movie = movieController.getMovieById(idEditar);
				request.setAttribute("movie", movie);
				request.getRequestDispatcher("/movieForm.jsp").forward(request, response);
				break;
			case "mostrarFormCrear":
				request.getRequestDispatcher("/movieForm.jsp").forward(request, response);
				break;
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String accion = request.getParameter("accion");
		
		switch (accion) {
			case "crear":
				Movie newMovie = new Movie();
				newMovie.setId_api(Integer.parseInt(request.getParameter("id_api")));
				newMovie.setTitulo(request.getParameter("titulo"));
				newMovie.setTituloOriginal(request.getParameter("tituloOriginal"));
				newMovie.setSinopsis(request.getParameter("sinopsis"));
				newMovie.setEstrenoYear(Integer.parseInt(request.getParameter("estrenoYear")));
				newMovie.setDuracion(Time.valueOf(request.getParameter("duracion") + ":00"));
				newMovie.setAdulto(Boolean.parseBoolean(request.getParameter("adulto")));
				newMovie.setPuntuacionApi(Double.parseDouble(request.getParameter("puntuacionApi")));
				newMovie.setIdiomaOriginal(request.getParameter("idiomaOriginal"));
				newMovie.setPosterPath(request.getParameter("posterPath"));
				newMovie.setPopularidad(Double.parseDouble(request.getParameter("popularidad")));
				newMovie.setVotosApi(Integer.parseInt(request.getParameter("votosApi")));
				newMovie.setId_imdb(request.getParameter("id_imdb"));
				movieController.addMovie(newMovie);
				break;
			case "actualizar":
				Movie updateMovie = new Movie();
				updateMovie.setId(Integer.parseInt(request.getParameter("id")));
				updateMovie.setId_api(Integer.parseInt(request.getParameter("id_api")));
				updateMovie.setTitulo(request.getParameter("titulo"));
				updateMovie.setTituloOriginal(request.getParameter("tituloOriginal"));
				updateMovie.setSinopsis(request.getParameter("sinopsis"));
				updateMovie.setEstrenoYear(Integer.parseInt(request.getParameter("estrenoYear")));
				updateMovie.setDuracion(Time.valueOf(request.getParameter("duracion") + ":00"));
				updateMovie.setAdulto(Boolean.parseBoolean(request.getParameter("adulto")));
				updateMovie.setPuntuacionApi(Double.parseDouble(request.getParameter("puntuacionApi")));
				updateMovie.setIdiomaOriginal(request.getParameter("idiomaOriginal"));
				updateMovie.setPosterPath(request.getParameter("posterPath"));
				updateMovie.setPopularidad(Double.parseDouble(request.getParameter("popularidad")));
				updateMovie.setVotosApi(Integer.parseInt(request.getParameter("votosApi")));
				updateMovie.setId_imdb(request.getParameter("id_imdb"));
				movieController.updateMovie(updateMovie);
				break;
			case "eliminar":
				int idEliminar = Integer.parseInt(request.getParameter("id"));
				Movie deleteMovie = new Movie();
				deleteMovie.setId(idEliminar);
				movieController.deleteMovie(deleteMovie);
				break;
		}
		response.sendRedirect(request.getContextPath() + "/movies?accion=listar&exito=true");
	}
}
