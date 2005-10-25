


/*
 * date: 21.10.2005
 */
 
package de.ingrid.ibus.cswinterface.transform;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.ingrid.ibus.cswinterface.exceptions.CSWException;
import de.ingrid.ibus.cswinterface.exceptions.CSWInvalidParameterValueException;
import de.ingrid.ibus.cswinterface.exceptions.CSWMissingParameterValueException;
import de.ingrid.ibus.cswinterface.exceptions.CSWNoApplicableCodeException;
import de.ingrid.ibus.cswinterface.exceptions.CSWOperationNotSupportedException;
import de.ingrid.ibus.cswinterface.exceptions.CSWFilterException;


/**
*
* This class generates an OGC filter encoding into a
* string for an ingrid query 
*/ 
public class FilterToIngridQueryString {
	
	/**
	 * Comment for <code>sb</code>
	 */
	private StringBuffer sb = new StringBuffer();
	
	
	/**
	 * Comment for <code>not</code>
	 */
	private boolean not = false;
	
	/**
	 * the current field
	 */
	private String field = "";	
	
    
	/**
	 * Comment for <code>colon</code>
	 */
	private final String colon = ":";
	
	/**
	 * Comment for <code>greaterThan</code>
	 */
	private final String greaterThan = ">";
	
    /**
     * Comment for <code>greaterThanOrEqualTo</code>
     */
    private final String greaterThanOrEqualTo = ">=";
	
	/**
	 * Comment for <code>lessThan</code>
	 */
	private final String lessThan = "<";
	
	/**
	 * Comment for <code>lessThanOrEqualTo</code>
	 */
	private final String lessThanOrEqualTo = "<=";
	
	
	/**
	 * Comment for <code>leftBracket</code>
	 */
	private final String leftBracket = "(";
	
    /**
     * Comment for <code>rightBracket</code>
     */
    private final String rightBracket = ")";
	
	
   
	/**
	 * Comment for <code>logger</code>
	 */
	private final Logger logger = Logger.getLogger(this.getClass());
   
	/**
	 * constructor.
	 */
	public FilterToIngridQueryString() {
		
		logger.debug("constructor");
			
	}
			
   
   


	/**
	 * does the generation of the query string
	 * @param filter Filter
	 * @return StringBuffer sb the ingrid query string
	 * @throws Exception e
	 */
	public final String generateQueryFromFilter(final Filter filter) throws Exception {	
		
	 
		logger.debug("entering generateQueryFromFilter");
		
		if (filter == null) { 
		   return null; 
		 }
		
		int type = filter.getOperationType();

	    try {
		 
		 switch (type) {
		 
			case FilterConst.COMPARISON: 
			    
					runComparison(filter.getComparisonOps());
					break;
			   				
			case FilterConst.LOGICAL:
			    
					runLogicalExpr(filter.getLogicalOps());
					break;
			 
			case FilterConst.SPATIAL:
			    
					runSpatExpr(filter.getSpatialOps());
					break;
				
			case FilterConst.UNKNOWNOPERATION: 
		
				throw new CSWOperationNotSupportedException("This filter operation is not supported by this server.", 
				                                          "Filter");
			 default:
			     
			     throw new CSWOperationNotSupportedException("This filter operation is not supported by this server.", 
                                                            "Filter");
			       	 
		}
		
	} catch (CSWOperationNotSupportedException e) {
	    
		CSWException inGeoWCASException = (CSWException) e;
		
		throw new CSWOperationNotSupportedException(e.getExceptionText(), e.getLocator());
	  
	} catch (CSWInvalidParameterValueException e) {
	    
		 CSWException cswException = (CSWException) e;
		
		 throw new CSWInvalidParameterValueException(e.getExceptionText(), e.getLocator());
	  
	 } catch (CSWMissingParameterValueException e) {
	     
	     CSWException cswException = (CSWException) e;
	    
		 throw new CSWMissingParameterValueException(e.getExceptionText(), e.getLocator());
	   
	} catch (CSWFilterException e) { 
		
		throw new CSWNoApplicableCodeException(e +  
		         " in method FilterToIngridQueryString.generateQueryFromFilter(...)");
	   
	  //catch all other exceptions
	} catch (Exception e) {
		
		throw new CSWNoApplicableCodeException(e +  
		        " in method FilterToIngridQueryString.generateQueryFromFilter(...)");
		
	}
	
	
		String ingridQueryString = sb.toString();
		
	    logger.debug("exiting generateQueryFromFilter returning string (ingrid query): " + ingridQueryString);
	   		    		    		    		
		return ingridQueryString;
	}
	
	
	
