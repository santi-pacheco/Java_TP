package service;

import info.movito.themoviedbapi.model.people.PersonDb;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.RateLimiter;

import controller.GenreController;
import controller.MovieController;
import controller.PersonController;
import entity.Genre;
import entity.Movie;
import info.movito.themoviedbapi.tools.TmdbException;
import repository.GenreRepository;
import repository.MovieRepository;
import repository.PersonRepository;
import service.ExternalApiService.MovieDetailsDTO;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import repository.CountryRepository;
import controller.CountryController;
import exception.ErrorFactory;

public class DatosApiService {

	public void loadGenres() {
	    ExternalApiService apiService = new ExternalApiService("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhNDdiYTBiMTI3NDk5YjBlMWIyOGNlYjBhMTgzZWM1NyIsIm5iZiI6MTc1NTYwOTMwOC44NzIsInN1YiI6IjY4YTQ3OGRjNWJkMTI3ZjcyY2RhNThjYSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._mkAgrQSPf-YCaYm1TFxuNDEgAtESQEaBOPI5t-8i8Q");

	    GenreRepository genreRepository = new GenreRepository();
	    GenreService genreService = new GenreService(genreRepository);
	    GenreController genreController = new GenreController(genreService); 

	    try {
	    	System.out.println("   -> Consultando listado de géneros en TMDB...");
	    	List<info.movito.themoviedbapi.model.core.Genre> tmdbGenres = apiService.getMovieGenres();
	    	if (tmdbGenres != null && !tmdbGenres.isEmpty()) {
	            System.out.println("   -> Se encontraron " + tmdbGenres.size() + " géneros. Procesando datos...");
	            List<entity.Genre> localGenres = apiService.convertToLocalGenres(tmdbGenres);
	            System.out.println("   -> Insertando/Actualizando géneros en la Base de Datos...");
	            genreController.saveAllGenres(localGenres);
	        } else {
	            System.out.println("   -> ! La API devolvió una lista vacía de géneros.");
	        }
	    } catch (TmdbException e) {
	    	throw ErrorFactory.internal("Error de comunicación con TMDB: " + e.getMessage());
	    } catch (Exception e) {
	    	throw ErrorFactory.internal("Error inesperado cargando géneros: " + e.getMessage());
	    }
	}
	
	public void loadMovies() {
	    ExternalApiService apiService = new ExternalApiService("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhNDdiYTBiMTI3NDk5YjBlMWIyOGNlYjBhMTgzZWM1NyIsIm5iZiI6MTc1NTYwOTMwOC44NzIsInN1YiI6IjY4YTQ3OGRjNWJkMTI3ZjcyY2RhNThjYSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._mkAgrQSPf-YCaYm1TFxuNDEgAtESQEaBOPI5t-8i8Q");

	    MovieRepository movieRepository = new MovieRepository();
	    MovieService movieService = new MovieService(movieRepository);
	    MovieController movieController = new MovieController(movieService);
	    
	    GenreRepository genreRepository = new GenreRepository();
    	GenreService genreService = new GenreService(genreRepository);
    	GenreController genreController = new GenreController(genreService);

	    try {
	    	System.out.println("   -> Verificando géneros locales...");
	    	List<Genre> localGenres = genreController.getGenres();
	    	if (localGenres.isEmpty()) {
	    		throw ErrorFactory.badRequest("No hay géneros en la BD. Por favor, carga los géneros primero.");
	    	}
	    	System.out.println("   -> Descargando películas populares por género desde TMDB...");
	        List<info.movito.themoviedbapi.model.core.Movie> tmdbMovies = apiService.getMoviesByGenre(localGenres);
	        List<Movie> moviesToSave = new ArrayList<>();
	        for (var tmdbMovie : tmdbMovies) {
	            Movie localMovie = apiService.mapAndUpsertFromDiscover(tmdbMovie);
	            localMovie.setGenerosTemporales(tmdbMovie.getGenreIds());
	            moviesToSave.add(localMovie);
	        }
	        if (moviesToSave.isEmpty()) {
                throw ErrorFactory.notFound("No se encontraron películas nuevas para guardar.");
            }
	        System.out.println("   -> Guardando " + moviesToSave.size() + " películas en Base de Datos...");
	        movieController.saveAllMovies(moviesToSave);
	        System.out.println("   -> Vinculando películas con sus géneros...");
	        List<Integer> apiIds = moviesToSave.stream().map(Movie::getId_api).collect(Collectors.toList());
	        Map<Integer, Integer> mapApiIdToDbId = movieController.getMoviesByApiIds(apiIds);

	        List<Object[]> batchRelations = new ArrayList<>();

	        for (Movie movie : moviesToSave) {
	            Integer realDbId = mapApiIdToDbId.get(movie.getId_api());
	            
	            if (realDbId != null && movie.getGenerosTemporales() != null) {
	                for (Integer idGenero : movie.getGenerosTemporales()) {
	                    batchRelations.add(new Object[]{ realDbId, idGenero });
	                }
	            }
	        }
	        movieController.saveAllMovieGenres(batchRelations);
	        System.out.println("   -> ¡Carga de películas completada con éxito!");
	    } catch (TmdbException e) {
	    	throw ErrorFactory.internal("Error de comunicación con TMDB al cargar películas: " + e.getMessage());
	    } catch (Exception e) {
	    	if (e instanceof RuntimeException && e.getMessage().contains("No hay géneros")) { 
	    		throw (RuntimeException) e;
            }
            throw ErrorFactory.internal("Error inesperado durante la carga de películas: " + e.getMessage());
	    }
	}
	
