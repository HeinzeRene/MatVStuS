package Projektarbeit.MailVersenden;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class MailNachfrageAngebot implements JavaDelegate {

	public void execute(DelegateExecution execution) throws Exception {

		String anrede = (String) execution.getVariable("anrede");
		String vorname = (String) execution.getVariable("vorname");
		String nachname = (String) execution.getVariable("nachname");
		String taetigkeit = (String) execution.getVariable("taetigkeit");
		String email = (String) execution.getVariable("email");

		/*
		 * Mail umfasst eine vollständige Absage. Wenn einzelne Artikel nicht vorhanden sind 
		 * muss der Text mit entsprechenden Variablen umgeschrieben werden
		 */
		String mailtext = "Sehr geehrte/er " + anrede + " " + vorname + " " + nachname + ",\n" 
		+ "\nvielen Dank für Ihre Leihanfrage an die Initiative Studimeile."
		+ "\nWir sind eine anerkannte studentische Initiative, welche Aufgrund von finanzieller Unterstützung seitens der Studierendenschaft, dieses Angebot zur Verfügung stellt"		
		+ "\nLeider müssen wir Ihnen mitteilen, dass die von Ihnen gewünschten Artikel sind nicht vollstädig im Bestand der Initiative."
		+ "\nSofern Sie weiterhin einzelne Artikel benötigen, welche im Bestand sind, stellen Sie bitte eine neue Anfrage auf genau diese Artikel um die Bedarfsdeckung zu prüfen."
		+ "\n\nMit freundlichen Grüßen,\n die Initiative Studimeile";

		String subject = "Ihre Leihanfrage - Absage mangels Bestand";
		sendEmail(mailtext, subject, email);
	}

	public void sendEmail(String mailtext, String subject, String toEmail) throws EmailException {
		//https://anleitungen.rz.htw-berlin.de/de/email/e-mail_programm/

		/*
		 * Host und Mail anpassen	
		 */
		MultiPartEmail email = new MultiPartEmail();
		email.setCharset("utf-8");
		email.setSSL(true);
		email.setSmtpPort(587);
//		email.setHostName("mail.gmx.net");
//		email.setAuthentication("XXXX@gmx.de", "XXXXXXX");
//		email.addTo(toEmail);
//		email.setFrom("XXXXXX@gmx.de");
		email.setHostName("mail.htw-berlin.de");
		email.setAuthentication("s0558874", "@Swastika83");
		email.addTo(toEmail);
		email.setFrom("s0558874@htw-berlin.de");
		email.setSubject(subject);
		email.setMsg(mailtext);
		email.send();

	}

}
