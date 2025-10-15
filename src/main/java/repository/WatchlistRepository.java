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
		System.out.println("🔍 Buscando watchlist del usuario con ID desde el repositorio: " + id_user);
		Watchlist wl = new Watchlist();
		String sql = "Select * from watchlists_peliculas where id_list = 1 and id_user = ?";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				) {
			boolean hasData = false;
			stmt.setInt(1, id_user);
			ResultSet rs = stmt.executeQuery();
			System.out.println("Resultado de la consulta: " + rs);
			ArrayList<String> movies = new ArrayList<String>();
			while (rs.next()) {
				System.out.println("Procesando fila de resultado: id_list=" + rs.getInt("id_list") + ", id_user=" + rs.getInt("id_user") + ", id_pelicula=" + rs.getString("id_api"));
				if (!hasData) {
	                // Primera vez, setea los datos de la watchlist
	                wl.setNroLista(rs.getInt("id_list"));
	                wl.setId_user(rs.getInt("id_user"));
	                hasData = true;
	            }
				movies.add(rs.getString("id_api"));
			}
			wl.setMovies(movies);
			System.out.println("Peliculas encontradas" + wl.getMovies());
		} catch (SQLException e) {
			throw new RuntimeException("Error fetching watchlists from database", e);
		}
		return wl;
	}
	
	
	public void addWatchlist (int id_user) {
		String sql = "INSERT INTO watchlists (id_list, name, id_user) VALUES (?,?,?)";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, 1);
			stmt.setString(2, "Mi Watchlist");
			stmt.setInt(3, id_user);
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Watchlist created successfully.");
			}
			conn.close();
		} catch(SQLException e) {
			throw new RuntimeException("Error creating watchlist", e);
		}
	}
	
	public Watchlist addMovie(int id_movie, int id_user) {
		
			Watchlist wl = new Watchlist();
			String sql = "INSERT INTO watchlists_peliculas (id_list, id_user, id_api) VALUES (?,?,?)";
			try (Connection conn = DataSourceProvider.getDataSource().getConnection();
					PreparedStatement stmt = conn.prepareStatement(sql)) {
					
				stmt.setInt(1, 1);
				stmt.setInt(2, id_user);
				stmt.setInt(3, id_movie);
				int rowsAffected = stmt.executeUpdate();
				conn.close();
				System.out.println(
						"Movie with ID " + id_movie + " added to watchlist for user ID " + id_user + "." + rowsAffected); 
				
			} catch(SQLException e) {
				throw new RuntimeException("Error adding movie to watchlist", e);
			}
			return wl;
		
	}
	
	public void deleteMovie(int id_user, String id_movie) {
		Watchlist wl = new Watchlist(); // Ver que hacer con esto
		String sql = "DELETE FROM Watchlists_peliculas WHERE id_user = ? AND id_api = ? AND id_list = 1";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id_user);
			stmt.setString(2, id_movie);
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Movie removed from watchlist successfully.");
			}
			conn.close();
		}
		catch(SQLException e) {
			throw new RuntimeException("Error removing movie from watchlist", e);
		}
	}
}
