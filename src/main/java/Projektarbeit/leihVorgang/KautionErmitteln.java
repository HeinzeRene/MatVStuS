package Projektarbeit.leihVorgang;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class KautionErmitteln implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub

		double 	preis = (double) execution.getVariable("wert");
		execution.setVariable("Preis", preis);
		
		if ((String) execution.getVariable("matNr")!= null) {
			execution.setVariable("student?", false);
		} else {
			execution.setVariable("student?", true);
		}
		
		if ((String) execution.getVariable("gremium") != null) {
			execution.setVariable("gremium?", false);
		}else {
			execution.setVariable("gremium?", true);
		}
	}// end of execute

}
