package service;

import java.util.List;
import entity.Movie;
import repository.MovieRepository;
import exception.ErrorFactory;
import controller.GenreController;
import java.util.Objects;
import java.util.stream.Collectors;


public class MovieService {

	private MovieRepository movieRepository;
	
	public MovieService(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}
	
	public List<Movie> getAllMovies() {
		return movieRepository.findAll();
	}
	
	public Movie getMovieById(int id) {
		Movie movie = movieRepository.findOne(id);
		if (movie == null) {
			throw ErrorFactory.notFound("Movie not found with ID: " + id);
		}
		return movie;
	}
	public Movie createMovie(Movie movie) {
		return movieRepository.add(movie);
	}
	public Movie updateMovie(Movie movie) {
		// 1. Primero, verifica que la película exista
	    Movie existingMovie = movieRepository.findOne(movie.getId());
	    if (existingMovie == null) {
	        throw ErrorFactory.notFound("No se puede actualizar. Película con ID " + movie.getId() + " no encontrada.");
	    }
	    // 2. Si existe, ahora sí actualiza
	    return movieRepository.update(movie);	
	}
	
	public Movie deleteMovie(Movie movie) {
		Movie movieToDelete = movieRepository.delete(movie);
		return movieToDelete;
	}
	
	public void saveAllMovies(List<Movie> movies) {
		movieRepository.saveAll(movies);
	}
	
	public void updateMovieGenres(int movieId, List<Integer> genres, GenreController genreController) {
		Movie movie = movieRepository.findOne(movieId);
		if (movie == null) {
			throw ErrorFactory.notFound("Movie not found with ID: " + movieId);
		}
		 List<Integer> genresId = genres.stream()                        // 1. Convierte la lista en un "flujo" de datos.
                 .map(idApi -> genreController.getGeneresByIdApi(idApi)) // 2. Para cada idApi, llama al método.
                 .filter(Objects::nonNull)                               // 3. Filtra y descarta cualquier resultado que sea null.
                 .distinct()                                             // 4. Elimina todos los duplicados.
                 .collect(Collectors.toList());
		System.out.println("Genres to update: " + genresId);
		System.out.println("Id of movie to update genres: " + movieId);
		//genresId puede tener algún null? No, porque se filtran en el stream
		movieRepository.updateMovieGenres(movieId, genresId);
	}
	
	public void updateMovieActors(int movieId, List<util.DiscoverReflectionMain.actorCharacter> ac) {
		movieRepository.updateMovieActors(movieId, ac);
	}
	
	public void updateMovieDirectors(int movieId, List<entity.Person> directors) {
		movieRepository.updateMovieDirectors(movieId, directors);
	}
}