package com.database.bb.cinema.component;

import java.util.Vector;

import com.database.bb.cinema.CinemaMain;
import com.database.bb.cinema.engine.BitmapManager;
import com.database.bb.cinema.engine.ScreenEngine;
import com.database.bb.cinema.facade.FacadeImagenes;
import com.database.bb.cinema.i18n.TextResource;
import com.database.bb.cinema.objetos.Film;
import com.database.bb.cinema.objetos.Showtime;
import com.database.bb.cinema.objetos.ShowtimeByMovieListaObj;
import com.database.bb.cinema.objetos.ShowtimeMovie;
import com.database.bb.cinema.objetos.ShowtimeMoviePerformance;
import com.database.bb.cinema.persistencia.CacheFilmes;
import com.database.bb.cinema.utilidades.ScreenProperties;
import com.database.bb.cinema.utilidades.UrlConfig;

import net.rim.blackberry.api.browser.Browser;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

/**
 * @author Edgardo Bermúdez
 * This class was modified to be able to be shared online via GitHub. 
 * This is a list item. This item contains all the information about a movie and let's the user get detailed data if clicked.
 * 
 * BlackBerry 5-7 
 */
public class ShowtimeListStyleButtonField extends ListStyleButtonField implements FieldChangeListener, TextResource{

	Font fontBig = ScreenProperties.bigFieldFontBold;
	Font fontMedium = ScreenProperties.normalFieldFont;
	Font fontSmall = ScreenProperties.normalFieldFont;
	
	protected static ResourceBundle _resources = ResourceBundle.getBundle(BUNDLE_ID, BUNDLE_NAME);
	
	/**
	 * isNormalField says if the focus is on the left (movie) or on the right (trailer button)
	 */
	private boolean isNormalField = true;
	private ShowtimeMovie shows = null;
	private int extraVPaddingNeeded = -1;
	private boolean tienePoster = false;
	private FacadeImagenes facadeImg;
	private int h = 0;
	private String horarios = "";
	private int drawn = 0;
	private boolean supportsTrailer = true;
	private int limitesHorizontales = Display.getWidth() - BitmapManager.getInstance().getDummySmallPoster().getWidth() - BitmapManager.getInstance().getIcontrailer().getWidth();
	private int header = 0;
	private int barraXD = 30;
	private String lineasHorarios;
	
	public ShowtimeListStyleButtonField(ShowtimeMovie shows, Bitmap leftImg, String nombre,
			Bitmap actionBitmap, long style){
		super(leftImg, nombre, actionBitmap, style);
		this.shows  = shows;
		setChangeListener(this);
		this.setBorder(BorderFactory.createSimpleBorder(new XYEdges(0, 0, 1, 0), Border.STYLE_SOLID));
		setFocusedTrailer(false);
	}

	public ShowtimeListStyleButtonField(ShowtimeMovie filme, long style, Font normalFieldFontBold, Font normalFieldFont) {
		this(filme, BitmapManager.getInstance().getDummySmallPoster(), filme.getTitle() , BitmapManager.getInstance().getIcontrailer() , style);
		shows = filme;
		config();
	}

	private void config() {
		try{
			Film p = CacheFilmes.getInstance().getFilmFromShowtimeMovie(shows);
			if(p.getTrailerID() == null ||  p.getTrailerID().equals("")){
				supportsTrailer = false;
				set_actionIcon(BitmapManager.getInstance().getIcontrailer_desabil());
			}
		}catch(Exception e){
			return;
		};
	}

	protected void onDisplay() {
		super.onDisplay();
	}

	public void fieldChanged(Field arg0, int arg1) {
		Film p = CacheFilmes.getInstance().getFilmFromShowtimeMovie(shows);
		if(p == null)
			return;
		if(isNormalField){
			ScreenEngine.getInstance().goPeliculaDetalle(p, -1);
		}else{
			try{
				if(shows.getId() != -1){
					if(((DeviceInfo.getSoftwareVersion())).startsWith("5"))
						Browser.getDefaultSession().displayPage(UrlConfig.url_trailer_listas + shows.getId() + "&device=blackberry" + "5");
					else
						Browser.getDefaultSession().displayPage(UrlConfig.url_trailer_listas + shows.getId() + "&device=blackberry");
				}
			}catch(Exception e){
				ScreenEngine.getInstance().goPeliculaDetalle(p, -1);
			}
		}		
	}
	
