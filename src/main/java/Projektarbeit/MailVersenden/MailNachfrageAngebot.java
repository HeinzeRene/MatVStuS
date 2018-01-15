package Projektarbeit.MailVersenden;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CamundaProjekt.leihVorgangStuS.EMailZugangsdaten;

public class MailNachfrageAngebot implements JavaDelegate {
	private static final Logger L = LoggerFactory.getLogger(MailNachfrageAngebot.class);
	public void execute(DelegateExecution execution) throws Exception {

		String anrede = (String) execution.getVariable("anrede");
		String vorname = (String) execution.getVariable("vorname");
		String nachname = (String) execution.getVariable("nachname");
		String leihscheinNummer = (String) execution.getVariable("leihscheinNummer");
		String eMailAdresse = (String) execution.getVariable("eMailAdresse");

		/*
		 * Mail umfasst eine vollständige Absage. Wenn einzelne Artikel nicht vorhanden sind 
		 * muss der Text mit entsprechenden Variablen umgeschrieben werden
		 */
		String mailtext = "Sehr geehrte/er " + anrede + " " + vorname + " " + nachname + ",\n" 
		+ "\nvielen Dank für Ihre Leihanfrage an die Initiative Studimeile."
		+ "\nWir sind eine anerkannte studentische Initiative, welche Aufgrund von finanzieller Unterstützung seitens der Studierendenschaft, dieses Angebot zur Verfügung stellt"		
		+ "\n Leider kam bis dato noch keine Antwort auf das Angebot. Sie haben noch 7 Tage Zeit bis die Reservierung erlischt."
		+ "Bitte antworten Sie auf die E-Mailadresse: ini-studimeile@students-htw.de"
		+ "\n\nMit freundlichen Grüßen,\n die Initiative Studimeile";

		String subject = "Ihre Leihanfrage - Leihscheinnummer" + leihscheinNummer;
		sendEmail(mailtext, subject, eMailAdresse);
	}

	public void sendEmail(String mailtext, String subject, String eMailAdresse) throws EmailException {

		MultiPartEmail email = new MultiPartEmail();
		email.setCharset("utf-8");
		email.setSSL(true);
		email.setSmtpPort(EMailZugangsdaten.SMTPPORT);
//		email.setHostName("mail.gmx.net");
//		email.setAuthentication("XXXX@gmx.de", "XXXXXXX");
//		email.addTo(toEmail);
//		email.setFrom("XXXXXX@gmx.de");
		email.setHostName(EMailZugangsdaten.HOSTNAME);
		email.setAuthentication(EMailZugangsdaten.NUTZERNAME, EMailZugangsdaten.PASSWORT);
		email.addTo(eMailAdresse);
		email.setFrom(EMailZugangsdaten.ABSENDER);
		email.setSubject(subject);
		email.setMsg(mailtext);
		try
		{
			email.send();
		}
		catch(EmailException e)
		{
			L.warn("Die Nachfrage EMail konnte nicht gesendet werden");
		}

	}

}
