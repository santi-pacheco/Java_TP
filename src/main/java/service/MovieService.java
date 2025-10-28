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
	

    public float getMovieRating(int movieId) throws IOException, InterruptedException {
    	System.out.println("📡 Obteniendo rating para movieId: " + movieId);
    	
    	try {
    		// Primero obtenemos la pelicula para sacar su id_imdb
    		Movie movie = movieRepository.findOne(movieId);
    		if (movie == null) {
    			// Comprobamos si la pelicula existe
				System.out.println("❌ Movie with ID " + movie.getId_imdb() + " not found in the database.");
				return 0.0f;
			}
			// Hacemos la llamada a la API externa
    		URL url = new URL("https://api.imdbapi.dev/titles/" + movie.getId_imdb() );
    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    		conn.setRequestMethod("GET");
    		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    		StringBuilder response = new StringBuilder();
    		Gson gson = new Gson();
    		JsonObject json = gson.fromJson(reader.readLine(), JsonObject.class);
			reader.close();
			conn.disconnect();
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
    	return 0.0f; // Valor por defecto si hay errores
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
}