package Projektarbeit.leihVorgang;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	private String seriennummer = "";
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

		L.info("Erstellung Leihschein in leihschein");
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
		L.info("Start auslesen der Leihscheinnummer");
		sql = "select LAST_INSERT_ID() from leihschein";
		L.info(sql);
		try (Statement s = conn.createStatement()){
			try(ResultSet rs = s.executeQuery(sql))
			{				
				if(rs.next())
				{
					L.info(""+rs.getInt(1));
				leihscheinNummer = rs.getInt(1);
				execution.setVariable("leihscheinNummer", leihscheinNummer);
				L.info("Der Leihschein hat die Nummer: " + leihscheinNummer);	
				}
				else
				{
					L.warn("Es gibt kein letzten Leihschein eintrag");
				}
			}
		} catch (SQLException e ) {
			L.error("" + e);
			throw new DataFormatException();	
		}		
		L.info("Ende Leihscheinerstellung");
		L.info("Erstellung eintrag in materialLeihschein");
		sql = "INSERT INTO materialLeihschein (leihscheinNummer, idMatExp, kaution) values(?,?,?)";
		try(PreparedStatement ps =  conn.prepareStatement(sql))
		{
			int idMatExp = (int) execution.getVariable("matExemplarID");
			double kaution = (double)execution.getVariable("kaution");
			ps.setInt(1, leihscheinNummer);
			ps.setInt(2, idMatExp);
			ps.setDouble(3, kaution);
			L.debug(ps.toString());
			ps.executeUpdate();
			L.info("materialLeihschein eintrag erstellt mit leihscheinNummer: " + leihscheinNummer+ " idMatExp: " +idMatExp+ " und kaution: " + kaution);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		L.info("Ende erstellung materialLeihschein");
		
		
		
		
		
		L.info("Start auslesen der Seriennummer und Beschreibung");
		sql = "SELECT me.seriennummer, ma.beschreibung FROM MaterialExemplar me INNER JOIN materialLeihschein ml ON me.idMatExp = ml.idMatExp INNER JOIN MaterialArt ma ON ma.idMatArt=me.materialArt WHERE ml.leihscheinNummer = ?";
		L.info(sql);
		try (PreparedStatement s = conn.prepareStatement(sql)){
			s.setInt(1, leihscheinNummer);
			L.info(s.toString());
			try(ResultSet rs = s.executeQuery())
			{
				seriennummer = rs.getString("seriennummer");
				execution.setVariable("seriennummer", seriennummer);
				L.info("ausgelesene Seriennummer: " + seriennummer);
				
				beschreibung = rs.getString("beschreibung");
				execution.setVariable("beschreibung", beschreibung);
				L.info("ausgelesene Beschreibung: " + beschreibung);

			}
		} catch (SQLException e ) {
			L.error("" + e);
			
		}
		
		
		
	}
	
	private Timestamp getTimestamp(String datum, String uhrzeit)
	{
		String[] d = datum.split("\\.");
		String[] u = uhrzeit.split(":");
		return Timestamp.valueOf(""+d[2]+"-"+d[1]+"-"+d[0]+" "+u[0]+":"+u[1]+":00");
	}


}
