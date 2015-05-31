package com.database.bb.cinema.persistencia;

import java.util.Hashtable;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

/**
 * @author Edgardo Bermúdez
 * This class was modified to be able to be shared online via GitHub. 
 * Class that controls the access to a persistent object inside the BlackBerry inner memory. 
 * 
 * BlackBerry 5-7 
 * Cache Controller
 */
public class CacheMyCinema {
	// TODO - Fix the apparent mix of languages. 
	private static CacheMyCinema instance = null;
	
	private PersistentObject repositorioMeu, repositorioMeuCines, respositoriogeneros;
	
	private Hashtable ciudadesMyCinema, cinesMyCinema, cinesMyCinemaGeneros;
	
	//Change with your own long values
	private long repositorioMeuLong = 0x01L;
	private long repositorioMeuCinesLong = 0x02L; 
	private long repositorioGenerosLong = 0x03L;
	
	/**
	* To be able to have a singleton.
	**/
	public static CacheMyCinema getInstance(){
		if(instance == null){
			return new CacheMyCinema();
		}
		return instance;
	}

	private CacheMyCinema(){
		repositorioMeu = PersistentStore.getPersistentObject(repositorioMeuLong);	
		
		ciudadesMyCinema = (Hashtable)repositorioMeu.getContents();
		
		if (ciudadesMyCinema == null)
		{
			ciudadesMyCinema = new Hashtable();
			repositorioMeu.setContents(ciudadesMyCinema);	
			repositorioMeu.commit();
		}
		
		repositorioMeuCines = PersistentStore.getPersistentObject(repositorioMeuCinesLong);	
		
		cinesMyCinema = (Hashtable)repositorioMeuCines.getContents();
		
		if (cinesMyCinema == null)
		{
			cinesMyCinema = new Hashtable();
			repositorioMeuCines.setContents(cinesMyCinema);	
			repositorioMeuCines.commit();
		}
		
		respositoriogeneros = PersistentStore.getPersistentObject(repositorioGenerosLong);	
		
		cinesMyCinemaGeneros = (Hashtable)respositoriogeneros.getContents();
		
		if (cinesMyCinemaGeneros == null)
		{
			cinesMyCinemaGeneros = new Hashtable();
			respositoriogeneros.setContents(cinesMyCinemaGeneros);	
			respositoriogeneros.commit();
		}
	}
	
	public void setHashtable(Hashtable current){
		ciudadesMyCinema = current;
		repositorioMeu.setContents(ciudadesMyCinema);
		repositorioMeu.commit();
	}
	
	public Hashtable getCiudades(){
		return ciudadesMyCinema;
	}
	
	public void setHashtableCines(Hashtable current){
		cinesMyCinema = current;
		repositorioMeuCines.setContents(cinesMyCinema);
		repositorioMeuCines.commit();
	}
	
	public Hashtable getCines(){
		return cinesMyCinema;
	}
	
	public void setHashtableGeneros(Hashtable current){
		cinesMyCinemaGeneros = current;
		respositoriogeneros.setContents(cinesMyCinemaGeneros);
		respositoriogeneros.commit();
	}
	
	public Hashtable getGeneros(){
		return cinesMyCinemaGeneros;
	}
	
	public void deleteCache(){
		PersistentStore.destroyPersistentObject(repositorioMeuLong);
		PersistentStore.destroyPersistentObject(repositorioMeuCinesLong);
		PersistentStore.destroyPersistentObject(repositorioGenerosLong);
	}
}