	public int getPreferredHeight() {
		return _labelHeight + 2 * extraVPaddingNeeded + h + header;
	}
	
	public void layout(int width, int height) 
	{
		header = 0;
		if(((ShowtimeMoviePerformance)shows.getPerformances().elementAt(0)).isXd()){
			header = barraXD + 5;
		}
		
    	_leftOffset = HPADDING;
        if( _leftIcon != null ) {
            _leftOffset += _leftIcon.getWidth() + HPADDING;
            int altoIcon = _leftIcon.getHeight()+ 2 * VPADDING;
        	int altoFont = getFont().getHeight() / 2 * 3 + 2 * VPADDING;
        	_targetHeight = Math.max(altoIcon, altoFont);
        }else if (_actionIcon != null){
        	int altoIcon = _actionIcon.getHeight()+ 2 * VPADDING;
        	int altoFont = getFont().getHeight() / 2 * 3 + 2 * VPADDING;
        	_targetHeight = Math.max(altoIcon, altoFont);
        }else{
        	_targetHeight = 90;
        }
        _rightOffset = 0;
        if (_rightOffset < HPADDING)
        {
        	_rightOffset = HPADDING;
        }
        if( _actionIcon != null ) {
            _rightOffset += _actionIcon.getWidth() + HPADDING;
        }
        _labelField.layout( width - _leftOffset - _rightOffset, getHeight() );
        _labelHeight = getFont().getHeight();
        int labelWidth = _labelField.getWidth();
        if(labelWidth < limitesHorizontales)
        	limitesHorizontales = labelWidth;
        
        if( _labelField.isStyle( DrawStyle.HCENTER ) ) {
            _leftOffset = ( width - labelWidth ) / 2;
        } else if ( _labelField.isStyle( DrawStyle.RIGHT ) ) {
            _leftOffset = width - labelWidth - HPADDING - _rightOffset;
        } else if ( _labelField.isStyle( DrawStyle.LEFT ) ) {
            _leftOffset = HPADDING;
        }
        
        extraVPaddingNeeded = 0;
        if( _labelHeight < _targetHeight ) {
            // Make sure that they are at least 1.5 times font height
            extraVPaddingNeeded =  ( _targetHeight - _labelHeight ) / 2;
        }
       
        lineasHorarios = 
			_resources.getString(Texto_Sala) + " " + ((ShowtimeMoviePerformance) shows.getPerformances().elementAt(0)).getScreen()
			+ ": " + createShowtimeString(shows.getPerformances());
        Vector lines = wrap(lineasHorarios, limitesHorizontales );
        drawn = lines.size();
        CineMarkMain.log("Tam:: " + drawn + "") ;
        if(drawn > 2)
			h = (drawn - 1)*fontSmall.getHeight();
        
        setExtent( width, _labelHeight + 2 * extraVPaddingNeeded + h + header  );
	}
	
	protected void paint(Graphics g) {
		
		if(((ShowtimeMoviePerformance)shows.getPerformances().elementAt(0)).isXd()){
			int background = g.isDrawingStyleSet( Graphics.DRAWSTYLE_FOCUS ) ? COLOR_BACKGROUND_FOCUS : 0xf4b34c;
			g.setColor( background );
	        g.fillRect( 0, 0, getWidth(), getHeight());
		}

		boolean foco = g.isDrawingStyleSet( Graphics.DRAWSTYLE_FOCUS );

		pintar(g);
		
		String nombrePelicula = shows.getTitle() ;
		String generoPelicula = ((ShowtimeMoviePerformance)shows.getPerformances().elementAt(0)).getExhibition();
		String lineasHorarios = 
			_resources.getString(Texto_Sala) + " " + ((ShowtimeMoviePerformance) shows.getPerformances().elementAt(0)).getScreen()
			+ ": " + createShowtimeString(shows.getPerformances());
		
		int margenIzq = _leftIcon.getWidth() + HPADDING*2 ;
		int margenSup = VPADDING*2 + header;
		if(!foco)
			g.setColor(Color.GRAY);
		else
			g.setColor(Color.BLACK);
		g.setFont(fontBig);

		if(fontBig.getAdvance(nombrePelicula) >= getWidth() - _leftIcon.getWidth() - _actionIcon.getWidth() - 10){
			g.drawText(nombrePelicula, margenIzq, margenSup, Graphics.TOP | Graphics.LEFT, getWidth() - _leftIcon.getWidth() - _actionIcon.getWidth() - 10);
		}else
			g.drawText(nombrePelicula, margenIzq, margenSup, Graphics.TOP | Graphics.LEFT, getWidth() - _leftIcon.getWidth() - _actionIcon.getWidth() - 10);
      
		g.setFont(fontMedium);
		
		margenIzq = _leftIcon.getWidth() + HPADDING*2 ;

		margenSup += fontBig.getHeight();
		if(!foco)
			g.setColor(0x2393fc);
		else
			g.setColor(Color.WHITE);
		g.drawText(generoPelicula, margenIzq , margenSup);
		g.setFont(fontSmall);
		
		margenIzq = _leftIcon.getWidth() + HPADDING*2 ;

		margenSup += fontMedium.getHeight();

		g.setColor(0xfe0006);
		drawListRow(g, margenIzq, margenSup, lineasHorarios);
	}
	
