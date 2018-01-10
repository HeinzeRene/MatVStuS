package Projektarbeit.model;

import java.sql.Timestamp;

import sun.util.calendar.LocalGregorianCalendar.Date;

public class Leihschein {

	private Timestamp anfangausleihe, endeausleihe;
	private Date datum;
	private int leihscheinnummer, kaution;
	private String anrede, vorname, nachname, matrikelnummer, adresse, plz, wohnort, eMailAdresse, beschreibung, seriennummer;

	public Leihschein (Date datum, Timestamp anfangausleihe, Timestamp endeausleihe, int leihscheinnummer, int kaution, String anrede, String vorname, String nachname, String matrikelnummer, String adresse, String plz, String wohnort, String eMailAdresse, String beschreibung, String seriennummer) {
		this.datum = datum;
		this.anfangausleihe = anfangausleihe;
		this.endeausleihe = endeausleihe;
		this.anrede = anrede;
		this.vorname = vorname;
		this.nachname = nachname;
		this.matrikelnummer = matrikelnummer;
		this.adresse = adresse;
		this.plz = plz;
		this.wohnort= wohnort;
		this.eMailAdresse = eMailAdresse;
		this.kaution = kaution;
		this.leihscheinnummer = leihscheinnummer;
		this.beschreibung = beschreibung;
		this.seriennummer = seriennummer;
		
	}
}
