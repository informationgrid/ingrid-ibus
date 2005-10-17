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
 * Rundturmstr. 12                                                            *
 * D-64283 Darmstadt                                                          *
 * info@gistec-online.de                          http://www.gistec-online.de *
 *----------------------------------------------------------------------------*
 *                                                                            *
 * Copyright © 2005 GIStec GmbH                                               *
 * ALL Rights Reserved.                                                       *
 *                                                                            *
 *+---------------------------------------------------------------------------*
 *                                                                            *
 * Author           : Ralf Schäfer                                            *
 * Erstellungsdatum : 06.10.2005                                              *
 * Version          : 1.0                                                     *
 * Beschreibung     :  transformiert ein OGC filter encoding in Ingrid query  *
 *                                                                            *
 *                                                                            *
 *----------------------------------------------------------------------------*
 * Änderungen (Datum, Version, Author, Beschreibung)                          *
 *----------------------------------------------------------------------------*
 *            |         |          |                                          *
 *            |         |          |                                          *
 *----------------------------------------------------------------------------*
*/


 


package de.ingrid.ibus.cswinterface.transform;


import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.ingrid.ibus.cswinterface.InitParameters;
import de.ingrid.ibus.cswinterface.exceptions.CSWException;
import de.ingrid.ibus.cswinterface.exceptions.CSWInvalidParameterValueException;
import de.ingrid.ibus.cswinterface.exceptions.CSWMissingParameterValueException;
import de.ingrid.ibus.cswinterface.exceptions.CSWNoApplicableCodeException;
import de.ingrid.ibus.cswinterface.exceptions.CSWOperationNotSupportedException;
import de.ingrid.ibus.cswinterface.exceptions.CSWFilterException;
import de.ingrid.ibus.cswinterface.tools.PatternTools;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;


/**
*
* Diese Klasse transformiert ein OGC filter encoding in einen
* Lucene query
*/ 
public class FilterToIngridQuery {
	
	private StringBuffer sb = null;
	
	
	private IngridQuery ingridQuery = null;
	
	private IngridQuery currQuery = null;
	
	
	
	private boolean not = false;
	
	/**
	 * Das Feld im Lucene-Index, an welches die eine Anfrage gerichtet sein kann
	 */
	private String field = "";	
	 
	
	private String operator = "";
	
	//private String srs = "";
	
	
	private final String defaultLowerLimit = "0";
	
	private final String defaultUpperLimit = "99999999999999999999999";
   
   
    private final String dateLowerLimit = "00000101";
	
	
	private final String dateUpperLimit = "90000101";
    
	private final String colon = ":";
	
	private final String leftBracket = "(";
    private final String rightBracket = ")";
	
	private final String leftSquareBracket = "[";
	private final String rightSquareBracket = "]";
	private final String leftCurlyBracket = "{";
    private final String rightCurlyBracket = "}";
	
   
	private final Logger LOGGER = Logger.getLogger(this.getClass());
   
	/**
	 * Konstructor.
	 */
	public FilterToIngridQuery() {
		
		LOGGER.debug("constructor");
			
		//this.srs = srs;
	}
			
   
   


