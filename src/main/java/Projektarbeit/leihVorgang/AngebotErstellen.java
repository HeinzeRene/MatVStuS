package Projektarbeit.leihVorgang;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.zip.DataFormatException;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CamundaProjekt.leihVorgangStuS.Datenbankzugang;
import org.apache.poi.ss.usermodel.Cell;

public class AngebotErstellen implements JavaDelegate {

	private static final Logger L = LoggerFactory.getLogger(AngebotErstellen.class);

	private int idPerson = -1;
	private String datum;
	
	private static HSSFCellStyle createStyleForTitle(HSSFWorkbook workbook) {
		HSSFFont font = workbook.createFont();
		font.setBold(true);
		HSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);
		return style;
	}

	private String aktuellesDatum() {
		GregorianCalendar now = new GregorianCalendar();
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);   // 14.04.12 
		df = DateFormat.getDateInstance(DateFormat.LONG);               // 14. April 2012 
		datum = (String) df.format(now.getTime());
		
		return datum;
	}
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
			String url = "jdbc:mysql://" + Datenbankzugang.hostname + ":" + Datenbankzugang.port + "/"
					+ Datenbankzugang.dbname;
			conn = DriverManager.getConnection(url, Datenbankzugang.user, Datenbankzugang.password);
		} catch (SQLException sqle) {
			L.error("SQLException: " + sqle.getMessage() + "/n SQLState: " + sqle.getSQLState() + " VendorError: "
					+ sqle.getErrorCode());

		}

		L.info("Start einlesen von Leihscheindaten");
		String sqlZwei = " insert into leihschein (idPerson, anfangausleihe, endeausleihe)" + " values (?, ?, ?)";
		L.info(sqlZwei);
		try (PreparedStatement s = conn.prepareStatement(sqlZwei)) {
			s.setInt(1, idPerson);
			
			s.setTimestamp(2, getTimestamp((String) execution.getVariable("anfangausleihe"),(String)execution.getVariable("uhrzUeber")));
			s.setTimestamp(3, getTimestamp((String) execution.getVariable("anfangausleihe"),(String)execution.getVariable("uhrzRueck")));
			s.executeUpdate();
		} catch (SQLException e) {
			L.error("" + e);
			throw new DataFormatException();
		}
		L.info("Ende des Einlesens");

		L.info("Dokument wird angelegt");
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Employees sheet");

		int rownum = 0;
		Cell cell;
		Row row;

		HSSFCellStyle style = createStyleForTitle(workbook);

		row = sheet.createRow(rownum);

		cell = row.createCell(0, CellType.STRING);
		cell.setCellValue("Studierendenschaft der HTW Berlin");

		cell = row.createCell(5, CellType.STRING);
		cell.setCellValue("Berlin,");

		cell = row.createCell(6, CellType.STRING);
		cell.setCellValue(aktuellesDatum());

		cell = row.createCell(1, CellType.STRING);
		cell.setCellValue("Anerkannte studentische Initiative Studimeile");
		cell = row.createCell(2, CellType.STRING);
		cell.setCellValue("Treskowallee 8");
		cell = row.createCell(3, CellType.STRING);
		cell.setCellValue("10318 Berlin");

		cell = row.createCell(8, CellType.STRING);
		cell.setCellValue((String) execution.getVariable("anrede"));
		cell = row.createCell(9, CellType.STRING);
		cell.setCellValue((String) execution.getVariable("vorname") + (String) execution.getVariable("nachname"));
		cell = row.createCell(10, CellType.STRING);
		cell.setCellValue("Matrikelnummer: " + (String) execution.getVariable("matrikelnummer"));
		cell = row.createCell(11, CellType.STRING);
		cell.setCellValue((String) execution.getVariable("adresse"));

		cell = row.createCell(13, CellType.STRING);
		cell.setCellValue((String) execution.getVariable("plz") + (String) execution.getVariable("wohnort"));
		cell = row.createCell(14, CellType.STRING);
		cell.setCellValue((String) execution.getVariable("matrikelnummer"));

		cell = row.createCell(17, CellType.STRING);
		cell.setCellValue("Leihschein" + (String) execution.getVariable("leihscheinNummer"));
		cell.setCellStyle(style);

		cell = row.createCell(19, CellType.STRING);
		cell.setCellValue("Vielen Dank für Ihre Anfrage.");
		cell = row.createCell(20, CellType.STRING);
		cell.setCellValue("Folgenden Leihschein haben wir nach Ihren Vorgaben erstellt:");

		cell = row.createCell(22, CellType.STRING);
		cell.setCellValue("Material: " + (String) execution.getVariable("beschreibung"));
		cell = row.createCell(23, CellType.STRING);
		cell.setCellValue("Serialnummer: " + (String) execution.getVariable("seriennummer"));
		cell = row.createCell(24, CellType.STRING);
		cell.setCellValue("Kautionsanteil: " + (String) execution.getVariable("kaution"));
		cell = row.createCell(25, CellType.STRING);
		cell.setCellValue("Übergabetermin: " + (String) execution.getVariable("anfangausleihe"));
		cell = row.createCell(26, CellType.STRING);
		cell.setCellValue("Rückgabetermin: " + (String) execution.getVariable("endeausleihe"));

		cell = row.createCell(28, CellType.STRING);
		cell.setCellValue(
				"Die Kaution richtet sich nach der Zugehörigkeit von Gremium und Immatrikulation an der HTW Berlin");
		cell = row.createCell(29, CellType.STRING);
		cell.setCellValue("Bitte bringen sie den genannten Betrag bei der Übergabe in Bar mit.");
		cell = row.createCell(30, CellType.STRING);
		cell.setCellValue("Sie erhalten diesen bei vollständiger und funktionfähiger Rückgabe des Materials zurück.");

		cell = row.createCell(32, CellType.STRING);
		cell.setCellValue("Schadensbemerkung bei Übergabe (Datum:			");

		cell = row.createCell(39, CellType.STRING);
		cell.setCellValue("Schadensbemerkung bei Rückgabe (Datum:			");

		cell = row.createCell(45, CellType.STRING);
		cell.setCellValue("Euer Ini Studimeile-Team");

		cell = row.createCell(49, CellType.STRING);
		cell.setCellValue("i.A.");
		cell = row.createCell(50, CellType.STRING);
		cell.setCellValue("(Unterschrift Ini-Mitglied)									(Unterschrift Kunde)");

		L.info("Dokument wurde erstellt.");
		String dateiPfad = "C:/Users/Erdmann/Documents/3. Semester/MAS/Neu/leihVorgangStuS";
		File file = new File(dateiPfad);
		L.info("Dokument in " + file + " gespeichert.");
		FileOutputStream outFile = new FileOutputStream(file);
		workbook.write(outFile);
		L.info("Datei auf der Festplatte gespeichert.");

	}
	
	private Timestamp getTimestamp(String datum, String uhrzeit)
	{
		String[] d = datum.split("\\.");
		String[] u = uhrzeit.split(":");
		return Timestamp.valueOf(""+d[2]+"-"+d[1]+"-"+d[0]+" "+u[0]+":"+u[1]+":00");
	}
}
