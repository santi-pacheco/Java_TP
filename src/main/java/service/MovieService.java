package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MovieService {

    public static float getMovieRating(String movieId) throws IOException, InterruptedException {
    	System.out.println("📡 Obteniendo rating para movieId: " + movieId);
    	
    	try {
    		URL url = new URL("https://api.imdbapi.dev/titles/" + movieId );
    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    		conn.setRequestMethod("GET");
    		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    		StringBuilder response = new StringBuilder();
    		Gson gson = new Gson();
    		JsonObject json = gson.fromJson(reader.readLine(), JsonObject.class);
			reader.close();
            if (json.has("rating") && !json.get("rating").isJsonNull()) {
            	System.out.println("Rating encontrado: " + json.get("metacritic").toString());
                JsonObject ratingObj = json.getAsJsonObject("metacritic");
                
                System.out.println("Rating: " + ratingObj.get("score").getAsFloat());
                return ratingObj.get("score").getAsFloat();
            }
    		
    	} catch (IOException e) {
			System.err.println("❌ Error al obtener rating: " + e.getMessage());
			throw e;
		}
    	return 0.0f; // Valor por defecto si hay errorS
    }
	
}