	/**
	 * Erstellt aus einem Filter eine Lucene-Anfrage als String
	 * @param Filter filter
	 * @return StringBuffer sb mit der Lucene query
	 * @throws Exception
	 */
	public final IngridQuery generateQueryFromFilter(Filter filter) throws Exception
	{	
		
		LOGGER.debug("entering generateQueryFromFilter");
		
		//clear
		sb = new StringBuffer();
		not = false;
		field = "";
		operator = "";
		currQuery = null;
		
		//TODO which constructor to use?
		ingridQuery = new IngridQuery();
		
		//TODO which source: e.g. meta or map ...
		//ingridQuery.addField(new FieldQuery(IngridQuery.AND, "source:meta"));
		
		//currQuery = new IngridQuery(); 
		
		
		if ( filter == null ) return null;
		
		int type = filter.getOperationType();

	    try {
		 
		 switch (type) {
			case FilterConst.COMPARISON: {
					runComparison( filter.getComparisonOps() );
					break;
				}    				
			case FilterConst.LOGICAL: {
					runLogicalExpr( filter.getLogicalOps() );
					break;
				}  
		
			case FilterConst.SPATIAL: {
					runSpatExpr( filter.getSpatialOps() );
					break;
				}  
			case FilterConst.UNKNOWNOPERATION: {
		
				throw new CSWOperationNotSupportedException("This filter operation is not supported by this server.", "Filter");								
			}  	
				 
		}
		
	} catch(CSWOperationNotSupportedException e) {
		CSWException InGeoWCASException = (CSWException)e;
		throw new CSWOperationNotSupportedException( e.getExceptionText(), e.getLocator()  );
	  
	} catch(CSWInvalidParameterValueException e) {
		 CSWException CSWException = (CSWException)e;
		 throw new CSWInvalidParameterValueException( e.getExceptionText(), e.getLocator()  );
	  
	 } catch(CSWMissingParameterValueException e) {
		 CSWException CSWException = (CSWException)e;
	   throw new CSWMissingParameterValueException( e.getExceptionText(), e.getLocator()  );
	   
	} catch(CSWFilterException e) { 
		
		throw new CSWNoApplicableCodeException( e +  " in method FilterToIngridQuery.generateQueryFromFilter(...)" );
	   
	  //alle anderen Ausnahmen
	} catch(Exception e) {
		//Debug.debugException( e, " - " );
      
		throw new CSWNoApplicableCodeException( e +  " in method FilterToIngridQuery.generateQueryFromFilter(...)" );
		
	}
	
	
		
		
	   
	   		    		    		    		
		return ingridQuery;
		
	    
	    
		
	}
	
	
	
	/**
	 * runs a comparison expression
	 * @param ComparisonOps comp
	 */

