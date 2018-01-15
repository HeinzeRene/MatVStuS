package Projektarbeit.SubProc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CamundaProjekt.leihVorgangStuS.Datenbankzugang;
/**
 * 
 * @author René Heinze
 * Erstellt am: 23.12.2017
 * Zuletzt geaendert von: Chris Lohr
 * Zuletzt geaendert am: 15.01.2018
 */
public class PruefenKundenkonto implements JavaDelegate{

	private int idPerson;
	private boolean kontoVorh;
	private static final Logger L =  (Logger) LoggerFactory.getLogger(PruefenKundenkonto.class);
	
	
	@Override
	/**
	 * Errechnet, ob der Nutzer mit der Mail schon vorhanden ist.
	 * Wenn er vorhanden ist, dann werden die Daten aus der Datenbank mit den Eingegebenen verglichen und gegebenenfalls werden die Daten aus der Datenbank mit den Daten aus dem Forumlar aktuallisiert
	 */
	public void execute(DelegateExecution execute) throws Exception {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			L.info("* Treiber laden");
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception e) {
			L.error("Unable to load driver.");
			e.printStackTrace();
		}
		try {
			L.info("* Verbindung aufbauen");
			String url = "jdbc:mysql://" + Datenbankzugang.hostname + ":" + Datenbankzugang.port + "/" + Datenbankzugang.dbname;
			conn = DriverManager.getConnection(url, Datenbankzugang.user, Datenbankzugang.password);
			
			
			
		} catch (SQLException sqle) {
			L.error("SQLException: " + sqle.getMessage() + "/n SQLState: " + sqle.getSQLState() + " VendorError: " + sqle.getErrorCode());

		}
		boolean isIdPerson = false;
		L.info("Start Auslesen von Kundendaten");
		String sql = "select * from Person where eMailAdresse = ?";
		
