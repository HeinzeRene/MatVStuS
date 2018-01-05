package Projektarbeit.leihVorgang;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartListenZusammenstellen implements JavaDelegate {

	
	private static final Logger L = LoggerFactory.getLogger(StartListenZusammenstellen.class);
	public void execute(DelegateExecution arg0) throws Exception {
		// TODO Auto-generated method stub
		final String hostname = "141.45.123.80";
		final String port = "3306";
		final String dbname = "db69_trese";
		final String user = "user69";
		final String password = "Acht+9";
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
			String url = "jdbc:mysql://" + hostname + ":" + port + "/" + dbname;
			conn = DriverManager.getConnection(url, user, password);
			arg0.setVariable("connection", conn);
			
			
		} catch (SQLException sqle) {
			L.error("SQLException: " + sqle.getMessage() + "/n SQLState: " + sqle.getSQLState() + " VendorError: " + sqle.getErrorCode());

		}
	}

}
