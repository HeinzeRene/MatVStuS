package Projektarbeit.model;

import java.sql.Timestamp;
/**
 * Ein zeitraum mit Anfang und ende
 * @author Marwin
 * Erstellt am: 15.01.2018
 * Zuletzt geaendert von:
 * Zuletzt geaendert am: 15.01.2018
 */
public class Zeitraum {
	private Timestamp anfang;
	private Timestamp ende;
	
	
	/**
	 * Erstellt einen Zeitraum aus zwei Timestamps
	 * @param anfang Anfangszeitpunkt
	 * @param ende Endzeitpunkt
	 */
	public Zeitraum(Timestamp anfang, Timestamp ende)
	{
		if(anfang.before(ende))
		{
			this.anfang = anfang;
			
			this.ende = ende;
		}
		else
		{
			this.ende = anfang;
			this.anfang = ende;
		}
	}
	/**
	 * Erstellt einen Zeitraum aus zwei Timestamps
	 * @param anfang Anfangszeitpunkt im Format "DD.MM.JJJJ" als Zeit wird 00:00:00 genommen
	 * @param ende Endzeitpunkt im Format "DD.MM.JJJJ" als Zeit wird 23:59:59 genommen
	 */
	public Zeitraum(String anfang, String ende)
	{
		String[] anf = anfang.split("\\.");
		String[] end = ende.split("\\.");
		Timestamp a = Timestamp.valueOf(anf[2]+"-"+anf[1]+"-"+anf[0] + " 00:00:00");
		Timestamp e = Timestamp.valueOf(end[2]+"-"+end[1]+"-"+end[0] + " 23:59:59");
		if(a.after(e))
		{
			this.anfang = e;
			this.ende = a;
		}
		else
		{
			this.anfang = a;
			this.ende = e;
		}
	}
	/**
	 * Uebreprueft ob sich zwei Zeitraeume ueberschneiden
	 * @param z2
	 * @return
	 */
	public boolean ueberschneidung(Zeitraum z2)
	{
		if(this.ende.compareTo(z2.anfang)<0||this.anfang.compareTo(z2.ende)>0) 
		{
			return false;
		}
		else 
		{
			return true;
		}
	}
	/**
	 * Erstellt einen String aus dem Zeitraum
	 * @return String im Format "Anfang<->Ende"
	 */
	public String toString()
	{
		return anfang +" <-> " +ende;
	}

}