	private String createShowtimeString(Vector performances) {
		ShowtimeMoviePerformance smp = null;
		horarios = "";
		for(int i = 0; i < performances.size() ; i++){
			smp = (ShowtimeMoviePerformance) performances.elementAt(i);
			if(i > 0)
				horarios += " - ";
			horarios += smp.getHorario();
			if(!smp.getLegend().equals(""))
				horarios += "(" + smp.getLegend() + ") ";
		}
		return horarios;
	}

	public void updateThisLayout(){
		return;
	}

	private void pintar(Graphics g) {
		//Upper bar: it shows "XD"
		colocarXD(g);
		// Left Bitmap
        if( _leftIcon != null ) {
        	if (ScreenProperties.isHighRes & ScreenProperties.isWide){
        		g.drawBitmap( HPADDING, VPADDING + header, _leftIcon.getWidth(), _leftIcon.getHeight(), _leftIcon, 0, 0 );
      	   	}else if (ScreenProperties.isHighRes & !ScreenProperties.isWide){
      		   	g.drawBitmap( HPADDING, VPADDING + header, _leftIcon.getWidth(), _leftIcon.getHeight(), _leftIcon, 0, 0 );
      	   	}else{
      	   		g.drawBitmap( HPADDING, (getHeight() - (_leftIcon.getHeight() - 20))/2 + header, _leftIcon.getWidth(), _leftIcon.getHeight() - 20, _leftIcon, 0, 10 );
      		}
        }
        
        // Right (Action) Bitmap
        if( _actionIcon != null ) {
            g.drawBitmap( getWidth() - HPADDING - _actionIcon.getWidth(), ( getHeight() - _actionIcon.getHeight() ) / 2, _actionIcon.getWidth(), _actionIcon.getHeight(), _actionIcon, 0, 0 );
        }		
	}
	
	private void colocarXD(Graphics g) {
		if(Display.getHeight() < 360)
			barraXD = 20;
		if(((ShowtimeMoviePerformance)shows.getPerformances().elementAt(0)).isXd()){
			int oldC = g.getColor();
			int background = g.isDrawingStyleSet( Graphics.DRAWSTYLE_FOCUS ) ? 0xf4b34c : 0xff8a00;
			g.setColor( background );
	        g.fillRect( 0, 0, getWidth(), barraXD);
	        colocarTextoXD(g);
	        g.setColor(oldC);
		}
	}

	private void colocarTextoXD(Graphics g) {
		Font f = g.getFont();
		int c = g.getColor();
		g.setFont(ScreenProperties.normalFieldFontBold);
		g.setColor(0x0);
		g.drawText("SALA XD Extreme Digital Cinema",2, (barraXD  - getFont().getHeight())/2,  Graphics.LEFT | DrawStyle.HCENTER | DrawStyle.ELLIPSIS , Display.getWidth());
		g.setFont(f);
		g.setColor(c);
		
	}

	public Bitmap toBitmap(byte[] data)
	{
		if (data.length > 0)
		{
			return EncodedImage.createEncodedImage(data, 0, -1).getBitmap();
		}
		return null;
	}
	
