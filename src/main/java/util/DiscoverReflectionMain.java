package util;

import info.movito.themoviedbapi.TmdbApi;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
public class DiscoverReflectionMain {

    public static void main(String[] args) throws Exception {
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
    }
}