	private void runComparison(ComparisonOps comp) throws Exception {
		
		LOGGER.debug("entering runComparison");
		
		//try {
			ComparisonOps.CompOperation co = null;
			try {
				co = comp.getCompOperation();
			} catch (CSWFilterException e) {
				System.out.println("FilterToIngridQuery Exception: " + e);
			}
			
			
			
			if ( co instanceof ComparisonOps.PropertyIsBetween ) {
				//throw new NoSuchMethodError("Operation: PropertyIsBetween is not supported");
				throw new CSWOperationNotSupportedException("The operation 'PropertyIsBetween' is not supported by this server.", "PropertyIsBetween");
				
			}
		   else if ( co instanceof ComparisonOps.PropertyIsNull) {
				//throw new NoSuchMethodError("Operation: PropertyIsNull is not supported");
				throw new CSWOperationNotSupportedException("The operation 'PropertyIsNull' is not supported by this server.", "PropertyIsNull");
			}
		   else if ( co instanceof ComparisonOps.PropertyIsEqualTo ) {
		    	
               //	operator = "=";
			   runPropertyIsEqualTo(co);	
                	    	
			}
			
		   else if ( co instanceof ComparisonOps.PropertyIsLike ) {
                //operator = "LIKE";
                
			   runPropertyIsLike(co); 
          
			}
		   else if ( co instanceof ComparisonOps.PropertyIsGreaterThan) {
				//operator = ">"; 
				          
				runPropertyIsGreaterThan(false, co);
			   	    	
			} 
		   else if ( co instanceof ComparisonOps.PropertyIsGreaterThanOrEqualTo ) {
				
                  //operator = ">=";
				runPropertyIsGreaterThan(true, co);
				   	    	
			 }
			    
			    		
		   else if ( co instanceof ComparisonOps.PropertyIsLessThan) {
              //	operator = "<";
			  runPropertyIsLessThan(false, co);  		 
					    	
			}	
		   else if ( co instanceof ComparisonOps.PropertyIsLessThanOrEqualTo ) {
              //	operator = "<=";
			    
			  runPropertyIsLessThan(true, co);  	    	
			}	
	
		/*
		} catch(Exception e) {
			//Debug.debugException( e, " - " );
		}
		*/
		
	   LOGGER.debug("exiting runComparison");	
		
	}
	
	
	/**
	 * runs a logical expression
	 * @param LogicalOps log
	 */
	private void runLogicalExpr(LogicalOps log) throws Exception
	{
		
		 
	    LOGGER.debug("entering runLogicalExpr"); 
		
		
		ClauseQuery currClauseQuery = null;
		
		LogicalOps.Logic lo = log.getLogicalOperation();
		// get first operation (if lo == Not it's the only operation)
		
		if (lo instanceof LogicalOps.Not) {
			
		    not = true;
			
			operator = "NOT";
			
		}
		else{
			//TODO add ClauseQuery ..
		    
		    sb.append(leftBracket);
		    
		    operator = lo.getOpName().toUpperCase();
		    
			    
		    currClauseQuery = new ClauseQuery(mapOperator(operator));
			
			if (currQuery == null) {
			     
			    ingridQuery.addClause((ClauseQuery) currClauseQuery);
			    
			    //currQuery = currClauseQuery;
			    
			 } else {
			     
			     currQuery.addClause((ClauseQuery) currClauseQuery);
			 }
			
			 currQuery = currClauseQuery;
		   
		}   
	
		

		 System.out.println("operator: " + operator);
		 
		
		
		
		 
		 FilterOperation fo = lo.getFirstFilterOperation();
    
      	
    
		if (fo instanceof ComparisonOps) {
			runComparison( (ComparisonOps)fo );
		}
	   else 
	    if (fo instanceof LogicalOps) {
			runLogicalExpr( (LogicalOps)fo );
		}	
	   else	
	    if (fo instanceof SpatialOps) {
			runSpatExpr( (SpatialOps)fo );
		}    		    		
		
		if (lo instanceof LogicalOps.Not) {
		    return;
		} 
		
		// get additional operations if lo != Not
		FilterOperation[] fos = lo.getAdditionalFilterOperations();
		
		for (int i = 0; i < fos.length; i++) {
			
			sb.append(" " + lo.getOpName().toUpperCase() + " ");
			
			operator = lo.getOpName().toUpperCase();
			
			if (fos[i] instanceof ComparisonOps) {
			runComparison( (ComparisonOps)fos[i] );
		     }
	          else if (fos[i] instanceof LogicalOps) {
			 runLogicalExpr( (LogicalOps)fos[i] );
		      }	
	           else	 if (fos[i] instanceof SpatialOps) {
			      runSpatExpr( (SpatialOps)fos[i] );
	        }    		 
		 } //end for
		 
		sb.append(rightBracket);
		
		LOGGER.debug("exiting runLogicalExpr"); 
	}
	