	/**
	 * runs a comparison expression
	 * @param comp ComparisonOps
	 * @throws Exception e
	 */

	private void runComparison(final ComparisonOps comp) throws Exception {
		
		   logger.debug("entering runComparison");
	
			ComparisonOps.CompOperation co = null;
			
			try {
			    
				co = comp.getCompOperation();
			
			} catch (CSWFilterException e) {
				
			    System.out.println("FilterToIngridQueryString Exception: " + e);
				
				throw e;
			}
			
			
			
		  if (co instanceof ComparisonOps.PropertyIsBetween) {
			
				throw new CSWOperationNotSupportedException("The operation 'PropertyIsBetween' " +
						  "is not supported by this server.", 
				          "PropertyIsBetween");
				
			} else if (co instanceof ComparisonOps.PropertyIsNull) {
				
				throw new CSWOperationNotSupportedException("The operation 'PropertyIsNull' " +
						   "is not supported by this server.", 
				           "PropertyIsNull");
			
			} else if (co instanceof ComparisonOps.PropertyIsEqualTo) {
		    	
               //operator = "=";
			   
			    runPropertyIsEqualTo(co);	
                	    	
			} else if (co instanceof ComparisonOps.PropertyIsLike) {
                
			    //operator = "LIKE";
                
			   runPropertyIsLike(co); 
          
			} else if (co instanceof ComparisonOps.PropertyIsGreaterThan) {
				
			    //operator = ">"; 
				          
				runPropertyIsGreaterThan(false, co);
			   	    	
			} else if (co instanceof ComparisonOps.PropertyIsGreaterThanOrEqualTo) {
				
                  //operator = ">=";
				runPropertyIsGreaterThan(true, co);
				   	    	
			} else if (co instanceof ComparisonOps.PropertyIsLessThan) {
              
			    //operator = "<";
			  runPropertyIsLessThan(false, co);  		 
					    	
			} else if (co instanceof ComparisonOps.PropertyIsLessThanOrEqualTo) {
              //operator = "<=";
			    
			  runPropertyIsLessThan(true, co);  	    	
			
		   }	

	      logger.debug("exiting runComparison");	
		
	}
	
	
	/**
	 * runs a logical expression
	 * @param log LogicalOps
	 * @throws Exception e
	 */
	private void runLogicalExpr(final LogicalOps log) throws Exception {
		
		logger.debug("entering runLogicalExpr"); 
		 
        //get first operation (if lo == Not it's the only operation)
		LogicalOps.Logic lo = log.getLogicalOperation();
		
		if (lo instanceof LogicalOps.Not) {
			
		    not = true;
		    
		    //delete operators close to the next NOT
		    sb = deletePreOperator(sb);
		    
		    sb.append(" NOT ");
		    
		    //not = false;  
		
		} else {
		    
			sb.append(leftBracket);
		
		}
		
	
		FilterOperation fo = lo.getFirstFilterOperation();
    
      
		  if (fo instanceof ComparisonOps) {
			
		    runComparison((ComparisonOps) fo);
		
		} else if (fo instanceof LogicalOps) {
			
		    runLogicalExpr((LogicalOps) fo);
		
		} else if (fo instanceof SpatialOps) {
			
		    runSpatExpr((SpatialOps) fo);
		
		}    		    		
		
		
		if (lo instanceof LogicalOps.Not) {
		    return;
		} 
		
		// get additional operations if lo != Not
		FilterOperation[] fos = lo.getAdditionalFilterOperations();
		
		for (int i = 0; i < fos.length; i++) {
			
			  //append the logigal operator  
		    sb.append(" " + lo.getOpName().toUpperCase() + " ");
				
		    
			if (fos[i] instanceof ComparisonOps) {
			  
			    runComparison((ComparisonOps) fos[i]);
		     
			} else if (fos[i] instanceof LogicalOps) {
			  
			    runLogicalExpr((LogicalOps) fos[i]);
		     
			} else if (fos[i] instanceof SpatialOps) {
			    
			    runSpatExpr((SpatialOps) fos[i]);
	        
			}
			
		 } //end for
		 
		sb.append(rightBracket);
		
		logger.debug("exiting runLogicalExpr"); 
	}
	
