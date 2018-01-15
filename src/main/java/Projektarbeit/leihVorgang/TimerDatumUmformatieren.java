package Projektarbeit.leihVorgang;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author Marwin
 * Erstellt am: 15.01.2018
 * Zuletzt geaendert von:Marwin Möllers
 * Zuletzt geaendert am: 15.01.2018
 */
public class TimerDatumUmformatieren implements JavaDelegate {
	private static final Logger L = LoggerFactory.getLogger(TimerDatumUmformatieren.class);
	@Override
	/**
	 * Rechnet den Beginn der Ausleihe in das von Camunda geforderte Format um
	 */
	public void execute(DelegateExecution execute) throws Exception {
		String anf = (String)execute.getVariable("anfangausleihe");
		String uhr = (String)execute.getVariable("uhrzUeber");
		
		String[] geteilt = anf.split("\\.");
		String timer = ""+geteilt[2]+"-"+geteilt[1]+"-"+geteilt[0]+"T"+uhr+":00Z";
		L.info("Ende Timer gesetzt auf: "+ timer);
		execute.setVariable("timerBegin", timer);
	}

}
