package Projektarbeit.MailVersenden;
import java.io.InputStream;
import javax.activation.DataHandler;
import javax.mail.internet.MimeBodyPart;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.value.FileValue;

public class MailAngebotVersenden implements JavaDelegate {

	public void execute(DelegateExecution execution) throws Exception {

		String anrede = (String) execution.getVariable("anrede");
		String vorname = (String) execution.getVariable("vorname");
		String nachname = (String) execution.getVariable("nachname");
		String AntragLeihNr = (String) execution.getVariable("AntragLeihNr"); //-->Sollte nach Plan ein Integer sein - Prüfen!!
		String toEmail = (String) execution.getVariable("email");
		String subject = "Ihre Leihanfrage " + AntragLeihNr + "/Übersendung Leihangebot";
		String mailtext = "Sehr geehrte/er " + anrede + " " + vorname + " " + nachname + ",\n" 
		+ "\nvielen Dank für Ihre Teilnahme am Bewerbungsgespräch."
		+ "\nHiermit teilen wir Ihnen mit, dass wir die gewünschten Artikel im Bestand haben."
		+ "\nAnbei übersenden wir Ihnen das Leihangebot. Bitte lesen Sie sich dieses Dokument vollständig durch."
		+ "\nSofern der gewünschte Zeitraum nicht zur Verfügung steht, wird im Dokument ein alternativer Leihzeitraum vorgeschlagen"
		+ "\nBitte geben Sie uns binnen 7 Tagen eine Rückmeldung. So lang sind die Artikel für Sie reserviert." 
		+ "\nZur Annahme oder Absage drücken Sie bitte die in der Mail angezeigte Button mit entsprechenden Bezeichnung."
		+ "\n\nMit freundlichen Grüßen,\n die Initiative Studimeile.";

		// https://docs.camunda.org/manual/7.5/user-guide/process-engine/variables/

		FileValue retrievedTypedFileValue = execution.getVariableTyped("AngebotLeihe");
		InputStream fileContent = retrievedTypedFileValue.getValue(); // bytestream
		String fileName = retrievedTypedFileValue.getFilename(); // filename
		String mimeType = retrievedTypedFileValue.getMimeType(); // memetype
		String encoding = retrievedTypedFileValue.getEncoding(); // encodung

		MimeBodyPart attachment = new MimeBodyPart();
		ByteArrayDataSource ds = new ByteArrayDataSource(fileContent, mimeType);

		attachment.setDataHandler(new DataHandler(ds));
		attachment.setFileName(fileName);

		sendEmail(mailtext, subject, toEmail, ds, fileName, encoding);
	}

	public void sendEmail(String mailtext, String subject, String toEmail, ByteArrayDataSource ds, String fileName, String encoding) throws EmailException {
		//https://anleitungen.rz.htw-berlin.de/de/email/e-mail_programm/

		/*
		 * Host und Mail anpassen!!!
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
		email.setAuthentication("Matrikelnummer", "Passwort");
		email.addTo(toEmail);
		email.setFrom("Matrikelnummer@htw-berlin.de");
		email.setSubject(subject);
		email.setMsg(mailtext);
		email.attach(ds, fileName, encoding);
		email.send();

	}

}