package Projektarbeit.leihVorgang;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.zip.DataFormatException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AngebotErstellen  {


	private static final Logger L = LoggerFactory.getLogger(AngebotErstellen.class);
	//private int leihscheinNummer = -1;	Leihscheinnummer nur anwenden, wenn MySQL nicht automatisch eine vergibt
	private int idPerson = -1;
	
	private Connection connection;
		public void setConnection(Connection connection) {
		this.connection = connection;
	}//end of connection
	
	private Connection getConnection() {
		if (connection == null) {
			try {
				throw new Exception("Connection not set");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return connection;
	}//end of getConnection 

//	public int erstelleLeihscheinNummer() {
//		L.info("Start einlesen der letzten vergebenen Leihscheinnummer");
//		String sql = "select max(leihscheinNummer) from leihschein";
//		L.info(sql);
//		try(PreparedStatement s = connection.prepareStatement(sql)){
//			
//			leihscheinNummer = Integer.parseInt(sql) +1;
//			s.executeUpdate();
//		}catch  (SQLException e) {
//			L.error(""+e);
//			try {
//				throw new Exception();
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//		}
//		L.info("Ende des Einlesens");
//		return leihscheinNummer;
//	}
	
	public void execute(DelegateExecution execution) throws Exception {
	
		//leihscheinNummer = erstelleLeihscheinNummer();
				
		L.info("Auslesen der IdPerson");
		String sql = "select idPerson from person where vorname = ? and nachname = ?" + 
				"value (?, ?)";
		L.info(sql);	
		try(PreparedStatement s = connection.prepareStatement(sql)){
			s.setString(1, (String) execution.getVariable("vorname"));
			s.setString(2, (String) execution.getVariable("nachname"));
			s.executeUpdate();
		}catch  (SQLException e) {
			L.error(""+e);
			throw new DataFormatException();
		}
		L.info("Ende des Einlesens");
		idPerson = Integer.parseInt(sql);
		
		L.info("Start einlesen von Leihscheindaten");
		String sqlZwei = "insert into leihschein(idPerson, anfangausleihe, endeausleihe" + 
				"values (?, ?, ?)";
		L.info(sqlZwei);
		try(PreparedStatement s = connection.prepareStatement(sqlZwei)){
			s.setInt(1, idPerson);
			s.setTimestamp(2, (Timestamp) execution.getVariable("anfangausleihe"));
			s.setTimestamp(3, (Timestamp) execution.getVariable("endeausleihe"));
			s.executeUpdate();
		}catch  (SQLException e) {
			L.error(""+e);
			throw new DataFormatException();
		}
		L.info("Ende des Einlesens");
	}// end of execution

}
