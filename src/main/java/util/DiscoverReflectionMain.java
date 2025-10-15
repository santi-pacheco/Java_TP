package util;

import info.movito.themoviedbapi.TmdbApi;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import controller.GenreController;
import java.util.List;
import service.ExternalApiService;
import service.GenreService;
import repository.GenreRepository;
import info.movito.themoviedbapi.tools.TmdbException;
import entity.Genre;
import controller.MovieController;
import controller.WatchlistController;
import entity.Movie;
import repository.MovieRepository;
import repository.UserRepository;
import service.MovieService;
import service.UserService;
import service.WatchlistService;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import controller.GenreController;
import service.GenreService;
import repository.GenreRepository;
import entity.Country;
import entity.Watchlist;
import repository.WatchlistRepository;

public class DiscoverReflectionMain {

    public static void main(String[] args) throws Exception {
    	/*
        // 1) API key: poné tu API key aquí o configúrala como variable de entorno TMDB_API_KEY
        String apiKey = System.getenv("TMDB_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = ("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhNDdiYTBiMTI3NDk5YjBlMWIyOGNlYjBhMTgzZWM1NyIsIm5iZiI6MTc1NTYwOTMwOC44NzIsInN1YiI6IjY4YTQ3OGRjNWJkMTI3ZjcyY2RhNThjYSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._mkAgrQSPf-YCaYm1TFxuNDEgAtESQEaBOPI5t-8i8Q"); // <- reemplazá por tu API key si no usás variable de entorno
        }

        // 2) Crear instancia del wrapper (la clase proviene de themoviedbapi)
        TmdbApi tmdbApi = new TmdbApi(apiKey);
        
        Arrays.stream(tmdbApi.getDiscover().getClass().getMethods())
        .sorted(Comparator.comparing(Method::getName))
        .forEach(System.out::println);
        /*
        // 3) Obtener el objeto discover y listar métodos relevantes
        Object discoverObj = tmdbApi.getDiscover();
        System.out.println("Discover CLASS: " + discoverObj.getClass().getName());
        Method[] methods = discoverObj.getClass().getMethods();

        System.out.println("=== Métodos getMovies() disponibles ===");
        for (Method m : methods) {
            if (m.getName().contains("getMovies") || m.getName().toLowerCase().contains("discover")) {
                System.out.println(m.toGenericString());
                System.out.println("  param types: " + Arrays.toString(m.getParameterTypes()));
                System.out.println("---");
            }
        }
*/
        // También imprimimos todos los métodos (opcional, comentar si hay demasiado)
        // for (Method m : methods) System.out.println(m.toGenericString());
    	// En alguna parte de tu aplicación, por ejemplo, en un método de inicialización
    	// o en un controlador que se llame para poblar la base de datos.

    	// 1. Necesitas una instancia de tu ExternalApiService.
//    	    Recuerda que el constructor espera tu clave de la API de TMDB.
//------------------------------------------------------------------------------------------------------------
    /*
    	ExternalApiService apiService = new ExternalApiService("a47ba0b127499b0e1b28ceb0a183ec57");

    	// 2. Necesitas una instancia de tu GenreController.
//    	    (La forma de obtenerla puede variar según cómo gestiones tus dependencias).
    	GenreRepository genreRepository = new GenreRepository();
    	GenreService genreService = new GenreService(genreRepository);
    	GenreController genreController = new GenreController(genreService); 

    	try {
    	    // 3. Obtener los géneros desde la API de TMDB.
    	    //    Esta es una llamada de red, por lo que puede lanzar una excepción.
    	    System.out.println("Buscando géneros en la API de TMDB...");
    	    List<info.movito.themoviedbapi.model.core.Genre> tmdbGenres = apiService.getMovieGenres();
    	    
    	    // 4. Convertir los géneros de TMDB a tu formato local (entity.Genre).
    	    System.out.println("Convirtiendo " + tmdbGenres.size() + " géneros al formato local...");
    	    List<entity.Genre> localGenres = apiService.convertToLocalGenres(tmdbGenres);
    	    
    	    // 5. Guardar la lista de géneros convertidos en tu base de datos.
    	    //    Aquí usas el método que ya tienes preparado en tu controlador.
    	    System.out.println("Guardando los géneros en la base de datos...");
    	    genreController.saveAllGenres(localGenres);
    	    
    	    System.out.println("¡Proceso completado! Los géneros se han guardado correctamente.");

    	} catch (TmdbException e) {
    	    // Es importante manejar posibles errores de conexión con la API.
    	    System.err.println("Error al comunicarse con la API de TMDB: " + e.getMessage());
    	    // Aquí podrías añadir un log más detallado del error si lo necesitas.
    	    e.printStackTrace();
    	} catch (Exception e) {
    	    // Capturar cualquier otro error inesperado durante el proceso.
    	    System.err.println("Ha ocurrido un error inesperado: " + e.getMessage());
    	    e.printStackTrace();
    	}
    

//------------------------------------------------------------------------------------------------------------
    	// --- CONFIGURACIÓN INICIAL ---
        // 1. Instancia de tu servicio de API externa.
        //    ¡IMPORTANTE! Reemplaza "TU_API_KEY_AQUI" con tu clave real.
        ExternalApiService apiService1 = new ExternalApiService("a47ba0b127499b0e1b28ceb0a183ec57");

        // 2. Instancia de tu controlador de películas.
        //    (La forma de obtenerlo puede variar según tu framework,
        //    pero para un main, crearlo con 'new' es suficiente).
        MovieRepository movieRepository = new MovieRepository();
    	MovieService movieService = new MovieService(movieRepository);
    	MovieController movieController = new MovieController(movieService);

        System.out.println("--- INICIANDO PROCESO DE IMPORTACIÓN DE PELÍCULAS ---");

        try {
        	GenreRepository genreRepository1 = new GenreRepository();
        	GenreService genreService1 = new GenreService(genreRepository1);
        	GenreController genreController1 = new GenreController(genreService1);
            List<Genre> localGenres = genreController1.getGenres();

            // --- PASO 2: OBTENER PELÍCULAS POR GÉNEROS ---
            // Usamos la lista de géneros obtenida para buscar las películas.
            System.out.println("\nPaso 2: Buscando películas populares basadas en los géneros encontrados...");
            List<info.movito.themoviedbapi.model.core.Movie> tmdbMovies = apiService1.getMoviesByGenre(localGenres);
            System.out.println("Se encontraron " + tmdbMovies.size() + " películas.");

         // --- PASO 3: MAPEAR LAS PELÍCULAS AL FORMATO LOCAL ---
            System.out.println("\nPaso 3: Mapeando películas al formato de la base de datos...");
            List<Movie> localMovies = tmdbMovies.stream()
                    // Usamos una lambda para llamar al método con sus dos parámetros.
                    .map(tmdbMovie -> apiService1.mapAndUpsertFromDiscover(tmdbMovie))
                    .collect(Collectors.toList());
            System.out.println("Mapeo completado.");
           
         // --- AÑADE ESTO PARA DEPURAR ---
            System.out.println("\n--- Inspeccionando los datos de las " + localMovies.size() + " películas antes de guardar ---");
            int i = 0;
            for (Movie movie : localMovies) {
            	if (i >= 15) break; // Limitar a las primeras 15 películas para no saturar la salida.
                // Imprimimos los campos más importantes de cada película.
                // Puedes añadir o quitar los que quieras ver.
                System.out.println(movie);
                i++;
            }
            System.out.println("--- Fin de la inspección ---\n");
            // -------------------------------
            
            // --- PASO 4: GUARDAR TODAS LAS PELÍCULAS EN LA BASE DE DATOS ---
            // Llamamos al nuevo método para un guardado masivo y eficiente.
            System.out.println("\nPaso 4: Guardando " + localMovies.size() + " películas en la base de datos (esto puede tardar un momento)...");
            movieController.saveAllMovies(localMovies);
            System.out.println("¡Todas las películas se han guardado correctamente!");


        } catch (TmdbException e) {
            System.err.println("ERROR: Ha ocurrido un problema al comunicarse con la API de TMDB.");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERROR: Ha ocurrido un error inesperado durante el proceso.");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n--- PROCESO DE IMPORTACIÓN FINALIZADO ---");
    
    */
    
    
    // TESTING
    	
        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        
        MovieRepository movieRepository = new MovieRepository();
        MovieService movieService = new MovieService(movieRepository);
       
        
        System.out.println("Obteniendo watchlist del usuario con ID 2...");
 
        WatchlistRepository watchlistRepository = new WatchlistRepository(movieRepository);
        WatchlistService watchlistService = new WatchlistService(watchlistRepository, userService, movieService);
    
        WatchlistController watchlistController = new WatchlistController(watchlistService);
    
        List<String> wl = watchlistController.getMoviesInWatchlist(2);
        System.out.println("PELIS EN WATCHLIST: " + wl);
    
    
    
    
    
    
    }
}
