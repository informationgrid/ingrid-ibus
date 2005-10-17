/*----------------------------------------------------------------------------*
 *          @@@@@      @@@       @@@@@                                        *
 *      @@@@@@@@@@@    @@@@    @@@@@@@@        @                              *
 *     @@@@@@@@@@@@    @@@@   @@@@@@@@@     @@@@                              *
 *    @@@@@            @@@@  @@@@           @@@@                              *
 *   @@@@@             @@@@  @@@@@        @@@@@@@@@   @@@@@@@@      @@@@@@@   *
 *   @@@@    @@@@@@@   @@@@   @@@@@@@     @@@@@@@@@  @@@@@@@@@@   @@@@@@@@@   *
 *   @@@@   @@@@@@@@   @@@@    @@@@@@@@     @@@@    @@@@    @@@   @@@@        *
 *   @@@@    @@@@@@@   @@@@      @@@@@@@    @@@@    @@@@@@@@@@@@ @@@@         *
 *   @@@@@      @@@@   @@@@         @@@@    @@@@    @@@@@@@@@@@@ @@@@         *
 *    @@@@@     @@@@   @@@@   @     @@@@    @@@@    @@@@      @   @@@@        *
 *     @@@@@@@@@@@@@   @@@@   @@@@@@@@@@    @@@@@@@  @@@@@@@@@@   @@@@@@@@@   *
 *       @@@@@@@@@@@   @@@@   @@@@@@@@       @@@@@@   @@@@@@@@@     @@@@@@@   *
 *                            Neue Wege mit GIS                               *
 *                                                                            *
 * Fraunhoferstr. 5                                                           *
 * D-64283 Darmstadt                                                          *
 * info@gistec-online.de                          http://www.gistec-online.de *
 *----------------------------------------------------------------------------*
 *                                                                            *
 * Copyright © 2004 GIStec GmbH                                               *
 * ALL Rights Reserved.                                                       *
 *                                                                            *
 *+---------------------------------------------------------------------------*
 *                                                                            *
 * Author           : Ralf Schäfer                                            *
 * Erstellungsdatum : 28.05.2004                                              *
 * Version          : 1.0                                                     *
 * Beschreibung     :  Hilfsfunktionen zum Testen und Erstellen von Formaten  *
 *                                                                            *
 *                                                                            *
 *----------------------------------------------------------------------------*
 * Änderungen (Datum, Version, Author, Beschreibung)                          *
 *----------------------------------------------------------------------------*
 *            |         |          |                                          *
 *            |         |          |                                          *
 *----------------------------------------------------------------------------*
*/

package de.ingrid.ibus.cswinterface.tools;

import java.util.StringTokenizer;

import org.apache.lucene.document.NumericField;

import de.ingrid.ibus.cswinterface.exceptions.CSWInvalidParameterValueException;

/**
 * Diese Klasse stellt Hilfsfunktionen zum Testen und Erstellen von (Zahlen)Formaten zur Verfuegung
 * @author rschaefer
 */
public class PatternTools {
	
	private static String zero = "0";
	private static char comma = '.';
	
	/**
	 * Diese Methode testet das Format der Versionsangaben des Catalog Service.
	 * Gefordertes Format der Versionsangaben:
	 * Drei nicht-negative Zahlen in der Form x.y.z, 
     * davon sollen y und z die Zahl 99 nicht überschreiten.
	 * @param String version
	 * @return boolean isVersionFormatValid
	 */
	public static boolean isVersionFormatValid(String version){
	
	   boolean isVersionFormatValid = false;
	   
		//TODO regexp fuer x > 99?
	   if (version.matches("[0-9][.][0-9][.][0-9]") || 
	       version.matches("[0-9][0-9][.][0-9][.][0-9]") || 
		   version.matches("[0-9][0-9][.][0-9][0-9][.][0-9]") || 
		   version.matches("[0-9][0-9][.][0-9][.][0-9][0-9]") || 	              
		   version.matches("[0-9][.][0-9][0-9][.][0-9]") ||
		   version.matches("[0-9][.][0-9][0-9][.][0-9][0-9]") ||  
		   version.matches("[0-9][.][0-9][.][0-9][0-9]") || 
		   version.matches("[0-9][0-9][.][0-9][0-9][.][0-9][0-9]") ) 
		  {
			
			isVersionFormatValid = true;
	     }
	   
	   
	   return isVersionFormatValid;
	   
   } 
   
   
     
     
     
