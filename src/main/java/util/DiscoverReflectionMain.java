package util;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.people.PersonDb;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import controller.GenreController;
import java.util.List;
import service.ExternalApiService;
import service.GenreService;
import repository.CountryRepository;
import repository.GenreRepository;
import info.movito.themoviedbapi.tools.TmdbException;
import entity.Genre;
import controller.WatchlistController;
import repository.UserRepository;
import service.UserService;
import service.WatchlistService;
import controller.MovieController;
import entity.Movie;
import entity.Person;
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
import entity.Country;
import entity.Watchlist;
import repository.WatchlistRepository;
import java.util.ArrayList;

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
    //CARGA DE GENEROS
		//loadGenres();
	//CARGA DE PELICULAS
		//loadMovies();
	//CARGA DE ACTORES, DIRECTORES Y DURACION
		//loadActorsDirectorsAndRuntime();
    //loadPersons();
    	countryPerMovie();
    }   	
    public static void loadGenres() {
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
    }
    //------------------------------------------------------------------------------------------------

    public static void countryPerMovie() {
    	final String YOUR_API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhNDdiYTBiMTI3NDk5YjBlMWIyOGNlYjBhMTgzZWM1NyIsIm5iZiI6MTc1NTYwOTMwOC44NzIsInN1YiI6IjY4YTQ3OGRjNWJkMTI3ZjcyY2RhNThjYSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._mkAgrQSPf-YCaYm1TFxuNDEgAtESQEaBOPI5t-8i8Q"; // ⚠️ Reemplaza con tu API key real

        // Inicializamos las clases necesarias según tus indicaciones
        MovieRepository movieRepository1 = new MovieRepository(); // Asumo que el controller lo necesita
        MovieService movieService1 = new MovieService(movieRepository1);
        MovieController movieController1 = new MovieController(movieService1);
        ExternalApiService externalApiService = new ExternalApiService(YOUR_API_KEY);
        
        GenreRepository genreRepository = new GenreRepository();
        GenreService genreService = new GenreService(genreRepository);
        GenreController genreController = new GenreController(genreService);
        
        PersonRepository personRepository = new PersonRepository();
        PersonService personService = new PersonService(personRepository);
        PersonController personController = new PersonController(personService);
        
        // Configuramos el RateLimiter para ser cuidadosos.
        // TMDB permite ~40-50 peticiones/10s. 4 por segundo (4.0) es un límite muy seguro.
        RateLimiter rateLimiter = RateLimiter.create(40.0); // 🚦 Permisos por segundo

        // --- 2. OBTENCIÓN DE DATOS DE LA BD ---
        System.out.println("Obteniendo la lista de películas desde la base de datos...");
        List<Movie> allMovies = movieController1.getMovies();
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
                List<Country> paisesPeli = externalApiService.mapCountries(details.getProductionCountries());
                
                
                
              

                for (Country c : paisesPeli) {
                    CountryRepository cr = new CountryRepository();
                    int co =  cr.findOneByISO(c.getIso_3166_1());
                    System.out.println("   -> Guardando país: " + co + " para la película: " + movie.getTitulo());
                    cr.saveCountryMovie(co, movie.getId());
                }
                
                 
                 
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
    
    public static void loadMovies() {
    //CARGA DE PELICULAS EN LA BASE DE DATOS.
    //SE NECESITAN TENER CARGADOS LOS GENEROS.
    //------------------------------------------------------------------------------------------------------------
    	
    	// --- CONFIGURACIÓN INICIAL ---
    	// 1. Instancia de tu servicio de API externa.
    	//¡IMPORTANTE! Reemplaza "TU_API_KEY_AQUI" con tu clave real.
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
    }
    //------------------------------------------------------------------------------------------------------
    	
    	
    	
    public static void loadActorsDirectorsAndRuntime() {
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
    }
      //------------------------------------------------------------------------------------------------------
    	
    
public static void loadPersons() {
    	
        final String YOUR_API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhNDdiYTBiMTI3NDk5YjBlMWIyOGNlYjBhMTgzZWM1NyIsIm5iZiI6MTc1NTYwOTMwOC44NzIsInN1YiI6IjY4YTQ3OGRjNWJkMTI3ZjcyY2RhNThjYSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._mkAgrQSPf-YCaYm1TFxuNDEgAtESQEaBOPI5t-8i8Q"; // ⚠️ Reemplaza con tu API key real

    	PersonRepository personRepository = new PersonRepository();
    	PersonService personService = new PersonService(personRepository);
    	PersonController personController = new PersonController(personService);
    	
    	ExternalApiService externalApiService = new ExternalApiService(YOUR_API_KEY);
    	
    	List<Person> listaPersonas = personController.getPersons();

        // --- CORRECCIÓN CRÍTICA ---
    	// Inicializa la lista, de lo contrario tendrás NullPointerException
    	List<Person> personas = new ArrayList<>(); 
    	
    	if (listaPersonas == null || listaPersonas.isEmpty()) {
            System.out.println("La lista de personas está vacía. No hay nada que cargar.");
            return;
        }

        System.out.println("Iniciando carga de detalles para " + listaPersonas.size() + " personas...");
        
        String language = null; 

        // --- AJUSTE DE RATE LIMIT ---
        // Objetivo: 40 peticiones por segundo (margen de seguridad bajo el límite de 50).
        // 1000 milisegundos / 40 peticiones = 25ms por petición.
        final long TIEMPO_OBJETIVO_POR_PETICION_MS = 25; 

        for (int i = 0; i < listaPersonas.size(); i++) {
            
        	Person personaOriginal = listaPersonas.get(i);
            
            if (personaOriginal.getId_api() == 0) {
                System.err.println("Saltando persona (índice " + i + ") sin id_api.");
                continue;
            }

            try {
                int idApi = personaOriginal.getId_api();
                System.out.println("Procesando: " + (i + 1) + "/" + listaPersonas.size() + " - ID: " + idApi);

                // 1. Inicia el cronómetro ANTES de la llamada a la API
                long startTime = System.currentTimeMillis();

                // 2. Llama a fetchBasicPersonDetails
                PersonDb tmdbPerson = externalApiService.fetchBasicPersonDetails(idApi, language);

                // 3. Llama a mapTmdbToPersona
                Person personaMapeada = externalApiService.mapTmdbToPersona(tmdbPerson);

                // 4. "Une" los dos objetos
                if (personaMapeada != null) {
                    personaOriginal.setPlace_of_birth(personaMapeada.getPlace_of_birth());
                    personaOriginal.setBirthDate(personaMapeada.getBirthDate());
                }

                // 5. Agrega a la lista para el lote (batch)
                personas.add(personaOriginal);

                // 6. MANEJO DE RATE LIMIT (MODIFICADO)
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime; // Tiempo que tomó la API + Mapeo

                if (duration < TIEMPO_OBJETIVO_POR_PETICION_MS) {
                    // Si el trabajo fue más rápido que nuestro objetivo (25ms),
                    // dormimos solo por el tiempo restante.
                    long sleepTime = TIEMPO_OBJETIVO_POR_PETICION_MS - duration;
                    Thread.sleep(sleepTime);
                }
                // Si la API tardó MÁS de 25ms, no dormimos nada y continuamos.

            } catch (IllegalStateException e) {
                System.err.println("Error al buscar persona (ID: " + personaOriginal.getId_api() + "): " + e.getMessage());
            } catch (TmdbException e) { 
                System.err.println("Error de TMDB al buscar (ID: " + personaOriginal.getId_api() + "): " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("El hilo fue interrumpido. Deteniendo la carga.");
                break; // Sale del bucle for
            } catch (Exception e) {
                System.err.println("Error inesperado procesando (ID: " + personaOriginal.getId_api() + "): " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // 7. Guarda TODA la lista en la base de datos EN UNA SOLA TRANSACCIÓN
        System.out.println("Guardando " + personas.size() + " personas en la base de datos...");
        personController.updateAllPersonsbyId_api(personas);
        
        System.out.println("Carga de detalles de personas finalizada.");
    }
    	
    	
    	//TESTEO DE OBTENCIÓN DE ACTORES Y DIRECTORES DE UNA PELÍCULA
    	/*
    	//--------------------------------------------------------------------------------------------------
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
        MovieDetailsDTO details = externalApiService.fetchMovieDetailsWithCredits(823219, null);
        List<service.ExternalApiService.PersonWithCharacter> personWithCharacter = externalApiService.mapCast(details.getCredits().getCast());
        List<entity.Person> director = externalApiService.mapCrew(details.getCredits().getCrew());
        System.out.println("----------------------------------------------------------");
        System.out.println("Directores:");
    	System.out.println(director);
    	System.out.println("----------------------------------------------------------");
    	System.out.println("Actores:");
    	System.out.println(personWithCharacter);
    	//Hagamos lo siguiente: quiero que guardes en una lista todas las tuplas que posean el character en un string vacio.
    	List<service.ExternalApiService.PersonWithCharacter> personWithCharacterNoRole = personWithCharacter.stream()
				.filter(pwc -> pwc.getCharacterName() == null || pwc.getCharacterName().isBlank())
				.collect(Collectors.toList());
    	System.out.println("----------------------------------------------------------");
    	System.out.println("Actores sin rol asignado:");
    	System.out.println(personWithCharacterNoRole);
    	*/
    	//--------------------------------------------------------------------------------------------------
    	//HAY PELICULAS DE ANIMACION QUE NO TIENEN ACTORES
    	
    	//--------------------------------------------------------------------------------------------------
		/*
    	// TESTING WATCHLIST
    	
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
        */
    	//--------------------------------------------------------------------------------------------------
    	
    	// Testing búsqueda de películas por genero
    	
    	
    	
<<<<<<< HEAD
//    	MovieRepository movieRepository = new MovieRepository();
//    	MovieService movieService = new MovieService(movieRepository);
//     	MovieController movieController = new MovieController(movieService);
//     	System.out.println("Buscando películas de género 'Accion' estrenadas en 2020...");
//     	List<Movie> mv = movieService.getMovieByFilter("", "", 2019, 0);

    	
     	// Testing country Repository
=======
    	MovieRepository movieRepository = new MovieRepository();
     	MovieService movieService = new MovieService(movieRepository);
     	MovieController movieController = new MovieController(movieService);
     	//List<Movie> mv = movieRepository.movieFilter("Accion", 2025, 2025, null);
>>>>>>> f41d9872a1c6865a1bded2487daf3abc6ec426c3
     	
     	
//
//    	CountryRepository countryRepository = new CountryRepository();
//     	CountryService countryService = new CountryService(countryRepository);
//     	List<Country> countries = ExternalApiService.getMovieCountries();
//     	countryRepository.saveAll(countries);
     	
     	
     	// Cargar peliculas con paises
     	
//     	
//     	saveMovieCountries();
    
   
    
    
  
    
}