	/**
	 * runs a spatial expression like a bounding box 
	 * @param spat SpatialOps
	 * @throws Exception e
	 */
	private void runSpatExpr(final SpatialOps spat) throws Exception {
		
		logger.debug("entering runSpatExpr");
			
		if (not) {
		   throw new CSWOperationNotSupportedException("The operation 'Not BBOX ...' is not supported by this server.", 
		                                                 "BBOX");
		}	
		
		SpatialOps.Spatial spatial = spat.getSpatialOperation();
			
		// at the moment only box requests are valid
		 if (!(spatial instanceof SpatialOps.Box)) {
				return;
		  }
				
		 // get box object from the spatial object
		   SpatialOpsImpl.BoxImpl box = (SpatialOpsImpl.BoxImpl) spatial;
	
          //TODO andere Form der XML BBOX?
		   //TODO Koord system testen ?
		   Element elemGeometry = box.getGeometryElement();
		   //System.out.println(elemGeometry.toString());
		   
		   NodeList nl = elemGeometry.getChildNodes();
		   
		  //System.out.println(nl.getLength());
		  Element elemCoords = null;
		 
		  for (int i = 0; i < nl.getLength(); i++) {
	        
		        if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
					 elemCoords = (Element) nl.item(i);
					//System.out.println(elemCoords.toString());
		        }
		
		  }
		   	   
		  String strCoords = null;
		  Text ndTextCoords = null;
		  nl = elemCoords.getChildNodes();
		
