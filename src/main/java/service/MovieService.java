package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import entity.Movie;
import repository.MovieRepository;
import exception.ErrorFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import controller.GenreController;
import java.util.Objects;
import java.util.stream.Collectors;


public class MovieService {
	
	private MovieRepository movieRepository;
	
	public MovieService(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}
	
	public List<Movie> getAllMovies() {
		return movieRepository.findAll();
	}
	
	public Movie getMovieById(int id) {
		Movie movie = movieRepository.findOne(id);
		if (movie == null) {
			throw ErrorFactory.notFound("Movie not found with ID: " + id);
		}
		return movie;
	}
	public Movie createMovie(Movie movie) {
		return movieRepository.add(movie);
	}
	public Movie updateMovie(Movie movie) {
		// 1. Primero, verifica que la película exista
	    Movie existingMovie = movieRepository.findOne(movie.getId());
	    if (existingMovie == null) {
	        throw ErrorFactory.notFound("No se puede actualizar. Película con ID " + movie.getId() + " no encontrada.");
	    }
	    existingMovie.setEstrenoYear(movie.getEstrenoYear());
	    existingMovie.setDuracion(movie.getDuracion());
	    existingMovie.setAdulto(movie.getAdulto());
	    existingMovie.setTitulo(movie.getTitulo());
	    existingMovie.setPopularidad(movie.getPopularidad());
	    existingMovie.setTituloOriginal(movie.getTituloOriginal());
	    existingMovie.setSinopsis(movie.getSinopsis());
	    existingMovie.setPuntuacionApi(movie.getPuntuacionApi());
	    existingMovie.setIdiomaOriginal(movie.getIdiomaOriginal());
	    existingMovie.setPosterPath(movie.getPosterPath());
	    
	    // 2. Si existe, ahora sí actualiza
	    return movieRepository.update(existingMovie);	
	}
	
	public void deleteMovie(Movie movie) {
		movieRepository.delete(movie);
	}
	
	public void saveAllMovies(List<Movie> movies) {
		movieRepository.saveAll(movies);
	}
	
	public List<Movie> searchMoviesByName(String searchTerm) {
		if (searchTerm == null || searchTerm.trim().isEmpty()) {
			return new ArrayList<>();
		}
		return movieRepository.findByName(searchTerm.trim());
	}
	

    public static float getMovieRating(String movieId) throws IOException, InterruptedException {
    	System.out.println("📡 Obteniendo rating para movieId: " + movieId);
    	
    	try {
    		URL url = new URL("https://api.imdbapi.dev/titles/" + movieId );
    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    		conn.setRequestMethod("GET");
    		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    		StringBuilder response = new StringBuilder();
    		Gson gson = new Gson();
    		JsonObject json = gson.fromJson(reader.readLine(), JsonObject.class);
			reader.close();
            if (json.has("rating") && !json.get("rating").isJsonNull()) {
            	System.out.println("Rating encontrado: " + json.get("metacritic").toString());
                JsonObject ratingObj = json.getAsJsonObject("metacritic");
                
                System.out.println("Rating: " + ratingObj.get("score").getAsFloat());
                return ratingObj.get("score").getAsFloat();
            }
    		
    	} catch (IOException e) {
			System.err.println("❌ Error al obtener rating: " + e.getMessage());
			throw e;
		}
    	return 0.0f; // Valor por defecto si hay errorS
    }
	
	
	public void updateMovieGenres(int movieId, List<Integer> genres, GenreController genreController) {
		Movie movie = movieRepository.findOne(movieId);
		if (movie == null) {
			throw ErrorFactory.notFound("Movie not found with ID: " + movieId);
		}
		 List<Integer> genresId = genres.stream()                        // 1. Convierte la lista en un "flujo" de datos.
                 .map(idApi -> genreController.getGeneresByIdApi(idApi)) // 2. Para cada idApi, llama al método.
                 .filter(Objects::nonNull)                               // 3. Filtra y descarta cualquier resultado que sea null.
                 .distinct()                                             // 4. Elimina todos los duplicados.
                 .collect(Collectors.toList());
		System.out.println("Genres to update: " + genresId);
		System.out.println("Id of movie to update genres: " + movieId);
		//genresId puede tener algún null? No, porque se filtran en el stream
		movieRepository.updateMovieGenres(movieId, genresId);
	}
	
	public void updateMovieActors(int movieId, List<util.DiscoverReflectionMain.actorCharacter> ac) {
		movieRepository.updateMovieActors(movieId, ac);
	}
	
	public void updateMovieDirectors(int movieId, List<entity.Person> directors) {
		movieRepository.updateMovieDirectors(movieId, directors);
	}
	
	public List<Movie> getMostPopularMovies(int limit) {
		return movieRepository.findMostPopular(limit);
	}
	
	public List<Movie> getTopRatedMovies(int limit) {
		return movieRepository.findTopRated(limit);
	}
	
	public List<Movie> getRecentMovies(int limit) {
		return movieRepository.findRecentMovies(limit);
	}
}