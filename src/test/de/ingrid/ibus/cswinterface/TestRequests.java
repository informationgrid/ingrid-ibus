/*
 * Created on 03.10.2005
 *
 */
package de.ingrid.ibus.cswinterface;

/**
 * @author rschaefer
 *
 */
public final class TestRequests {
    
    
    //SOAP 1.2 requests:
    
    public static final String GETCAP1 = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        
        //ns just for SOAP 1.1
        //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
        "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
        "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        
//        " <soapenv:Header>\n" +
//        "  <shw:Hello xmlns:shw=\"http://localhost:8080/axis/services/MessageService\">\n" +
//        "    <shw:Myname>Ralf</shw:Myname>\n" +
//        "  </shw:Hello>\n" +
//        " </soapenv:Header>\n" +
         
        " <soapenv:Body>\n" +
           "<GetCapabilities service=\"CSW\" >\n" +
             "<AcceptVersions>\n" +
               "<Version>2.0.0</Version>\n" +
              "</AcceptVersions>\n" +
            "</GetCapabilities>\n" +
          " </soapenv:Body>\n" +
        "</soapenv:Envelope>";
    
    
    
    
    
    public static final String GETCAP2 = 
        
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    
    //ns just for SOAP 1.1
    //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
    "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
    "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    
//    " <soapenv:Header>\n" +
//    "  <shw:Hello xmlns:shw=\"http://localhost:8080/axis/services/MessageService\">\n" +
//    "    <shw:Myname>Ralf</shw:Myname>\n" +
//    "  </shw:Hello>\n" +
//    " </soapenv:Header>\n" +
    
    " <soapenv:Body>\n" +
       "<GetCapabilities service=\"CSW\" />\n" +  
      " </soapenv:Body>\n" +
    "</soapenv:Envelope>";
    
    
    
    
    
    public static final String GETCAPINVALID1 = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    
    //ns just for SOAP 1.1
    //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
    "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
    "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

    " <soapenv:Body>\n" +
       "<GetCap service=\"CSW\" >\n" +
         "<AcceptVersions>\n" +
            "<Version>0.0.6</Version>\n" +
            "<Version>1.0.0</Version>\n" +
          "</AcceptVersions>\n" +
        "</GetCap>\n" +
      " </soapenv:Body>\n" +
    "</soapenv:Envelope>";
    
    
    public static final String GETCAPINVALID2 = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    
    //ns just for SOAP 1.1
    //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
    "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
    "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

    " <soapenv:Body>\n" +
       "<GetCapabilities>\n" +
         "<AcceptVersions>\n" +
            "<Version>0.0.6</Version>\n" +
            "<Version>1.0.0</Version>\n" +
          "</AcceptVersions>\n" +
        "</GetCapabilities>\n" +
      " </soapenv:Body>\n" +
    "</soapenv:Envelope>";

    
    public static final String GETCAPINVALID3 = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    
    //ns just for SOAP 1.1
    //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
    "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
    "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

    " <soapenv:Body>\n" +
    "<GetCapabilities service=\"WMS\" >\n" +
         "<AcceptVersions>\n" +
           "<Version>0.0.6</Version>\n" +
           "<Version>1.0.0</Version>\n" +
          "</AcceptVersions>\n" +
        "</GetCapabilities>\n" +
      " </soapenv:Body>\n" +
    "</soapenv:Envelope>";
    
    public static final String GETCAPINVALID4 = 
   
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    
    //ns just for SOAP 1.1
    //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
    "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
    "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

    " <soapenv:Body>\n" +
    "<GetCapabilities service=\"CSW\" >\n" +
         "<AcceptVersions>\n" +
           "<Version>0.0.6</Version>\n" +
           "<Version>1.0.0</Version>\n" +
          "</AcceptVersions>\n" +
        "</GetCapabilities>\n" +
      " </soapenv:Body>\n" +
    "</soapenv:Envelope>";
    
    
    
    
    public static final String GETREC1 = 
     
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        
        //ns just for SOAP 1.1
        //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
        "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
        "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

        " <soapenv:Body>\n" +
    
