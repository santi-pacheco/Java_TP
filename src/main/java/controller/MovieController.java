package controller;

import java.util.List;

import service.MovieService;
import entity.Country;
import entity.Movie;
import java.util.Map;

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
	
	public void updateMovieActors(List<Object[]> relations) {
		movieService.updateMovieActors(relations);
	}
	
	public void updateMovieDirectors(List<Object[]> relations) {
		movieService.updateMovieDirectors(relations);
	}
	
	public List<Movie> searchMoviesByName(String searchTerm) {
		return movieService.searchMoviesByName(searchTerm);
	}
	
	public List<Movie> getMostPopularMovies(int limit) {
		return movieService.getMostPopularMovies(limit);
	}
	
	public List<Movie> getTopRatedMovies(int limit) {
		return movieService.getTopRatedMovies(limit);
	}
	
	public List<Movie> getRecentMovies(int limit) {
		return movieService.getRecentMovies(limit);
	}
	
	
	public List<Movie> getMovieByFilter(String name, String genre, int year1, int year2) {
		return movieService.getMovieByFilter(name, genre, year1, year2);
	}
	
	public List<Country> getCountriesByMovieId(int movieId) {
	    return movieService.getCountriesByMovieId(movieId);
	}

	public Map<Integer, Integer> getMoviesByApiIds(List<Integer> movieApiIds) {
		return movieService.getMoviesByApiIds(movieApiIds);
	}
	
	public void saveAllMovieGenres(List<Object[]> relacionesMovieGenre) {
		movieService.saveAllMovieGenres(relacionesMovieGenre);
	}
	
	public void updateBatchMovies(List<Movie> movies) {
		movieService.updateBatchMovies(movies);
	}
	
}
