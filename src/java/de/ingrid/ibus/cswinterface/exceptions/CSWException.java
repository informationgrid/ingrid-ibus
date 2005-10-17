



/*
 * Created on 24.05.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.ingrid.ibus.cswinterface.exceptions;

/**
 * Von dieser Klasse werden alle moeglichen Ausnahmen des Catalog Service abgeleitet
 *  
 * @author rschaefer
 */
public class CSWException extends Exception {
    
    
    /**
     * Comment for <code>serialVersionUID</code>
     */
    static final long serialVersionUID = 0;

	/**
	 * Der exceptionCode des Exception reports
	 */
	private String exceptionCode = "NoApplicableCode"; 
	
	/**
     * Der locator des Exception reports
	 */
	private String locator = null; 
	
	/**
	 * Der exceptionText des Exception reports
	 */
	private String exceptionText = "CSWException"; 
  
	  

	/**
	 * Konstruktor
	 * @param message String
	 */
	public CSWException(final String message) {
		super(message);
	}
	
	
	
	
	

    /**
     * @return Returns the exceptionCode.
     */
    public final String getExceptionCode() {
        return exceptionCode;
    }
    /**
     * @param excCode The exceptionCode to set.
     */
    public final void setExceptionCode(final String excCode) {
        this.exceptionCode = excCode;
    }
    /**
     * @return Returns the exceptionText.
     */
    public final String getExceptionText() {
        return exceptionText;
    }
    /**
     * @param excText The exceptionText to set.
     */
    public final void setExceptionText(final String excText) {
        this.exceptionText = excText;
    }
    /**
     * @return Returns the locator.
     */
    public final String getLocator() {
        return locator;
    }
    /**
     * @param loc The locator to set.
     */
    public final void setLocator(final String loc) {
        this.locator = loc;
    }
}