	/**
	 *
	 * @param SpatialOps spat
	 */
	private void runSpatExpr(SpatialOps spat) throws Exception
	{
		
		LOGGER.debug("entering runSpatial");
		
		
		
		if ( not ) { 
			throw new CSWOperationNotSupportedException("The operation 'Not BBOX ...' is not supported by this server.", "BBOX");
		 }	
		
		SpatialOps.Spatial spatial = spat.getSpatialOperation();
			
		// at the moment only box requests are valid
		 if ( !(spatial instanceof SpatialOps.Box) ) {
				return;
		  }
			
			
			
			
		 // get box object from the spatial object
		   SpatialOpsImpl.BoxImpl box = (SpatialOpsImpl.BoxImpl)spatial;
		
		 
          //TODO andere Form der XML BBOX?
		   //TODO Koord system testen ?
		   Element elemGeometry = box.getGeometryElement();
		   
		   //System.out.println(elemGeometry.toString());
		   
		   
		   NodeList nl = elemGeometry.getChildNodes();
		   
		  //System.out.println(nl.getLength());
		  Element elemCoords = null;
		 
		  for(int i = 0; i < nl.getLength(); i++){
	
		        
		        if (nl.item(i).getNodeType() == Node.ELEMENT_NODE){
					elemCoords = (Element)nl.item(i);
					//System.out.println(elemCoords.toString());
		        }
			  
		    
		  }
		   
		   
		   
		   
		  String strCoords = null;
		  Text ndTextCoords = null;
		  nl = elemCoords.getChildNodes();
		
		 for(int i = 0; i < nl.getLength(); i++){
	
			   if (nl.item(i).getNodeType() == Node.TEXT_NODE){
				   ndTextCoords	= (Text)nl.item(i);	
				   strCoords = ndTextCoords.getNodeValue();
				   strCoords = strCoords.trim();
				   //System.out.println( "strCoords: "+  strCoords  );
				 }
			  
		    
		 }
       
		
		
		String minxCommaMiny = null;
		String maxxCommaMaxy = null;
		String minx = null;
		String miny = null;
		
		String maxx = null;
	    String maxy = null;
	    
	    String xRange = null;
		String yRange = null;
	    
		
	    //Koordinatenpaare X/Y extrahieren
		String delim = "' '";
		
	    StringTokenizer myStringTokenizerDelimBlank = new StringTokenizer(strCoords, delim, false);
		
	
		if(myStringTokenizerDelimBlank.countTokens() != 2) {
			throw new CSWInvalidParameterValueException("Value of Element 'gml:coordinates' is not correct.", 
																										"gml:coordinates");
		}
				
		//System.out.println( "Tokens : "+  myStringTokenizerDelimBlank.countTokens());
		
		minxCommaMiny = myStringTokenizerDelimBlank.nextToken();
		maxxCommaMaxy = myStringTokenizerDelimBlank.nextToken();
		
		//System.out.println( "minxCommaMiny: "+  minxCommaMiny );
		//System.out.println( "maxCommaMaxy: "+  maxxCommaMaxy );
		
		
		
		//Einzelne Koordinaten extrahieren
		delim = "','";
		StringTokenizer myStringTokenizerDelimComma = new StringTokenizer(minxCommaMiny, delim, false);
		
		if(myStringTokenizerDelimComma.countTokens() != 2) {
			 throw new CSWInvalidParameterValueException("Value of Element 'gml:coordinates' is not correct.", 
																										 "gml:coordinates");
		 }
		
		minx = myStringTokenizerDelimComma.nextToken();
		miny = myStringTokenizerDelimComma.nextToken();
		
		//System.out.println( "minx: "+  minx  + "  miny: " + miny);
		
       
       
		myStringTokenizerDelimComma = new StringTokenizer(maxxCommaMaxy, delim, false);
       
		if(myStringTokenizerDelimComma.countTokens() != 2) {
			 throw new CSWInvalidParameterValueException("Value of Element 'gml:coordinates' is not correct.", 
																										 "gml:coordinates");
		 }
       
       
		maxx = myStringTokenizerDelimComma.nextToken();
		maxy = myStringTokenizerDelimComma.nextToken();
		
		//System.out.println( "maxx: "+  maxx  + "  maxy: " + maxy);
       
       //Zahlen umwandeln (nur geogr. Koord. z.B. WGS 84) wegen lexikographischer Suche in Lucene 
		   
	   minx = PatternTools.toLuceneIndexCoordFormat(minx);
	   miny = PatternTools.toLuceneIndexCoordFormat(miny);
	   maxx = PatternTools.toLuceneIndexCoordFormat(maxx);	
	   maxy = PatternTools.toLuceneIndexCoordFormat(maxy);	
		
		
	   xRange = "[" + minx + " TO " + maxx + "]";
	   yRange = "[" + miny + " TO " + maxy + "]";
	
		
		
		sb.append(leftBracket);	
		
		sb.append(leftBracket);
		sb.append("WEST:" +  xRange);
		sb.append(" AND ");
		sb.append("SUED:" + yRange);
		sb.append(rightBracket);			
		
		sb.append(" OR ");		

		sb.append(leftBracket);
		sb.append("WEST:" +  xRange);
		sb.append(" AND ");
		sb.append("NORD:" + yRange);
		sb.append(rightBracket);	
         	
         	
		sb.append(" OR ");		

		sb.append(leftBracket);
		sb.append("OST:" +  xRange);
		sb.append(" AND ");
		sb.append("SUED:" + yRange);
		sb.append(rightBracket); 
      
      
		sb.append(" OR ");		

		sb.append(leftBracket);
		sb.append("OST:" +  xRange);
		sb.append(" AND ");
		sb.append("NORD:" + yRange);
		sb.append(rightBracket); 
         
         
		//Fall:Suchbereich liegt innerhalb Deutschlands und Datensatz hat z.B. 
		//Koordinaten von Gesamtdeutschland
		
		sb.append(" OR ");		 
         
         		
		sb.append(leftBracket);
		
		sb.append("WEST:" +  "[" + 
		       PatternTools.toLuceneIndexCoordFormat(InitParameters.getDefault_WEST_Coord()) + 
               " TO " + minx + "]");
	    
	    sb.append(" AND ");
		
		sb.append("OST:" +  "[" + maxx + " TO " + 
		          PatternTools.toLuceneIndexCoordFormat(InitParameters.getDefault_OST_Coord()) + "]");
		
		sb.append(" AND ");
		
		sb.append("SUED:" +  "[" + 
		        PatternTools.toLuceneIndexCoordFormat(InitParameters.getDefault_SUED_Coord()) + 
               " TO " + miny + "]");
		
		sb.append(" AND ");
		
		sb.append("NORD:" +  "[" + maxy + " TO " + 
		        PatternTools.toLuceneIndexCoordFormat(InitParameters.getDefault_NORD_Coord()) + "]");
		
		sb.append(rightBracket); 		
         		
	 sb.append(rightBracket); 	    		
		
	 LOGGER.debug("exiting runSpatial");		
	 
	}
	
	
		
	
	

