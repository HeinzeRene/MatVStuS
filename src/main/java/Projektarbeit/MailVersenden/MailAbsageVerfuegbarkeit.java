package Projektarbeit.MailVersenden;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CamundaProjekt.leihVorgangStuS.EMailZugangsdaten;
import Projektarbeit.SubProc.AnlegenKonto;

public class MailAbsageVerfuegbarkeit implements JavaDelegate {

	private static final Logger L = LoggerFactory.getLogger(MailAbsageVerfuegbarkeit.class);

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		
			String anrede = (String) execution.getVariable("anrede");
			String vorname = (String) execution.getVariable("vorname");
			String nachname = (String) execution.getVariable("nachname");
			String eMailAdresse = (String) execution.getVariable("eMailAdresse");
			String materialB = (String) execution.getVariable("matBez");
			String leihBegin = (String)execution.getVariable("leihBegin");
			String leihEnde = (String)execution.getVariable("leihEnde");
			
			L.info("Variablen ausgelesen: " + anrede + " " + vorname + " " + nachname + " " + eMailAdresse);
			
			String mailtext = "Sehr geehrte/er " + anrede + " " + vorname + " " + nachname + ",\n" 
			+ "\nvielen Dank für Ihre Leihanfrage an die Initiative Studimeile."
			+ "\ndas angefrage Material: " + materialB + " im Zeitraum von " + leihBegin+  " bis " + leihEnde
			+ "\nWir sind eine anerkannte studentische Initiative, welche Aufgrund von finanzieller Unterstützung seitens der Studierendenschaft, dieses Angebot zur Verfügung stellt."		
			+ "\nLeider müssen wir Ihnen mitteilen, dass die von Ihnen gewünschten Artikel, zum gewünschten Zeitpunkt nicht verfügbar sind."
			+ "\n\nSie können gerne per Mail eine Anfrage stellen um freie Zeiträume zu erhalten. Die Aussagen sind dann aber nur für den ermittelte Zeitpunkt fixiert."
			+ "Währendessen gestellte Anfragen sind dahingegen nicht berücksichtigt."
			+ "\n\nMit freundlichen Grüßen,\n die Initiative Studimeile";

			String subject = "Ihre Leihanfrage - Absage mangels Verfügbarkeit";
			sendEmail(mailtext, subject, eMailAdresse);
		}

	public void sendEmail(String mailtext, String subject, String toEmail) throws EmailException {
		//https://anleitungen.rz.htw-berlin.de/de/email/e-mail_programm/

		MultiPartEmail email = new MultiPartEmail();
		email.setCharset("utf-8");
		email.setSSL(true);
		email.setSmtpPort(EMailZugangsdaten.SMTPPORT);
//		email.setSmtpPort(587) -> diese Informationen sind je Provider unterschiedlich
		email.setHostName(EMailZugangsdaten.HOSTNAME); 
		email.setAuthentication(EMailZugangsdaten.NUTZERNAME, EMailZugangsdaten.PASSWORT);
		email.addTo(toEmail);
		email.setFrom(EMailZugangsdaten.ABSENDER);
//		email.setHostName("mail.htw-berlin.de");
//		email.setAuthentication("s0558270", "Chrischris123");
//		email.addTo(toEmail);
//		email.setFrom("s0558270@htw-berlin.de");
		email.setSubject(subject);
		email.setMsg(mailtext);
		try
		{
			email.send();
		}
		catch(EmailException e)
		{
			L.warn("Die Absage EMail konnte nicht gesendet werden");
		}
	}
}
