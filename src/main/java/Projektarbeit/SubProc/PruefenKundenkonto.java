package Projektarbeit.SubProc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

public class PruefenKundenkonto {

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
	
	private boolean getPerson() throws Exception {
			
		L.info("Start Auslesen von Kundendaten");
		String sql = "select * person from where vorname = ? and nachname is ?";
		L.info(sql);
		try(PreparedStatement ps = connection.prepareStatement(sql)){
			ps.setString(1, "vorname");
			ps.setString(2, "nachname");
			try(ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
					eMailAdresse = rs.getString("eMailAdresse");
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
