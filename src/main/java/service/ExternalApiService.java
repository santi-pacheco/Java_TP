package service;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.tools.TmdbException;
import info.movito.themoviedbapi.tools.appendtoresponse.*;
import info.movito.themoviedbapi.tools.builders.discover.*;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.movies.Credits;
import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.model.core.ProductionCountry;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.format.DateTimeParseException;

import entity.Movie;
import entity.Genre;
import entity.Person;

import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.model.people.PersonDb;
import exception.ErrorFactory;

public class ExternalApiService {
    
    private TmdbApi tmdbApi;
    
    public TmdbApi getTmdbApi() {
        return tmdbApi;
    }
    
    public ExternalApiService(String apiKey) {
        this.tmdbApi = new TmdbApi(apiKey);
    }
    
    public List<info.movito.themoviedbapi.model.core.Genre> getMovieGenres() throws TmdbException {
        return tmdbApi.getGenre().getMovieList("es");
    }
    
    public Genre convertToLocalGenre(info.movito.themoviedbapi.model.core.Genre tmdbGenre) {
        Genre localGenre = new Genre();
        localGenre.setApiId(tmdbGenre.getId());
        localGenre.setName(tmdbGenre.getName());
        return localGenre;
    }
    
    public List<Genre> convertToLocalGenres(List<info.movito.themoviedbapi.model.core.Genre> tmdbGenres) {
        return tmdbGenres.stream()
                .map(this::convertToLocalGenre)
                .collect(Collectors.toList());
    }
      
    public List<info.movito.themoviedbapi.model.core.popularperson.PopularPerson> getMoviePersons() throws TmdbException {
        return tmdbApi.getPeopleLists().getPopular(null, 1).getResults();
    }
    
    public List<entity.Person> convertToLocalPersons(List<info.movito.themoviedbapi.model.core.popularperson.PopularPerson> tmdbPersons) {
        return tmdbPersons.stream()
                .map(tmdbPerson -> {
                    entity.Person localPerson = new entity.Person();
                    localPerson.setApiId(tmdbPerson.getId());
                    localPerson.setName(tmdbPerson.getName());
                    return localPerson;
                })
                .collect(Collectors.toList());
    }

