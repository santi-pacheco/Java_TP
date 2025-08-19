package service;

import entity.Genre;
import repository.GenreRepository;
import info.movito.themoviedbapi.tools.TmdbException;
import java.util.List;
import java.util.Optional;

public class GenreService {
    
    private ExternalApiService externalApiService;
    private GenreRepository genreRepository;
    
    public GenreService(ExternalApiService externalApiService, GenreRepository genreRepository) {
        this.externalApiService = externalApiService;
        this.genreRepository = genreRepository;
    }
    
    public List<Genre> getAllGenres() {
        // First try to get from local database
        List<Genre> localGenres = genreRepository.findAll();
        
        if (localGenres.isEmpty()) {
            // If empty, fetch from external API and save locally
            try {
                List<info.movito.themoviedbapi.model.core.Genre> tmdbGenres = 
                    externalApiService.getMovieGenres();
                localGenres = externalApiService.convertToLocalGenres(tmdbGenres);
                
                // Save to database for future use
                genreRepository.saveAll(localGenres);
                
            } catch (TmdbException e) {
                throw new RuntimeException("Error fetching genres from external API", e);
            }
        }
        
        return localGenres;
    }
    
    
}