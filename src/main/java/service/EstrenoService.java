package service;

import java.util.List;
import repository.EstrenoRepository;
import entity.Estreno;

public class EstrenoService {
	
	private EstrenoRepository estrenoRepository;
	
	public EstrenoService(EstrenoRepository estrenoRepository) {
		this.estrenoRepository = estrenoRepository;
	}
	
	public List<Estreno> getAllEstrenos() {
		return estrenoRepository.findAll();
	}
	
	public Estreno getEstrenoById(int id) {
		return estrenoRepository.findOne(id);
	}
	
	public Estreno createEstreno(Estreno estreno) {
			return estrenoRepository.add(estreno);
	}
	
	public Estreno updateEstreno(Estreno estreno) {
		return estrenoRepository.update(estreno);
	}
	
	public Estreno deleteEstreno(Estreno estreno) {
		return estrenoRepository.delete(estreno);
	}
}
