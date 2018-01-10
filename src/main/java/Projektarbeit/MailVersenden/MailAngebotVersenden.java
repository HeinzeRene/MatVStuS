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
		String leihscheinNummer = (String) execution.getVariable("leihscheinNummer"); //-->Sollte nach Plan ein Integer sein - Prüfen!!
		String eMailAdresse = (String) execution.getVariable("eMailAdresse");
		String subject = "Ihre Leihanfrage " + leihscheinNummer + "/Übersendung Leihangebot";
		String mailtext = "Sehr geehrte/er " + anrede + " " + vorname + " " + nachname + ",\n" 
		+ "\nVielen Dank für Ihre Anfrage bei der Initiative Studimeile."
		+ "\nWir sind eine anerkannte studentische Initiative, welche durch Mittel der Studierendenschaft der HTW Berlin finanziert wird."
		+ "\n\n anbei übersenden wir Ihnen das Angebot zur Anfrage"
		+ "\n Bitte beantworten Sie per Mail an: ini-studimeile@studets-htw.de, ob Sie dieses Angebot annehmen möchten."
		+ "\n\nMit freundlichen Grüßen,\n die Initiative Studimeile.";

		// https://docs.camunda.org/manual/7.5/user-guide/process-engine/variables/

		FileValue retrievedTypedFileValue = execution.getVariableTyped("Leihanfrage: " + leihscheinNummer);
		InputStream fileContent = retrievedTypedFileValue.getValue(); // bytestream
		String fileName = retrievedTypedFileValue.getFilename(); // filename
		String mimeType = retrievedTypedFileValue.getMimeType(); // memetype
		String encoding = retrievedTypedFileValue.getEncoding(); // encodung

		MimeBodyPart attachment = new MimeBodyPart();
		ByteArrayDataSource ds = new ByteArrayDataSource(fileContent, mimeType);

		attachment.setDataHandler(new DataHandler(ds));
		attachment.setFileName(fileName);

		sendEmail(mailtext, subject, eMailAdresse, ds, fileName, encoding);
	}

	public void sendEmail(String mailtext, String subject, String eMailAdresse, ByteArrayDataSource ds, String fileName, String encoding) throws EmailException {

		MultiPartEmail email = new MultiPartEmail();
		email.setCharset("utf-8");
		email.setSSL(true);
		email.setSmtpPort(587);
//		email.setHostName("mail.gmx.net");
//		email.setAuthentication("XXXX@gmx.de", "XXXXXXX");
//		email.addTo(toEmail);
//		email.setFrom("XXXXXX@gmx.de");
		email.setHostName("mail.students-htw.de");
		email.setAuthentication("rheinze", "mdma.42");
		email.addTo(eMailAdresse);
		email.setFrom("r.heinze@students-htw.de");
		email.setSubject(subject);
		email.setMsg(mailtext);
		email.attach(ds, fileName, encoding);
		email.send();

	}

}