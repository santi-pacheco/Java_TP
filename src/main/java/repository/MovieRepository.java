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
	 id_imdb / getId_imdb() setId_imdb(String id_imdb)
	 */
	public List<Movie> findAll() {
	    List<Movie> movies = new ArrayList<>();
	    String sql = "SELECT id_pelicula, id_api, name, sinopsis, duracion, adulto, titulo_original, puntuacion_api, idioma_original, poster_path, popularidad, votos_api, anioEstreno, id_imdb FROM peliculas ORDER BY id_pelicula";
	    
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
	            movie.setId_imdb(rs.getString("id_imdb"));
	            
	            movies.add(movie);
	        }
	    } catch (SQLException e) {
	    	throw ErrorFactory.internal("Error fetching movies from database");
	    }
	    return movies;
	}
	public Movie findOne(int id) {
		Movie movie = null;
		String sql = "SELECT id_pelicula, id_api, name, sinopsis, duracion, adulto, titulo_original, puntuacion_api, idioma_original, poster_path, popularidad, votos_api, anioEstreno, id_imdb FROM peliculas WHERE id_api = ?";
		
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
		            movie.setId_imdb(rs.getString("id_imdb"));
				}
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching movie with ID " + id + " from database");
		}
		return movie;
	}
	public Movie add(Movie m) {
		String sql = "INSERT INTO peliculas (id_api, name, sinopsis, duracion, adulto, titulo_original, puntuacion_api, idioma_original, poster_path, popularidad, votos_api, anioEstreno, id_imdb) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
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
			stmt.setString(13, m.getId_imdb());
			
			int affectedRows = stmt.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						m.setId(generatedKeys.getInt(1));
					}
				}
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) { // Código SQL para violación de restricción de unicidad en PostgreSQL
				throw ErrorFactory.duplicate("A movie with the same API ID already exists.");
			} else {
				throw ErrorFactory.internal("Error adding movie to database");
			}
		}
		return m;
	}
	
	public Movie update(Movie m) {
		String sql = "UPDATE peliculas SET id_api = ?, name = ?, sinopsis = ?, duracion = ?, adulto = ?, titulo_original = ?, puntuacion_api = ?, idioma_original = ?, poster_path = ?, popularidad = ?, votos_api = ?, anioEstreno = ?, id_imdb = ? WHERE id_pelicula = ?";
		
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
			stmt.setString(13, m.getId_imdb());
			stmt.setInt(14, m.getId());
			
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) { // Código SQL para violación de restricción de unicidad en PostgreSQL
				throw ErrorFactory.duplicate("A movie with the same API ID already exists.");
			} else {
				throw ErrorFactory.internal("Error updating movie in database");
			}
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
		String sql = "INSERT INTO peliculas (id_api, name, sinopsis, duracion, adulto, titulo_original, puntuacion_api, idioma_original, poster_path, popularidad, votos_api, anioEstreno, id_imdb) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id_api = VALUES(id_api), name = VALUES(name), sinopsis = VALUES(sinopsis), duracion = VALUES(duracion), adulto = VALUES(adulto), titulo_original = VALUES(titulo_original), puntuacion_api = VALUES(puntuacion_api), idioma_original = VALUES(idioma_original), poster_path = VALUES(poster_path), popularidad = VALUES(popularidad), votos_api = VALUES(votos_api), anioEstreno = VALUES(anioEstreno), id_imdb = VALUES(id_imdb)";
		
		 // Usamos try-with-resources para asegurar que la conexión y el statement se cierren automáticamente
		
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
				stmt.setString(13, m.getId_imdb());
				
				stmt.addBatch();
			}
			stmt.executeBatch();
			
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error saving movies to database");
		}
	}
	
	public void updateMovieGenres(int movieId, List<Integer> genres) {
		String sql = "INSERT INTO generos_peliculas (id_pelicula, id_genero) VALUES (?, ?)";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			for (Integer genreId : genres) {
				stmt.setInt(1, movieId);
				stmt.setInt(2, genreId);
				stmt.addBatch();
			}
			stmt.executeBatch();
			
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error updating movie genres in database");
		}
		
	}
	
	//Antes hay tener esto en cuenta:
	//TABLE actor_pelicula
	//id_persona INT NOT NULL,
    //id_pelicula INT NOT NULL,
    //character_name VARCHAR(255) NULL,
	//Hay que crear tuplas de esta tabla. Me llega un id de una pelicula, y me llega una
	//lista de actorCharacter, que tiene id_persona y character_name.
	//Entonces por cada id_persona y character_name creo una tupla actor_pelicula
	//para ese unico id_pelicula.
	public void updateMovieActors(int movieId, List<util.DiscoverReflectionMain.actorCharacter> ac) {
		String sql = "INSERT INTO actores_peliculas (id_persona, id_pelicula, character_name) VALUES (?, ?, ?)";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			for (util.DiscoverReflectionMain.actorCharacter actorChar : ac) {
				stmt.setInt(1, actorChar.getIdActor());
				stmt.setInt(2, movieId);
				stmt.setString(3, actorChar.getCharacter());
				stmt.addBatch();
			}
			stmt.executeBatch();
			
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error updating movie actors in database");
		}
	}
	
	
	//Ahora es lo mismo... La tabla es:
	//TABLE director_pelicula
    //id_persona INT NOT NULL,
    //id_pelicula INT NOT NULL,
	//Hay que hacer lo mismo que en actores, pero sin character_name.
	
	public void updateMovieDirectors(int movieId, List<entity.Person> directors) {
		String sql = "INSERT INTO directores_peliculas (id_persona, id_pelicula) VALUES (?, ?)";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			for (entity.Person director : directors) {
				stmt.setInt(1, director.getId());
				stmt.setInt(2, movieId);
				stmt.addBatch();
			}
			stmt.executeBatch();
			
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error updating movie directors in database");
		}
	}
	
	public List<Movie> findByName(String searchTerm) {
		List<Movie> movies = new ArrayList<>();
		String sql = "SELECT DISTINCT p.id_pelicula, p.id_api, p.name, p.sinopsis, p.duracion, p.adulto, p.titulo_original, p.puntuacion_api, p.idioma_original, p.poster_path, p.popularidad, p.votos_api, p.anioEstreno, p.id_imdb " +
					 "FROM peliculas p " +
					 "LEFT JOIN actores_peliculas ap ON p.id_pelicula = ap.id_pelicula " +
					 "LEFT JOIN directores_peliculas dp ON p.id_pelicula = dp.id_pelicula " +
					 "LEFT JOIN personas per_actor ON ap.id_persona = per_actor.id_persona " +
					 "LEFT JOIN personas per_director ON dp.id_persona = per_director.id_persona " +
					 "WHERE p.name LIKE ? OR p.titulo_original LIKE ? OR per_actor.name LIKE ? OR per_director.name LIKE ? " +
					 "ORDER BY p.popularidad DESC LIMIT 20";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			String searchPattern = "%" + searchTerm + "%";
			stmt.setString(1, searchPattern);
			stmt.setString(2, searchPattern);
			stmt.setString(3, searchPattern);
			stmt.setString(4, searchPattern);
			
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					movies.add(mapResultSetToMovie(rs));
				}
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error searching movies by name");
		}
		return movies;
	}
	
	public List<Movie> findMostPopular(int limit) {
		List<Movie> movies = new ArrayList<>();
		String sql = "SELECT id_pelicula, id_api, name, sinopsis, duracion, adulto, titulo_original, puntuacion_api, idioma_original, poster_path, popularidad, votos_api, anioEstreno, id_imdb FROM peliculas ORDER BY popularidad DESC LIMIT ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, limit);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					movies.add(mapResultSetToMovie(rs));
				}
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching most popular movies");
		}
		return movies;
	}
	
	public List<Movie> findTopRated(int limit) {
		List<Movie> movies = new ArrayList<>();
		String sql = "SELECT id_pelicula, id_api, name, sinopsis, duracion, adulto, titulo_original, puntuacion_api, idioma_original, poster_path, popularidad, votos_api, anioEstreno, id_imdb FROM peliculas WHERE puntuacion_api > 7.0 ORDER BY puntuacion_api DESC LIMIT ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, limit);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					movies.add(mapResultSetToMovie(rs));
				}
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching top rated movies");
		}
		return movies;
	}
	
	public List<Movie> findRecentMovies(int limit) {
		List<Movie> movies = new ArrayList<>();
		String sql = "SELECT id_pelicula, id_api, name, sinopsis, duracion, adulto, titulo_original, puntuacion_api, idioma_original, poster_path, popularidad, votos_api, anioEstreno, id_imdb FROM peliculas WHERE anioEstreno >= 2020 ORDER BY anioEstreno DESC, popularidad DESC LIMIT ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, limit);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					movies.add(mapResultSetToMovie(rs));
				}
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching recent movies");
		}
		return movies;
	}
	
	public List<Movie> findRandom(int limit) {
		List<Movie> movies = new ArrayList<>();
		String sql = "SELECT id_pelicula, id_api, name, sinopsis, duracion, adulto, titulo_original, puntuacion_api, idioma_original, poster_path, popularidad, votos_api, anioEstreno, id_imdb FROM peliculas ORDER BY RAND() LIMIT ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, limit);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					movies.add(mapResultSetToMovie(rs));
				}
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching random movies");
		}
		return movies;
	}
	
	private Movie mapResultSetToMovie(ResultSet rs) throws SQLException {
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
		movie.setId_imdb(rs.getString("id_imdb"));
		return movie;
	}
}