		try(PreparedStatement ps = conn.prepareStatement(sql)){
			
			ps.setString(1, (String)execute.getVariable("eMailAdresse"));
			L.debug(ps.toString());
			try(ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
					isIdPerson = true;
					idPerson = rs.getInt("idPerson");
					execute.setVariable("idPerson", idPerson);
					L.info("E-Mail Adresse: " + (String)execute.getVariable("eMailAdresse") + " idPerson: " + rs.getInt("idPerson") + " name: " + rs.getString("vorname") + " " + rs.getString("nachname"));
					if(!((String)execute.getVariable("anrede")).equalsIgnoreCase(rs.getString("anrede")))
					{
						L.info("Die Anrede stimmt nicht mit der Datenbank überein. Neuer Vorname wird in die Datenbank geschrieben.");
						L.info("Alt: " + rs.getString("anrede"));
						L.info("Neu: " + (String)execute.getVariable("anrede"));
						try(PreparedStatement pps = conn.prepareStatement("UPDATE Person SET anrede = ? WHERE idPerson = ?"))
						{
							pps.setString(1, (String)execute.getVariable("anrede"));
							pps.setInt(2, idPerson);
						}
					}
					else
					{
						L.info("Die Anrede stimmt mit der Datenbank überein.");
					}
					if(!((String)execute.getVariable("vorname")).equalsIgnoreCase(rs.getString("vorname")))
					{
						L.info("Der Vorname stimmt nicht mit der Datenbank überein. Neuer Vorname wird in die Datenbank geschrieben.");
						L.info("Alt: " + rs.getString("vorname"));
						L.info("Neu: " + (String)execute.getVariable("vorname"));
						try(PreparedStatement pps = conn.prepareStatement("UPDATE Person SET vorname = ? WHERE idPerson = ?"))
						{
							pps.setString(1, (String)execute.getVariable("vorname"));
							pps.setInt(2, idPerson);
						}
					}
					else
					{
						L.info("Der Vorname stimmt mit der Datenbank überein.");
					}
					if(!((String)execute.getVariable("nachname")).equalsIgnoreCase(rs.getString("nachname")))
					{
						L.info("Der Nachname stimmt nicht mit der Datenbank überein. Neuer Nachname wird in die Datenbank geschrieben.");
						String alt = rs.getString("nachname");
						String neu = (String)execute.getVariable("nachname");
						L.info("Alt: " + alt);
						L.info("Neu: " + neu);
						try(PreparedStatement pps = conn.prepareStatement("UPDATE Person SET vorname = ? WHERE idPerson = ?"))
						{
							pps.setString(1, neu);
							pps.setInt(2, idPerson);
							pps.execute();
						}
					}
					else
					{
						L.info("Der Nachname stimmt mit der Datenbank überein.");
					}
					if(!((String)execute.getVariable("nachname")).equalsIgnoreCase(rs.getString("nachname")))
					{
						L.info("Der Nachname stimmt nicht mit der Datenbank überein. Neuer Nachname wird in die Datenbank geschrieben.");
						String alt = rs.getString("nachname");
						String neu = (String)execute.getVariable("nachname");
						L.info("Alt: " + alt);
						L.info("Neu: " + neu);
						try(PreparedStatement pps = conn.prepareStatement("UPDATE Person SET nachname = ? WHERE idPerson = ?"))
						{
							pps.setString(1, neu);
							pps.setInt(2, idPerson);
							pps.execute();
						}
					}
					else
					{
						L.info("Der Matrikelnummer stimmt mit der Datenbank überein.");
					}
					if(!((String)execute.getVariable("matNr")).equalsIgnoreCase(rs.getString("matrikelnummer")))
					{
						L.info("Die Matrikelnummer stimmt nicht mit der Datenbank überein. Neue Matrikelnummer wird in die Datenbank geschrieben.");
						String alt = rs.getString("matrikelnummer");
						String neu = (String)execute.getVariable("matNr");
						L.info("Alt: " + alt);
						L.info("Neu: " + neu);
						try(PreparedStatement pps = conn.prepareStatement("UPDATE Person SET matrikelnummer = ? WHERE idPerson = ?"))
						{
							pps.setString(1, neu);
							pps.setInt(2, idPerson);
							pps.execute();
						}
					}
					else
					{
						L.info("Die Adresse stimmt mit der Datenbank überein.");
					}
					if(!((String)execute.getVariable("adresse")).equalsIgnoreCase(rs.getString("adresse")))
					{
						L.info("Die Adresse stimmt nicht mit der Datenbank überein. Neue Adresse wird in die Datenbank geschrieben.");
						String alt = rs.getString("adresse");
						String neu = (String)execute.getVariable("adresse");
						L.info("Alt: " + alt);
						L.info("Neu: " + neu);
						try(PreparedStatement pps = conn.prepareStatement("UPDATE Person SET adresse = ? WHERE idPerson = ?"))
						{
							pps.setString(1, neu);
							pps.setInt(2, idPerson);
							pps.execute();
						}
					}
					else
					{
						L.info("Die Adresse stimmt mit der Datenbank überein.");
					}
					if(!((String)execute.getVariable("plz")).equalsIgnoreCase(rs.getString("plz")))
					{
						L.info("Die Postleitzahl stimmt nicht mit der Datenbank überein. Neue Postleitzahl wird in die Datenbank geschrieben.");
						String alt = rs.getString("plz");
						String neu = (String)execute.getVariable("plz");
						L.info("Alt: " + alt);
						L.info("Neu: " + neu);
						try(PreparedStatement pps = conn.prepareStatement("UPDATE Person SET plz = ? WHERE idPerson = ?"))
						{
							pps.setString(1, neu);
							pps.setInt(2, idPerson);
							pps.execute();
						}
					}
					else
					{
						L.info("Die Postleitzahl stimmt mit der Datenbank überein.");
					}
					if(!((String)execute.getVariable("wohnort")).equalsIgnoreCase(rs.getString("wohnort")))
					{
						L.info("Der Wohnort stimmt nicht mit der Datenbank überein. Neuer Wohnort wird in die Datenbank geschrieben.");
						String alt = rs.getString("wohnort");
						String neu = (String)execute.getVariable("wohnort");
						L.info("Alt: " + alt);
						L.info("Neu: " + neu);
						try(PreparedStatement pps = conn.prepareStatement("UPDATE Person SET wohnort = ? WHERE idPerson = ?"))
						{
							pps.setString(1, neu);
							pps.setInt(2, idPerson);
							pps.execute();
						}
					}
					else
					{
						L.info("Der Wohnort stimmt mit der Datenbank überein.");
					}
					if((String)execute.getVariable("gremium")==null)
					{
						L.info("Es ist kein Gremium angegeben, es werden alle Gremien zuheörigkeiten gelöscht.");
						try(PreparedStatement pps = conn.prepareStatement("DELETE FROM personGremium WHERE personid = ?"))
						{
							pps.setInt(1, idPerson);
							pps.execute();
						}
					}
					else
					{
						L.info("Gremien zugehörigkeit wird beibehalten");
					}
					
					
					
				}
			}catch  (SQLException e) {
			L.error(""+e);
			throw new Exception(e);
			}
		L.info("Ende des Einlesens");
		} catch (SQLException e) {	
			e.printStackTrace();
		}
		
		if(!isIdPerson) {
			kontoVorh = false;	
			L.info("Es gibt KEIN Konto mit der E-Mail adresse");
		}else {
			kontoVorh = true;
			L.info("Es gibt EIN Konto mit der E-Mail adresse");
		}
		
		execute.setVariable("kontoVorh",kontoVorh);
	}
	

	
}//end of class
