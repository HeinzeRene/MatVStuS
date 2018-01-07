package Projektarbeit.leihVorgang;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class VerfZeitraum implements JavaDelegate{

	@Override
	public void execute(DelegateExecution execute) throws Exception {
		// TODO Auto-generated method stub
		execute.setVariable("verfZeit", true);
	}

	
	
}
