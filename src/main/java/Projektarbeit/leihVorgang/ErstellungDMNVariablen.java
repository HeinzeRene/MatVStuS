package Projektarbeit.leihVorgang;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CamundaProjekt.leihVorgangStuS.Datenbankzugang;
/**
 * 
 * @author Chris Lohr
 * Erstellt am: 23.12.2017
 * Zuletzt geaendert von: René Heinze
 * Zuletzt geaendert am: 12.01.2018
 */
public class ErstellungDMNVariablen implements JavaDelegate {
	private static final Logger L =  (Logger) LoggerFactory.getLogger(ErstellungDMNVariablen.class);
	@Override
	/**
	 * Stellt die Werte fuer das DMN Diagramm zusammen.
	 * Ob die person ein STudent ist
	 * Ob die person in einem Grmeium ist
	 */
	public void execute(DelegateExecution execute) throws Exception {
		// TODO Auto-generated method stub
		String sql;
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
		int idPerson = (int)execute.getVariable("idPerson");
		sql = "SELECT matrikelnummer FROM Person WHERE idPerson = ?";
		try(PreparedStatement ps = conn.prepareStatement(sql))
		{
			ps.setInt(1, idPerson);
			L.info("Prüfung von Matrikelnummer zur PersonID: " + idPerson);
			L.debug(ps.toString());
			try(ResultSet rs = ps.executeQuery())
			{
				if(rs.next())
				{
					if (rs.getString("matrikelnummer")==null) {
						L.info("Die Person ist kein Student.");
						execute.setVariable("studentBool", false);
					} else {
						L.info("Die Person ist ein Student mit der Matrikelnummer: "+ rs.getString("matrikelnummer"));
						execute.setVariable("studentBool", true);
					}
				}
				else
				{
					L.warn("Es gibt keine Person mit der idPerson: "+ idPerson + " studentBool wird auf false gesetzt");
					execute.setVariable("studentBool", false);
				}
			}
		}
		sql = "SELECT pg.gremiumid FROM Person p INNER JOIN personGremium pg ON p.idPerson=pg.personid WHERE p.idPerson = ?";
		try(PreparedStatement ps = conn.prepareStatement(sql))
		{
			ps.setInt(1, idPerson);
			try(ResultSet rs = ps.executeQuery())
			{
				if (rs.next()) {
					if(rs.getString("pg.gremiumid") != null) {
					L.info("Die Person ist teil mindestens einen Gremiums.");
					execute.setVariable("gremiumBool", true);
					}else {
						L.info("Die Person ist nicht teil eines Gremiums oder die Person gibt es nicht");
						execute.setVariable("gremiumBool", false);
					}
				}
				else
				{
					L.warn("Die Person: " + idPerson + " ist nicht Teil eines Gremium - gremiumBool wird auf false gesetzt");
					execute.setVariable("gremiumBool", false);
				}
			}
		
		}
	}

}
