package Projektarbeit.leihVorgang;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class KautionErmitteln implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub

		boolean gremium = false;
		boolean student = false;
		double preis = 0;
		int anteilKaution = (int) execution.getVariable("ProzKaution");
		int kaution = -1;
		
		if ((String) execution.getVariable("matNr")!= null) {
			student = true;
		}
		if ((String) execution.getVariable("gremium") != null) {
			gremium = true;
		}
		
		
		execution.setVariable("gremium?", gremium);
		execution.setVariable("student?", student);
		
		preis = (double) execution.getVariable("wert");
		kaution = ((int) preis) * (anteilKaution/100);
		
		execution.setVariable("kaution", kaution);
		
		
	}

}
