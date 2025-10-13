package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.List;
import entity.Movie;
import repository.MovieRepository;
import exception.ErrorFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
	    // 2. Si existe, ahora sí actualiza
	    return movieRepository.update(movie);	
	}
	
	public Movie deleteMovie(Movie movie) {
		Movie movieToDelete = movieRepository.delete(movie);
		return movieToDelete;
	}
	
	public void saveAllMovies(List<Movie> movies) {
		movieRepository.saveAll(movies);
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
	
}
