package service;

import entity.Genre;
import repository.GenreRepository;
import java.util.List;
import exception.ErrorFactory;

public class GenreService {
    
    private GenreRepository genreRepository;
    
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }
    
    public List<Genre> getAllGenres() {
    	return genreRepository.findAll();
    }

    public Genre getGenreById(int id) {
		Genre genre = genreRepository.findOne(id);
		if (genre == null) {
			throw ErrorFactory.notFound("Genre not found with ID: " + id);
		}
		return genre;
	}
    
    public Genre CreateGenre(Genre genre) {
    	return genreRepository.add(genre);
	}
    
    public Genre updateGenre(Genre genre) {
	    Genre existingGenre = genreRepository.findOne(genre.getId());
	    if (existingGenre == null) {
	        throw ErrorFactory.notFound("No se puede actualizar. Género con ID " + genre.getId() + " no encontrado.");
	    }
	    existingGenre.setName(genre.getName());
	    return genreRepository.update(existingGenre);
    }
    
    public Genre deleteGenre(Genre genre) {
		return genreRepository.delete(genre);
    }
    
    public void saveAllGenres(List<Genre> genres) {
    	genreRepository.saveAll(genres);
    }
    
    public Integer getGeneresByIdApi(Integer idApi) {
    	Integer genreId = genreRepository.findByIdApi(idApi);
		if (genreId == null) {
			System.out.println("Genre not found with idApi: " + idApi);
		}
		return genreId;
    }
}