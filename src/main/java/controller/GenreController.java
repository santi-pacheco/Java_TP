package controller;

import entity.Genre;
import repository.GenreRepository;
import service.ExternalApiService;
import service.GenreService;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GenreController {
    
    private GenreService genreService;
    
    public GenreController() {
    	//Ahora el repository no necesita la conexión en el constructor, ya no se encarga de eso
        GenreRepository genreRepository = new GenreRepository();
    	this.genreService = new GenreService(genreRepository);
    }
    
    public List<Genre> getGenres() {
        try {
            List<Genre> genres = genreService.getAllGenres();
            System.out.println("Géneros obtenidos exitosamente: " + genres.size() + " registros");
            return genres;
        } catch (Exception e) {
            System.err.println("Error al obtener géneros: " + e.getMessage());
            throw new RuntimeException("Error getting genres", e);
        }
    }
    
    public static void main(String[] args) {
        String apiKey = "a47ba0b127499b0e1b28ceb0a183ec57";
        
        try {
            GenreController controller = new GenreController(apiKey);
            List<Genre> genres = controller.getGenres();
            
            System.out.println("Géneros obtenidos:");
            for (Genre genre : genres) {
                System.out.println(genre.getId() + " - " + genre.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}