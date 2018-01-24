package Projektarbeit.leihVorgang;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CamundaProjekt.leihVorgangStuS.Datenbankzugang;

public class StartMateriallisteErstellen implements JavaDelegate {
	private static final Logger L =  (Logger) LoggerFactory.getLogger(StartMateriallisteErstellen.class);
	@Override
	public void execute(DelegateExecution execute) throws Exception {
		LinkedList<String> s = new LinkedList<String>();
		String sql;
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
		sql = "SELECT FROM MaterialExemplar WHERE "
		try(PreparedStatement ps = conn.prepareStatement(sql))
		{
			
		}
		execute.setVariable("materialListe", Variables.objectValue(s).serializationDataFormat(Variables.SerializationDataFormats.JSON));
	}

}
