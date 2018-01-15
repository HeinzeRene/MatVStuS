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
import java.util.ArrayList;
import java.util.Collection;
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
import org.camunda.bpm.engine.variable.impl.value.builder.FileValueBuilderImpl;
import org.camunda.bpm.engine.variable.value.FileValue;
import org.camunda.bpm.engine.variable.value.builder.FileValueBuilder;
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

	//Methode um das aktuelle Datum als String zu bekommen
	private String aktuellesDatum() {
		GregorianCalendar now = new GregorianCalendar();
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);   // 14.04.12 
		df = DateFormat.getDateInstance(DateFormat.LONG);               // 14. April 2012 
		datum = (String) df.format(now.getTime());
		System.out.println(datum);
		return datum;
	}
	
	//Methode fuer das Einfuegen von Leerzeilen im Excel-Dokument
	public Collection<String> gaps(int rowAmount) {
		Collection<String> collec = new ArrayList<String>();
		for(int i = 0; i<rowAmount; i++)
			collec.add(" ");
		
		return collec;
	}
	public void execute(DelegateExecution execution) throws Exception {

		int leihscheinNummer = (int) execution.getVariable("leihscheinNummer");
		L.info("leihscheinNummer auslesen ergab: " + leihscheinNummer);
		
		L.info("" + leihscheinNummer);
		
		L.info("Dokument wird angelegt");
		L.info("Test: " + aktuellesDatum() 
		+ " " + execution.getVariable("anrede")  
		+ " " + execution.getVariable("vorname")  
		+ " " + execution.getVariable("nachname") 
		+ " " + execution.getVariable("matrikelnummer") 
		+ " " + execution.getVariable("adresse")  
		+ " " + execution.getVariable("plz")  
		+ " " + execution.getVariable("wohnort") 
		+ " " + execution.getVariable("eMailAdresse")  
		+ " " + execution.getVariable("leihscheinNummer")
		+ " " + execution.getVariable("beschreibung") 
		+ " " + execution.getVariable("seriennummer")  
		+ " " + execution.getVariable("kaution") 
		+ " " + execution.getVariable("anfangausleihe") 
		+ " " + execution.getVariable("endeausleihe"));
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Leihschein_" + execution.getVariable("leihscheinNummer"));

		Cell cell;
		Row row;

		HSSFCellStyle style = createStyleForTitle(workbook);

		//Definieren der Werte, welche in Spalte A existieren
		ArrayList<String> column1 = new ArrayList<>();
		column1.add("Studierendenschaft der HTW Berlin");
		column1.add("Anerkannte studentische Initiative Studimeile");
		column1.add("Treskowallee 8");
		column1.add("10318 Berlin");
		column1.addAll(gaps(3));
		column1.add((String) execution.getVariable("anrede"));
		column1.add((String) execution.getVariable("vorname") + " " + (String) execution.getVariable("nachname"));
		column1.add("Matrikelnummer: " + (String) execution.getVariable("matNr"));
		column1.add((String) execution.getVariable("adresse"));
		column1.addAll(gaps(1));
		column1.add((String) execution.getVariable("plz") + " " + (String) execution.getVariable("wohnort"));
		column1.add((String) execution.getVariable("eMailAdresse"));
		column1.addAll(gaps(2));
		column1.add("Leihschein: " + leihscheinNummer);
		column1.addAll(gaps(1));
		column1.add("Vielen Dank für Ihre Anfrage.");
		column1.add("Folgenden Leihschein haben wir nach Ihren Vorgaben erstellt:");
		column1.addAll(gaps(1));
		column1.add("Material: " + (String) execution.getVariable("beschreibung"));
		column1.add("Serialnummer: " + (String) execution.getVariable("seriennummer"));
		column1.add("Kautionsanteil: " + (double) execution.getVariable("kaution"));
		column1.add("Übergabetermin: " + (String) execution.getVariable("anfangausleihe"));
		column1.add("Rückgabetermin: " + (String) execution.getVariable("endeausleihe"));
		column1.addAll(gaps(1));
		column1.add("Die Kaution richtet sich nach der Zugehörigkeit von Gremium und Immatrikulation an der HTW Berlin.");
		column1.add("Bitte bringen sie den genannten Betrag bei der Übergabe in Bar mit.");
		column1.add("Sie erhalten diesen bei vollständiger und funktionfähiger Rückgabe des Materials zurück.");
		column1.addAll(gaps(2));
		column1.add("Schadensbemerkung bei Übergabe (Datum:			");
		column1.add("Schadensbemerkung bei Rückgabe (Datum:			");
		column1.addAll(gaps(5));
		column1.add("Eure Ini Studimeile-Team");
		column1.addAll(gaps(3));
		column1.add("i.A.");
		column1.add("(Unterschrift Ini-Mitglied)");
		
		//Eintragen in Spalte A von oben definierten Werten aus ArrayList
		for (int i=0; i<column1.size(); i++) {
			row = sheet.createRow(i);
			cell = row.createCell(0, CellType.STRING);
			cell.setCellValue(column1.get(i));
		}
		
		//Eintrag von extra Daten in bereits erstellten Zeilen
		row = sheet.getRow(0);
		cell = row.createCell(4, CellType.STRING);
		cell.setCellValue("Berlin, " + aktuellesDatum());
		
		row = sheet.getRow(44);
		cell = row.createCell(4, CellType.STRING);
		cell.setCellValue("(Unterschrift Kunde)");
		
		//Autosize von Spalten A und E in Excel-Dokument
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(4);


		L.info("Dokument wurde erstellt.");
		leihscheinNummer = (int) execution.getVariable("leihscheinNummer");
		String dateiPfad = "" + leihscheinNummer + "_Leihschein.xls";
		File file = new File(dateiPfad);
//		file.getParentFile().mkdirs();
		L.info("Dokument in " + file + " gespeichert.");
		FileOutputStream outFile = new FileOutputStream(file);
		workbook.write(outFile);
		L.info("Datei auf der Festplatte gespeichert." + file.getAbsolutePath());
		FileValueBuilder build = new FileValueBuilderImpl(file.getName());
		build = build.file(file);
		
				
		execution.setVariable("Leihschein", build.create());
	}
	

}
