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
        if (genre.getName() == null || genre.getName().trim().isEmpty()) {
            throw ErrorFactory.badRequest("El nombre del género es obligatorio.");
        }
        return genreRepository.add(genre);
    }
    
    public Genre updateGenre(Genre genre) {
        if (genre.getName() == null || genre.getName().trim().isEmpty()) {
            throw ErrorFactory.badRequest("El nombre del género no puede estar vacío.");
        }
        
        Genre existingGenre = genreRepository.findOne(genre.getGenreId());
        if (existingGenre == null) {
            throw ErrorFactory.notFound("No se puede actualizar. Género con ID " + genre.getGenreId() + " no encontrado.");
        }
        
        existingGenre.setName(genre.getName().trim());
        existingGenre.setApiId(genre.getApiId());
        return genreRepository.update(existingGenre);
    }
    
    public Genre deleteGenre(Genre genre) {
        if (genre == null || genre.getGenreId() <= 0) {
            throw ErrorFactory.badRequest("Género inválido para eliminar.");
        }
        return genreRepository.delete(genre);
    }
    
    public void saveAllGenres(List<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            genreRepository.saveAll(genres);
        }
    }
    
    public Integer getGeneresByIdApi(Integer idApi) {
        if (idApi == null) {
            throw ErrorFactory.badRequest("El ID de API no puede ser nulo.");
        }
        return genreRepository.findByIdApi(idApi);
    }
}