    public List<info.movito.themoviedbapi.model.core.Movie> getMoviesByGenre(List<Genre> genre) throws TmdbException {
        final int targetResults = 10000;
        final int perPageEstimate = 20;
        final int maxPagesToFetch = (int) Math.ceil((double) targetResults / perPageEstimate);
        final int requestsPerSecond = 3;
        final long delayBetweenRequestsMs = 1000L / requestsPerSecond;
        final int maxRetries = 3;
        final long initialRetryDelayMs = 500L;

        List<Integer> genreIds = genre.stream()
                .map(g -> g.getApiId())
                .filter(id -> id != null)
                .collect(Collectors.toList());

        DiscoverMovieParamBuilder builder = new DiscoverMovieParamBuilder()
                .withGenres(genreIds, true)
                .language("es")
                .sortBy(info.movito.themoviedbapi.tools.sortby.DiscoverMovieSortBy.POPULARITY_DESC)
                .voteCountGte(50)
                .voteAverageGte(5.0);

        List<info.movito.themoviedbapi.model.core.Movie> all = new ArrayList<>();
        int pageNum = 1;
        MovieResultsPage page;

        while (true) {
            builder.page(pageNum);
            int attempt = 0;
            long retryDelay = initialRetryDelayMs;
            while (true) {
                try {
                    page = tmdbApi.getDiscover().getMovie(builder);
                    break;
                } catch (TmdbException e) {
                    attempt++;
                    if (attempt > maxRetries) {
                        throw e;
                    }
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new TmdbException("Interrupted while retrying discover requests");
                    }
                    retryDelay *= 2;
                }
            }

            List<info.movito.themoviedbapi.model.core.Movie> results = page.getResults();
            if (results == null || results.isEmpty()) {
                break;
            }
            all.addAll(results);
            if (all.size() >= targetResults) break;
            int totalPages = page.getTotalPages();
            if (pageNum >= totalPages) break;
            if (pageNum >= maxPagesToFetch) break;

            pageNum++;

            try {
                Thread.sleep(delayBetweenRequestsMs);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (all.size() > targetResults) {
            return new ArrayList<>(all.subList(0, targetResults));
        } else {
            return all;
        }
    }
    
    public List<entity.Country> mapCountries(List<info.movito.themoviedbapi.model.core.ProductionCountry> tmdbCountries) {
        if (tmdbCountries == null) {
            return new ArrayList<>();
        }

        return tmdbCountries.stream()
                .map(tmdbCountry -> {
                    entity.Country localCountry = new entity.Country();
                    localCountry.setIsoCode(tmdbCountry.getIsoCode());
                    localCountry.setName(tmdbCountry.getName());
                    return localCountry;
                })
                .collect(Collectors.toList());
    }
    
    public static List<entity.Country> getMovieCountries() throws TmdbException, IOException {
        try {
            URL urlCountries = new URL("https://api.themoviedb.org/3/configuration/countries?api_key=a47ba0b127499b0e1b28ceb0a183ec57");
            HttpURLConnection conn = (HttpURLConnection)urlCountries.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            Gson gson = new Gson();
            entity.Country[] countriesArray = gson.fromJson(response.toString(), entity.Country[].class);
            List<entity.Country> countriesList = new ArrayList<>();
            
            for(entity.Country c : countriesArray) {
                countriesList.add(c);
            }
            return countriesList;
        } catch(IOException e) {
            throw ErrorFactory.internal("Error al obtener la lista de países desde la API externa.");
        }
    }
    
    public Movie mapAndUpsertFromDiscover(info.movito.themoviedbapi.model.core.Movie tmdbMovie) {
        Movie m = new Movie();
        m.setApiId(tmdbMovie.getId());
        m.setTitle(tmdbMovie.getTitle());
        m.setOriginalTitle(tmdbMovie.getOriginalTitle());
        m.setSynopsis(tmdbMovie.getOverview());
        m.setApiRating(tmdbMovie.getVoteAverage() != null ? tmdbMovie.getVoteAverage() : null);
        m.setApiVotes(tmdbMovie.getVoteCount() != null ? tmdbMovie.getVoteCount() : 0);
        m.setPopularity(tmdbMovie.getPopularity() != null ? tmdbMovie.getPopularity() : null);
        m.setIsAdult(tmdbMovie.getAdult());
        m.setPosterPath(tmdbMovie.getPosterPath());
        m.setOriginalLanguage(tmdbMovie.getOriginalLanguage());
        if (tmdbMovie.getReleaseDate() != null && !tmdbMovie.getReleaseDate().isBlank()) {
            try {
                LocalDate release = LocalDate.parse(tmdbMovie.getReleaseDate());
                int year = release.getYear();
                m.setReleaseYear(year);
                m.setReleaseDate(release);
            } catch (DateTimeParseException ex) {
                // Silencioso
            }
        }
        m.setDuration(null);
        m.setImdbId(null);
        return m;
    }

    public static class MovieDetailsDTO {
        private final Integer runtime;
        private final String id_imdb;
        private final List<ProductionCountry> productionCountries;
        private final List<info.movito.themoviedbapi.model.core.Genre> genres;
        private final info.movito.themoviedbapi.model.movies.Credits credits;

        public MovieDetailsDTO(Integer runtime,String id_imdb , List<info.movito.themoviedbapi.model.core.Genre> genres, Credits credits, List<ProductionCountry> productionCountries) {
            this.runtime = runtime;
            this.id_imdb = id_imdb;
            this.genres = genres;
            this.credits = credits;
            this.productionCountries = productionCountries;
        }

        public Integer getRuntime() { return runtime; }
        public String getId_imdb() { return id_imdb; }
        public List<info.movito.themoviedbapi.model.core.Genre> getGenres() { return genres; }
        public info.movito.themoviedbapi.model.movies.Credits getCredits() { return credits; }
        public List<ProductionCountry> getProductionCountries() { return productionCountries; }
    }

    public MovieDetailsDTO fetchMovieDetailsWithCredits(int tmdbMovieId, String language) throws TmdbException {
        TmdbMovies moviesApi = tmdbApi.getMovies();
        MovieDb movieDb = moviesApi.getDetails(tmdbMovieId, language,
            MovieAppendToResponse.CREDITS,
            MovieAppendToResponse.EXTERNAL_IDS
        );

        if (movieDb == null) {
            throw ErrorFactory.notFound("El resultado de la API es nulo para el ID: " + tmdbMovieId);
        }

        Integer runtime = movieDb.getRuntime();
        List<info.movito.themoviedbapi.model.core.Genre> genres = movieDb.getGenres();
        Credits credits = movieDb.getCredits();
        String imdbId = null;

        if (movieDb.getExternalIds() != null) {
            imdbId = movieDb.getExternalIds().getImdbId();
        }
        List<ProductionCountry> countries = movieDb.getProductionCountries();
        return new MovieDetailsDTO(runtime, imdbId, genres, credits, countries);
    }

    public class PersonWithCharacter extends entity.Person {
        private String characterName;
        
        public String getCharacterName() {
            return characterName;
        }
        public void setCharacterName(String characterName) {
            this.characterName = characterName;
        }
    }
    
    public List<PersonWithCharacter> mapCast(List<info.movito.themoviedbapi.model.movies.Cast> tmdbCast) {
        if (tmdbCast == null) {
            return new ArrayList<>();
        }

        return tmdbCast.stream()
                .limit(20) 
                .map(tmdbPerson -> {
                    PersonWithCharacter personWithRole = new PersonWithCharacter();
                    personWithRole.setApiId(tmdbPerson.getId());
                    personWithRole.setName(tmdbPerson.getName());
                    personWithRole.setCharacterName(tmdbPerson.getCharacter());
                    return personWithRole;
                })
                .collect(Collectors.toList());
    }
    
    public List<entity.Person> mapCrew(List<info.movito.themoviedbapi.model.movies.Crew> tmdbCrew) {
        if (tmdbCrew == null) {
            return new ArrayList<>();
        }

        return tmdbCrew.stream()
                .filter(tmdbMember -> "Director".equals(tmdbMember.getJob()))
                .map(director -> {
                    entity.Person localPerson = new entity.Person();
                    localPerson.setName(director.getName());
                    localPerson.setApiId(director.getId()); 
                    return localPerson;
                })
                .collect(Collectors.toList());
    }
    
    public PersonDb fetchBasicPersonDetails(int tmdbPersonId, String language) throws TmdbException {
        TmdbPeople peopleApi = tmdbApi.getPeople();
        PersonDb personDb = peopleApi.getDetails(tmdbPersonId, language);

        if (personDb == null) {
            throw ErrorFactory.notFound("Detalles de persona no encontrados para ID: " + tmdbPersonId);
        }
        return personDb;
    }
  
    public Person mapTmdbToPersona(PersonDb tmdbPerson) {
        if (tmdbPerson == null) return null;
        
        Person personaDB = new Person();
        personaDB.setApiId(tmdbPerson.getId());
        personaDB.setPlaceOfBirth(tmdbPerson.getPlaceOfBirth());
        
        java.sql.Date sqlBirthDate = null;
        String birthdayString = tmdbPerson.getBirthday();
        
        if (birthdayString != null && !birthdayString.isEmpty()) {
            try {
                LocalDate localDate = LocalDate.parse(birthdayString);
                sqlBirthDate = java.sql.Date.valueOf(localDate); 
            } catch (DateTimeParseException e) {
                // Falla silenciosa
            }
        }
        personaDB.setBirthdate(sqlBirthDate);
        personaDB.setProfilePath(tmdbPerson.getProfilePath());
        
        List<String> alsoKnownAs = tmdbPerson.getAlsoKnownAs();
        if (alsoKnownAs != null && !alsoKnownAs.isEmpty()) {
            String joinedAlias = String.join(", ", alsoKnownAs);
            if (joinedAlias.length() > 255) {
                joinedAlias = joinedAlias.substring(0, 255);
            }
            personaDB.setAlsoKnownAs(joinedAlias);
        } else {
            personaDB.setAlsoKnownAs(null);
        }
        
        return personaDB;
    }
    
    public entity.Person toEntity(PersonWithCharacter pwc) {
        if (pwc == null) return null;
        entity.Person p = new entity.Person();
        p.setApiId(pwc.getApiId());
        p.setName(pwc.getName());
        p.setAlsoKnownAs(pwc.getAlsoKnownAs());
        p.setBirthdate(pwc.getBirthdate());
        p.setPlaceOfBirth(pwc.getPlaceOfBirth());
        return p;
    }
}