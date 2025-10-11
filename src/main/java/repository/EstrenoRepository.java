/*
package repository;

import java.sql.Connection;
import java.util.List;
import entity.Estreno;
import util.DataSourceProvider;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import exception.ErrorFactory;

public class EstrenoRepository {
	
	public EstrenoRepository() {}
*/
/*
 Esquema de la tabla estrenos:
 id_estreno / getId() setId(int id)
 anio / getAnio() setAnio(int anio)
*/
/*
	public List<Estreno> findAll() {
	    List<Estreno> estrenos = new ArrayList<>();
	    String sql = "SELECT id_estreno, anio FROM estrenos ORDER BY id_estreno";
	    
	    try (Connection conn = DataSourceProvider.getDataSource().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	         
	        while (rs.next()) {
	            Estreno estreno = new Estreno();
	            estreno.setId(rs.getInt("id_estreno"));
	            estreno.setAnio(rs.getInt("anio"));
	            
	            estrenos.add(estreno);
	        }
	    } catch (SQLException e) {
	    	throw ErrorFactory.internal("Error fetching estrenos from database");
	    }
	    
	    return estrenos;
	}
	
	public Estreno findOne(int id) {
		Estreno estreno = null;
		String sql = "SELECT id_estreno, anio FROM estrenos WHERE id_estreno = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					estreno = new Estreno();
					estreno.setId(rs.getInt("id_estreno"));
					estreno.setAnio(rs.getInt("anio"));
				}
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching estreno by ID from database");
		}
		return estreno;
	}
	
	public Estreno add(Estreno e) {
		String sql = "INSERT INTO estrenos (anio) VALUES (?)";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			
			stmt.setInt(1, e.getAnio());
			int affectedRows = stmt.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						e.setId(generatedKeys.getInt(1));
					}
				}
			}
		} catch (SQLException ex) {
			if (ex.getSQLState().equals("23505")) { // Código SQLState para violación de restricción única en PostgreSQL
				throw ErrorFactory.duplicate("Username or email already exists");
			} else {
				throw ErrorFactory.internal("Error adding user to database");
			}
		}
		return e;
	}
	
	public Estreno update(Estreno e) {
		String sql = "UPDATE estrenos SET anio = ? WHERE id_estreno = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, e.getAnio());
			stmt.setInt(2, e.getId());
			stmt.executeUpdate();
			
		} catch (SQLException ex) {
			throw ErrorFactory.internal("Error updating estreno in database");
		}
		return e;
	}
	
	public Estreno delete(Estreno e) {
		String sql = "DELETE FROM estrenos WHERE id_estreno = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, e.getId());
			stmt.executeUpdate();
			
		} catch (SQLException ex) {
			throw ErrorFactory.internal("Error deleting estreno from database");
		}
		return e;
	}
	
	public Estreno findByYear(int year) {
		Estreno estreno = null;
		String sql = "SELECT id_estreno, anio FROM estrenos WHERE anio = ?";
		
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, year);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					estreno = new Estreno();
					estreno.setId(rs.getInt("id_estreno"));
					estreno.setAnio(rs.getInt("anio"));
				}
			}
		} catch (SQLException e) {
			throw ErrorFactory.internal("Error fetching estreno by year from database");
		}
		return estreno;
	}
}
*/