package servlet;

import java.io.IOException;
import java.sql.Time;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletContext;
import controller.MovieController;
import entity.Movie;
import repository.MovieRepository;
import service.MovieService;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import exception.ErrorFactory;
import exception.AppException;

@WebServlet("/movies")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MovieController movieController;
    private Validator validator;

	@Override
	public void init() throws ServletException {
		super.init();
		MovieRepository movieRepository = new MovieRepository();
		MovieService movieService = new MovieService(movieRepository);
		this.movieController = new MovieController(movieService);
        
        ServletContext context = getServletContext();
        this.validator = (Validator) context.getAttribute("miValidador");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String accion = request.getParameter("accion");
		if (accion == null) accion = "listar";
		
        String jspForm = "/movieForm.jsp"; 

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
				request.getRequestDispatcher(jspForm).forward(request, response);
				break;
			case "mostrarFormCrear":
				request.getRequestDispatcher(jspForm).forward(request, response);
				break;
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        String accion = request.getParameter("accion");
        String jspTarget = "/movieForm.jsp";
        Movie movieFromForm = null;

        try {
            switch (accion) {
                case "crear":
                    movieFromForm = new Movie();
                    populateMovieFromRequest(movieFromForm, request);
                    Set<ConstraintViolation<Movie>> violationsCreate = validator.validate(movieFromForm);
                    if (!violationsCreate.isEmpty()) {
                        request.setAttribute("errors", getErrorMessages(violationsCreate));
                        request.setAttribute("movie", movieFromForm);
                        request.getRequestDispatcher(jspTarget).forward(request, response);
                        return;
                    }
                    movieController.createMovie(movieFromForm);
                    break;
                    
                case "actualizar":
                    movieFromForm = new Movie();
                    movieFromForm.setId(parseIntParam(request.getParameter("id"), "ID"));
                    populateMovieFromRequest(movieFromForm, request);
                    
                    Set<ConstraintViolation<Movie>> violationsUpdate = validator.validate(movieFromForm);
                    if (!violationsUpdate.isEmpty()) {
                        request.setAttribute("errors", getErrorMessages(violationsUpdate));
                        request.setAttribute("movie", movieFromForm);
                        request.getRequestDispatcher(jspTarget).forward(request, response);
                        return;
                    }
                    movieController.modifyMovie(movieFromForm);
                    break;
                    
                case "eliminar":
                    int idEliminar = parseIntParam(request.getParameter("id"), "ID");
                    Movie deleteMovie = new Movie();
                    deleteMovie.setId(idEliminar);
                    movieController.removeMovie(deleteMovie);
                    break;
            }
            
            // Si todo fue bien redirigimos a la lista
            response.sendRedirect(request.getContextPath() + "/movies?accion=listar&exito=true");

        } catch (AppException e) {
            if (e.getErrorType().equals("VALIDATION_ERROR")) {
                
                request.setAttribute("errors", Set.of(e.getMessage())); 
                request.setAttribute("movie", movieFromForm);
                request.getRequestDispatcher(jspTarget).forward(request, response);
            
            } else {
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
	}
    
    private void populateMovieFromRequest(Movie movie, HttpServletRequest request) {
        movie.setId_api(parseIntParam(request.getParameter("id_api"), "ID API"));
        movie.setTitulo(request.getParameter("titulo"));
        movie.setTituloOriginal(request.getParameter("tituloOriginal"));
        movie.setSinopsis(request.getParameter("sinopsis"));
        movie.setEstrenoYear(parseIntParam(request.getParameter("estrenoYear"), "Año de Estreno"));
        movie.setDuracion(parseTimeParam(request.getParameter("duracion"), "Duración"));
        movie.setAdulto(Boolean.parseBoolean(request.getParameter("adulto")));
        movie.setPuntuacionApi(parseDoubleParam(request.getParameter("puntuacionApi"), "Puntuación API"));
        movie.setIdiomaOriginal(request.getParameter("idiomaOriginal"));
        movie.setPosterPath(request.getParameter("posterPath"));
        movie.setPopularidad(parseDoubleParam(request.getParameter("popularidad"), "Popularidad"));
        movie.setVotosApi(parseIntParam(request.getParameter("votosApi"), "Votos API"));
        movie.setId_imdb(request.getParameter("id_imdb"));
    }

    private int parseIntParam(String param, String fieldName) {
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException e) {
            throw ErrorFactory.validation("El campo '" + fieldName + "' debe ser un número entero.");
        }
    }
    
    private double parseDoubleParam(String param, String fieldName) {
        try {
            return Double.parseDouble(param);
        } catch (NumberFormatException e) {
            throw ErrorFactory.validation("El campo '" + fieldName + "' debe ser un número decimal.");
        }
    }
    
    private Time parseTimeParam(String param, String fieldName) {
         if (param == null || !param.matches("\\d{2}:\\d{2}")) {
            throw ErrorFactory.validation("El campo '" + fieldName + "' debe tener formato HH:mm.");
        }
        try {
            return Time.valueOf(param + ":00");
        } catch (IllegalArgumentException e) {
            throw ErrorFactory.validation("El campo '" + fieldName + "' tiene un formato de hora inválido.");
        }
    }

    private Set<String> getErrorMessages(Set<ConstraintViolation<Movie>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}