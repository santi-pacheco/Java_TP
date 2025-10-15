package entity;

import java.util.ArrayList;


public class Watchlist {
	private int id_list;
	private String name;
	private int id_user;
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
	
	public int getNroLista() {
		return id_list;
	}
	
	public void setNroLista(int nroLista) {
		this.id_list = nroLista;
	}
	
	public String getListName() {
		return name;
	}
	
	public void setListName(String listName) {
		this.name = listName;
	}
	
	public int getId_user() {
		return id_user;
	}
	
	public void setId_user(int id_user) {
		this.id_user = id_user;
	}
	
	public boolean removeMovie(String movie) {
		Boolean remove = this.movies.remove(String.valueOf(movie));
		return remove;
	}
	
	
}
