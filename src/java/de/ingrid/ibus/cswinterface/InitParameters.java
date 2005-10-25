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
 * Erstellungsdatum : 13.06.2004                                              *
 * Version          : 1.0                                                     *
 * Beschreibung     : Die Klasse speichert alle init params aus Tomcat Web.xml*
 *                                                                            *
 *                                                                            *
 *----------------------------------------------------------------------------*
 * Änderungen (Datum, Version, Author, Beschreibung)                          *
 *----------------------------------------------------------------------------*
 *            |         |          |                                          *
 *            |         |          |                                          *
 *----------------------------------------------------------------------------*
*/


package de.ingrid.ibus.cswinterface;

/**
 * Die Klasse speichert alle init params 
 * aus der Apache Tomcat web.xml der Applikation 
 * zum besseren Zugriff
 * @author rschaefer
 */

public class InitParameters {
	
	/**
     *  Die benutzte Version des Catalog Service 
    */
	private static String cswVersion = "2.0.0";
	
	/**
	  *  der Pfad zur Capabilities - Datei
	  *
	 **/
		  
	private static String capabilitiesFile = null;
	
	
	
	private static String describeRecordFile = null;
	
	
	
	/**
	*  die maximale Anzahl an Metadatensaetzen, die in einer Antwort 
	* geliefert werden sollen  
	*/
	private static int maxRecords = 10000;
 
	
	/*
	 * Die folgenden vier Parameter geben die Koordinaten von Gesamtdeutschland an 
	 * Sie werden benoetigt, um im Lucene-Index auf eine raeumliche Anfrage hin zu suchen
	 */

	/**
	* Westliche Standard-Koordinate von Gesamtdeutschland   
	*/
   private static String default_WEST_Coord = "5.3";
	
   /**
	* Oestliche Standard-Koordinate von Gesamtdeutschland   
	*/
   private static String default_OST_Coord = "14.77";
   
   /**
	* Suedliche Standard-Koordinate von Gesamtdeutschland   
    */

   private static String default_SUED_Coord = "46.76";
   
   
   /**
	* Noerdliche Standard-Koordinate von Gesamtdeutschland   
	*/
   
   private static String default_NORD_Coord = "54.73";
   
   
   /**
	  *  der absolute Pfad zur log4j.properties-Datei
	  */
  
   private static String log4jPropertiesFile = null;
   
  

    /**
  * gibt default_NORD_Coord zurueck
 * @return String
 */
public static String getDefault_NORD_Coord() {
    return default_NORD_Coord;
}

/**
 * gibt default_OST_Coord zurueck
 * @return String
 */
public static String getDefault_OST_Coord() {
    return default_OST_Coord;
}

/**
 * gibt default_SUED_Coord zurueck
 * @return String
 */
public static String getDefault_SUED_Coord() {
    return default_SUED_Coord;
}

    /**
     * gibt default_WEST_Coord zurueck
     * @return String
     */
    public static String getDefault_WEST_Coord() {
        return default_WEST_Coord;
    }

/**
 * gibt log4jPropertiesFile zurueck
 * @return String
 */
public static String getLog4jPropertiesFile() {
    return log4jPropertiesFile;
}

    /**
     * gibt maxRecords zurueck
     * @return int 
     */
    public static int getMaxRecords() {
        return maxRecords;
    }

    
 
    /**
 * setzt default_NORD_Coord
 * @param string
 */
public static void setDefault_NORD_Coord(String string) {
    default_NORD_Coord = string;
}

/**
 * setzt default_OST_Coord
 * @param string
 */
public static void setDefault_OST_Coord(String string) {
    default_OST_Coord = string;
}

/**
 * setzt default_SUED_Coord
 * @param string
 */
public static void setDefault_SUED_Coord(String string) {
    default_SUED_Coord = string;
}

    /**
     * setzt default_WEST_Coord
     * @param string
     */
    public static void setDefault_WEST_Coord(String string) {
        default_WEST_Coord = string;
    }

/**
 * setzt log4jPropertiesFile
 * @param string
 */
public static void setLog4jPropertiesFile(String string) {
    log4jPropertiesFile = string;
}

    /**
     * setzt maxRecords
     * @param i
     */
    public static void setMaxRecords(int i) {
        maxRecords = i;
    }

    /**
      * gibt cswVersion zurueck
     * @return String
     */
    public static String getCswVersion() {
        return cswVersion;
    }

    /**
     * setzt cswVersion
     * @param string
     */
    public static void setCswVersion(String string) {
        cswVersion = string;
    }

    /**
     * gibt capabilitiesFile zurueck
     * @return String
     */
    public static String getCapabilitiesFile() {
        return capabilitiesFile;
    }

    /**
     * setzt capabilitiesFile
     * @param string
     */
    public static void setCapabilitiesFile(String string) {
        capabilitiesFile = string;
    }

    /**
     * @return Returns the describeRecordFile.
     */
    public static String getDescribeRecordFile() {
        return describeRecordFile;
    }
    /**
     * @param descRecordFile The describeRecordFile to set.
     */
    public static void setDescribeRecordFile(final String descRecordFile) {
        InitParameters.describeRecordFile = descRecordFile;
    }
}



