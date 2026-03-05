package service;

import repository.WatchlistRepository;
import entity.Watchlist;
import exception.ErrorFactory;
import entity.User;
import entity.Movie;

public class WatchlistService {
    
    private WatchlistRepository watchlistRepository;
    private UserService userService;
    private MovieService movieService;
    

    public WatchlistService(WatchlistRepository watchlistRepository, UserService userService, MovieService movieService) {
        this.watchlistRepository = watchlistRepository;
        this.userService = userService;
        this.movieService = movieService;
    }
    
    public Watchlist getWatchlist(int userId) {
        if (userId <= 0) {
            throw ErrorFactory.badRequest("ID de usuario inválido.");
        }
        
        User user = userService.getUserById(userId);
        if (user != null) {
            return watchlistRepository.findOne(userId);
        } else {
            throw ErrorFactory.notFound("User with ID " + userId + " does not exist.");
        }
    }
    
    public void addMovie(int userId, String movieId) {
        if (movieId == null || movieId.trim().isEmpty()) {
            throw ErrorFactory.badRequest("El ID de la película es inválido.");
        }

        try {
            int parsedMovieId = Integer.parseInt(movieId);
            Movie movie = movieService.getMovieById(parsedMovieId);
            User user = userService.getUserById(userId);
            
            if (user != null && movie != null) {
                watchlistRepository.addMovie(parsedMovieId, userId);
            } else {
                throw ErrorFactory.notFound("Usuario o Película no encontrados.");
            }
        } catch (NumberFormatException e) {
            throw ErrorFactory.badRequest("El ID de la película debe ser un número válido.");
        }
    }

    public void removeMovie(int userId, String movieId) {
        if (movieId == null || movieId.trim().isEmpty()) {
            throw ErrorFactory.badRequest("El ID de la película es inválido.");
        }
        
        try {
            int parsedMovieId = Integer.parseInt(movieId);
            Movie movie = movieService.getMovieById(parsedMovieId);
            User user = userService.getUserById(userId);
            
            if (user != null && movie != null) {
                watchlistRepository.deleteMovie(userId, movieId);
            } else {
                throw ErrorFactory.notFound("Usuario o Película no encontrados.");
            }
        } catch (NumberFormatException e) {
            throw ErrorFactory.badRequest("El ID de la película debe ser un número válido.");
        }
    }
}