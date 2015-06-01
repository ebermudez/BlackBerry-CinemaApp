package com.database.bb.cinema.handler;

import java.util.Random;
import java.util.Vector;

import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.WLANInfo;

import com.database.bb.cinema.CinemaMain;
import com.database.bb.cinema.UI.TrailerHoverScreen;
import com.database.bb.cinema.engine.ScreenEngine;
import com.database.bb.cinema.facade.FacadeImagenes;
import com.database.bb.cinema.facade.FacadeTrailerHoverScreen;
import com.database.bb.cinema.objetos.HomeTrailer;
import com.database.bb.cinema.utilidades.UrlConfig;

/**
 * @author Edgardo Bermúdez
 * This class was modified to be able to be shared online via GitHub. 
 * Controller: This class handles everything needed to be able to deliver a video trailer and an image to a screen.
 * 
 * BlackBerry 5-7 
 */
public class HandlerTrailerHoverScreen {

	TrailerHoverScreen screen = null;
	String url = UrlConfig.url_home_trailers;
	private FacadeTrailerHoverScreen facade;
	private HomeTrailer trailer;
	private boolean standalone = true;
	
	public HandlerTrailerHoverScreen(TrailerHoverScreen trailerHoverScreen, boolean standalone) {
		this.standalone  = standalone;
		screen = trailerHoverScreen;
	}
	
	public void downloadXML() {
		if(DeviceInfo.isSimulator() || 
				(WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED) ||
				((CoverageInfo.getCoverageStatus() & CoverageInfo.COVERAGE_DIRECT) == CoverageInfo.COVERAGE_DIRECT) ||
				((CoverageInfo.getCoverageStatus() & CoverageInfo.COVERAGE_MDS) == CoverageInfo.COVERAGE_MDS)
				){
			facade = new FacadeTrailerHoverScreen(this, url);
			facade.enviarSolicitud();
		}else if(CoverageInfo.getCoverageStatus() == CoverageInfo.COVERAGE_NONE){
			ScreenEngine.getInstance().dataTrailerScreen(null, null);
		}else
			ScreenEngine.getInstance().dataTrailerScreen(null, null);
	}

	public boolean isWiFiConnected()
	{
      return  (WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED);
	}
	
	protected static boolean isMDSConfigured()
    {
        return CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_MDS) ? true : false; 
    } 
	
	/*
	 * Trailers list
	 */
	public void setContenido(Vector lista) {
		final Random aleatorio = new Random();
		int num = Math.abs(aleatorio.nextInt()) % (lista.size());
		trailer = (HomeTrailer)lista.elementAt(num);
		// This method gets the image associated to the trailer.
		FacadeImagenes facade = new FacadeImagenes(this, trailer.getImageURL());
	}
	
	public void callback(byte[] dataBytes){
		if(dataBytes == null){
			if(standalone)
				screen.callBackFailed(null);
			else{
				ScreenEngine.getInstance().dataTrailerScreen(trailer, dataBytes);
			}
		}
		if(standalone)
			screen.callBack(trailer, dataBytes);
		else{
			ScreenEngine.getInstance().dataTrailerScreen(trailer, dataBytes);
		}
	}

}
