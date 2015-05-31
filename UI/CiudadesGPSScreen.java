package com.database.bb.cinema.UI;

import java.util.Enumeration;
import java.util.Vector;

import com.database.bb.cinema.component.PlainListStyleButtonField;
import com.database.bb.cinema.component.SeparatorBGField;
import com.database.bb.cinema.container.ListStyleButtonSet;
import com.database.bb.cinema.engine.GlobalControl;
import com.database.bb.cinema.engine.ScreenEngine;
import com.database.bb.cinema.objetos.Ciudad;
import com.database.bb.cinema.objetos.Ciudades;
import com.database.bb.cinema.objetos.Theater;
import com.database.bb.cinema.persistencia.CacheFilmes;
import com.database.bb.cinema.utilidades.AlphabeticalComparator;
import com.database.bb.cinema.utilidades.DoubleComparator;
import com.database.bb.cinema.utilidades.GoogleGPS;
import com.database.bb.cinema.utilidades.ScreenProperties;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.SimpleSortingVector;

/**
 * @author Edgardo Bermúdez
 * This class is used to show the nearest cinemas in the city where the user is.
 * 
 * BlackBerry 5-7 
 * View
 */
public class CiudadesGPSScreen extends PrincipalScreen{

	private VerticalFieldManager vfm = new VerticalFieldManager(USE_ALL_WIDTH);
	private VerticalFieldManager vfmCiudades = new VerticalFieldManager(USE_ALL_WIDTH | VERTICAL_SCROLL);
	private PlainListStyleButtonField city;
	private double latitud = -1;
	private double longitud = -1;
	private Vector vectorRetorno = new Vector();
	
	public CiudadesGPSScreen(){
		super(false);
		addHeader();
		setInfoHeader();
		addMenu();
		setCinesSelected();
		init();
	}
	
	public void init() {
		add(vfm);
		add(vfmCiudades);
		SeparatorBGField etiquetaCentral = new SeparatorBGField(_resources.getString(PesquisaCiudades), DrawStyle.HCENTER){			
			protected void paint(Graphics graphics) {
				graphics.setColor(0xFFFFFF);
				super.paint(graphics);
			}
			
			protected void paintBackground(Graphics g) {
				g.setColor(GlobalControl.RedColor);
				super.paintBackground(g);
			}
		};
		etiquetaCentral.setPadding(4, 0, 4, 0);
		etiquetaCentral.setFont(ScreenProperties.normalFieldFontBold);
		
		vfm.add(etiquetaCentral);
		
	}
	
	/**
	* desplegarLista: 
	* Class used to show a list of cities.
	**/
	private void desplegarLista() {
		vfmCiudades.deleteAll();
		Vector listaCiudades = ordenarLista();
		int tam = listaCiudades.size();
		ListStyleButtonSet Cities = new ListStyleButtonSet(USE_ALL_WIDTH);
		for(int i = 0; i < listaCiudades.size() ; i++){
			city = new PlainListStyleButtonField((Ciudad)listaCiudades.elementAt(i), null , false);
			city.setChangeListener(this);
			Cities.add(city);
		}
		Cities.setMargin(5, 15, 5 , 15);
		vfmCiudades.add(Cities);
		
	}

	/**
	* ordenarLista
	* This function sorts the cities according to the alphabetical order.
	**/
	private Vector ordenarLista() {
		SimpleSortingVector vtipo = new SimpleSortingVector();
		vtipo.setSortComparator(new AlphabeticalComparator());
		vtipo.setSort(true);
		Enumeration enumerador = Ciudades.getInstance().getCiudades();
		while(enumerador.hasMoreElements()){
			Ciudad city = (Ciudad) enumerador.nextElement();
			vtipo.addElement(city);
		}
		return vtipo;
	}
	
	protected void onUiEngineAttached(boolean attached) {
		super.onUiEngineAttached(attached);
		if(attached){
			desplegarLista();
		}
	}
	
	/**
	* calcularCercanos:
	* It gets the distance between the theaters inside the vector v and the current position of the user.
	**/
	private void calcularCercanos(Vector v) {
		Theater t = null;
		GoogleGPS goo = ScreenEngine.getInstance().getGoogleCoords();
		Vector distancias = new Vector();
		for(int i = 0 ; i < v.size() ; i++){
			t = (Theater)v.elementAt(i);
			// distanciaGeodesicaDouble: it calculates the geodesic distance between two points
			t.setMiDistancia(goo.distanciaGeodesicaDouble(latitud, longitud, 
			Double.valueOf(t.getLatitud()).doubleValue(), 
			Double.valueOf(t.getLongitud()).doubleValue()));
			distancias.addElement(t);
		}
		if(!distancias.isEmpty()){
			ScreenEngine.getInstance().goGPSTheaterListScreen(ordenarVector(distancias));
		}	
	}

	private Vector ordenarVector(Vector distancias) {
		SimpleSortingVector vtipo = new SimpleSortingVector();
		vtipo.setSortComparator(new DoubleComparator());
		vtipo.setSort(true);
		for(int i = 0; i < distancias.size() ; i++){
//			vtipo.addElement(ScreenEngine.getInstance().getGoogleCoords().limpDist(((Double)distancias.elementAt(i)).toString()));
			vtipo.addElement(((Theater)distancias.elementAt(i)));
		}
		return vtipo;
	}

	protected void paintBackgroundPattern(Graphics g) {
    	g.setColor(Color.WHITE);
    	g.drawRect(0, 0, Display.getWidth(), Display.getHeight());
    	g.setBackgroundColor(0xFFFFFF);
		g.clear();
	}
	
	public void fieldChanged(Field field, int context) {
		if(field instanceof PlainListStyleButtonField ){
			Vector v = CacheFilmes.getInstance().getCiudadesCinesPerID(((PlainListStyleButtonField)field).getCity().getId());
			calcularCercanos(v);
		}
		super.fieldChanged(field, context);
	}
	
	public void setPosition(double latitud, double longitud) {
		this.latitud = latitud;
		this.longitud = longitud;
	}

}
