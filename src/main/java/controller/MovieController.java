package controller;

import java.util.List;

import service.MovieService;
import entity.Movie;

public class MovieController {

	private MovieService movieService;
	
	public MovieController(MovieService movieService) {
		this.movieService = movieService;
	}
	
	public List<Movie> getMovies() {
		List<Movie> movies = movieService.getAllMovies();
		System.out.println("Movies retrieved successfully: " + movies.size() + " records");
		return movies;
	}
	
	public Movie getMovieById(int id) {
		Movie movie = movieService.getMovieById(id);
		return movie;
	}
	
	public Movie createMovie(Movie m) {
			return movieService.createMovie(m);
	}
	
	public Movie modifyMovie(Movie m) {
			return movieService.updateMovie(m);
	}
	
	public void removeMovie(Movie m) {
			movieService.deleteMovie(m);
	}
	
	public void saveAllMovies(List<Movie> movies) {
		movieService.saveAllMovies(movies);
	}
	
	public void updateMovieGenres(int movieId, List<Integer> genres, GenreController genreController) {
		movieService.updateMovieGenres(movieId, genres, genreController);
	}
	
	public void updateMovieActors(int movieId, List<util.DiscoverReflectionMain.actorCharacter> ac) {
		movieService.updateMovieActors(movieId, ac);
	}
	
	public void updateMovieDirectors(int movieId, List<entity.Person> directors) {
		movieService.updateMovieDirectors(movieId, directors);
	}
}
