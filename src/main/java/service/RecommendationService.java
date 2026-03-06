package service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import com.opencsv.CSVWriter;

import exception.ErrorFactory;
import repository.MovieRepository;
import util.DataSourceProvider;
import controller.MovieController;
import entity.Movie;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;  

public class RecommendationService {
	
	public MovieController movieController;
	public MovieService movieService;

	private static final String CSV_PATH;
    static {
        ResourceBundle config = ResourceBundle.getBundle("config");

        CSV_PATH = config.getString("CSV_PATH").trim();
    }
	
	
	/**
	 * Obtiene un archivo dentro del proyecto
	 */

	public void exportRatingCsv() {
		System.out.println("Exportando ratings a CSV...");
	    String sql = "SELECT \n"
	            + "    r.user_id as idUser,\n"
	            + "    r.movie_id as idMovie,\n"
	            + "    r.rating as Rating\n"
	            + "FROM reviews r;\n";
	    System.out.println("Ruta del file" + CSV_PATH);
	    File file = new File(CSV_PATH);
	    

	    file.getParentFile().mkdirs();
	    
	    try (Connection conn = DataSourceProvider.getDataSource().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	        
	        int rowCount = 0;
	        System.out.println("Escribiendo datos al CSV...");
	        
	        try (FileWriter outputfile = new FileWriter(file);
	        		CSVWriter writer = new CSVWriter(outputfile,
	        		        CSVWriter.DEFAULT_SEPARATOR,      // ,
	        		        CSVWriter.NO_QUOTE_CHARACTER,     // ✅ Sin comillas
	        		        CSVWriter.NO_ESCAPE_CHARACTER,
	        		        CSVWriter.DEFAULT_LINE_END)) {
	        	System.out.println("Escribiendo encabezados..." + rs.getMetaData().getColumnCount());
	            while(rs.next()) {
                	System.out.println("Escribiendo fila: userId=" + rs.getInt("idUser") + ", movieId=" + rs.getInt("idMovie") + ", rating=" + rs.getDouble("Rating"));
	                String[] data = {
	                    String.valueOf(rs.getInt("idUser")),
	                    String.valueOf(rs.getInt("idMovie")),
	                    String.valueOf(rs.getDouble("Rating"))
	                };
	                writer.writeNext(data);
	                rowCount++;
	            }
	            
	            writer.flush();
	            
	            System.out.println("✅ Exportados " + rowCount + " registros a: " + file.getAbsolutePath());
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	            throw ErrorFactory.internal("Error escribiendo CSV: " + e.getMessage());
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw ErrorFactory.internal("Error fetching ratings: " + e.getMessage());
	    }
	}
	
    public RecommendationService() {
        MovieRepository movieRepository = new MovieRepository();
        MovieService movieService = new MovieService(movieRepository);
        this.movieController = new MovieController(movieService);
    }
   @SuppressWarnings("null")
   public List<Movie> getRecommendations(int userId, int numRecommendations) {
	    List<Movie> recommendedMovies = new ArrayList<>();
	    
	    try {
	        File ratingsFile = new File(CSV_PATH);

	        DataModel model = new FileDataModel(ratingsFile);
	        
	        System.out.println("Modelo cargado: " + model.getNumUsers() + " usuarios, " + model.getNumItems() + " películas");
	        
	        // ✅ CONFIGURACIÓN OPTIMIZADA PARA DATOS DISPERSOS
	        
	        // Probar con UncenteredCosineSimilarity (mejor para datos dispersos)
	        ItemSimilarity itemSimilarity = new UncenteredCosineSimilarity(model);
	        Recommender recommender = new GenericItemBasedRecommender(model, itemSimilarity);
	        
	        List<RecommendedItem> recommendations = recommender.recommend(userId, numRecommendations);
	        
	        System.out.println("\nRecomendaciones para usuario " + userId + ":");
	        if (recommendations.isEmpty()) {
	            System.out.println("No se encontraron recomendaciones");
	        } else {
	            for (RecommendedItem recommendation : recommendations) {
	                int movieId = (int) recommendation.getItemID();
	                Movie movie = movieController.getMovieById(movieId);
	                recommendedMovies.add(movie);
	                System.out.println("- " + movie.getTitle() + " (Score: " + String.format("%.2f", recommendation.getValue()) + ")");
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return recommendedMovies;
	}
}
