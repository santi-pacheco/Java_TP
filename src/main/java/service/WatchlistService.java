package service;

import repository.MovieRepository;
import repository.WatchlistRepository;
import java.util.List;
import entity.Watchlist;
import service.UserService;
import entity.User;
import entity.Movie;
import service.MovieService;;

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
	
	public Watchlist getWatchlist(int id_user) {
		//Verificamos si existe el usuario
		System.out.println("ID USUARIO EN SERVICE: " + id_user);
		
		User user = userService.getUserById(id_user);
		if (user != null) {
			Watchlist wl = watchlistRepository.findOne(id_user);
			return wl;
		} else {
			throw new IllegalArgumentException("User with ID " + id_user + " does not exist.");
		}
		
	}
	
	
	
	
	public void addMovie(int id_user, String id_pelicula) {
		//Verificamos si existe el usuario
		Movie movie = movieService.getMovieById(Integer.parseInt(id_pelicula));
		
		User user = userService.getUserById(id_user);
		if (user != null && movie != null) {
			
			Watchlist wl = watchlistRepository.addMovie(id_user, Integer.parseInt(id_pelicula));
			
		} else {
			throw new IllegalArgumentException("User with ID " + id_user + " does not exist.");
		}
	}
			
	public void removeMovie(int id_user, String id_pelicula) {
		//Verificamos si existe el usuario
		System.out.println("ID PELICULA: " + id_pelicula);
		System.out.println("ID USUARIO: " + id_user);
		Movie movie = movieService.getMovieById(Integer.parseInt(id_pelicula));
		System.out.println("MOVIE: " + movie);
		User user = userService.getUserById(id_user);
		if (user != null && movie != null) {
			
			watchlistRepository.deleteMovie(id_user, id_pelicula);
			
		} else {
			throw new IllegalArgumentException("User with ID " + id_user + " does not exist.");
		}
		
	}
}
