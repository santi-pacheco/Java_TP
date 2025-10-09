package repository;

import java.sql.Connection;
import java.util.List;
import entity.Estreno;
import util.DataSourceProvider;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

public class EstrenoRepository {
	
	public EstrenoRepository() {}
	
	public List<Estreno> findAll() {
		// Implementación para obtener todos los estrenos
		return null; // Reemplazar con la lista real de estrenos
	}

	public Estreno findOne(int id) {
		// Implementación para obtener un estreno por ID
		return null; // Reemplazar con el estreno real
	}
	
	public Estreno add(Estreno estreno) {
		// Implementación para agregar un nuevo estreno
		return null; // Reemplazar con el estreno agregado
	}
	
	public Estreno update(Estreno estreno) {
		// Implementación para actualizar un estreno existente
		return null; // Reemplazar con el estreno actualizado
	}
	
	public Estreno delete(Estreno estreno) {
		// Implementación para eliminar un estreno
		return null; // Reemplazar con el estreno eliminado
	}
}
