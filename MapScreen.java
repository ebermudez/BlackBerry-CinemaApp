package com.database.bb.cinema.UI;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.URLEncodedPostData;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.database.bb.cinema.component.ProgressAnimationField;
import com.database.bb.cinema.container.BitmapButtonField;
import com.database.bb.cinema.engine.GlobalControl;
import com.database.bb.cinema.handler.HandlerImagenes;
import com.database.bb.cinema.objetos.Theater;
import com.database.bb.cinema.utilidades.ScreenProperties;

/**
 * @author Edgardo Bermúdez
 * This class is used to create a map in the screen with Google Maps. 
 * If Google Maps is not installed in the device, it'll ask you if you want to download it.
 * 
 * BlackBerry 5-7 
 */
public final class MapScreen extends PrincipalScreen{

	String latitud = "";
	String longitud = "";
	String name = "";
	String address = "";
	
	private Theater teatro;
	private HandlerImagenes mapaHandler;
	private BitmapButtonField mapa;
	ProgressAnimationField progreso = new ProgressAnimationField(Bitmap.getBitmapResource("img/spinner.png"), 6, FIELD_HCENTER);
	VerticalFieldManager principal = new VerticalFieldManager(USE_ALL_WIDTH);
	private FieldChangeListener fcl;
	
	public MapScreen(Theater teatro) {
		super(false);
		addHeader();
		setInfoHeader();
		addMenu();
		this.teatro = teatro;
		latitud = teatro.getLatitud();
		longitud = teatro.getLongitud();
		name = teatro.getName();
		address = teatro.getAddress();
		init();
		askforMap();
	}

	private void askforMap() {
		String mapWidth = String.valueOf((Display.getWidth() - 32));
		String mapHeight = String.valueOf(Display.getHeight() - 100);
		// TODO - Change this url into a global variable.
		final String url = "http://maps.google.com/maps/api/staticmap?maptype=roadmap&markers=color:green|"+latitud+","+longitud+"&size="+mapWidth+"x"+mapHeight+"&sensor=true&zoom=14";
		mapaHandler = new HandlerImagenes(this, url);
		mapaHandler.accessFacade();
	}

	public void init() {
		progreso.setMargin(10,10,10,10);
		principal.setMargin(5,0,5,0);
		principal.add(colocarEspera());
		add(principal);
	}

	private Field colocarEspera() {
		VerticalFieldManager tmp = new VerticalFieldManager(USE_ALL_WIDTH);
		LabelField label = new LabelField(_resources.getString(Cargando), DrawStyle.HCENTER | Field.FIELD_HCENTER){
			protected void paint(Graphics graphics) {
				graphics.setColor(Color.WHITE);
				super.paint(graphics);
			}
		};
		label.setFont(ScreenProperties.bigFieldFontBold);
		label.setMargin(10,0,10,0);
		tmp.add(label);
		tmp.add(progreso);
		return tmp;
	}

	protected void onUiEngineAttached(boolean attached) {
		super.onUiEngineAttached(attached);
	}

	public void setMapa(byte[] dataBytes) {
		Bitmap bitmap = toBitmap(dataBytes);
		synchronized(UiApplication.getEventLock()) 
        {
			principal.deleteAll();
        }
		try{		
			mapa = new BitmapButtonField(bitmap, bitmap,Field.FIELD_HCENTER){
				protected void paint(Graphics g) {
					super.paint(g);
						g.setColor(isFocus() ? GlobalControl.SoftRedColor:GlobalControl.SoftRedColor);
						g.drawRect(0, 0, getWidth(), getHeight());
						g.drawRect(1, 1, getWidth()-2, getHeight()-2);
				};
			};
	        fcl = new FieldChangeListener() {
				public void fieldChanged(Field field, int context) {
					if (field == mapa) {
						googleMaps();
					}
				}
			};
			mapa.setPadding(0,0,0,0);
			mapa.setChangeListener(null);
			mapa.setChangeListener(fcl);
			synchronized(UiApplication.getEventLock()) 
	        {
				principal.add(mapa);
	        }
		}catch (Exception e) {
//			setErrorMapa();
		}
	}

	private void googleMaps() {
		int mh = CodeModuleManager.getModuleHandle("GoogleMaps");
		if (mh == 0) {
			// TODO - Change this text into a global variable.
			int response=Dialog.ask(Dialog.D_YES_NO, "¿Deseas descargar GoogleMaps ahora?");
			if (response == Dialog.YES){
				Browser.getDefaultSession().displayPage("http://www.google.com/maps");
			}
		}else{
		URLEncodedPostData uepd = new URLEncodedPostData("utf-8", true);
		uepd.append("action","LOCN");
		uepd.append("a", "@latlon:"+latitud+","+longitud);
		uepd.append("title", name);
		uepd.append("description", address);
		String[] args = { "http://gmm/x?"+uepd.toString() };
		ApplicationDescriptor ad = CodeModuleManager.getApplicationDescriptors(mh)[0];
		ApplicationDescriptor ad2 = new ApplicationDescriptor(ad, args);
		try {
			ApplicationManager.getApplicationManager().runApplication(ad2, true);
		} catch (ApplicationManagerException e) {
			// TODO - Change this text into a global variable.
			int response=Dialog.ask(Dialog.D_YES_NO, "¿Deseas descargar GoogleMaps ahora?");
			if (response == Dialog.YES){
				Browser.getDefaultSession().displayPage("http://www.google.com/maps");
			}
		}
		}
	}
	
	protected boolean keyDown(int code, int time) {
    	if(Keypad.KEY_ESCAPE == Keypad.key(code)){
    		close();
    		return true;
    	}else 
    		return super.keyDown(code, time);
	}

}
