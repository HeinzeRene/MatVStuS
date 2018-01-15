package CamundaProjekt.leihVorgangStuS;
/**
 * EMail Zugangsdaten fuer das Verschicken von EMails
 * @author Chris Lohr
 * Erstellt am: 25.12.2017
 * Zuletzt geaendert von: René Heinze
 * Zuletzt geaendert am: 15.01.2018
 */
public class EMailZugangsdaten {
	/**
	 * Hostname des EMail ausgangsservers
	 */
	public static final String HOSTNAME = "mail.htw-berlin.de";
	/**
	 * Nutzername fuer den Mail Server
	 */
	public static final String NUTZERNAME = "s0559985";
	/**
	 * Passwort passend zu dem Nutzernamen
	 * @see NUTZERNAME
	 */
	public static final String PASSWORT = "Muffini12";
	/**
	 * Die EMail Adresse die als Abender Angezeigt wird
	 */
	public static final String ABSENDER = "marwin.moellers@student.htw-berlin.de";
	/**
	 * Der Port ueber den der SMTP Server zu erreichen ist
	 */
	public static final int SMTPPORT = 587;
}