	private void flushBuffers(ExternalApiService externalApiService,
			List<Movie> movies, 
            List<service.ExternalApiService.PersonWithCharacter> actors,
            List<entity.Person> directors,
            List<entity.Country> countries,
            Map<Integer, List<service.ExternalApiService.PersonWithCharacter>> mapMovieActors,
            Map<Integer, List<entity.Person>> mapMovieDirectors,
            Map<Integer, List<entity.Country>> mapMovieCountries,
            MovieController movieCtrl,
            PersonController personCtrl,
            CountryController countryCtrl) {
		
		System.out.println("      -> [BATCH] Guardando lote de " + movies.size() + " películas y sus relaciones...");
		movieCtrl.updateBatchMovies(movies); 
		Map<Integer, entity.Person> uniquePeople = new HashMap<>();
		
		for (var pwc : actors) {
			if (!uniquePeople.containsKey(pwc.getId_api())) {
			uniquePeople.put(pwc.getId_api(), externalApiService.toEntity(pwc));
			}
		}
		for (var dir : directors) {
			if (!uniquePeople.containsKey(dir.getId_api())) {
			uniquePeople.put(dir.getId_api(), dir);
			}
		}
		if (!uniquePeople.isEmpty()) {
	        personCtrl.saveAllPersons(new ArrayList<>(uniquePeople.values()));
	    }
		
		Map<String, entity.Country> uniqueCountries = new HashMap<>();
	    for (var c : countries) {
	        if (!uniqueCountries.containsKey(c.getIso_3166_1())) {
	            uniqueCountries.put(c.getIso_3166_1(), c);
	        }
	    }

	    if (!uniqueCountries.isEmpty()) {
	        countryCtrl.saveAllCountries(new ArrayList<>(uniqueCountries.values()));
	    }
		
		List<Integer> apiIds = new ArrayList<>(uniquePeople.keySet());
		Map<Integer, Integer> peopleMap = personCtrl.getMapIds(apiIds);

		List<String> isoCodes = new ArrayList<>(uniqueCountries.keySet());
	    Map<String, Integer> countryMap = countryCtrl.getMapIds(isoCodes);
		
		List<Object[]> batchCast = new ArrayList<>();
		List<Object[]> batchCrew = new ArrayList<>();
		List<Object[]> batchCountries = new ArrayList<>();
		
		for (Movie m : movies) {
			List<service.ExternalApiService.PersonWithCharacter> movieCast = mapMovieActors.get(m.getId_api());
			if (movieCast != null) {
				for (var actor : movieCast) {
				  Integer dbPersonId = peopleMap.get(actor.getId_api());
				  if (dbPersonId != null) {
				      batchCast.add(new Object[]{ 
				          m.getId(),
				          dbPersonId,
				          actor.getCharacterName()
				      });
				  }
				}
			}
		
			List<entity.Person> movieCrew = mapMovieDirectors.get(m.getId_api());
			if (movieCrew != null) {
				for (var dir : movieCrew) {
				  Integer dbPersonId = peopleMap.get(dir.getId_api());
				  
				  if (dbPersonId != null) {
				       batchCrew.add(new Object[]{ 
				           m.getId(), 
				           dbPersonId, 
				           "Director"
				       });
				  }
				}
			}
			List<entity.Country> peliPaises = mapMovieCountries.get(m.getId_api());
	        if (peliPaises != null) {
	            for (var c : peliPaises) {
	                Integer dbCountryId = countryMap.get(c.getIso_3166_1());
	                if (dbCountryId != null) {
	                    batchCountries.add(new Object[]{ m.getId(), dbCountryId });
	                }
	            }
	        }
		}
		if (!batchCast.isEmpty()) {
			movieCtrl.updateMovieActors(batchCast);
		}
		if (!batchCrew.isEmpty()) {
			movieCtrl.updateMovieDirectors(batchCrew);
		}
		if (!batchCountries.isEmpty()) {
			countryCtrl.saveBatchRelations(batchCountries);
		}
		
		movies.clear();
		actors.clear();
		directors.clear();
		countries.clear();
		mapMovieActors.clear();
		mapMovieDirectors.clear();
		mapMovieCountries.clear();
		uniquePeople.clear();
		uniqueCountries.clear();
	}

