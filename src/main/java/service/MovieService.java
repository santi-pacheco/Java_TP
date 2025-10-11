package service;

import java.util.List;
import entity.Movie;
import repository.MovieRepository;
import exception.ErrorFactory;

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
	
}
