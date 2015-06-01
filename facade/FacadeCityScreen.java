package com.database.bb.cinema.facade;

import java.io.ByteArrayInputStream;

import net.rim.device.api.ui.Screen;

import com.database.bb.cinema.CinemaMain;
import com.database.bb.cinema.handler.HandlerCityScreen;
import com.database.bb.cinema.parser.ParserCityScreen;
import com.database.bb.cinema.transporte.*;

/**
 * @author Edgardo Bermúdez
 * This class was modified to be able to be shared online via GitHub. 
 * Given an url, this class sets the connection to the network and processes the obtained information.
 * 
 * BlackBerry 5-7 
 * Facade - Network controller
 */
public class FacadeCityScreen implements BBRequest.Listener{

	private String url;
	private HttpRequest req;
	private HandlerCityScreen handler = null;
	
	public FacadeCityScreen(String uRL) {
		url = uRL;
	}

	public FacadeCityScreen(HandlerCityScreen handlerCityScreen, String uRL) {
		url = uRL;
		handler = handlerCityScreen;
	}

	public void enviarSolicitud() {
		req = new HttpRequest();
		req.setRequestURL(url);		
        req.setListener(this);
        BBRequestQueue.getInstance().addRequest(req);
	}

	public void requestStarted(BBRequest request) {
	
	}

	public void requestSucceeded(BBRequest request) {
		procesarXML(request);
	}

	public void requestCancelled(BBRequest request) {
	
	}

	public void requestFailed(BBRequest request) {
		handler.requestFailed();
	}
	
	/**
	* Process XML
	**/
	private void procesarXML(BBRequest request) {
		HttpRequest httpRequest = (HttpRequest) request;
        byte[] dataBytes = httpRequest.getResponseByte();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(dataBytes);
        
        ParserCityScreen parser = new ParserCityScreen();
        parser.parseDocument(bis);
        
        handler.setContent(parser.getList());
        
	}
}
