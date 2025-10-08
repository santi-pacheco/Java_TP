package service;

import entity.Genre; // 
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.movielists.*;
import info.movito.themoviedbapi.tools.TmdbException;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.tools.builders.discover.*;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;


import java.lang.reflect.Method;
import java.util.Arrays;
public class ExternalApiService {
    
    private TmdbApi tmdbApi;
    
    public void test() {
    	Method[] methods = tmdbApi.getDiscover().getClass().getMethods();
    	for (Method m : methods) {
    	    if (m.getName().contains("getMovies")) {
    	        System.out.println(m);
    	        System.out.println(Arrays.toString(m.getParameterTypes()));
    	    }
    	}
    }
    
    public TmdbApi getTmdbApi() {
		return tmdbApi;
	}
    
    public ExternalApiService(String apiKey) {
        this.tmdbApi = new TmdbApi(apiKey);
        this.tmdbApi = new TmdbApi("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhNDdiYTBiMTI3NDk5YjBlMWIyOGNlYjBhMTgzZWM1NyIsIm5iZiI6MTc1NTYwOTMwOC44NzIsInN1YiI6IjY4YTQ3OGRjNWJkMTI3ZjcyY2RhNThjYSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._mkAgrQSPf-YCaYm1TFxuNDEgAtESQEaBOPI5t-8i8Q");
    }
    

    public List<info.movito.themoviedbapi.model.core.Genre> getMovieGenres() throws TmdbException {
        return tmdbApi.getGenre().getMovieList("es");
    }
    
 
    public entity.Genre convertToLocalGenre(info.movito.themoviedbapi.model.core.Genre tmdbGenre) {
        entity.Genre localGenre = new entity.Genre();
        localGenre.setId_api(tmdbGenre.getId());//Cambié setId por setId_api
        localGenre.setName(tmdbGenre.getName());
        return localGenre;
    }
    

    public List<entity.Genre> convertToLocalGenres(List<info.movito.themoviedbapi.model.core.Genre> tmdbGenres) {
        return tmdbGenres.stream()
                .map(this::convertToLocalGenre)
                .collect(Collectors.toList());
    }
     
    public List<info.movito.themoviedbapi.model.core.popularperson.PopularPerson> getMoviePersons() throws TmdbException {
		return tmdbApi.getPeopleLists().getPopular(null, 1).getResults();
	}
    
    public List<entity.Person> convertToLocalPersons(List<info.movito.themoviedbapi.model.core.popularperson.PopularPerson> tmdbPersons) {
		return tmdbPersons.stream()
				.map(tmdbPerson -> {
					entity.Person localPerson = new entity.Person();
					localPerson.setId(tmdbPerson.getId());
					localPerson.setName(tmdbPerson.getName());
					// Additional fields can be mapped here
					return localPerson;
				})
				.collect(Collectors.toList());
	}
    /*
    public List<info.movito.themoviedbapi.model.core.Movie> getMoviesByGenre(List<entity.Genre> genre) throws TmdbException {
    	
    	 * with_genres
    	 * language="es"
    	 * sort_by ("popularity.desc")
    	 * vote_count.gte=50
    	 * vote_average.gte=5
    	 * primary_release_year=2000
    	 * 
    	//Haz un metodo que solicite las peliculas por genero. Donde: el genero es un string de id's separados por |.
    	List<Integer> genreIds = genre.stream()
    			.map(g -> g.getId_api()) // si getId_api() ya da Integer/int
    		    .collect(Collectors.toList());
    			DiscoverMovieParamBuilder discover = new DiscoverMovieParamBuilder()
    					.withGenres(genreIds, true)
						.language("es")
						.sortBy(info.movito.themoviedbapi.tools.sortby.DiscoverMovieSortBy.POPULARITY_DESC)
						.voteCountGte(50)
						.voteAverageGte(5.0)
						.primaryReleaseYear(2000)
    					.page(1);
    	return tmdbApi.getDiscover().getMovie(discover).getResults();
		}
		*/
    
 // Reemplaza el método existente por este:
    public List<info.movito.themoviedbapi.model.core.Movie> getMoviesByGenre(List<entity.Genre> genre) throws TmdbException {
        // --- CONFIGURABLE ---
        final int targetResults = 5000;                      // cuántas películas queremos como máximo
        final int perPageEstimate = 20;                      // TMDB suele devolver 20 por página
        final int maxPagesToFetch = (int) Math.ceil((double) targetResults / perPageEstimate);
        final int requestsPerSecond = 3;                     // tasa segura por defecto (ajustá si sabés el límite)
        final long delayBetweenRequestsMs = 1000L / requestsPerSecond;
        final int maxRetries = 3;                            // reintentos por llamada antes de fallar
        final long initialRetryDelayMs = 500L;               // ms para el primer retry (exponencial)
        // ----------------------

        List<Integer> genreIds = genre.stream()
                .map(g -> g.getId_api())
                .collect(Collectors.toList());

        DiscoverMovieParamBuilder builder = new DiscoverMovieParamBuilder()
                .withGenres(genreIds, true)
                .language("es")
                .sortBy(info.movito.themoviedbapi.tools.sortby.DiscoverMovieSortBy.POPULARITY_DESC)
                .voteCountGte(50)
                .voteAverageGte(5.0)
                .primaryReleaseYear(2000);

        List<info.movito.themoviedbapi.model.core.Movie> all = new ArrayList<>();

        int pageNum = 1;
        MovieResultsPage page;

        while (true) {
            builder.page(pageNum);

            // retries exponenciales para errores transitorios (TmdbException)
            int attempt = 0;
            long retryDelay = initialRetryDelayMs;
            while (true) {
                try {
                    page = tmdbApi.getDiscover().getMovie(builder);
                    break; // éxito
                } catch (TmdbException e) {
                    attempt++;
                    if (attempt > maxRetries) {
                        throw e;
                    }
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); // restaurar la interrupción
                        // lanzar TmdbException sólo con mensaje (la clase no acepta causa)
                        throw new TmdbException("Interrupted while retrying discover requests");
                    }
                    retryDelay *= 2;
                }
            }

            List<info.movito.themoviedbapi.model.core.Movie> results = page.getResults();
            if (results == null || results.isEmpty()) {
                break;
            }

            all.addAll(results);

            if (all.size() >= targetResults) break;
            int totalPages = page.getTotalPages();
            if (pageNum >= totalPages) break;
            if (pageNum >= maxPagesToFetch) break;

            pageNum++;

            try {
                Thread.sleep(delayBetweenRequestsMs);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (all.size() > targetResults) {
            return new ArrayList<>(all.subList(0, targetResults));
        } else {
            return all;
        }
    }
}