		 for (int i = 0; i < nl.getLength(); i++) {
	
			   if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
				   ndTextCoords	= (Text) nl.item(i);	
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
	     
	    //Koordinatenpaare X/Y extrahieren
		String delim = "' '";
		
	    StringTokenizer myStringTokenizerDelimBlank = new StringTokenizer(strCoords, delim, false);
		
		if (myStringTokenizerDelimBlank.countTokens() != 2) {
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
		
		if (myStringTokenizerDelimComma.countTokens() != 2) {
			 throw new CSWInvalidParameterValueException("Value of Element 'gml:coordinates' is not correct.", 
																		       "gml:coordinates");
		 }
		
		minx = myStringTokenizerDelimComma.nextToken();
		miny = myStringTokenizerDelimComma.nextToken();
		
		//System.out.println( "minx: "+  minx  + "  miny: " + miny);
		
       
       
		myStringTokenizerDelimComma = new StringTokenizer(maxxCommaMaxy, delim, false);
       
		if (myStringTokenizerDelimComma.countTokens() != 2) {
			 throw new CSWInvalidParameterValueException("Value of Element 'gml:coordinates' is not correct.", 
																                   "gml:coordinates");
		 }
       
      	maxx = myStringTokenizerDelimComma.nextToken();
		maxy = myStringTokenizerDelimComma.nextToken();
		
		//System.out.println( "maxx: "+  maxx  + "  maxy: " + maxy);
     
		//test coordinates
		try {
		    
		   Double.parseDouble(minx);
		   Double.parseDouble(miny);
		   Double.parseDouble(maxx);
		   Double.parseDouble(maxy);
		
		}	catch (NumberFormatException nfe) {
		    
		    
		    //System.err.println("NumberFormatException: " + nfe);
		    
		    throw new CSWInvalidParameterValueException("Value of Element 'gml:coordinates' is not correct: " + 
		                                                nfe, 
                                                       "gml:coordinates");
		    
		}
		
		sb.append(leftBracket);	
		
		sb.append("x1:" + minx);
		sb.append(" ");
		sb.append("x2:" + maxx);
		sb.append(" ");
		sb.append("y1:" + miny);
		sb.append(" ");
		sb.append("y2:" + maxy);
		
		sb.append(" ");
		sb.append("coord:include");
		
		sb.append(rightBracket); 

	   logger.debug("exiting runSpatExpr");		
	 
	}
	
	
		
	
	

	/**
	 * runs a single expression e.g. a property
	 * @param expr Expression
	 * @throws Exception e 
	 */
	private void runExpr(final Expression expr) throws Exception {
		
		logger.debug("entering runExpr");
		
		Expression.BaseExpr eb = expr.getExpression();
		
		if (eb instanceof Expression.PropertyName) {
				
			String s = ((Expression.PropertyName) eb).getPropertyName();
			
			s = mapProperty(s);
			
		    sb.append(s);
		    
			field = s;
			
		
		} else
	    // if the property is a literal its value is taken
	    // to compare it to a value stored within the 'database'.
	      if (eb instanceof Expression.Literal) {	
	    	
	    
	    		Object obj = null;
	    		
				try {
					obj = ((Expression.Literal) eb).getLiteral();
				
				} catch (CSWFilterException e) {
					
					System.err.println("FilterToIngridQueryString CSWFilterException: " + e);
					
					logger.error("runExpr: " + e, e);
				   
					throw e;
				}
	    		
	    		if (obj instanceof String) {
	    			
	    			 String literal = (String) obj;
	    			 
	    			 literal = literal.trim();
	    			 
	    			 if (literal.startsWith("*")) {
	    			 	//System.out.println("literal.startsWith('*')");
						
	    			     throw new CSWNoApplicableCodeException("Leading wildcards like in literal '" + 
						                                 literal + "' are not supported by this server.");
	    			 
	    			 } else if (literal.startsWith("?")) {
						
	    			     throw new CSWNoApplicableCodeException("Leading singleChars like in literal '" + 
						                     literal + "' are not supported by this server.");
	    			 }
	    			 
	    			 
//					if (field.equals("DATUM")) {
//						
//					     sb.append(PatternTools.toValidDateFormat(literal, true));
//		
//					} else{
						
						sb.append(obj);
					
					//}
	    			
				} else if (obj instanceof Double  || obj instanceof Integer) {
			    	
           		    	
	    				if (field.equals("WEST") ||
	    			        field.equals("OST") ||
	    			        field.equals("SUED") ||
	    			        field.equals("NORD")) {
						      
						        String coord = obj.toString();
								
						        coord = coord.trim();
								
								sb.append(coord);
						
						} else {
						    
							sb.append(obj.toString());	
						
						}
					 
				  }
			   

	 	} else {
	    	throw new NoSuchMethodError(expr + " not supported at the moment");	
	 	//TODO other expressions
	 	}
	
      logger.debug("exiting runExpr");
      
	}

	

	
	/**
	 * runs a PropertyIsGreaterThan or
	 * a PropertyIsGreaterThanOrEqual to if orEqualTo is true
	 * @param orEqualTo boolean
	 * @param co ComparisonOps.CompOperation
	 * @throws Exception e
	 */
	private void runPropertyIsGreaterThan(final boolean orEqualTo, final ComparisonOps.CompOperation co) 
	                                 throws Exception {
	                                 	
			logger.debug("entering runPropertyIsGreaterThan");
			
			logger.debug("runPropertyIsGreaterThan orEqualTo: " + orEqualTo);
		   
//		    String leftBracket = leftCurlyBracket;
//			String rightBracket = rightCurlyBracket;
//		   
//		    if(orEqualTo){
//			
//		        leftBracket = leftSquareBracket;
//				rightBracket = rightSquareBracket;
//		    
//		    }
		    
		
		
		   if (not) { 
				//sb.append(" NOT ");
		      
				not = false; 
			} 
				
			runExpr(co.getFirstExpression());	
			     
			sb.append(this.colon); 
			
			if (orEqualTo) {
					
			    sb.append(this.greaterThanOrEqualTo); 
			    
		    } else {
		        
		        sb.append(this.greaterThan);
		    }
			
			runExpr(co.getSecondExpression()); 
				
			//System.out.println("FilterToIngridQueryString field: " + field);
				
//			if (field.equals("DATUM")) {
//			    
//				sb.append(" TO " + dateUpperLimit + rightBracket);
//			
//			}
//			else {
//			    
//				sb.append(" TO " + defaultUpperLimit + rightBracket);
//			 
//			}
			
			 logger.debug("exiting runPropertyIsGreaterThan");
	}
	
	
	/**
	 * runs a PropertyIsLessThan or
	 * a PropertyIsLessThanOrEqual to if orEqualTo is true
	 * @param orEqualTo boolean
	 * @param co ComparisonOps.CompOperation
	 * @throws Exception e
	 */
    private void runPropertyIsLessThan(final boolean orEqualTo, final ComparisonOps.CompOperation co)
                                    throws Exception {
                                    	
           
		   logger.debug("entering runPropertyIsLessThan");
			
		   logger.debug("runPropertyIsLessThan orEqualTo: " + orEqualTo);
                                    	
       
		   if (not) {
			 
            //sb.append(" NOT ");
		    
		      not = false; 
		   } 
				
		  runExpr(co.getFirstExpression());
				
		 // System.out.println("FilterToIngridQueryString field: " + field);
				
//		  if (field.equals("DATUM")){
//			  sb.append(colon + leftBracket + dateLowerLimit + " TO ");
//		  }
//		  else{
//			  sb.append(colon + leftBracket + defaultLowerLimit + " TO ");
//		  }
				 
		  
		  sb.append(this.colon);
		  
		  
		  if (orEqualTo) {
				
		    sb.append(this.lessThanOrEqualTo); 
		    
	    } else {
	        
	        sb.append(this.lessThan);
	    }
		  
		  runExpr(co.getSecondExpression());
		  
		   
		  logger.debug("exiting runPropertyIsLessThan");
		
	}
	
	
	
 /**
  * runs a PropertyIsEqualTo
 * @param co ComparisonOps.CompOperation
 * @throws Exception e
 */
private void runPropertyIsEqualTo(final ComparisonOps.CompOperation co)
                                  throws Exception {
                              	

			logger.debug("entering runPropertyIsEqualTo");

			
			if (not) { 
			    
			  //sb.append(" NOT ");
			      
				not = false; 
				
			  } 
			
			runExpr(co.getFirstExpression());    	    	
		    
			sb.append(colon); 
			
			runExpr(co.getSecondExpression());                         	
                              	
			logger.debug("exiting runPropertyIsEqualTo");
     }
     
     
     
     
  /**
   * runs a PropertyIsLike
   * @param co ComparisonOps.CompOperation
   * @throws Exception e
   */
private void runPropertyIsLike(final ComparisonOps.CompOperation co)
								  throws Exception {
								  	
		logger.debug("entering runPropertyIsLike");
		
										  
		 //Wildcards ersetzen mit '*'
	    // SingleChars ersetzen mit '?'
	    //Escape char beruecksichtigen
	    ComparisonOps.PropertyIsLike propIsLike = (ComparisonOps.PropertyIsLike) co;
		char charWildcard =  propIsLike.getWildCard();
		char charSingleChar =  propIsLike.getSingleChar(); 
	    char charEscapeChar =  propIsLike.getEscape();
        
       
        
		if (charWildcard == ' ') {
		    
			 throw new CSWMissingParameterValueException("Attribute 'wildCard' of Element " +
			 		                                  "'PropertyIsLike' is not present.", 
														"wildCard");
	     }
        
		if (charSingleChar == ' ') {
		   
		    throw new CSWMissingParameterValueException("Attribute 'singleChar' of Element " +
		    		                                     "'PropertyIsLike' is not present.", 
		 												"singleChar");
	    }
        
		if (charEscapeChar == ' ') {
			 
		     throw new CSWMissingParameterValueException("Attribute 'escapeChar'  or  'escape' " +
		     		                                       "of Element 'PropertyIsLike' is not present.", 
															"escapeChar");
	    }
        
        
       /*
        System.out.println("runPropertyIsLike: wildcard: " + charWildcard ); 
		System.out.println("runPropertyIsLike: singleChar: " + charSingleChar ); 
		System.out.println("runPropertyIsLike: escapeChar: " + charEscapeChar );
       */       
	     
	    
	   logger.debug("runPropertyIsLike: wildcard: " + charWildcard); 
	   logger.debug("runPropertyIsLike: singleChar: " + charSingleChar); 
	   logger.debug("runPropertyIsLike: escapeChar: " + charEscapeChar);  
           
           
             
		ExpressionImpl.LiteralImpl literal = (ExpressionImpl.LiteralImpl) propIsLike.getLiteral();
                
		Object obj = literal.getLiteral();
                
												   
		if (obj instanceof String) {
                 
				String literalValue = (String) obj;
				//System.out.println("literalValue: " + literalValue ); 
				
				char [] charArray = literalValue.toCharArray();
				
			    literalValue = "";
				char currentChar;
				
			   //jeden Buchstaben durchgehen 	
			   for (int i = 0; i < charArray.length; i++) {
			     
			     	currentChar = charArray[i];
			    
			        if (currentChar == charEscapeChar) {
			        	
			            i++; 
						currentChar = charArray[i];
						literalValue = literalValue + currentChar;
			        
			        } else if (currentChar ==  charWildcard) {
						
			            literalValue = literalValue + '*';
			        
			        } else if (currentChar == charSingleChar) {
						
			            literalValue = literalValue + '?';
				   
			        } else {
						
			            literalValue = literalValue + currentChar;
				    }
			 
			 
			   } //end for
				
					
			//System.out.println("literalValue: " + literalValue ); 
		     literal.setLiteral(literalValue);
		     
		     propIsLike.setLiteral(literal);
	     }
             
               
		 if (not) { 
		     
            //sb.append(" NOT ");
		    
			not = false; 
			
		  } 
		
		   runExpr(co.getFirstExpression());
			

		   sb.append(colon);   
		   
		   runExpr(co.getSecondExpression());    	  
		    	  									  					  	
		  logger.debug("exiting runPropertyIsLike");						  	
  
  } 
  
  
  
 
 /**
  * deletes operators like AND / OR 
  * which occur directly before a NOT operator.
  * Cause: some query parsers can't handle this.
 * @param stringBuffer StringBuffer
 * @return stringBuffer StringBuffer
 */
public final StringBuffer deletePreOperator(final StringBuffer stringBuffer) {
     
    
     //System.out.println("deletePreOperator stringBuffer: " + stringBuffer.toString());
    
      //the length of 'AND' plus a blank 
      final int andLength = 4;
//    the length of 'OR' plus a blank 
      final int orLength = 3;
      
       int sbLength = stringBuffer.length();
     
    
       int lastIndexOfAND = stringBuffer.lastIndexOf("AND");
       
        
       int lastIndexOfOR = stringBuffer.lastIndexOf("OR");
       
    
       //System.out.println("sbLength: " + sbLength);
       
       
       //System.out.println("lastIndexOfAND: " + lastIndexOfAND);
      
       if (lastIndexOfAND != -1 && lastIndexOfAND + andLength == sbLength) {
           
           stringBuffer.delete(lastIndexOfAND, sbLength);
       }
       
       
       //System.out.println("lastIndexOfOR: " + lastIndexOfOR);
 		
       if (lastIndexOfOR != -1 && lastIndexOfOR + orLength == sbLength) {
           
           stringBuffer.delete(lastIndexOfOR, sbLength);
       }
       
 		
     return stringBuffer;
       
  
 }


    /**
     * maps the OGC property names
     * to the internal names (ingrid)
     * 
     * @param inprop String property string
     * @return outprop String 
     * @throws Exception e
     */
    
    
    private String mapProperty(final String inprop) throws Exception {
        
    	logger.debug("entering mapProperty");
    	logger.debug("mapProperty: input property: " + inprop);
    
    	String outprop = new String("");
    	
    	/*
    	String[] tmp = StringExtend.toArray( inprop, "/", false );
    	inprop = tmp[tmp.length-1];
    	*/
       //TODO check all names
    	
    	//all text fields
    	if (inprop.equals("anyText")) {  
    	    outprop = "anyText";
       // MD_Metadata/identificationInfo/MD_DataIdentification/citation/title
    	} else if (inprop.equals("title")) { 
    	    outprop = "title";
    	 //  metadata language
    	} else if (inprop.equals("language")) {
    		outprop = "language";		
        //  MD_Metadata/identificationInfo/MD_DataIdentification/citation/alternateTitle
    	} else if (inprop.equals("alternateTitle")) { 
             outprop = "alternateTitle";
         //responsible party organisation name
    	} else if (inprop.equals("organisationName")) {
            outprop = "organisationName";
         //do security constraints (resource constraints) exist (boolean) 
    	} else if (inprop.equals("hasSecurityConstraints")) { 
            outprop = "hasSecurityConstraints";
         // metadata hierarchy level name  
    	} else if (inprop.equals("hierarchyLevelName")) { 
              outprop = "hierarchyLevelName";
    	
           // metadata parent identifier  
    	} else if (inprop.equals("parentIdentifier")) { 
              outprop = "parentIdentifier";      
       //FIXME dates       
      //MD_Metadata/identificationInfo/MD_DataIdentification/citation/date/date
       //TODO date type: revision 
    	} else if (inprop.equals("modified")) {
    	       outprop = "t0";		
 	
     // TODO date type: creation
    	} else if (inprop.equals("creationDate")) {
    	    	outprop = "t0";		
    
       //begin
    	} else if (inprop.equals("tempExtent_begin")) {
   		      outprop = "t1";	
       	
       //end
    	} else if (inprop.equals("tempExtent_end")) {
     		 outprop = "t2";		
         	
       //MD_Metadata/identificationInfo/MD_DataIdentification/abstract
       } else if (inprop.equals("abstract")) {
          outprop = "abstract";
       
       //distribution format
       } else if (inprop.equals("format")) {
          outprop = "format";
       	
      //citation identifier
       } else if (inprop.equals("identifier")) {
          outprop = "identifier";
       
       //MD_Metadata/identificationInfo/MD_DataIdentification/descriptiveKeywords/MD_Keywords/keyword
       } else if (inprop.equals("subject")) {
    	     outprop = "subject";
    	//keyword type     
       } else if (inprop.equals("keywordType")) {
  	     outprop = "keywordType";				
    	     
       // topic category     
       } else if (inprop.equals("topicCategory")) {
  	     outprop = "topicCategory";  
  	     
        // spatial resolution     
       } else if (inprop.equals("spatialResolution")) {
  	     outprop = "spatialResolution";
  	     
  	     //denominator (integer)
       } else if (inprop.equals("denominator")) {
    	     outprop = "denominator";
    	     
    	   //distance value (float)
       } else if (inprop.equals("distanceValue")) {
    	     outprop = "distanceValue";     
    	     
    	    //distance uom (measure: meter)
       } else if (inprop.equals("distanceUOM")) {
    	     outprop = "distanceUOM";       
    	     
  	     
       //spatial representation type
       } else if (inprop.equals("type")) {
    		outprop = "type";		
       
    	//reference system
       } else if (inprop.equals("crs")) {
    		outprop = "crs";		
  
     // MD_Metadata/identificationInfo/MD_DataIdentification/extent/geographicElement/
    //EX_GeographicDescription/geographicIdentifier/code
       } else if (inprop.equals("geographicDescriptionCode")) {	
    	     outprop =  "area";
       
       // MD_Metadata/identificationInfo/MD_DataIdentification/extent/geographicElement/
       //EX_GeographicBoundingBox/westBoundLongitude
       } else if (inprop.equals("westBoundLongitude")) {
    		outprop = "WEST";		
       
  
      //MD_Metadata/identificationInfo/MD_DataIdentification/extent/geographicElement/
       //EX_GeographicBoundingBox/eastBoundLongitude
       } else if (inprop.equals("eastBoundLongitude")) {
        	 outprop =  "OST";			 			 
    
      //MD_Metadata/identificationInfo/MD_DataIdentification/extent/geographicElement/
       //EX_GeographicBoundingBox/southBoundLongitude
    
       } else if (inprop.equalsIgnoreCase("southBoundLongitude")) {
        	outprop =  "SUED";	
      
      //MD_Metadata/identificationInfo/MD_DataIdentification/extent/geographicElement/
       //EX_GeographicBoundingBox/northBoundLongitude
       } else if (inprop.equalsIgnoreCase("northBoundLongitude")) {
        		outprop =  "NORD";		
    		
      
      //MD_Metadata/fileIdentifier
       } else if (inprop.equals("fileIdentifier")) {   
    			 outprop = "ID";
    		
      } else {
    	
          throw new CSWInvalidParameterValueException("Search for PropertyName '" + inprop + 
    	                                            "' is not supported by this server." , 
    	                                            "PropertyName");
    }
    	
    
      logger.debug("exiting mapProperty returning string out property: " + outprop);
    
      return outprop;
    }
	
	
}
