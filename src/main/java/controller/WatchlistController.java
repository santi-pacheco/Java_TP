package controller;
import entity.Watchlist;
import service.WatchlistService;
import java.util.List;

public class WatchlistController {
	
	private WatchlistService watchlistService;
	
	
	public WatchlistController(WatchlistService watchlistService) {
		this.watchlistService = watchlistService;
	}
	
	public Watchlist getWatchlists(int id_user) {
		Watchlist wl = watchlistService.getWatchlist(id_user );
		System.out.println("Watchlists retrieved successfully: " + wl.getMovies() + " records");
		return wl;
	}
	
	public List<String> getMoviesInWatchlist(int id_user) {
		System.out.println("Fetching from controller " + id_user);
		Watchlist wl = watchlistService.getWatchlist(id_user);
		return wl.getMovies();
	}
	
	public void addMovie(int id_user, String id_pelicula) {
		watchlistService.addMovie(id_user, id_pelicula);
		System.out.println("Movie added to watchlist successfully.");
	}
	
	public void removeMovie(int id_user, String id_pelicula) {
		watchlistService.removeMovie(id_user, id_pelicula);
		System.out.println("Movie removed from watchlist successfully.");
	}
	
}
