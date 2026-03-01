package controller;
import entity.Watchlist;
import service.WatchlistService;
import java.util.List;

public class WatchlistController {
	
	private WatchlistService watchlistService;
	
	
	public WatchlistController(WatchlistService watchlistService) {
		this.watchlistService = watchlistService;
	}
	
	public Watchlist getWatchlists(int userId) {
		Watchlist wl = watchlistService.getWatchlist(userId);
		System.out.println("Watchlists retrieved successfully: " + wl.getMovies() + " records");
		return wl;
	}

	public List<String> getMoviesInWatchlist(int userId) {
		System.out.println("Fetching from controller " + userId);
		Watchlist wl = watchlistService.getWatchlist(userId);
		return wl.getMovies();
	}

	public void addMovie(int userId, String movieId) {
		watchlistService.addMovie(userId, movieId);
		System.out.println("Movie added to watchlist successfully.");
	}

	public void removeMovie(int userId, String movieId) {
		watchlistService.removeMovie(userId, movieId);
		System.out.println("Movie removed from watchlist successfully.");
	}
	
}
