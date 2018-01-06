package Projektarbeit.SubProc;

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

public class PruefenKundenkonto implements JavaDelegate{

	private String eMailAdresse;
	private boolean kontoVorh;
	private static final Logger L =  (Logger) LoggerFactory.getLogger(PruefenKundenkonto.class);
	
	
	@Override
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
		L.info("Start Auslesen von Kundendaten");
		String sql = "select * person from where vorname = ? and nachname is ?";
		
		try(PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, (String)execute.getVariable("vorname"));
			ps.setString(2, (String)execute.getVariable("nachname"));
			L.info("SQL Anfrage: " + ps.toString());
			try(ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
					eMailAdresse = rs.getString("eMailAdresse");
					L.info("E-Mail Adresse ist: " + eMailAdresse);
				}
			}catch  (SQLException e) {
			L.error(""+e);
			throw new Exception(e);
			}
		L.info("Ende des Einlesens");
		} catch (SQLException e) {	
			e.printStackTrace();
		}
		
		if(eMailAdresse == null) {
			kontoVorh = false;	
			L.info("Es gibt KEIN Konto mit der E-Mail adresse");
		}else {
			kontoVorh = true;
			L.info("Es gibt EIN Konto mit der E-Mail adresse");
		}
		
		execute.setVariable("kontoVorh",kontoVorh);
	}
	

	
}//end of class
