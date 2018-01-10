package Projektarbeit.SubProc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.zip.DataFormatException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CamundaProjekt.leihVorgangStuS.Datenbankzugang;

public class AnlegenKonto implements JavaDelegate{

	private static final Logger L = LoggerFactory.getLogger(AnlegenKonto.class);
	private int idPerson = -1;
	
	public void execute(DelegateExecution execution) throws Exception {
	
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
		L.info("Start einlesen von Personendaten");
		String sql = " insert into Person (anrede, vorname, nachname, matrikelnummer, adresse, plz, wohnort, eMailAdresse)" + 
				" values (?, ?, ?, ?, ?, ?, ?, ?)";
		L.info(sql);
		try(PreparedStatement s = conn.prepareStatement(sql)){
			s.setString(1, (String) execution.getVariable("anrede"));
			s.setString(2, (String) execution.getVariable("vorname"));
			s.setString(3, (String) execution.getVariable("nachname"));
			s.setString(4, (String) execution.getVariable("matNr"));
			s.setString(5, (String) execution.getVariable("adresse"));
			s.setString(6, (String) execution.getVariable("plz"));
			s.setString(7, (String) execution.getVariable("wohnort"));
			s.setString(8, (String) execution.getVariable("eMailAdresse"));
			L.debug(s.toString());
			s.executeUpdate();
		}catch  (SQLException e) {
			L.error(""+e);
			throw new DataFormatException();
		}
		
		
		L.info("Start Auslesen von idPerson");
		String sql2 = "select idPerson from Person where eMailAdresse = ?";
		
		try(PreparedStatement s = conn.prepareStatement(sql2)){
			s.setString(1, (String) execution.getVariable("eMailAdresse"));
			L.debug(s.toString());
			try(ResultSet rs = s.executeQuery()){
				if(rs.next()) {
					idPerson = rs.getInt("idPerson");
					L.info("Folgende idPerson wurde ausgelesen: " + idPerson + ", gehört zu: " + execution.getVariable("eMailAdresse"));;
				}
			}catch  (SQLException e) {
			L.error(""+e);
			throw new Exception(e);
			}
			execution.setVariable("idPerson", idPerson);
		}
		sql = "insert into personGremium (personid, gremiumid) values (?,?)";
		try(PreparedStatement ps = conn.prepareStatement(sql))
		{
			L.info("Erstellen Verbindung zwischen Person und Gremium");
			ps.setInt(1,idPerson);
			ps.setInt(2, (int)execution.getVariable("idGremium"));
			ps.execute();
		}
	}// end of execution
	
	  public void mapOutputVariables(DelegateExecution execution, VariableScope subInstance) {
	    execution.setVariable("idPerson", idPerson);
	  }
}//end of class
