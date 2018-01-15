package CamundaProjekt.leihVorgangStuS;
/**
 * Speichert die Zugangdaten für die Datenbank die genutzt wird
 * @author Marwin
 * Erstellt am: 15.01.2018
 * Zuletzt geaendert von: Marwin Möllers
 * Zuletzt geaendert am: 15.01.2018
 */
public class Datenbankzugang {
	/**
	 * Die IP Adresse unter der der Server zu erreichen ist
	 */
	public static final String hostname = "141.45.123.80";
	/**
	 * Der Port des MySQL Servers
	 */
	public static final String port = "3306";
	/**
	 * Name der Datenbank
	 */
	public static final String dbname = "db69_trese";
	/**
	 * Nutzername unter dem sich der WF an der Datenbank anmeldet
	 */
	public static final String user = "user69";
	/**
	 * Passwort fuer den Nutzernamen
	 * @see user
	 */
	public static final String password = "Acht+9";
}