	/**
	 * runs a single expression
	 * @param Expression expr
	 */
	private void runExpr(Expression expr) throws Exception {
		
		LOGGER.debug("entering runExpr");
		
		Expression.BaseExpr eb = expr.getExpression();
		
		if (eb instanceof Expression.PropertyName) {
				
			String s = ((Expression.PropertyName)eb).getPropertyName();
			s = mapProperty( s );
			
		    sb.append( s );
			field = s;
			
			
			
		}
	   else
	    // if the property is a literal its value is taken
	    // to compare it to a value stored within the database
	    if (eb instanceof Expression.Literal) {	
	    	
	    	//try {
	    		Object o = null;
				try {
					o = ((Expression.Literal) eb).getLiteral();
				} catch (CSWFilterException e) {
					
					System.err.println("FilterToIngridQuery CSWFilterException: " + e);
					
					LOGGER.error("runExpr: " + e, e);
				}
	    		
	    		if (o instanceof String) {
	    			
	    			 String literal = (String)o;
	    			 literal = literal.trim();
	    			 
	    			 if (literal.startsWith("*")){
	    			 	//System.out.println("literal.startsWith('*')");
						throw new CSWNoApplicableCodeException("Leading wildcards like in literal '" + literal + "' are not supported by this server.");
	    			 }
	    			 else if(literal.startsWith("?")){
						throw new CSWNoApplicableCodeException("Leading singleChars like in literal '" + literal + "' are not supported by this server.");
	    			 }
	    			 
	    			 
					if (field.equals("DATUM")) {
						
						sb.append(PatternTools.toValidDateFormat(literal, true));
					
					}
					else{
						
						sb.append( o );
					
						//TODO how to set the operator?
						
						//TODO search in all fields: any?
						if(field.equals("any")) {
						    
						    currQuery.addTerm(new TermQuery(mapOperator(operator), o.toString()));
						}
						else {
						    
						    currQuery.addField(new FieldQuery(mapOperator(operator), field, o.toString()));
					   
						}  
						
//						ClauseQuery cq = new ClauseQuery(0);
//						cq.addField(new FieldQuery(mapOperator(operator), field + colon + o));
//						ingridQuery.addClause(cq);
						
					}
	    			
				} 
			
			   else if (o instanceof Double  || o instanceof Integer) {
			    	
                        
                      			    	
	    				if (field.equals("WEST") ||
	    			        field.equals("OST") ||
	    			        field.equals("SUED") ||
	    			        field.equals("NORD") ) {
						      
						        String coord = o.toString();
								coord = coord.trim();
								coord = PatternTools.toLuceneIndexCoordFormat(coord);
								sb.append( coord );
						
						} else {
							sb.append( o.toString() );	
						}
					 
				  }
			   
			/*
			} catch(Exception e) {
				Debug.debugException( e, " - " );
				
			}
			*/
		
	 	}
	   else
	    {
	    	throw new NoSuchMethodError( expr + " not supported at the moment" );	
	 	//TODO other expressions
	 	}
	
      LOGGER.debug("exiting runExpr");
      
	}

	

	
	/**
	 * 
	 * @param orEqualTo
	 * @param ComparisonOps.CompOperation co
	 * @throws Exception
	 */
	private void runPropertyIsGreaterThan(boolean orEqualTo, ComparisonOps.CompOperation co) 
	                                 throws Exception {
	                                 	
			LOGGER.debug("entering runPropertyIsGreaterThan");
			
			LOGGER.debug("runPropertyIsGreaterThan orEqualTo: " + orEqualTo);
		   
		    String leftBracket = leftCurlyBracket;
			String rightBracket = rightCurlyBracket;
		   
		    if(orEqualTo){
				leftBracket = leftSquareBracket;
				rightBracket = rightSquareBracket;
		    }
		    
		
		
		   if ( not ) { 
				sb.append(" NOT "); 
				not = false; 
			} 
				
			runExpr( co.getFirstExpression() );	
			     
			sb.append(colon + leftBracket); 
			
			runExpr( co.getSecondExpression() ); 
				
			//System.out.println("FilterToIngridQuery field: " + field);
				
			if (field.equals("DATUM")){
				sb.append(" TO " + dateUpperLimit + rightBracket);
			}
			else{
				sb.append(" TO " + defaultUpperLimit + rightBracket);
			 }
			
			 LOGGER.debug("exiting runPropertyIsGreaterThan");
	}
	
	
    /**
	 * 
	 * @param orEqualTo
	 * @param ComparisonOps.CompOperation co
	 * @throws Exception
	 */
    private void runPropertyIsLessThan(boolean orEqualTo, ComparisonOps.CompOperation co)
                                    throws Exception {
                                    	
           
		   LOGGER.debug("entering runPropertyIsLessThan");
			
		   LOGGER.debug("runPropertyIsLessThan orEqualTo: " + orEqualTo);
                                    	
    	
		   String leftBracket = leftCurlyBracket;
		   String rightBracket = rightCurlyBracket;
		   
		   if(orEqualTo){
			  leftBracket = leftSquareBracket;
			  rightBracket = rightSquareBracket;
		   }
    	
    	
		   if ( not ) {
			 sb.append(" NOT "); 
		   not = false; 
		   } 
				
		  runExpr( co.getFirstExpression() );
				
		 // System.out.println("FilterToIngridQuery field: " + field);
				
		  if (field.equals("DATUM")){
			  sb.append(colon + leftBracket + dateLowerLimit + " TO ");
		  }
		  else{
			  sb.append(colon + leftBracket + defaultLowerLimit + " TO ");
		  }
				 
		  runExpr( co.getSecondExpression() );
		  
		  sb.append(rightBracket);  
    	
		  LOGGER.debug("exiting runPropertyIsLessThan");
		
	}
	
	
	
