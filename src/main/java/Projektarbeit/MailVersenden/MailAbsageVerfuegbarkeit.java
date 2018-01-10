package Projektarbeit.MailVersenden;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class MailAbsageVerfuegbarkeit implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
			String anrede = (String) execution.getVariable("anrede");
			String vorname = (String) execution.getVariable("vorname");
			String nachname = (String) execution.getVariable("nachname");
			String eMailAdresse = (String) execution.getVariable("eMailAdresse");

			String mailtext = "Sehr geehrte/er " + anrede + " " + vorname + " " + nachname + ",\n" 
			+ "\nvielen Dank für Ihre Leihanfrage an die Initiative Studimeile."
			+ "\nWir sind eine anerkannte studentische Initiative, welche Aufgrund von finanzieller Unterstützung seitens der Studierendenschaft, dieses Angebot zur Verfügung stellt."		
			+ "\nLeider müssen wir Ihnen mitteilen, dass die von Ihnen gewünschten Artikel, zum gewünschten Zeitpunkt nicht verfügbar sind."
			+ "\n\nSie können gerne per Mail eine Anfrage stellen um freie Zeiträume zu erhalten. Die Aussagen sind dann aber nur für den ermittelte Zeitpunkt fixiert."
			+ "Währendessen gestellte Anfragen sind dahingegen nicht berücksichtigt."
			+ "\n\nMit freundlichen Grüßen,\n die Initiative Studimeile";

			String subject = "Ihre Leihanfrage - Absage mangels Verfügbarkeit";
			sendEmail(mailtext, subject, eMailAdresse);
		}

		public void sendEmail(String mailtext, String subject, String eMailAdresse) throws EmailException {
	
			MultiPartEmail email = new MultiPartEmail();
			email.setCharset("utf-8");
			email.setSSL(true);
			email.setSmtpPort(993);
//			email.setHostName("mail.gmx.net");
//			email.setAuthentication("XXXX@gmx.de", "XXXXXXX");
//			email.addTo(toEmail);
//			email.setFrom("XXXXXX@gmx.de");
			email.setHostName("mail.students-htw.de");
			email.setAuthentication("rheinze", "mdma.42");
			email.addTo(eMailAdresse);
			email.setFrom("r.heinze@students-htw.de");
			email.setSubject(subject);
			email.setMsg(mailtext);
			email.send();

		}
}
