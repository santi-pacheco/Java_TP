package repository;

import java.util.List;
import entity.Movie;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DataSourceProvider;
import exception.ErrorFactory;

public class MovieRepository {
	
	public MovieRepository() {
	    //Ya no se crea la conexión aquí, se obtiene en cada método usando el pool de conexiones
	}
	/*
	 "Esquema de la tabla movies en la BD"
	 id_pelicula (PK) /getId() setId(int id)
	 id_api /getId_api() setId_api(int id_api)
	 name / getTitulo() setTitulo(String titulo)
	 sinopsis / getSinopsis() setSinopsis(String sinopsis)
	 duracion / getDuracion() setDuracion(Integer duracion)
	 adulto / getAdulto() setAdulto(Boolean adulto)
	 titulo_original / getTituloOriginal() setTituloOriginal(String tituloOriginal)
	 puntuacion_api / getPuntuacionApi() setPuntuacionApi(Double puntuacionApi)
	 idioma_original / getIdiomaOriginal() setIdiomaOriginal(String idiomaOriginal)
	 poster_path / getPosterPath() setPosterPath(String posterPath)
	 popularidad / getPopularidad() setPopularidad(Double popularidad)
	 votos_api / getVotosApi() setVotosApi(Integer votosApi)
	 anioEstreno / getEstrenoYear() setEstrenoYear(int estrenoYear)
	*/
	public List<Movie> findAll() {
	    List<Movie> movies = new ArrayList<>();
	    String sql = "SELECT id_pelicula, id_api, name, sinopsis, duracion, adulto, titulo_original, puntuacion_api, idioma_original, poster_path, popularidad, votos_api, anioEstreno FROM peliculas ORDER BY id_pelicula";
	    
	    try (Connection conn = DataSourceProvider.getDataSource().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	         
	        while (rs.next()) {
	            Movie movie = new Movie();
	            movie.setId(rs.getInt("id_pelicula"));
	            movie.setId_api(rs.getInt("id_api"));
	            movie.setTitulo(rs.getString("name"));
	            movie.setSinopsis(rs.getString("sinopsis"));
	            movie.setDuracion(rs.getTime("duracion"));
	            movie.setAdulto(rs.getBoolean("adulto"));
	            movie.setTituloOriginal(rs.getString("titulo_original"));
	            movie.setPuntuacionApi(rs.getDouble("puntuacion_api"));
	            movie.setIdiomaOriginal(rs.getString("idioma_original"));
	            movie.setPosterPath(rs.getString("poster_path"));
	            movie.setPopularidad(rs.getDouble("popularidad"));
	            movie.setVotosApi(rs.getInt("votos_api"));
	            movie.setEstrenoYear(rs.getInt("anioEstreno"));
	            
	            movies.add(movie);
	        }
	    } catch (SQLException e) {
	    	throw ErrorFactory.internal("Error fetching movies from database");
	    }
	    return movies;
	}
	public Movie findOne(int id) {
		System.out.println("🔍 Buscando película con ID: " + id);
		Movie movie = null;
		String sql = "SELECT id_pelicula, id_api, name, sinopsis, duracion, adulto, titulo_original, puntuacion_api, idioma_original, poster_path, popularidad, votos_api, anioEstreno FROM peliculas WHERE id_api = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					movie = new Movie();
					movie.setId(rs.getInt("id_pelicula"));
		            movie.setId_api(rs.getInt("id_api"));
		            movie.setTitulo(rs.getString("name"));
		            movie.setSinopsis(rs.getString("sinopsis"));
		            movie.setDuracion(rs.getTime("duracion"));
		            movie.setAdulto(rs.getBoolean("adulto"));
		            movie.setTituloOriginal(rs.getString("titulo_original"));
		            movie.setPuntuacionApi(rs.getDouble("puntuacion_api"));
		            movie.setIdiomaOriginal(rs.getString("idioma_original"));
		            movie.setPosterPath(rs.getString("poster_path"));
		            movie.setPopularidad(rs.getDouble("popularidad"));
		            movie.setVotosApi(rs.getInt("votos_api"));
		            movie.setEstrenoYear(rs.getInt("anioEstreno"));
				}
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching movie with ID " + id + " from database");
		}
		return movie;
	}
	public Movie add(Movie m) {
		String sql = "INSERT INTO peliculas (id_api, name, sinopsis, duracion, adulto, titulo_original, puntuacion_api, idioma_original, poster_path, popularidad, votos_api, anioEstreno) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			
			stmt.setObject(1, m.getId_api());
			stmt.setString(2, m.getTitulo());
			stmt.setString(3, m.getSinopsis());
			stmt.setObject(4, m.getDuracion());
			stmt.setObject(5, m.getAdulto());
			stmt.setString(6, m.getTituloOriginal());
			stmt.setObject(7, m.getPuntuacionApi());
			stmt.setString(8, m.getIdiomaOriginal());
			stmt.setString(9, m.getPosterPath());
			stmt.setObject(10, m.getPopularidad());
			stmt.setObject(11, m.getVotosApi());
			stmt.setObject(12, m.getEstrenoYear());
			
			int affectedRows = stmt.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						m.setId(generatedKeys.getInt(1));
					}
				}
			}
		} catch (SQLException e) {
			if (e.getSQLState().equals("23505")) { // Código SQL para violación de restricción de unicidad en PostgreSQL
				throw ErrorFactory.duplicate("A movie with the same API ID already exists.");
			} else {
				throw ErrorFactory.internal("Error adding movie to database");
			}
		}
		return m;
	}
	
	public Movie update(Movie m) {
		String sql = "UPDATE peliculas SET id_api = ?, name = ?, sinopsis = ?, duracion = ?, adulto = ?, titulo_original = ?, puntuacion_api = ?, idioma_original = ?, poster_path = ?, popularidad = ?, votos_api = ?, anioEstreno = ? WHERE id_pelicula = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setObject(1, m.getId_api());
			stmt.setString(2, m.getTitulo());
			stmt.setString(3, m.getSinopsis());
			stmt.setObject(4, m.getDuracion());
			stmt.setObject(5, m.getAdulto());
			stmt.setString(6, m.getTituloOriginal());
			stmt.setObject(7, m.getPuntuacionApi());
			stmt.setString(8, m.getIdiomaOriginal());
			stmt.setString(9, m.getPosterPath());
			stmt.setObject(10, m.getPopularidad());
			stmt.setObject(11, m.getVotosApi());
			stmt.setObject(12, m.getEstrenoYear());
			stmt.setInt(13, m.getId());
			
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error updating movie in database");
		}
		return m;
	}
	
	public Movie delete(Movie m) {
		String sql = "DELETE FROM peliculas WHERE id_pelicula = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, m.getId());
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error deleting movie from database");
		}
		return m;
	}
	
	public void saveAll(List<Movie> movies) {
		String sql = "INSERT INTO peliculas (id_api, name, sinopsis, duracion, adulto, titulo_original, puntuacion_api, idioma_original, poster_path, popularidad, votos_api, anioEstreno) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id_api = VALUES(id_api), name = VALUES(name), sinopsis = VALUES(sinopsis), duracion = VALUES(duracion), adulto = VALUES(adulto), titulo_original = VALUES(titulo_original), puntuacion_api = VALUES(puntuacion_api), idioma_original = VALUES(idioma_original), poster_path = VALUES(poster_path), popularidad = VALUES(popularidad), votos_api = VALUES(votos_api), anioEstreno = VALUES(anioEstreno)";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			for (Movie m : movies) {
				stmt.setObject(1, m.getId_api());
				stmt.setString(2, m.getTitulo());
				stmt.setString(3, m.getSinopsis());
				stmt.setObject(4, m.getDuracion());
				stmt.setObject(5, m.getAdulto());
				stmt.setString(6, m.getTituloOriginal());
				stmt.setObject(7, m.getPuntuacionApi());
				stmt.setString(8, m.getIdiomaOriginal());
				stmt.setString(9, m.getPosterPath());
				stmt.setObject(10, m.getPopularidad());
				stmt.setObject(11, m.getVotosApi());
				stmt.setObject(12, m.getEstrenoYear());
				
				stmt.addBatch();
			}
			stmt.executeBatch();
			
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error saving movies to database");
		}
	}
}