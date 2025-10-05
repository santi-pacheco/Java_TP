package service;

import entity.Genre; // 
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.movielists.*;
import info.movito.themoviedbapi.tools.TmdbException;

import java.util.List;
import java.util.stream.Collectors;

public class ExternalApiService {
    
    private TmdbApi tmdbApi;
    
    public ExternalApiService(String apiKey) {
        this.tmdbApi = new TmdbApi(apiKey);
        this.tmdbApi = new TmdbApi("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhNDdiYTBiMTI3NDk5YjBlMWIyOGNlYjBhMTgzZWM1NyIsIm5iZiI6MTc1NTYwOTMwOC44NzIsInN1YiI6IjY4YTQ3OGRjNWJkMTI3ZjcyY2RhNThjYSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._mkAgrQSPf-YCaYm1TFxuNDEgAtESQEaBOPI5t-8i8Q");
    }
    

    public List<info.movito.themoviedbapi.model.core.Genre> getMovieGenres() throws TmdbException {
        return tmdbApi.getGenre().getMovieList("es");
    }
    
 
    public entity.Genre convertToLocalGenre(info.movito.themoviedbapi.model.core.Genre tmdbGenre) {
        entity.Genre localGenre = new entity.Genre();
        localGenre.setId(tmdbGenre.getId());
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
}