	protected boolean navigationMovement(int dx, int dy, int status, int time) {
		if(dx == 1){
			setFocusedTrailer(true);
		}else if(dx == 0){
			setFocusedTrailer(false);
		}else{
			setFocusedTrailer(false);
		}
		if (dx != 0)
		{
			return false;
		}
		return super.navigationMovement(dx, dy, status, time);
	}
	
	protected boolean touchEvent(TouchEvent message) {
		int x = message.getX( 1 );
        int y = message.getY( 1 );
        if( x < 0 || y < 0 || x > getExtent().width || y > getExtent().height ) {
            // Outside this field
        	setFocusedTrailer(false);
            return false;
        }
        if(x > Display.getWidth() - Display.getWidth()/5){
        	setFocusedTrailer(true);
        }else{
        	setFocusedTrailer(false);
        }
        
        switch( message.getEvent() ) {
       
            case TouchEvent.UNCLICK:
                return true;
        }
		return super.touchEvent(message);
	}
	
	private void setFocusedTrailer(boolean isFocused) {
		if(supportsTrailer){
			isNormalField  = !isFocused;
			if(isFocused){
				try{
						set_actionIcon(BitmapManager.getInstance().getIcontrailer_focus());
				}catch(Exception e){
					set_actionIcon(BitmapManager.getInstance().getIcontrailer_focus());
				}
			}else{
				set_actionIcon(BitmapManager.getInstance().getIcontrailer());
			}
		}else{
			//Nothing to do. It doesn't have a trailer to show.
		}
	}
	
	protected void onFocus(int direction) {
		pedirImagen();
		super.onFocus(direction);
	}
	
	public void pedirImagen() {
		if(!tienePoster()){
			facadeImg = new FacadeImagenes(this, UrlConfig.url_fotos_posters + shows.getId() + "/photo2.jpg");
		}
	}

	public boolean tienePoster(){
		return tienePoster;
	}
	
	public void callbackPoster(byte[] leftIcon)
	{
		tienePoster = true;
		super.set_leftIcon(toBitmap(leftIcon));
	}
	
	/**
	 * ENG: It paints a multilingual text with the drawtext method without using Fields. Is used inside the paint method.
	 * Pinta un texto multilineal con drawtext sin usar Fields. (Esto se usa dentro del paint)
	 * @param g : Graphics 
	 * @param y : altura de la línea
	 * @param sh : Texto largo o corto
	 */
	private void drawListRow( Graphics g, int x, int y, String sh) {
		Vector lines = wrap(sh, limitesHorizontales );
		for (int i = 0; i < lines.size(); i++) 
		{
		      int liney = y + (i * this.getFont().getHeight());
		      g.drawText((String)lines.elementAt(i), x ,liney,  DrawStyle.ELLIPSIS, limitesHorizontales);
		}	
		drawn = lines.size();
	}
	
	/**
	 * ENG: It cuts a string with the size that the variable "width" say.
	 * Pica un string en pedazos de tamaño width
	 * @param text : String a picar
	 * @param width : Tamaño del contenedor o línea
	 * @return
	 */
	private Vector wrap (String text, int width) 
	{
	    Vector result = new Vector ();
	    if (text ==null)
	       return result;
	 
	    boolean hasMore = true;
	 
	    // The current index of the cursor
	    int current = 0;
	 
	    // The next line break index
	    int lineBreak = -1;
	 
	    // The space after line break
	    int nextSpace = -1;
	 
	    while (hasMore) 
	    {
	       //Find the line break
	       while (true) 
	       {
	           lineBreak = nextSpace;
	           if (lineBreak == text.length() - 1) 
	           {
	               // We have reached the last line
	               hasMore = false;
	               break;
	           } 
	           else 
	           {
	               nextSpace = text.indexOf(' ', lineBreak+1);
	               if (nextSpace == -1)
	                  nextSpace = text.length() -1;
	               int linewidth = this.getFont().getAdvance(text,current, nextSpace-current);
	               // If too long, break out of the find loop
	               if (linewidth > width) 
	                  break;
	           }
	      }
	      String line = text.substring(current, lineBreak + 1);
	      result.addElement(line);
	      current = lineBreak + 1;
	 }
	 return result;
	}

	/**
	 * @return the lineasHorarios
	 */
	public final String getLineasHorarios() {
		return lineasHorarios;
	}

	/**
	 * @return the shows
	 */
	public final ShowtimeMovie getShows() {
		return shows;
	}
}
