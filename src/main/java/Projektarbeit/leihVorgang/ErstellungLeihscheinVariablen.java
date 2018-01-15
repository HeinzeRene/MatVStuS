package Projektarbeit.leihVorgang;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.zip.DataFormatException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CamundaProjekt.leihVorgangStuS.Datenbankzugang;

public class ErstellungLeihscheinVariablen implements JavaDelegate {

	private static final Logger L =  LoggerFactory.getLogger(ErstellungLeihscheinVariablen.class);
	private int leihscheinNummer = -1;
	private int seriennummer = -1;
	private String beschreibung = "";
	
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		L.info("Start Class ErstellungLeihscheinVariablen");
		int idPerson = (int) execution.getVariable("idPerson");

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
			String url = "jdbc:mysql://" + Datenbankzugang.hostname + ":" + Datenbankzugang.port + "/"
					+ Datenbankzugang.dbname;
			conn = DriverManager.getConnection(url, Datenbankzugang.user, Datenbankzugang.password);
		} catch (SQLException sqle) {
			L.error("SQLException: " + sqle.getMessage() + "/n SQLState: " + sqle.getSQLState() + " VendorError: "
					+ sqle.getErrorCode());
		}

		L.info("Start einlesen von Leihscheindaten");
		String sql = "insert into leihschein (idPerson, anfangausleihe, endeausleihe)" + " values (?, ?, ?)";
		L.info(sql);
		try (PreparedStatement s = conn.prepareStatement(sql)) {
			L.info(""+idPerson);
			s.setInt(1, idPerson);
			s.setTimestamp(2, getTimestamp((String) execution.getVariable("anfangausleihe"),(String)execution.getVariable("uhrzUeber")));
			s.setTimestamp(3, getTimestamp((String) execution.getVariable("endeausleihe"),(String)execution.getVariable("uhrzRueck")));
			s.executeUpdate();
		} catch (SQLException e) {
			L.error("" + e);
			throw new DataFormatException();
		}
		L.info("Ende des Einlesens");
		
		L.info("Start auslesen der Leihscheinnummer");
		sql = "select leihscheinNummer from leihschein where idPerson = ? and anfangausleihe = ? and endeausleihe = ? ";
		L.info(sql);
		try (PreparedStatement s = conn.prepareStatement(sql)){
			s.setInt(1, (int) execution.getVariable("idPerson"));
			s.setDate(2, ((Date) execution.getVariable("anfangausleihe"));
			L.info("idPerson zum Auslesen" + execution.getVariable("idPerson")); 
			try(ResultSet rs = s.executeQuery())
			{				
				
				leihscheinNummer = rs.getInt(sql);
				execution.setVariable("leihscheinNummer", leihscheinNummer);
				L.info("Ausgelesene Leihscheinnummer: " + rs.next() + " ,sprich: " + leihscheinNummer);	
			}
		} catch (SQLException e ) {
			L.error("" + e);
			throw new DataFormatException();	
		}		
		
		L.info("Start auslesen der Seriennummer");
		sql = "select seriennummer from MaterialExemplar me inner join materialLeihschein ml on me.idMatExp = ml.idMatExp where leihscheinNummer = ? ";
		L.info(sql);
		try (PreparedStatement s = conn.prepareStatement(sql)){
			s.setInt(1, (int) execution.getVariable("leihscheinNummer"));
			try(ResultSet rs = s.executeQuery())
			{
				seriennummer = rs.getInt(sql);
				execution.setVariable("seriennummer", seriennummer);
				L.info("ausgelesene Seriennummer: " + rs.next() + " sprich: " + seriennummer);

			}
		} catch (SQLException e ) {
			L.error("" + e);
			throw new DataFormatException();	
		}
		
		L.info("Start auslesen der Beschreibung/ Bezeichnung");
		sql = "select beschreibung from MaterialArt ma inner join MaterialExemplar me on me.idMatExp = ma.idMatArt inner join materialLeihschein ml on ml.idMatExp = me.idMatExp where leihscheinNummer = ? ";
		L.info(sql);
		try (PreparedStatement s = conn.prepareStatement(sql)){
			s.setInt(1, (int) execution.getVariable("leihscheinNummer"));
			try(ResultSet rs = s.executeQuery())
			{
				beschreibung = rs.getString(sql);
				execution.setVariable("beschreibung", beschreibung);
				L.info("ausgelesene Beschreibung: " + rs.next() + " sprich: " + beschreibung);

			}
		} catch (SQLException e ) {
			L.error("" + e);
			throw new DataFormatException();	
		}
		
	}
	
	private Timestamp getTimestamp(String datum, String uhrzeit)
	{
		String[] d = datum.split("\\.");
		String[] u = uhrzeit.split(":");
		return Timestamp.valueOf(""+d[2]+"-"+d[1]+"-"+d[0]+" "+u[0]+":"+u[1]+":00");
	}


}
