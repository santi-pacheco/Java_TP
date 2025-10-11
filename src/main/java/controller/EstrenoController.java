/*
package controller;

import service.EstrenoService;
import java.util.List;
import entity.Estreno;

public class EstrenoController {	
	
	private EstrenoService estrenoService;
	
	public EstrenoController(EstrenoService estrenoService) {
		this.estrenoService = estrenoService;
	}
	
	public Estreno getEstrenoByYear(int year) {
		Estreno estrenoByYear = estrenoService.getEstrenoByYear(year);
		return estrenoByYear;
	}
	
	public List<Estreno> getEstrenos() {
		List<Estreno> estrenos = estrenoService.getAllEstrenos();
		return estrenos;
	}
	
	public Estreno getEstrenoById(int id) {
		Estreno estreno = estrenoService.getEstrenoById(id);
		return estreno;
	}
	
	public Estreno createEstreno(Estreno estreno) {
			return estrenoService.createEstreno(estreno);
	}
	
	public Estreno modifyEstreno(Estreno estreno) {
		return estrenoService.updateEstreno(estreno);
	}
	
	public void removeEstreno(Estreno estreno) {
		estrenoService.deleteEstreno(estreno);
	}
}
*/