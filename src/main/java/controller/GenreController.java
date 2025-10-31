package controller;

import entity.Genre;
import service.GenreService;

import java.util.List;

public class GenreController {
    
    private GenreService genreService;
    
    public GenreController(GenreService genreService) {
    	//Ahora el repository no necesita la conexión en el constructor, ya no se encarga de eso
    	this.genreService = genreService;
    }
    
    public List<Genre> getGenres(){
			List<Genre> genres = genreService.getAllGenres();
			System.out.println("Géneros obtenidos exitosamente: " + genres.size() + " registros");
			return genres;
	}
    
    public Genre getGenreById(int id) {
    	Genre genre = genreService.getGenreById(id);
    	return genre;
    }
    
    public Genre createGenre(Genre genre) {
		return genreService.CreateGenre(genre);
	}
    
    public Genre modifyGenre(Genre genre) {
    	return genreService.updateGenre(genre);
    }
    
    public void removeGenre(Genre genre) {
		genreService.deleteGenre(genre);
	}

    public void saveAllGenres(List<Genre> genres) {
    	System.out.println("Guardando géneros en la base de datos: " + genres.size() + " registros GenreController");
		genreService.saveAllGenres(genres);
	}
    
    public Integer getGeneresByIdApi(Integer idApi) {
    	Integer genreId = genreService.getGeneresByIdApi(idApi);
		return genreId;
	}
    
}