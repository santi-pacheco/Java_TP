package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import entity.Country;
import entity.Movie;
import repository.MovieRepository;
import exception.ErrorFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import controller.GenreController;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Map;

public class MovieService {
    
    private MovieRepository movieRepository;
    
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }
    
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }
    
    public Movie getMovieById(int id) {
        if (id <= 0) {
            throw ErrorFactory.badRequest("ID de película inválido.");
        }
        Movie movie = movieRepository.findOne(id);
        if (movie == null) {
            throw ErrorFactory.notFound("Película no encontrada con ID: " + id);
        }
        return movie;
    }
    
    public Movie createMovie(Movie movie) {
        if (movie == null) {
            throw ErrorFactory.badRequest("La película no puede ser nula.");
        }
        return movieRepository.add(movie);
    }
    
    public Movie updateMovie(Movie movie) {
        if (movie == null || movie.getMovieId() <= 0) {
            throw ErrorFactory.badRequest("Datos de película inválidos para actualizar.");
        }
        
        Movie existingMovie = movieRepository.findOne(movie.getMovieId());
        if (existingMovie == null) {
            throw ErrorFactory.notFound("No se puede actualizar. Película con ID " + movie.getMovieId() + " no encontrada.");
        }
        
        existingMovie.setReleaseYear(movie.getReleaseYear());
        existingMovie.setDuration(movie.getDuration());
        existingMovie.setIsAdult(movie.getIsAdult());
        existingMovie.setTitle(movie.getTitle());
        existingMovie.setPopularity(movie.getPopularity());
        existingMovie.setOriginalTitle(movie.getOriginalTitle());
        existingMovie.setSynopsis(movie.getSynopsis());
        existingMovie.setApiRating(movie.getApiRating());
        existingMovie.setOriginalLanguage(movie.getOriginalLanguage());
        existingMovie.setPosterPath(movie.getPosterPath());
        existingMovie.setApiId(movie.getApiId());
        existingMovie.setApiVotes(movie.getApiVotes());
        existingMovie.setImdbId(movie.getImdbId());
        
        return movieRepository.update(existingMovie);   
    }
    
    public void deleteMovie(Movie movie) {
        if (movie != null && movie.getMovieId() > 0) {
            movieRepository.delete(movie);
        }
    }
    
    public void saveAllMovies(List<Movie> movies) {
        if (movies != null && !movies.isEmpty()) {
            movieRepository.saveAll(movies);
        }
    }
    
    public List<Movie> searchMoviesByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return movieRepository.findByName(searchTerm.trim());
    }
    
    public float getMovieRating(int movieId) throws IOException, InterruptedException {
        try {
            Movie movie = movieRepository.findOne(movieId);
            if (movie == null || movie.getImdbId() == null || movie.getImdbId().isEmpty()) {
                return 0.0f;
            }
            
            URL url = new URL("https://api.imdbapi.dev/titles/" + movie.getImdbId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(reader.readLine(), JsonObject.class);
                
                if (json != null && json.has("rating") && !json.get("rating").isJsonNull()) {
                    JsonObject ratingObj = json.getAsJsonObject("metacritic");
                    if (ratingObj != null && ratingObj.has("score")) {
                        return ratingObj.get("score").getAsFloat();
                    }
                }
            } finally {
                conn.disconnect();
            }
        } catch (IOException e) {
            throw e;
        }
        return 0.0f; 
    }
    
    public void updateMovieGenres(int movieId, List<Integer> genres, GenreController genreController) {
        if (movieId <= 0 || genres == null || genres.isEmpty()) return;
        
        Movie movie = movieRepository.findOne(movieId);
        if (movie == null) {
            throw ErrorFactory.notFound("Película no encontrada con ID: " + movieId);
        }
        
        List<Integer> genresId = genres.stream()
                 .map(idApi -> genreController.getGeneresByIdApi(idApi)) 
                 .filter(Objects::nonNull)                               
                 .distinct()                                             
                 .collect(Collectors.toList());
                 
        if (!genresId.isEmpty()) {
            movieRepository.updateMovieGenres(movieId, genresId);
        }
    }
    
    public void updateMovieActors(List<Object[]> relations) {
        if (relations != null && !relations.isEmpty()) {
            movieRepository.updateMovieActors(relations);
        }
    }
    
    public void updateMovieDirectors(List<Object[]> relations) {
        if (relations != null && !relations.isEmpty()) {
            movieRepository.updateMovieDirectors(relations);
        }
    }
    
    public List<Movie> getMostPopularMovies(int limit) {
        return movieRepository.findMostPopular(limit > 0 ? limit : 10);
    }
    
    public List<Movie> getTopRatedMovies(int limit) {
        return movieRepository.findTopRated(limit > 0 ? limit : 10);
    }
    
    public List<Movie> getRecentMovies(int limit) {
        return movieRepository.findRecentMovies(limit > 0 ? limit : 10);
    }
        
    public List<Movie> getMovieByFilter(String name, String genre, int year1, int year2) {
        return movieRepository.movieFilter(name, genre, year1, year2);
    }
    
    public List<Country> getCountriesByMovieId(int movieId) {
        if (movieId <= 0) return new ArrayList<>();
        return movieRepository.getCountriesByMovieId(movieId);
    }

    public void updateReviewStats(int movieId) {
        if (movieId > 0) {
            movieRepository.updateReviewStats(movieId);
        }
    }
    
    public Map<Integer, Integer> getMoviesByApiIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return new HashMap<>();
        return movieRepository.findAllByApiIds(ids);
    }
    
    public void saveAllMovieGenres(List<Object[]> relacionesMovieGenre) {
        if (relacionesMovieGenre != null && !relacionesMovieGenre.isEmpty()) {
            movieRepository.saveAllMovieGenres(relacionesMovieGenre);
        }
    }
    
    public void updateBatchMovies(List<Movie> movies) {
        if (movies != null && !movies.isEmpty()) {
            movieRepository.updateBatch(movies);
        }
    }
    
    public List<Movie> getRandomMovies(int limit) {
        return movieRepository.findRandom(limit > 0 ? limit : 10);
    }
}