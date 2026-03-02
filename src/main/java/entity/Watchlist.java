package entity;

import java.util.ArrayList;


public class Watchlist {
	private int watchlistId;
	private String name;
	private int userId;
	private ArrayList<String> movies;

	public Watchlist() {

	}

	public ArrayList<String> getMovies() {
		return movies;
	}

	public void setMovies(ArrayList<String> movies) {
		this.movies = movies;
	}

	public void addMovie(String movie) {
		this.movies.add(movie);
	}

	public int getWatchlistId() {
		return watchlistId;
	}

	public void setWatchlistId(int watchlistId) {
		this.watchlistId = watchlistId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public boolean removeMovie(String movie) {
		return this.movies.remove(String.valueOf(movie));
	}
}
