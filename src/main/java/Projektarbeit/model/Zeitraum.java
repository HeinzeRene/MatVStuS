package Projektarbeit.model;

import java.sql.Timestamp;

public class Zeitraum {
	private Timestamp anfang;
	private Timestamp ende;
	
	
//	public Zeitraum(java.sql.Date anfang, java.sql.Date ende)
//	{
//		this.anfang = anfang;
//		this.ende = ende;
//	}
	public Zeitraum(Timestamp anfang, Timestamp ende)
	{
		this.anfang = anfang;
		this.ende = ende;
	}
	public Zeitraum(String anfang, String ende)
	{
		String[] anf = anfang.split(".");
		String[] end = ende.split(".");
		System.out.println(anf.length);
		System.out.println(end.length);
		this.anfang = Timestamp.valueOf(anf[2]+"-"+anf[1]+"-"+anf[0]);
		this.ende = Timestamp.valueOf(end[2]+"-"+end[1]+"-"+end[0]);
	}
	
	public boolean ueberschneidung(Zeitraum z2)
	{
		if(this.ende.compareTo(z2.anfang)>0||this.anfang.compareTo(z2.ende)<0)
			return false;
		else
			return true;
	}
	
//	private static boolean zwischen(Timestamp anf, Timestamp end, Timestamp z)
//	{
//		if(anf.compareTo(z)==1&&end.compareTo(z)==-1)
//			return true;
//		else
//			return false;
//	}
}
