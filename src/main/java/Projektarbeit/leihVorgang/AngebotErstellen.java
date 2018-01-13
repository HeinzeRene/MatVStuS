package Projektarbeit.leihVorgang;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	private int leihscheinNummer = -1;
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
		System.out.println(datum);
		return datum;
	}
	public void execute(DelegateExecution execution) throws Exception {


		L.info("Dokument wird angelegt");
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Leihschein_" + execution.getVariable("leihscheinNummer"));

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
		cell.setCellValue("Leihschein" + (int) execution.getVariable("leihscheinNummer"));
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
		cell.setCellValue("Kautionsanteil: " + (double) execution.getVariable("kaution") + " €");
		cell = row.createCell(25, CellType.STRING);
		cell.setCellValue("Übergabetermin: " + (String) execution.getVariable("anfangausleihe"));
		cell = row.createCell(26, CellType.STRING);
		cell.setCellValue("Rückgabetermin: " + (String) execution.getVariable("endeausleihe"));

		cell = row.createCell(28, CellType.STRING);
		cell.setCellValue(
				"Die Kaution richtet sich nach der Zugehörigkeit von Gremium und Immatrikulation an der HTW Berlin.");
		cell = row.createCell(29, CellType.STRING);
		cell.setCellValue("Bitte bringen sie den genannten Betrag bei der Übergabe in Bar mit.");
		cell = row.createCell(30, CellType.STRING);
		cell.setCellValue("Sie erhalten diesen bei vollständiger und funktionfähiger Rückgabe des Materials zurück.");

		cell = row.createCell(32, CellType.STRING);
		cell.setCellValue("Schadensbemerkung bei Übergabe (Datum:			");

		cell = row.createCell(39, CellType.STRING);
		cell.setCellValue("Schadensbemerkung bei Rückgabe (Datum:			");

		
		cell = row.createCell(45, CellType.STRING);
		cell.setCellValue("Eure Ini Studimeile-Team");

		cell = row.createCell(49, CellType.STRING);
		cell.setCellValue("i.A.");
		cell = row.createCell(50, CellType.STRING);
		cell.setCellValue("(Unterschrift Ini-Mitglied)									(Unterschrift Kunde)");

		L.info("Dokument wurde erstellt.");
		leihscheinNummer = (int) execution.getVariable("leihscheinNummer");
		String dateiPfad = "C:/Users/Erdmann/Documents/3. Semester/MAS/" + leihscheinNummer + "_Leihschein.xls";
		File file = new File(dateiPfad);
		file.getParentFile().mkdirs();
		L.info("Dokument in " + file + " gespeichert.");
		FileOutputStream outFile = new FileOutputStream(file);
		workbook.write(outFile);
		L.info("Datei auf der Festplatte gespeichert." + file.getAbsolutePath());
		execution.setVariable("Leihschein", file);
	}
	

}
