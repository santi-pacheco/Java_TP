package controller;

import service.MovieService;
import entity.Movie;

public class MovieController {

	private MovieService movieService;
	
	public MovieController(MovieService movieService) {
		this.movieService = movieService;
	}
	
	public boolean addMovie(Movie m) {
		try {
			Movie createdMovie = movieService.createMovie(m);
			if (createdMovie != null) {
	            return true;
	        } else {
	            return false;
	        }
		} catch (Exception ex) {
			throw new RuntimeException("Error adding movie", ex);
		}
	}
	
}
