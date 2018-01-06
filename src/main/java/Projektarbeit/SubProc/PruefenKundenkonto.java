package Projektarbeit.SubProc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PruefenKundenkonto implements JavaDelegate{

	private String eMailAdresse;
	private boolean kontoVorh;
	private static final Logger L =  (Logger) LoggerFactory.getLogger(PruefenKundenkonto.class);
	
	private Connection connection;
		public void setConnection(Connection connection) {
		this.connection = connection;
	}//end of connection
	
	private Connection getConnection() {
		if (connection == null) {
			try {
				throw new Exception("Connection not set");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return connection;
	}//end of getConnection
	@Override
	public void execute(DelegateExecution arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	private boolean getPerson() throws Exception {
			
		L.info("Start Auslesen von Kundendaten");
		String sql = "select * person from where vorname = ? and nachname is ?";
		L.info("SQL Anfrage: " + sql);
		try(PreparedStatement ps = connection.prepareStatement(sql)){
			ps.setString(1, "vorname");
			ps.setString(2, "nachname");
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
		}else {
			kontoVorh = true;
		}	
		return kontoVorh;
	}//end of getPerson

	
}//end of class