     "<GetRecords maxRecords=\"20\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"  +
    "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"2\"\n" + 
    "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +
	  "<Query typeNames=\"csw:dataset\">\n" +
		"<ElementSetName typeNames=\"\">brief</ElementSetName>\n" +
		" <Constraint version=\"1.0.0\">\n" +
			"<Filter xmlns=\"http://www.opengis.net/ogc\">\n" +
				"<Or>\n" +
					"<PropertyIsLike escape=\"!\" singleChar=\"#\" wildCard=\"*\">\n" +
						"<PropertyName>abstract</PropertyName>\n" +
						 "<Literal>arte*</Literal>\n" +
					  "</PropertyIsLike>\n" +
					"<PropertyIsEqualTo>\n" +
						"<PropertyName>title</PropertyName>\n" +
						"<Literal>Test</Literal>\n" +
					"</PropertyIsEqualTo>\n" +
				"</Or>\n" +
			"</Filter>\n" +
		"</Constraint>\n" +
		"<SortBy>\n" + 
          "<SortProperty>\n" +
             "<PropertyName>title</PropertyName>\n" +
             "<SortOrder>ASC</SortOrder>\n" +
             //"<PropertyName>title</PropertyName>\n" +
             //"<SortOrder>DESC</SortOrder>\n" +
        "</SortProperty>\n" +
         "</SortBy>" +
	  "</Query>\n" +
    "</GetRecords>\n" + 
    
    " </soapenv:Body>\n" +
    "</soapenv:Envelope>";

    
    
    /**
     * Query:
     * fische NOT (froesche OR lurche)
     */
    public static final String GETREC2 = 
        
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
           "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
           "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
           "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

           " <soapenv:Body>\n" +
       
        "<GetRecords maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"  +
       "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n" + 
       "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +
   	  "<Query typeNames=\"csw:dataset\">\n" +
   		"<ElementSetName typeNames=\"\">brief</ElementSetName>\n" +
   		" <Constraint version=\"1.0.0\">\n" +
   			"<Filter xmlns=\"http://www.opengis.net/ogc\">\n" +
   				
   			     "<And>\n" +
   					
   			        "<PropertyIsEqualTo>\n" +
   						"<PropertyName>anyText</PropertyName>\n" +
   						"<Literal>fische</Literal>\n" +
   					"</PropertyIsEqualTo>\n" +
   					
	                "<Not>\n" +
					  
	                  "<Or>\n" +
	                  
					    "<PropertyIsEqualTo>\n" +
						    "<PropertyName>anyText</PropertyName>\n" +
						    "<Literal>froesche</Literal>\n" +
					     "</PropertyIsEqualTo>\n" +
					     
					     "<PropertyIsEqualTo>\n" +
						    "<PropertyName>anyText</PropertyName>\n" +
						    "<Literal>lurche</Literal>\n" +
					     "</PropertyIsEqualTo>\n" +
					  
					    "</Or>\n" +   
					
					 "</Not>\n" +
   				
			      "</And>\n" +
   			
			   "</Filter>\n" +
   		"</Constraint>\n" +
   	  "</Query>\n" +
       "</GetRecords>\n" + 
       
       " </soapenv:Body>\n" +
       "</soapenv:Envelope>";
    

    /**
     * Query:
     * fische ort:halle NOT (saale OR Hufeisensee)
     */
    public static final String GETREC3 = 
        
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
           
           //ns just for SOAP 1.1
           //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
           "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
           "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
           "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

           " <soapenv:Body>\n" +
       
