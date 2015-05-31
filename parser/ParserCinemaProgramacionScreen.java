package com.database.bb.cinema.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import net.rim.device.api.xml.parsers.ParserConfigurationException;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.database.bb.cinema.objetos.Film;
import com.database.bb.cinema.objetos.Showtime;
import com.database.bb.cinema.objetos.ShowtimeMoviePerformance;
import com.database.bb.cinema.objetos.Showtime.Leyendas;
import com.database.bb.cinema.objetos.ShowtimeMovie;

/**
 * @author Edgardo Bermúdez
 * Parser for the showtimes of a theater.  
 * 
 * BlackBerry 5-7 
 * SAX Parser
 */
public class ParserCinemaProgramacionScreen extends DefaultHandler{

	protected StringBuffer value;
	protected Vector lista;
	protected Film film;
	private Showtime showtimes;
	private Vector listaLeyendas;
	private Leyendas leyenda;
	private String idLeyenda;
	private Vector listaPerformance;
	private ShowtimeMovie filme;
	private Vector listaHorarios;
	private ShowtimeMoviePerformance horario;
	private String doblado;
	
	public ParserCinemaProgramacionScreen(){
		value = new StringBuffer();
		lista = new Vector();
	}
	
	public void characters(char[] ch, int start, int length) 
	{ 
		value.append(ch, start, length); 
	}
	
	public void parseDocument(InputStream input) 
	{
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(input, this);
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) 
	{
		if("showtimes".equals(qName)){
			showtimes = new Showtime();
			showtimes.setWeekStart(attributes.getValue("start")); 
			showtimes.setWeekEnd(attributes.getValue("end"));
		}
		else if ("theater".equals(qName)){
			showtimes.setTheaterID(Integer.parseInt(attributes.getValue("id")));
			listaPerformance = new Vector();
		}else if("film".equals(qName)){
			filme = new ShowtimeMovie();
			filme.setId(Integer.parseInt(attributes.getValue("id")));
		}else if("legends".equals(qName)){
			listaLeyendas = new Vector();
			showtimes.setListaLeyendas(listaLeyendas);
		}else if("legend".equals(qName)){
			idLeyenda = attributes.getValue("id");
		}else if("performances".equals(qName)){
			listaHorarios = new Vector();
		}else if("performance".equals(qName)){
			horario = new ShowtimeMoviePerformance();
			boolean tmp = (attributes.getValue("premier")).toLowerCase().equals("true")?true:false; 
			horario.setPremier(tmp);
			tmp = (attributes.getValue("xd")).toLowerCase().equals("true")?true:false;
			horario.setXd(tmp);
			doblado  = attributes.getValue("exhibition");
			if(doblado.equals("dubbed"))
				horario.setExhibition("Dublado");
			else if(doblado.equals("subtitled"))
				horario.setExhibition("Legendado");
			else if(doblado.equals("original"))
				horario.setExhibition("Original");
			else
				horario.setExhibition(doblado);
			
			horario.setScreen(Integer.parseInt(attributes.getValue("screen")));
			horario.setLegend(attributes.getValue("legend"));
		}
	}
	
	public void endElement(String uri, String localName, String qName) 
	{
		if("showtimes".equals(qName)){

		}
		else if ("theater".equals(qName)){
			
		}else if("film".equals(qName)){
			listaPerformance.addElement(filme);
		}else if("legends".equals(qName)){
			
		}else if("legend".equals(qName)){
			showtimes.setLeyenda(idLeyenda, value.toString());
		}else if("performances".equals(qName)){
			
		}else if("performance".equals(qName)){
			horario.setHorario(value.toString());
			filme.addPerformance(horario);
		}else if("title".equals(qName)){
			filme.setTitle(value.toString());
		}else if("original-title".equals(qName)){
			filme.setOriginal_title(value.toString());
		}else if("genre".equals(qName)){
			filme.setGenre(value.toString());
		}else if("parent-guide-rating".equals(qName)){
			filme.setParent_guide_rating(value.toString());
		}
		value.setLength(0);
	}
	
	public void endDocument() throws SAXException {
		showtimes.setListaPerformances(listaPerformance);
	}

	public Showtime getShowtimes(){
		return showtimes;
	}
}