   /**
 * @param ComparisonOps.CompOperation co
 * @throws Exception
 */
private void runPropertyIsEqualTo(ComparisonOps.CompOperation co)
                                  throws Exception {
                              	

			LOGGER.debug("entering runPropertyIsEqualTo");

			runExpr( co.getFirstExpression() );    	    	
		    	
			if ( not ) { 
		    	   
				int lastIndexOfBlank = sb.lastIndexOf(" ");
				   
			    if (lastIndexOfBlank == -1){
					lastIndexOfBlank = 0; 
				} 
				   
	            sb.insert(lastIndexOfBlank, " NOT ");
				   
				sb.append(colon);
				
				//TODO set in many methods?!
				operator = "NOT";
		    	
				not = false; 
				} 
			else { 
			   sb.append(colon); 
			 }
			
			runExpr( co.getSecondExpression() );                         	
                              	
			LOGGER.debug("exiting runPropertyIsEqualTo");
     }
     
     
     
     
  /**
   * 
   * @param ComparisonOps.CompOperation
   * @throws Exception
   */
private void runPropertyIsLike(ComparisonOps.CompOperation co)
								  throws Exception {
								  	
		LOGGER.debug("entering runPropertyIsLike");
								  
		 //Wildcards ersetzen mit '*'
	    // SingleChars ersetzen mit '?'
	    //Escape char beruecksichtigen
	    ComparisonOps.PropertyIsLike propIsLike = (ComparisonOps.PropertyIsLike)co;
		char charWildcard =  propIsLike.getWildCard();
		char charSingleChar =  propIsLike.getSingleChar(); 
	    char charEscapeChar =  propIsLike.getEscape();
        
       
        
		if(charWildcard == ' '){
			 throw new CSWMissingParameterValueException("Attribute 'wildCard' of Element 'PropertyIsLike' is not present.", 
																													 "wildCard");
	     }
        
		if(charSingleChar == ' '){
		   throw new CSWMissingParameterValueException("Attribute 'singleChar' of Element 'PropertyIsLike' is not present.", 
		 																									 "singleChar");
	    }
        
		if(charEscapeChar == ' '){
			 throw new CSWMissingParameterValueException("Attribute 'escapeChar'  or  'escape' of Element 'PropertyIsLike' is not present.", 
																											 "escapeChar");
	    }
        
        
       /*
        System.out.println("runPropertyIsLike: wildcard: " + charWildcard ); 
		System.out.println("runPropertyIsLike: singleChar: " + charSingleChar ); 
		System.out.println("runPropertyIsLike: escapeChar: " + charEscapeChar );
       */       
	     
	    
	   LOGGER.debug("runPropertyIsLike: wildcard: " + charWildcard ); 
	   LOGGER.debug("runPropertyIsLike: singleChar: " + charSingleChar ); 
	   LOGGER.debug("runPropertyIsLike: escapeChar: " + charEscapeChar );  
           
           
             
		ExpressionImpl.LiteralImpl literal = (ExpressionImpl.LiteralImpl)propIsLike.getLiteral();
                
		Object o = literal.getLiteral();
                
												   
		if(o instanceof String){
                 
				String literalValue = (String)o;
				//System.out.println("literalValue: " + literalValue ); 
				
				char [] charArray = literalValue.toCharArray();
				
			    literalValue = "";
				char currentChar;
				
			   //jeden Buchstaben durchgehen 	
			   for (int i= 0; i < charArray.length; i++){
			     
			     	currentChar = charArray[i];
			    
			        if(currentChar == charEscapeChar){
			        	i++; 
						currentChar = charArray[i];
						literalValue = literalValue + currentChar;
			        }
			        else if(currentChar ==  charWildcard){
						literalValue = literalValue + '*';
			        }
				    else if(currentChar == charSingleChar){
						literalValue = literalValue + '?';
				   }
				    else {
						literalValue = literalValue + currentChar;
				    }
			 
			 
			   }//end for
				
			
        // TODO why is AliasAnalyzer not always working?			
 //	//in jedem Fall ein '*' hinten anhaengen !!!
//			if (!literalValue.endsWith("*")){
//				literalValue = literalValue + "*";
//			}
			
			//System.out.println("literalValue: " + literalValue ); 
		     literal.setLiteral(literalValue);
		     propIsLike.setLiteral(literal);
	     }
             
               
			 runExpr( co.getFirstExpression() );
			
		     if ( not ) { 
		    	     
				int lastIndexOfBlank = sb.lastIndexOf(" ");
				   
			    if (lastIndexOfBlank == -1){
				lastIndexOfBlank = 0; 
				} 
				   
			    sb.insert(lastIndexOfBlank, " NOT ");
				   
				sb.append(colon);
		    	
				not = false; 
			} else { 
				sb.append(colon); 
		    }
		   
		   runExpr( co.getSecondExpression() );    	  
		    	  									  					  	
		   LOGGER.debug("exiting runPropertyIsLike");						  	
  } 
  
  
  
  
  
	
	
