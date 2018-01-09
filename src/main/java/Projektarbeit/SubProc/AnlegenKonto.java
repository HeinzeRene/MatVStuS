package Projektarbeit.SubProc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.zip.DataFormatException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnlegenKonto implements JavaDelegate{

	private static final Logger L = LoggerFactory.getLogger(AnlegenKonto.class);
	
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

	public void execute(DelegateExecution execution) throws Exception {
	
		L.info("Start einlesen von Personendaten");
		String sql = "insert into Person(anrede, vorname, nachname, matrikelnummer, adresse, plz, wohnort, eMailAdresse" + 
				" values (?, ?, ?, ?, ?, ?, ?, ?)";
		L.info(sql);
		try(PreparedStatement s = connection.prepareStatement(sql)){
			s.setString(1, (String) execution.getVariable("anrede"));
			s.setString(2, (String) execution.getVariable("vorname"));
			s.setString(3, (String) execution.getVariable("nachname"));
			s.setString(4, (String) execution.getVariable("matrikelnummer"));
			s.setString(5, (String) execution.getVariable("adresse"));
			s.setString(6, (String) execution.getVariable("plz"));
			s.setString(7, (String) execution.getVariable("wohnort"));
			s.setString(8, (String) execution.getVariable("eMailKunde"));
			s.executeUpdate();
		}catch  (SQLException e) {
			L.error(""+e);
			throw new DataFormatException();
		}
		L.info("Ende des Einlesens");
	}// end of execution
}//end of class
