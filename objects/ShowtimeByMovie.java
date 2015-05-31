package com.database.bb.cinema.objects;

import java.util.Vector;

import net.rim.device.api.util.Persistable;

/**
 * @author Edgardo Bermúdez
 * This class is only a vector with theaters. Is used inside a showtime, which contains theaters in every time set.
 * 
 * BlackBerry 5-7 
 * 
 */
public class ShowtimeByMovie implements Persistable{

	Vector teatros;
	
	public ShowtimeByMovie(){
		teatros = new Vector();
	}
	
	public void addTheater(Object obj){
		teatros.addElement(obj);
	}
	
	public Vector getTeatros(){
		return teatros;
	}
	
}