        "<GetRecords maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"  +
       "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n" + 
       "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +
   	  "<Query typeNames=\"csw:dataset\">\n" +
   		"<ElementSetName typeNames=\"\">brief</ElementSetName>\n" +
   		" <Constraint version=\"1.0.0\">\n" +
   			"<Filter xmlns=\"http://www.opengis.net/ogc\">\n" +
   				"<And>\n" +
   					 "<PropertyIsEqualTo>\n" +
   						"<PropertyName>anyText</PropertyName>\n" +
   						"<Literal>fische</Literal>\n" +
   					"</PropertyIsEqualTo>\n" +
   					"<PropertyIsEqualTo>\n" +
						"<PropertyName>geographicIdentifier</PropertyName>\n" +
						"<Literal>halle</Literal>\n" +
					"</PropertyIsEqualTo>\n" +
					"<Not>\n" +
					  "<Or>\n" +
					    "<PropertyIsEqualTo>\n" +
						    "<PropertyName>anyText</PropertyName>\n" +
						    "<Literal>saale</Literal>\n" +
					     "</PropertyIsEqualTo>\n" +
					     "<PropertyIsEqualTo>\n" +
						    "<PropertyName>anyText</PropertyName>\n" +
						    "<Literal>hufeisensee</Literal>\n" +
					     "</PropertyIsEqualTo>\n" +
					   "</Or>\n" +   
					"</Not>\n" +
   				"</And>\n" +
   			"</Filter>\n" +
   		"</Constraint>\n" +
   	  "</Query>\n" +
       "</GetRecords>\n" + 
       
       " </soapenv:Body>\n" +
       "</soapenv:Envelope>";
    
    
    
    
    
    
    
    public static final String GETRECINVALID1 = 
        
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
           
           //ns just for SOAP 1.1
           //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
           "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
           "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
           "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

           " <soapenv:Body>\n" +
       
        "<GetRecord maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"  +
       "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n" + 
       "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +
   	  "<Query typeNames=\"csw:dataset\">\n" +
   		"<ElementSetName typeNames=\"\">brief</ElementSetName>\n" +
   		" <Constraint version=\"1.0.0\">\n" +
   			"<Filter xmlns=\"http://www.opengis.net/ogc\">\n" +
   				"<Or>\n" +
   					"<PropertyIsLike escape=\"!\" singleChar=\"#\" wildCard=\"*\">\n" +
   						"<PropertyName>abstract</PropertyName>\n" +
   						 "<Literal>arte*</Literal>\n" +
   					  "</PropertyIsLike>\n" +
   					"<PropertyIsEqualTo>\n" +
   						"<PropertyName>title</PropertyName>\n" +
   						"<Literal>Test</Literal>\n" +
   					"</PropertyIsEqualTo>\n" +
   				"</Or>\n" +
   			"</Filter>\n" +
   		"</Constraint>\n" +
   	  "</Query>\n" +
       "</GetRecord>\n" + 
       
       " </soapenv:Body>\n" +
       "</soapenv:Envelope>";
    
    
    public static final String GETRECINVALID2 = 
        
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
           
           //ns just for SOAP 1.1
           //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
           "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
           "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
           "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

           " <soapenv:Body>\n" +
       
        "<GetRecords maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"  +
       "		 requestId=\"csw:1\" resultType=\"results\" service=\"WMS\" startPosition=\"1\"\n" + 
       "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +
   	  "<Query typeNames=\"csw:dataset\">\n" +
   		"<ElementSetName typeNames=\"\">brief</ElementSetName>\n" +
   		" <Constraint version=\"1.0.0\">\n" +
   			"<Filter xmlns=\"http://www.opengis.net/ogc\">\n" +
   				"<Or>\n" +
   					"<PropertyIsLike escape=\"!\" singleChar=\"#\" wildCard=\"*\">\n" +
   						"<PropertyName>abstract</PropertyName>\n" +
   						 "<Literal>arte*</Literal>\n" +
   					  "</PropertyIsLike>\n" +
   					"<PropertyIsEqualTo>\n" +
   						"<PropertyName>title</PropertyName>\n" +
   						"<Literal>Test</Literal>\n" +
   					"</PropertyIsEqualTo>\n" +
   				"</Or>\n" +
   			"</Filter>\n" +
   		"</Constraint>\n" +
   	  "</Query>\n" +
       "</GetRecords>\n" + 
       
       " </soapenv:Body>\n" +
       "</soapenv:Envelope>";
    
    
    
    public static final String GETRECINVALID3 = 
        
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
           
           //ns just for SOAP 1.1
           //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
           "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
           "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
           "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

           " <soapenv:Body>\n" +
       
