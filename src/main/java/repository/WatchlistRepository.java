package repository;
import entity.Watchlist;

import java.util.ArrayList;
import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import util.DataSourceProvider;
import repository.MovieRepository;

public class WatchlistRepository {
	
	private MovieRepository movieRepository;
	
	public WatchlistRepository(MovieRepository movieRepository) {
		this.movieRepository = new MovieRepository();
	}
	
	public Watchlist findAll(int id_user){
		Watchlist wl = new Watchlist();
		
		String sql = "Select * from watchlist where id_list = ? and id_user = ?"; 
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				) {
			
			stmt.setInt(1, id_user);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				wl.setNroLista(rs.getInt("id_list"));
				wl.setListName(rs.getString("name"));
				wl.setId_user(rs.getInt("id_user"));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error fetching watchlists from database", e);
		}
		
		
		return wl;
	}
	
	public Watchlist findOne(int id_user) {
		Watchlist wl = new Watchlist();
		String sql = "SELECT w.id_list, w.id_user, wp.id_pelicula FROM watchlists w LEFT JOIN watchlists_peliculas wp ON w.id_list = wp.id_list WHERE w.id_user = ?";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			boolean hasData = false;
			stmt.setInt(1, id_user);
			ResultSet rs = stmt.executeQuery();
			ArrayList<String> movies = new ArrayList<String>();
			while (rs.next()) {
				if (!hasData) {
	                wl.setNroLista(rs.getInt("id_list"));
	                wl.setId_user(rs.getInt("id_user"));
	                hasData = true;
	            }
				int idPelicula = rs.getInt("id_pelicula");
				if (!rs.wasNull()) {
					movies.add(String.valueOf(idPelicula));
				}
			}
			wl.setMovies(movies);
			if (!hasData) {
				wl.setNroLista(id_user);
				wl.setId_user(id_user);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error fetching watchlists from database", e);
		}
		return wl;
	}
	
	
	public void addWatchlist (int id_user) {
		String sql = "INSERT INTO watchlists (name, id_user) VALUES (?,?)";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setString(1, "Mi Watchlist");
			stmt.setInt(2, id_user);
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Watchlist created successfully.");
			}
		} catch(SQLException e) {
			throw new RuntimeException("Error creating watchlist", e);
		}
	}
	
	public Watchlist addMovie(int id_movie, int id_user) {
		
			Watchlist wl = new Watchlist();
			int idList = 0;
			
			// Obtener la watchlist del usuario
			String checkSql = "SELECT id_list FROM watchlists WHERE id_user = ? LIMIT 1";
			try (Connection conn = DataSourceProvider.getDataSource().getConnection();
					PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
				
				checkStmt.setInt(1, id_user);
				ResultSet rs = checkStmt.executeQuery();
				
				if (rs.next()) {
					idList = rs.getInt("id_list");
				} else {
					addWatchlist(id_user);
				}
			} catch(SQLException e) {
				throw new RuntimeException("Error checking watchlist", e);
			}
			
			if (idList == 0) {
				try (Connection conn = DataSourceProvider.getDataSource().getConnection();
						PreparedStatement stmt = conn.prepareStatement(checkSql)) {
					stmt.setInt(1, id_user);
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						idList = rs.getInt("id_list");
					}
				} catch(SQLException e) {
					throw new RuntimeException("Error retrieving watchlist after creation", e);
				}
			}
			
			String sql = "INSERT INTO watchlists_peliculas (id_list, id_pelicula) VALUES (?,?)";
			try (Connection conn = DataSourceProvider.getDataSource().getConnection();
					PreparedStatement stmt = conn.prepareStatement(sql)) {
					
				stmt.setInt(1, idList);
				stmt.setInt(2, id_movie);
				int rowsAffected = stmt.executeUpdate();
				System.out.println(
						"Movie with ID " + id_movie + " added to watchlist for user ID " + id_user + "." + rowsAffected); 
				
			} catch(SQLException e) {
				throw new RuntimeException("Error adding movie to watchlist", e);
			}
			return wl;
		
	}
	
	public void deleteMovie(int id_user, String id_movie) {
		String checkSql = "SELECT id_list FROM watchlists WHERE id_user = ?";
		String sql = "DELETE FROM watchlists_peliculas WHERE id_list = ? AND id_pelicula = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection()) {
			int idList = 0;
			try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
				checkStmt.setInt(1, id_user);
				ResultSet rs = checkStmt.executeQuery();
				if (rs.next()) {
					idList = rs.getInt("id_list");
				}
			}
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, idList);
				stmt.setInt(2, Integer.parseInt(id_movie));
				stmt.executeUpdate();
			}
		} catch(SQLException e) {
			throw new RuntimeException("Error removing movie from watchlist", e);
		}
	}
}
