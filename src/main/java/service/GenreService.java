package service;

import entity.Genre;
import repository.GenreRepository;
import java.util.List;

public class GenreService {
    
    private GenreRepository genreRepository;
    
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }
    
    public List<Genre> getAllGenres() {
    	return genreRepository.findAll();
    }

    public Genre getGenreById(int id) {
		return genreRepository.findOne(id);
	}
    
    public Genre CreateGenre(Genre genre) {
		if (genre != null && genre.getName() != null && !genre.getName().isEmpty()) {
			return genreRepository.add(genre);
		} else {
			throw new IllegalArgumentException("Invalid user data");
		}
	}
    
}