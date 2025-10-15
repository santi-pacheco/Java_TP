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
import entity.Movie;
import repository.MovieRepository;
import service.MovieService;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import controller.GenreController;
import service.GenreService;
import repository.GenreRepository;
import com.google.common.util.concurrent.RateLimiter;
import service.ExternalApiService.MovieDetailsDTO;
import java.sql.Time;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import controller.PersonController;
import repository.PersonRepository;
import service.PersonService;

public class DiscoverReflectionMain {
	
	public static class actorCharacter{
    	private int idActor;
    	private String character;
    	
    	public actorCharacter(int idActor, String character) {
			this.idActor = idActor;
			this.character = character;
		}
    	
    	public int getIdActor() { return idActor; }
    	public String getCharacter() { return character; }
    	@Override
    	public String toString() {
			return "actorCharacter{idActor=" + idActor + ", character='" + character + "'}";
		}
    }
	
    public static void main(String[] args) throws Exception {
    	
    	
    	
    	//TEST DEL OBJETO DISCOVER DE TMDBAPI
    	//------------------------------------------------------------------------------------------------
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
    	//------------------------------------------------------------------------------------------------
    	
    	
    	
    /*	
    //CARGA DE GENEROS EN LA BASE DE DATOS	
    //------------------------------------------------------------------------------------------------
        // También imprimimos todos los métodos (opcional, comentar si hay demasiado)
        // for (Method m : methods) System.out.println(m.toGenericString());
    	// En alguna parte de tu aplicación, por ejemplo, en un método de inicialización
    	// o en un controlador que se llame para poblar la base de datos.

    	// 1. Necesitas una instancia de tu ExternalApiService.
    	//Recuerda que el constructor espera tu clave de la API de TMDB.
    	
    	ExternalApiService apiService = new ExternalApiService("a47ba0b127499b0e1b28ceb0a183ec57");

    	// 2. Necesitas una instancia de tu GenreController.
		//(La forma de obtenerla puede variar según cómo gestiones tus dependencias).
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
    	*/
    //------------------------------------------------------------------------------------------------
    	
    	
    	/*
    	//CARGA DE PELICULAS EN LA BASE DE DATOS.
    	//SE NECESITAN TENER CARGADOS LOS GENEROS.
    	//------------------------------------------------------------------------------------------------------------
    	
    	// --- CONFIGURACIÓN INICIAL ---
        // 1. Instancia de tu servicio de API externa.
        //    ¡IMPORTANTE! Reemplaza "TU_API_KEY_AQUI" con tu clave real.
        ExternalApiService apiService = new ExternalApiService("a47ba0b127499b0e1b28ceb0a183ec57");

        // 2. Instancia de tu controlador de películas.
        //    (La forma de obtenerlo puede variar según tu framework,
        //    pero para un main, crearlo con 'new' es suficiente).
        MovieRepository movieRepository = new MovieRepository();
    	MovieService movieService = new MovieService(movieRepository);
    	MovieController movieController = new MovieController(movieService);

        System.out.println("--- INICIANDO PROCESO DE IMPORTACIÓN DE PELÍCULAS ---");

        try {
        	GenreRepository genreRepository = new GenreRepository();
        	GenreService genreService = new GenreService(genreRepository);
        	GenreController genreController = new GenreController(genreService);
            List<Genre> localGenres = genreController.getGenres();

            // --- PASO 2: OBTENER PELÍCULAS POR GÉNEROS ---
            // Usamos la lista de géneros obtenida para buscar las películas.
            System.out.println("\nPaso 2: Buscando películas populares basadas en los géneros encontrados...");
            List<info.movito.themoviedbapi.model.core.Movie> tmdbMovies = apiService.getMoviesByGenre(localGenres);
            System.out.println("Se encontraron " + tmdbMovies.size() + " películas.");

         // --- PASO 3: MAPEAR LAS PELÍCULAS AL FORMATO LOCAL ---
            System.out.println("\nPaso 3: Mapeando películas al formato de la base de datos...");
            List<Movie> localMovies = tmdbMovies.stream()
                    // Usamos una lambda para llamar al método con sus dos parámetros.
                    .map(tmdbMovie -> apiService.mapAndUpsertFromDiscover(tmdbMovie))
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
    	//------------------------------------------------------------------------------------------------------
    	
    	
    	
    	
    	//------------------------------------------------------------------------------------------------------
    	// CARGA DE LAS TABLAS actores_peliculas, directores_peliculas, generos_peliculas y personas.
    	// Se necesitan tener cargadas las peliculas y los generos.
    	//------------------------------------------------------------------------------------------------------
    	// --- 1. CONFIGURACIÓN ---
        final String YOUR_API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhNDdiYTBiMTI3NDk5YjBlMWIyOGNlYjBhMTgzZWM1NyIsIm5iZiI6MTc1NTYwOTMwOC44NzIsInN1YiI6IjY4YTQ3OGRjNWJkMTI3ZjcyY2RhNThjYSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._mkAgrQSPf-YCaYm1TFxuNDEgAtESQEaBOPI5t-8i8Q"; // ⚠️ Reemplaza con tu API key real

        // Inicializamos las clases necesarias según tus indicaciones
        MovieRepository movieRepository = new MovieRepository(); // Asumo que el controller lo necesita
        MovieService movieService = new MovieService(movieRepository);
        MovieController movieController = new MovieController(movieService);
        ExternalApiService externalApiService = new ExternalApiService(YOUR_API_KEY);
        
        GenreRepository genreRepository = new GenreRepository();
        GenreService genreService = new GenreService(genreRepository);
        GenreController genreController = new GenreController(genreService);
        
        PersonRepository personRepository = new PersonRepository();
        PersonService personService = new PersonService(personRepository);
        PersonController personController = new PersonController(personService);
        
        // Configuramos el RateLimiter para ser cuidadosos.
        // TMDB permite ~40-50 peticiones/10s. 4 por segundo (4.0) es un límite muy seguro.
        RateLimiter rateLimiter = RateLimiter.create(4.0); // 🚦 Permisos por segundo

        // --- 2. OBTENCIÓN DE DATOS DE LA BD ---
        System.out.println("Obteniendo la lista de películas desde la base de datos...");
        List<Movie> allMovies = movieController.getMovies();
        System.out.println(allMovies.size() + " películas encontradas. Iniciando procesamiento...");

        int successCount = 0;
        int errorCount = 0;

        // --- 3. BUCLE DE PROCESAMIENTO ---
        for (int i = 0; i < allMovies.size(); i++) {
            Movie movie = allMovies.get(i);
            
            // ✅ Esta es la línea clave. El código se pausará aquí el tiempo justo para no superar el límite.
            rateLimiter.acquire();

            System.out.println("Procesando película " + (i + 1) + "/" + allMovies.size() + ": '" + movie.getTitulo() + "'");

            try {
                // Obtenemos el id_api de la película actual
                int apiId = movie.getId_api();
                
                // Llamamos a tu método en ExternalApiService
                MovieDetailsDTO details = externalApiService.fetchMovieDetailsWithCredits(apiId, null);

                // ✅ AQUÍ ES DONDE ACTUALIZARÍAS LA PELÍCULA EN LA BD
                // Asigna los nuevos valores a tu objeto 'movie'
                Integer runtimeInMinutes = details.getRuntime();
                // Verificamos que el runtime no sea nulo o inválido
                if (runtimeInMinutes != null && runtimeInMinutes > 0) {	
                    
                    // --- ✨ INICIO DE LA CONVERSIÓN A java.sql.Time ✨ ---
                    long hours = TimeUnit.MINUTES.toHours(runtimeInMinutes);
                    long remainingMinutes = runtimeInMinutes % 60;
                    
                    // Creamos un objeto LocalTime (HH:mm:ss)
                    LocalTime localTime = LocalTime.of((int) hours, (int) remainingMinutes, 0);
                    
                    // Convertimos LocalTime a java.sql.Time
                    Time sqlTime = Time.valueOf(localTime);
                    // --- FIN DE LA CONVERSIÓN ---

                    // Asignamos el valor de tipo Time a la entidad
                    movie.setDuracion(sqlTime);

                    // Aquí guardarías la película actualizada en la BD
                    // movieRepository.save(movie);

                    System.out.println("   -> Éxito: Película actualizada. Runtime guardado como: " + sqlTime);
                    successCount++;
                    
                } else {
                    System.out.println("   -> Info: No se encontró un runtime válido para la película. Se omite actualización.");
                    // Opcionalmente, puedes contar esto como un éxito si no lo consideras un error
                }
                movie.setId_imdb(details.getId_imdb());
                System.out.println("   -> Actualizando otros detalles de la película...");
                //Mostrar tipo de dato de la duracion y el de id_imdb 
                movieController.modifyMovie(movie);
                
                List<info.movito.themoviedbapi.model.core.Genre> genres = details.getGenres();
                //Por cada genre recolectar el idTmdb y guardarlo en una List...
                List<Integer> genreIds = genres.stream()
						.map(info.movito.themoviedbapi.model.core.Genre::getId)
						.collect(Collectors.toList());
                System.out.println("   -> Actualizando géneros asociados...");
                movieController.updateMovieGenres(movie.getId(), genreIds, genreController);
                System.out.println("   -> Géneros actualizados.");
                
                info.movito.themoviedbapi.model.movies.Credits credits = details.getCredits();
                List<service.ExternalApiService.PersonWithCharacter> personWithCharacter = externalApiService.mapCast(credits.getCast());
                System.out.println("   -> Actualizando actores asociados a personas...");
                List<actorCharacter> ac = personController.saveActors(personWithCharacter);

                System.out.println("   -> Actores guardados. Actualizando relación actores-película...");
                movieController.updateMovieActors(movie.getId(), ac);
                List<entity.Person> director = externalApiService.mapCrew(credits.getCrew());
                System.out.println("   -> Actualizando directores asociados a personas...");
                List<entity.Person> d = personController.saveDirectors(director);
                System.out.println("   -> Directores guardados. Actualizando relación directores-película...");
                movieController.updateMovieDirectors(movie.getId(), d);
				System.out.println("   -> Relación directores-película actualizada.");
				// Si llegamos aquí, todo fue bien
				System.out.println("   -> Proceso completado para '" + movie.getTitulo() + "'.\n");
            } catch (Exception e) {
                // ❌ Si falla una película, registramos el error y continuamos con la siguiente.
                System.err.println("   -> ERROR al procesar '" + movie.getTitulo() + "': " + e.getMessage());
                errorCount++;
            }
        }

        // --- 4. REPORTE FINAL ---
        System.out.println("\n------------------------------------");
        System.out.println("PROCESO DE ACTUALIZACIÓN FINALIZADO");
        System.out.println("------------------------------------");
        System.out.println("Películas actualizadas con éxito: " + successCount);
        System.out.println("Películas con error: " + errorCount);
      
      //------------------------------------------------------------------------------------------------------
      
    	
    	
    	
    	
    	
    	
    }
}
