package Projektarbeit.leihVorgang;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
		String anfang = (String)execute.getVariable("leihBeginn");
		
		String ende = (String)execute.getVariable("leihEnde");
		L.info("Leihzeitraum Anfang: " + anfang + " Ende: " + ende);
		Zeitraum leihe = new Zeitraum(anfang, ende);
		L.info("Start Auslesen der MaterialExemplare");
		
		String sql = "select * from MaterialExemplar me LEFT JOIN materialLeihschein ml ON ml.idMatExp = me.idMatExp LEFT JOIN leihschein l ON ml.leihscheinnummer = l.leihscheinnummer where materialArt = ?";
		try(PreparedStatement ps = conn.prepareStatement(sql)){
			
			ps.setInt(1, (int)execute.getVariable("matArtID"));
			L.info("SQL Anfrage: " + ps.toString());
			ArrayList<Integer> verfuegbar = new ArrayList<Integer>();
			ArrayList<Integer> nichtVerfuegbar = new ArrayList<Integer>();
			try(ResultSet rs = ps.executeQuery()){
				
				if(rs.next())
				{
					do
					{
						Zeitraum mat = new Zeitraum(rs.getTimestamp("anfangausleihe"), rs.getTimestamp("endeausleihe"));
						L.info("Überpruefung von leihzeitraum: " + leihe + " und leihschein: " + mat);
						if((rs.getTimestamp("anfangausleihe")==null||!mat.ueberschneidung(leihe))&&!verfuegbar.contains(rs.getInt("idMatExp")))
						{
							L.info("MatID: " + rs.getInt("idMatExp") + " Seriennummer: " + rs.getLong("seriennummer"));
							verfuegbar.add(rs.getInt("idMatExp"));
						}
						
					}while(rs.next());
				}
				
				if(verfuegbar.isEmpty())
				{
					L.info("Kein Material der MaterialArt: " + (int)execute.getVariable("marArtID") + " verfügbar im Zeitraum: "+ anfang +  "<->" + ende);
					execute.setVariable("verfZeit", false);
				}
				else
				{
					execute.setVariable("verfZeit", true);
					execute.setVariable("matExemplarID", verfuegbar.get(0));
					
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
