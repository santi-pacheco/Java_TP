package repository;
import java.sql.Connection;
import java.sql.PreparedStatement;

import entity.Review;
import util.DataSourceProvider;

public class ReviewRepository {
	public Review saveReview(Review review) {
		String sql = "INSERT INTO reviews (id_user, id_movie, rating, com, review_date) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, review.getId_user());
			stmt.setInt(2, review.getId_movie());
			stmt.setDouble(3, review.getRating());
			stmt.setString(4, review.getReview_text());
			stmt.setDate(5, java.sql.Date.valueOf(review.getReview_date()));
			stmt.executeUpdate();
			return review;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Review findReview(int id) {
		Review rev = new Review();
		String sql = "SELECT id_user, id_movie, review_text, rating, review_date FROM reviews WHERE id_review = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, id);
			
			var rs = stmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					rev.setId(rs.getInt("id"));
					rev.setId_user(rs.getInt("id_user"));
					rev.setId_movie(rs.getInt("id_movie"));
					rev.setReview_text(rs.getString("review_text"));
					rev.setRating(rs.getDouble("rating"));
					java.sql.Date sqlDate = rs.getDate("review_date");
					rev.setReview_date(sqlDate.toLocalDate());
				}
			} else {
				System.out.println("No se encontraron resultados para la reseña con ID: " + id);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rev;
	}
	
	public Review updateReview(Review review) {
		String sql = "UPDATE reviews SET review_text = ?, rating = ? WHERE id_review = ?";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setString(1, review.getReview_text());
			stmt.setDouble(2, review.getRating());
			stmt.setInt(3, review.getId());
						
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected == 0) {
				System.out.println("No se actualizó ninguna reseña. Verifica que el ID sea correcto: " + review.getId());
			} else {
				System.out.println("Reseña actualizada correctamente. Filas afectadas: " + rowsAffected);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return review;
	}
	
	public Review deleteReview(int id_review) {
		Review rev = new Review();
		String sql = "DELETE FROM reviews WHERE id_review = ?";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, id_review);
						
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected == 0) {
				System.out.println("No se eliminó ninguna reseña. Verifica que el ID sea correcto: " + id_review);
			} else {
				System.out.println("Reseña eliminada correctamente. Filas afectadas: " + rowsAffected);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rev;
		
	}
	

}
