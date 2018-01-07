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
import Projektarbeit.model.Zeitraum;

public class VerfZeitraum implements JavaDelegate{

	private static final Logger L =  (Logger) LoggerFactory.getLogger(VerfZeitraum.class);

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
		String anfang = (String)execute.getVariable("leihAnfang");
		
		String ende = (String)execute.getVariable("leihEnde");
		
		Zeitraum leihe = new Zeitraum(anfang, ende);
		L.info("Start Auslesen der MaterialExemplare");
		
		String sql = "select * from MaterialExemplar me INNER JOIN materialLeihschein ml ON ml.matExpId = me.matExpId INNER JOIN Leihschein l ON ml.leihscheinnummer = l.leihschein where materialArt = ?";
		try(PreparedStatement ps = conn.prepareStatement(sql)){
			
			ps.setInt(1, (int)execute.getVariable("matArtID"));
			L.info("SQL Anfrage: " + ps.toString());
			try(ResultSet rs = ps.executeQuery()){
				
				if(rs.next())
				{
					do
					{
						Zeitraum mat = new Zeitraum(rs.getTimestamp("anfangausleihe"), rs.getTimestamp("endeausleihe"));
						if(!mat.ueberschneidung(leihe))
						{
							L.info("MatID: " + rs.getInt("idMatExp") + " Seriennummer: " + rs.getInt(columnIndex));
						}
					}while(rs.next());
				}
				
			}catch  (SQLException e) {
			L.error(""+e);
			//throw new Exception(e);
			}
		L.info("Ende des Einlesens");
		} catch (SQLException e) {	
			e.printStackTrace();
		}
	}

	
	
}