        "<GetRecords maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"  +
       "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n" + 
       "        version=\"1.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +
   	  "<Query typeNames=\"csw:dataset\">\n" +
   		"<ElementSetName typeNames=\"\">brief</ElementSetName>\n" +
   		" <Constraint version=\"1.0.0\">\n" +
   			"<Filter xmlns=\"http://www.opengis.net/ogc\">\n" +
   				"<Or>\n" +
   					"<PropertyIsLike escape=\"!\" singleChar=\"#\" wildCard=\"*\">\n" +
   						"<PropertyName>abstract</PropertyName>\n" +
   						 "<Literal>arte*</Literal>\n" +
   					  "</PropertyIsLike>\n" +
   					"<PropertyIsEqualTo>\n" +
   						"<PropertyName>title</PropertyName>\n" +
   						"<Literal>Test</Literal>\n" +
   					"</PropertyIsEqualTo>\n" +
   				"</Or>\n" +
   			"</Filter>\n" +
   		"</Constraint>\n" +
   	  "</Query>\n" +
       "</GetRecords>\n" + 
       
       " </soapenv:Body>\n" +
       "</soapenv:Envelope>";
    
    
    
    
    public static final String GETRECINVALID4 = 
        
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
           
           //ns just for SOAP 1.1
           //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
           "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
           "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
           "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

           " <soapenv:Body>\n" +
       
        "<GetRecords maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"  +
       "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n" + 
       "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +
   	  //"<Query typeNames=\"csw:dataset\">\n" +
   		"<ElementSetName typeNames=\"\">brief</ElementSetName>\n" +
   		" <Constraint version=\"1.0.0\">\n" +
   			"<Filter xmlns=\"http://www.opengis.net/ogc\">\n" +
   				"<Or>\n" +
   					"<PropertyIsLike escape=\"!\" singleChar=\"#\" wildCard=\"*\">\n" +
   						"<PropertyName>abstract</PropertyName>\n" +
   						 "<Literal>arte*</Literal>\n" +
   					  "</PropertyIsLike>\n" +
   					"<PropertyIsEqualTo>\n" +
   						"<PropertyName>title</PropertyName>\n" +
   						"<Literal>Test</Literal>\n" +
   					"</PropertyIsEqualTo>\n" +
   				"</Or>\n" +
   			"</Filter>\n" +
   		"</Constraint>\n" +
   	  //"</Query>\n" +
       "</GetRecords>\n" + 
       
       " </soapenv:Body>\n" +
       "</soapenv:Envelope>";
    
    
    
    
    
    public static final String GETRECINVALID5 = 
        
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
           
           //ns just for SOAP 1.1
           //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
           "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
           "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
           "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

           " <soapenv:Body>\n" +
       
        "<GetRecords maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"  +
       "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n" + 
       "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +
   	  "<Query>\n" +
   		"<ElementSetName typeNames=\"\">brief</ElementSetName>\n" +
   		" <Constraint version=\"1.0.0\">\n" +
   			"<Filter xmlns=\"http://www.opengis.net/ogc\">\n" +
   				"<Or>\n" +
   					"<PropertyIsLike escape=\"!\" singleChar=\"#\" wildCard=\"*\">\n" +
   						"<PropertyName>abstract</PropertyName>\n" +
   						 "<Literal>arte*</Literal>\n" +
   					  "</PropertyIsLike>\n" +
   					"<PropertyIsEqualTo>\n" +
   						"<PropertyName>title</PropertyName>\n" +
   						"<Literal>Test</Literal>\n" +
   					"</PropertyIsEqualTo>\n" +
   				"</Or>\n" +
   			"</Filter>\n" +
   		"</Constraint>\n" +
   	  "</Query>\n" +
       "</GetRecords>\n" + 
       
       " </soapenv:Body>\n" +
       "</soapenv:Envelope>";
    
    
    public static final String GETRECINVALID6 = 
        
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        
       
        "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
        "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

        " <soapenv:Body>\n" +
    
     "<GetRecords maxRecords=\"20\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"  +
    "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"2\"\n" + 
    "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +
	  "<Query typeNames=\"csw:dataset\">\n" +
		"<ElementSetName typeNames=\"\">brief</ElementSetName>\n" +
		" <Constraint version=\"1.0.0\">\n" +
		"</Constraint>\n" +
	  "</Query>\n" +
    "</GetRecords>\n" + 
    
