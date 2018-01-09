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
			L.debug(ps.toString());
			ArrayList<Integer> verfuegbar = new ArrayList<Integer>();
			ArrayList<Integer> nichtVerfuegbar = new ArrayList<Integer>();
			try(ResultSet rs = ps.executeQuery()){
				
				if(rs.next())
				{
					do
					{
						int matArtID = rs.getInt("idMatExp");
						if(!nichtVerfuegbar.contains(matArtID))
						{
							Zeitraum mat = new Zeitraum(rs.getTimestamp("anfangausleihe"), rs.getTimestamp("endeausleihe"));
							if(rs.getTimestamp("anfangausleihe")!=null)
							{
								if(!leihe.ueberschneidung(mat))
								{
									L.info("Überpruefung von leihzeitraum: " + leihe + " und leihschein: " + mat + " erg: " + leihe.ueberschneidung(mat));
									if(!verfuegbar.contains(matArtID))
									{
										L.info(matArtID + "wird als verfuegbar angenommen");
										verfuegbar.add(rs.getInt("idMatExp"));
									}
									else
									{
										L.info(matArtID + "ist bereits verfuegbar");
									}
								}
								else
								{
									L.info(matArtID + " ist nicht verfuegbar");
									if(verfuegbar.contains(matArtID))
									{
										L.info(matArtID + " wird nicht mehr als verfuegbar angenommen");
										verfuegbar.remove(new Integer(matArtID));
									}
									nichtVerfuegbar.add(rs.getInt("idMatExp"));
								}
							}
							else
							{
								L.info(matArtID + " wurde noch nie gebucht und ist damit verfuegbar.");
								verfuegbar.add(rs.getInt("idMatExp"));
							}
						}
						
					}while(rs.next());
				}
				
				if(verfuegbar.isEmpty())
				{
					L.info("Kein Material der MaterialArt: " + (int)execute.getVariable("matArtID") + " verfügbar im Zeitraum: "+ anfang +  "<->" + ende);
					execute.setVariable("verfZeit", false);
				}
				else
				{
					execute.setVariable("verfZeit", true);
					execute.setVariable("matExemplarID", verfuegbar.get(0));
					L.info("verfuegbar im Zeitraum: ");
					for(int i:verfuegbar)
					{
						L.info(""+ i);
					}
					
				}
				
			}catch  (SQLException e) {
			L.error(""+e);
			//throw new Exception(e);
			}
		L.info("Ende des Einlesens");
		} catch (SQLException e) {	
			e.printStackTrace();
		}
		sql = "SELECT wert FROM MaterialArt WHERE idMatArt = ?";
		try(PreparedStatement ps = conn.prepareStatement(sql))
		{
			ps.setInt(1, (int)execute.getVariable("matArtID"));
			L.info("Einlesen von Wert der MaterialArt");
			L.debug(ps.toString());
			try(ResultSet rs = ps.executeQuery())
			{
				if(rs.next())
				{
					L.info("Wert: " + rs.getDouble("wert"));
					execute.setVariable("Preis", rs.getDouble("wert"));
				}
				else
				{
					L.warn("Es gibt kein Material mit der MaterialArt ID:" + execute.getVariable("matArtID"));
				}
			}
		}
		int idPerson = (int)execute.getVariable("idPerson");
		sql = "SELECT matrikelnummer FROM Person WHERE idPerson = ?";
		try(PreparedStatement ps = conn.prepareStatement(sql))
		{
			ps.setInt(1, idPerson);
			L.info("Prüfung von Matrikelnummer zur PersonID: " + idPerson);
			L.debug(ps.toString());
			try(ResultSet rs = ps.executeQuery())
			{
				if(rs.next())
				{
					if (rs.getString("matrikelnummer")==null) {
						L.info("Die Person ist kein Student.");
						execute.setVariable("studentBool", false);
					} else {
						L.info("Die Person ist ein Student mit der Matrikelnummer: "+ rs.getString("matrikelnummer"));
						execute.setVariable("studentBool", true);
					}
				}
				else
				{
					L.warn("Es gibt keine Person mit der idPerson: "+ idPerson + " studentBool wird auf false gesetzt");
					execute.setVariable("studentBool", false);
				}
			}
		}
		sql = "SELECT pg.gremiumid FROM Person p INNER JOIN personGremium pg ON p.idPerson=pg.personid WHERE p.idPerson = ?";
		try(PreparedStatement ps = conn.prepareStatement(sql))
		{
			ps.setInt(1, idPerson);
			try(ResultSet rs = ps.executeQuery())
			{
				if (rs.next()) {
					if(rs.getString("pg.gremiumid") != null) {
					L.info("Die Person ist teil mindestens einen Gremiums.");
					execute.setVariable("gremiumBool", true);
					}else {
						L.info("Die Person ist nicht teil eines Gremiums oder die Person gibt es nicht");
						execute.setVariable("gremiumBool", false);
					}
				}
				else
				{
					L.warn("Die Person: " + idPerson + " ist nicht Teil eines Gremium - gremiumBool wird auf false gesetzt");
					execute.setVariable("gremiumBool", false);
				}
			}
		
		}
	}
}
