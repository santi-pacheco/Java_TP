/*
package service;

import java.util.List;
import repository.EstrenoRepository;
import entity.Estreno;
import exception.ErrorFactory; 

public class EstrenoService {
	
	private EstrenoRepository estrenoRepository;
	
	public EstrenoService(EstrenoRepository estrenoRepository) {
		this.estrenoRepository = estrenoRepository;
	}
	
	public List<Estreno> getAllEstrenos() {
		return estrenoRepository.findAll();
	}
	
	public Estreno getEstrenoById(int id) {
		Estreno estreno = estrenoRepository.findOne(id);
		if (estreno == null) {
			throw ErrorFactory.notFound("Estreno not found with ID: " + id);
		}
		return estreno;
	}
	
	public Estreno createEstreno(Estreno estreno) {
		return estrenoRepository.add(estreno);
	}
	
	public Estreno updateEstreno(Estreno estreno) {
		// 1. Primero, verifica que el estreno exista
	    Estreno existingEstreno = estrenoRepository.findOne(estreno.getId());
	    if (existingEstreno == null) {
	        throw ErrorFactory.notFound("No se puede actualizar. Estreno con ID " + estreno.getId() + " no encontrado.");
	    }
	    // 2. Si existe, ahora sí actualiza
	    return estrenoRepository.update(estreno);	
	}
	
	public Estreno deleteEstreno(Estreno estreno) {
		Estreno estrenoToDelete = estrenoRepository.delete(estreno);
		return estrenoToDelete;
	}
	
	public Estreno getEstrenoByYear(int year) {
		Estreno estrenoByYear = estrenoRepository.findByYear(year);
		if (estrenoByYear == null) {
			throw ErrorFactory.notFound("Estreno not found for year: " + year);
		}
		return estrenoByYear; // Reemplazar con el estreno real
	}
}
*/