	/**
	 * 
	 * 
	 * @param String inprop property string
	 * @return String outprop
	 * @throws Exception
	 */

	
	private String mapProperty(String inprop) throws Exception
	{
		LOGGER.debug("entering mapProperty");
		
		LOGGER.debug("mapProperty: input property: " + inprop);
	 
		String outprop = new String("");
		
		/*
		String[] tmp = StringExtend.toArray( inprop, "/", false );
		inprop = tmp[tmp.length-1];
		*/
		//Debug.debugObject( "in property: ", inprop );
		
		if ( inprop.equals("anyText") ) {  
    	    
    	      outprop = "anyText";
    	}
		
		//if ( inprop.equals("MD_Metadata/identificationInfo/MD_DataIdentification/citation/title") ) {
		    //outprop = "TITEL";
		else if ( inprop.equals("title") ) {   
		    
		    outprop = "title";
		}
		else if ( inprop.equals("MD_Metadata/identificationInfo/MD_DataIdentification/citation/alternateTitle") ) {
			 outprop = "KURZBEZEICHNUNG";
		}

	   // else if ( inprop.equals("MD_Metadata/identificationInfo/MD_DataIdentification/extent/geographicElement/EX_GeographicDescription/geographicIdentifier/code") ) {    
		//	outprop =  "GEOGR_NAME";
		 else if ( inprop.equals("geographicIdentifier") ) {	
		     outprop =  "area";
	   }
	   
	  else if ( inprop.equals("MD_Metadata/identificationInfo/MD_DataIdentification/citation/date/date") ) {
			outprop = "DATUM";
		} 	
	    	
	  //else if ( inprop.equals("MD_Metadata/identificationInfo/MD_DataIdentification/abstract") ) {
	       //outprop = "ZUSAMMENFASSUNG";
	  else if ( inprop.equals("abstract") ) {
	      outprop = "abstract";
	  } 	
	  else if ( inprop.equals("MD_Metadata/identificationInfo/MD_DataIdentification/descriptiveKeywords/MD_Keywords/keyword") ) {
			outprop = "SCHLAGWORT";
	  }			
	
	  else if ( inprop.equals("MD_Metadata/identificationInfo/MD_DataIdentification/extent/geographicElement/EX_GeographicBoundingBox/westBoundLongitude") ) {
			 outprop =  "WEST";
		 }	
	  else if ( inprop.equals("MD_Metadata/identificationInfo/MD_DataIdentification/extent/geographicElement/EX_GeographicBoundingBox/eastBoundLongitude") ) {
					 outprop =  "OST";
	  }	 
	  else if ( inprop.equalsIgnoreCase("MD_Metadata/identificationInfo/MD_DataIdentification/extent/geographicElement/EX_GeographicBoundingBox/southBoundLongitude") ) {
			outprop =  "SUED";
	   }
	  else if ( inprop.equalsIgnoreCase("MD_Metadata/identificationInfo/MD_DataIdentification/extent/geographicElement/EX_GeographicBoundingBox/northBoundLongitude") ) {
			outprop =  "NORD";
	 }	 		
	
	
	  else if ( inprop.equals("MD_Metadata/fileIdentifier") ) {
				 outprop = "ID";
			}	
			
	else {
		throw new CSWInvalidParameterValueException("Search for PropertyName '" +inprop+ "' is not supported by this server." , "PropertyName");
	}
		
	
	  LOGGER.debug("exiting mapProperty returning string out property: " + outprop);
	  
	  return outprop;
	}
  
	
	private int mapOperator(String operator) {
	    
	    //default is AND = 0
	    int op = 0;
	    
	    if (operator.equalsIgnoreCase("AND")){
	       op = 0;
	    }else if(operator.equalsIgnoreCase("OR")){
	        op = 1;
	    }else if(operator.equalsIgnoreCase("NOT")){
         op = -1;
    }
	    
	   return op; 
	}
	
	
	
	/** 
	 * @see java.lang.Object#toString()
	 */
	public final String toString() {
	    
	    return sb.toString();
	    
	}
	
	
}