    " </soapenv:Body>\n" +
    "</soapenv:Envelope>";
    
    
    
    public static final String GETRECBYID1 =  
        
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        
       
        "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
        "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

        " <soapenv:Body>\n" +
           "<GetRecordById service=\"CSW\" version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +
	           "<Id>2, 6, 8</Id>\n" +
               "<ElementSetName>summary</ElementSetName>\n" +
            "</GetRecordById>\n" +
       " </soapenv:Body>\n" +
       "</soapenv:Envelope>";
    
    
    public static final String GETRECBYID2 =  
        
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        
       
        "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
        "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

        " <soapenv:Body>\n" +
           "<GetRecordById service=\"CSW\" version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +
	           "<Id>2, 6, 8</Id>\n" +
               //"<ElementSetName>summary</ElementSetName>\n" +
            "</GetRecordById>\n" +
       " </soapenv:Body>\n" +
       "</soapenv:Envelope>";
    
    
    
    public static final String GETRECBYIDINVALID1 =  
        
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
       
        "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
        "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

        " <soapenv:Body>\n" +
           "<GetRecordById service=\"CSW\" version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +
	           //"<Id>2,6,8</Id>\n" +
               "<ElementSetName>summary</ElementSetName>\n" +
            "</GetRecordById>\n" +
       " </soapenv:Body>\n" +
       "</soapenv:Envelope>";
    
    
    
    public static final String DESCREC1 = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        
         "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
         "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
         "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

         " <soapenv:Body>\n" +
         
            "<DescribeRecord service=\"CSW\" version=\"2.0.0\" outputFormat=\"text/xml\" schemaLanguage=\"XMLSCHEMA\"/>\n" +
 	          
        " </soapenv:Body>\n" +
        "</soapenv:Envelope>";
    
    
    
    public static final String DESCREC2 = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        
         "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
         "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
         "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

         " <soapenv:Body>\n" +
         
            "<DescribeRecord service=\"CSW\" version=\"2.0.0\" />\n" +
 	          
        " </soapenv:Body>\n" +
        "</soapenv:Envelope>";
    
    
    
    //Keyword value pair (KVP) requests:
    
    public static final String KVPGETCAP1 = "REQUEST=GetCapabilities&SERVICE=CSW";
    public static final String KVPGETCAP2 = "SERVICE=CSW&REQUEST=GetCapabilities";
    public static final String KVPGETCAP3 = "REQUEST=GetCapabilities&SERVICE=CSW&ACCEPTVERSIONS=2.0.0";
    public static final String KVPGETCAP4 = "ACCEPTVERSIONS=2.0.0&REQUEST=GetCapabilities&SERVICE=CSW";
    
    
    public static final String KVPGETCAPINVALID1 = "SERVICE=CSW";
    public static final String KVPGETCAPINVALID2 = "REQUEST=GetCapabilities";
    public static final String KVPGETCAPINVALID3 = "REQUEST=GetCap&SERVICE=CSW";
    public static final String KVPGETCAPINVALID4 = "REQUEST=GetCapabilities&SERVICE=WMS";
    public static final String KVPGETCAPINVALID5 = "REQUEST=GetCapabilities&SERVICE=CSW&ACCEPTVERSIONS=1.0.0";
    
    
    public static final String UDK =  
        
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        
        //ns just for SOAP 1.1
        //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
        "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
        "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "                    xmlns:udk-query=\"http://www.umweltdatenkatalog.de/udk/query\">\n" +
        "<soapenv:Body>\n" +    
        "<udk-query:udk>\n" +
         "<udk-query:data-source-query>\n" +
            "<udk-query:general>\n" +
              "<udk-query:search-term>wasser</udk-query:search-term>\n" +
             "</udk-query:general>\n" +
         "</udk-query:data-source-query>\n" +
      "</udk-query:udk>\n" +
    " </soapenv:Body>\n" +
    "</soapenv:Envelope>";

    
}  
 