	private void updateMovieInMemory(Movie movie, MovieDetailsDTO details) {
	    Integer runtime = details.getRuntime();
	    if (runtime != null && runtime > 0) {
	        long hours = TimeUnit.MINUTES.toHours(runtime);
	        long mins = runtime % 60;
	        movie.setDuracion(Time.valueOf(LocalTime.of((int)hours, (int)mins, 0)));
	    }
	    movie.setId_imdb(details.getId_imdb());
	}
	
	public void loadActorsDirectorsAndRuntime() {
        MovieRepository movieRepository = new MovieRepository();
        MovieService movieService = new MovieService(movieRepository);
        MovieController movieController = new MovieController(movieService);
        
        ExternalApiService externalApiService = new ExternalApiService("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhNDdiYTBiMTI3NDk5YjBlMWIyOGNlYjBhMTgzZWM1NyIsIm5iZiI6MTc1NTYwOTMwOC44NzIsInN1YiI6IjY4YTQ3OGRjNWJkMTI3ZjcyY2RhNThjYSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._mkAgrQSPf-YCaYm1TFxuNDEgAtESQEaBOPI5t-8i8Q");
        
        GenreRepository genreRepository = new GenreRepository();
        GenreService genreService = new GenreService(genreRepository);
        GenreController genreController = new GenreController(genreService);
        
        PersonRepository personRepository = new PersonRepository();
        PersonService personService = new PersonService(personRepository);
        PersonController personController = new PersonController(personService);
        
        CountryRepository countryRepository = new CountryRepository();
        CountryService countryService = new CountryService(countryRepository);
        CountryController countryController = new CountryController(countryService);
        
        RateLimiter rateLimiter = RateLimiter.create(4.0);
        
        System.out.println("   -> Obteniendo catálogo de películas de la BD...");
        List<Movie> allMovies = movieController.getMovies();
        if (allMovies.isEmpty()) {
            throw ErrorFactory.notFound("No hay películas en la base de datos para procesar.");
        }
        System.out.println("   -> Se encontraron " + allMovies.size() + " películas. Iniciando descarga de detalles...");
        List<Movie> bufferMoviesToUpdate = new ArrayList<>();
        List<service.ExternalApiService.PersonWithCharacter> bufferActors = new ArrayList<>();
        List<entity.Person> bufferDirectors = new ArrayList<>();
        List<entity.Country> bufferCountries = new ArrayList<>();
        
        Map<Integer, List<service.ExternalApiService.PersonWithCharacter>> mapMovieActors = new HashMap<>();
        Map<Integer, List<entity.Person>> mapMovieDirectors = new HashMap<>();
        Map<Integer, List<entity.Country>> mapMovieCountries = new HashMap<>();
        
        int BATCH_SIZE = 50;
        int processedCount = 0;
        
        for (Movie movie : allMovies) {
            rateLimiter.acquire();
            processedCount++;

            try {
            	if (processedCount % 10 == 0) {
                    System.out.println("   -> Procesando: " + processedCount + "/" + allMovies.size() + " - " + movie.getTitulo());
                }
                MovieDetailsDTO details = externalApiService.fetchMovieDetailsWithCredits(movie.getId_api(), null);

                updateMovieInMemory(movie, details);
                bufferMoviesToUpdate.add(movie);
                List<service.ExternalApiService.PersonWithCharacter> actors = externalApiService.mapCast(details.getCredits().getCast());
                List<entity.Person> directors = externalApiService.mapCrew(details.getCredits().getCrew());
                List<entity.Country> countries = externalApiService.mapCountries(details.getProductionCountries());
                
                bufferActors.addAll(actors);
                bufferDirectors.addAll(directors);
                bufferCountries.addAll(countries);
                
                mapMovieActors.put(movie.getId_api(), actors);
                mapMovieDirectors.put(movie.getId_api(), directors);
                mapMovieCountries.put(movie.getId_api(), countries);

                if (bufferMoviesToUpdate.size() >= BATCH_SIZE) {
                    flushBuffers(externalApiService, bufferMoviesToUpdate, bufferActors, bufferDirectors, bufferCountries, mapMovieActors, mapMovieDirectors, mapMovieCountries, 
                                 movieController, personController, countryController);
                }

            } catch (Exception e) {
            	System.err.println("   -> [WARN] Error procesando película ID " + movie.getId_api() + ": " + e.getMessage());
            }
        }
        if (!bufferMoviesToUpdate.isEmpty()) {
            flushBuffers(externalApiService, bufferMoviesToUpdate, bufferActors, bufferDirectors, bufferCountries, mapMovieActors, mapMovieDirectors, mapMovieCountries, 
                         movieController, personController, countryController);
        }
        System.out.println("   -> ¡Carga de detalles finalizada!");
	}
	
