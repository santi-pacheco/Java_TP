package service;

import repository.MovieRepository;
import repository.WatchlistRepository;
import java.util.List;
import entity.Watchlist;
import exception.ErrorFactory;
import service.UserService;
import entity.User;
import entity.Movie;
import service.MovieService;

public class WatchlistService {
	
	private WatchlistRepository watchlistRepository;
	private UserService userService;
	private MovieService movieService;
	private MovieRepository movieRepository;
	
	
	public WatchlistService(WatchlistRepository watchlistRepository, UserService userService, MovieService movieService) {
		this.watchlistRepository = watchlistRepository;
		this.userService = userService;
		this.movieService = movieService;
	}
	
    public Watchlist getWatchlist(int userId) {
        System.out.println("ID USUARIO EN SERVICE: " + userId);

        User user = userService.getUserById(userId);
		if (user != null) {
			Watchlist wl = watchlistRepository.findOne(userId);
			return wl;
		} else {
			throw ErrorFactory.notFound("User with ID " + userId + " does not exist.");
		}
		
	}
	
	
	
	
	public void addMovie(int userId, String movieId) {
		Movie movie = movieService.getMovieById(Integer.parseInt(movieId));

		User user = userService.getUserById(userId);
		if (user != null && movie != null) {

			Watchlist wl = watchlistRepository.addMovie(Integer.parseInt(movieId), userId);

		} else {
			throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
		}
	}

	public void removeMovie(int userId, String movieId) {
		Movie movie = movieService.getMovieById(Integer.parseInt(movieId));
		User user = userService.getUserById(userId);
		if (user != null && movie != null) {

			watchlistRepository.deleteMovie(userId, movieId);

		} else {
			throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
		}
	}
}