     /**
      * wandelt aus String date ein gueltiges XML-Datum in String validDate
	 * @param String date
	 * @param boolean index (fuer Lucenen-Index?)
	 * @return String validDate
	 */
	public static String toValidDateFormat(String date, boolean index) throws Exception {
   	
	  
	    String validDate = "";
		
	   //TODO andere Formate als YYYY-MM-DD aus INI moeglich
       // String dateFormat = InitParameters.dateFormat;
        
        String year = null;
		String month = null;
		String day = null;
		
		date = date.trim(); 
		
		//gueltig ist nur das Minus zwischen den Zahlen!
	   	//TODO Exception werfen?
		 if (date.indexOf("-") == -1){
		 	return date;
		 }
		
		StringTokenizer stringTokenizer = new StringTokenizer(date, "-");
		
   
		year = stringTokenizer.nextToken(); 
		
		if (year.length() != 4){
			System.err.println("PatternTools toValidDateFormat year: " + year);	
		}
		
		
		month = stringTokenizer.nextToken(); 
		
		if (month.length() != 2){
			System.err.println("PatternTools toValidDateFormat month: " + month);	
			
		 }
		
		
		day = stringTokenizer.nextToken();
    
		if (day.length() != 2){
				
			if (day.length() > 2){
		      day = day.substring(0,2);
			}
			else{
				System.err.println("PatternTools toValidDateFormat day: " + day);  
			}
			
		}
        //nur fuer Lucene
		if (index){
		 validDate = year+month+day;
		}
	    else{
	     validDate = year+"-"+month+"-"+day;
	    }
		
		
		/*
		System.out.println("PatternTools toValidDateFormat date: " + date);
		System.out.println("PatternTools toValidDateFormat validDate: " + validDate);
	    */
	    
	    return validDate;
     }	
   
	/**
	 * wandelt geogr Koordinaten in ein Format fuer 
	 *die Indexierung mit Lucene um 
	 * @param String coord
	 * @return String validCoord
	 */
	public static String toLuceneIndexCoordFormat(String coord) throws Exception {
		
		if (coord == null)
		  return "";
		
		try {
			return NumericField.numberToString(Double.valueOf(coord.trim()));
        } catch (NumberFormatException e) {
			return "";
        }
	}	
	
	
	
	
	private static String handleCoordWithoutComma(String coordWithoutComma) throws Exception {
		
			if (coordWithoutComma.length() == 1){
							//eine Null vorne anhaengen
				coordWithoutComma = zero + coordWithoutComma;
							//zwei Nullen hinten anhaengen
				coordWithoutComma = coordWithoutComma + zero + zero;
			} 
			else if (coordWithoutComma.length() == 2){
				 //nur zwei Nullen hinten anhaengen
				coordWithoutComma = coordWithoutComma + zero + zero;	
			}
			else {
				
				throw new CSWInvalidParameterValueException("Value of coordinate is not valid: "+ coordWithoutComma, 
				                                                              "gml:coordinates");							
			}	
		
			return coordWithoutComma;
		
		}
   
	
	
	
	private static String handleCoordBeforeComma(String coordBeforeComma)throws Exception {
		
		if (coordBeforeComma.length() == 1){
						//eine Null vorne anhaengen
			coordBeforeComma = zero + coordBeforeComma;
		} 
		else if (coordBeforeComma.length() != 2){
		  
		   throw new CSWInvalidParameterValueException("Value of coordinate is not valid: "+ coordBeforeComma, 
																						 "gml:coordinates"); 
		}	
		
		return coordBeforeComma;
		
	}
   
   
	private static String handleCoordAfterComma(String coordAfterComma){
        
        if(coordAfterComma.length() == 1){
         //eine Null anhaengen
		 coordAfterComma = coordAfterComma + zero;
        }
        else {
         //nur zwei Ziffern vorne lassen, Rest abschneiden	
		 coordAfterComma = coordAfterComma.substring(0, 2);
        }
      
		return coordAfterComma;
		
	}

	public static void main(String[] args) {
		
		try {
			System.out.println("PatternTools main: " +  PatternTools.toLuceneIndexCoordFormat("755.898"));
		} catch (Exception e) {
			System.err.println("PatternTools ERROR: " + e);	
		}
		
	}	
	

}