	private static void flushPersonBuffer(PersonController controller, List<entity.Person> buffer) {
		System.out.println("      -> [BATCH] Actualizando datos de " + buffer.size() + " personas en DB...");
	    controller.updateAllPersonsbyId_api(buffer);
	    buffer.clear();
	}
	
	public void loadPersons() {
		
	    PersonRepository personRepository = new PersonRepository();
	    PersonService personService = new PersonService(personRepository);
	    PersonController personController = new PersonController(personService);
	    ExternalApiService externalApiService = new ExternalApiService("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhNDdiYTBiMTI3NDk5YjBlMWIyOGNlYjBhMTgzZWM1NyIsIm5iZiI6MTc1NTYwOTMwOC44NzIsInN1YiI6IjY4YTQ3OGRjNWJkMTI3ZjcyY2RhNThjYSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._mkAgrQSPf-YCaYm1TFxuNDEgAtESQEaBOPI5t-8i8Q");
	    
	    RateLimiter rateLimiter = RateLimiter.create(40.0); 

	    System.out.println("   -> Recuperando lista de personas de la BD...");
	    List<entity.Person> allPersons = personController.getPersons();

	    if (allPersons == null || allPersons.isEmpty()) {
            System.out.println("   -> No hay personas para procesar.");
            return;
        }
	    
	    System.out.println("   -> Total personas encontradas: " + allPersons.size());
	    List<entity.Person> bufferPersons = new ArrayList<>();
	    int BATCH_SIZE = 50;
	    int contador = 0;

	    for (entity.Person personaOriginal : allPersons) {
	        contador++;
	        
	        if (personaOriginal.getId_api() == 0) {
	            continue;
	        }
	        
	        if (personaOriginal.getBirthDate() != null && 
	        	    personaOriginal.getProfile_path() != null &&
	        	    personaOriginal.getAlso_known_as() != null) {
	        	    continue; 
	        }
	        
	        rateLimiter.acquire(); 

	        try {
	        	if (contador % 50 == 0) {
                    System.out.println("   -> Procesando persona " + contador + "/" + allPersons.size() + " (ID: " + personaOriginal.getId_api() + ")");
                }
	        	
	            PersonDb tmdbPerson = externalApiService.fetchBasicPersonDetails(personaOriginal.getId_api(), null);
	            entity.Person personaMapeada = externalApiService.mapTmdbToPersona(tmdbPerson);

	            if (personaMapeada != null) {
	                personaOriginal.setPlace_of_birth(personaMapeada.getPlace_of_birth());
	                personaOriginal.setBirthDate(personaMapeada.getBirthDate());
	                personaOriginal.setProfile_path(personaMapeada.getProfile_path());
	                personaOriginal.setAlso_known_as(personaMapeada.getAlso_known_as());
	                bufferPersons.add(personaOriginal);
	            }
	            if (bufferPersons.size() >= BATCH_SIZE) {
	                flushPersonBuffer(personController, bufferPersons);
	            }

	        } catch (Exception e) {
	        	System.err.println("   -> [WARN] Error con persona ID " + personaOriginal.getId_api() + ": " + e.getMessage());
	        }
	    }
	    if (!bufferPersons.isEmpty()) {
	        flushPersonBuffer(personController, bufferPersons);
	    }
	    System.out.println("   -> ¡Carga de personas finalizada!");
	}
	
}
