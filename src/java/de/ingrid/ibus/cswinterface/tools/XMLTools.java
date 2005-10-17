


package de.ingrid.ibus.cswinterface.tools;

// JAXP 1.1
import org.xml.sax.*;
import org.w3c.dom.*;
//import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// JDK 1.3
import java.io.*;


//Import log4j classes.
import org.apache.log4j.Logger;

/**
 * Diese Klasse stellt Hilfsfunktionen fuer XML zur Verfuegung  
 */
public class XMLTools {
	
	
	private final static Logger LOGGER = Logger.getLogger(XMLTools.class);
    
    /**
     * creates a new and empty dom document
     */
    public static Document create() {
        javax.xml.parsers.DocumentBuilder builder = null;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return builder.newDocument();
    }
    
    /**
     * Returns the attribute value of the given node.
     *
     *
     * @param node
     * @param attrName
     *
     * @return
     *
     * @see
     */
    public static String getAttrValue(Node node, String attrName) {
        
        // get attr name and dtype
        NamedNodeMap atts = node.getAttributes();
        
        if (atts == null) {
            return null;
        }
        
        Attr a = (Attr) atts.getNamedItem(attrName);
        
        if (a != null) {
            return a.getValue();
        }
        
        return null;
    }
    
    /**
     * Parses a XML document and returns a DOM object.
     *
     *
     * @param fileName the filename of the XML file to be parsed
     *
     * @return a DOM object
     *
     * @throws IOException
     * @throws SAXException
     *
     * @see
     */
    public static Document parse(String fileName) throws IOException, SAXException {
        
        Reader reader = new InputStreamReader(new FileInputStream(fileName));
        
        StringWriter stw = new StringWriter();
        
        // remove all not writeable characters
        int c = -1;
        int cc = -1;
        while ( (c = reader.read()) > -1) {
            if (c > 31) {
                if (cc == 32 && c == 32) {} else { stw.write( c ); }
                cc = c;
            }
        }
        
        // remove not need spaces (spaces between tags)
        StringBuffer sb = new StringBuffer( stw.toString() );
        stw.close();
        String s = sb.toString();
        while (s.indexOf("> <") > -1) {
            int idx = s.indexOf("> <");
            sb.replace(idx,idx+3,"><");
            s = sb.toString();
        }
        
        Document doc = parse( new StringReader( s ) );
        
        return doc;
    }
    
    /**
     * Parses a XML document and returns a DOM object.
     *
     *
     * @param fileName the filename of the XML file to be parsed
     *
     * @return a DOM object
     *
     * @throws IOException
     * @throws SAXException
     *
     * @see
     */
    
    /*
    public static Document parse(Reader reader) throws IOException, SAXException {
        javax.xml.parsers.DocumentBuilder parser = null;
        try {
            parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
            throw new IOException("Unable to initialize DocumentBuilder: " + ex.getMessage() );
        }
        Document doc = parser.parse(new InputSource(reader));
        
        return doc;
    }
    */
    
    
	public static Document parse(Reader reader) throws IOException {
        
		    LOGGER.debug("entering parse(reader)...");
		    
		    
		    
			javax.xml.parsers.DocumentBuilder builder   = null;
        
	
        
			 try {
        	
		
				 //builder = XMLParserUtils.getXMLDocBuilder();
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
		    
			  } catch (ParserConfigurationException ex) {
				  ex.printStackTrace();
				  throw new IOException("Unable to initialize DocumentBuilder: " + ex.getMessage() );
			  }
        
         
	            
			Document doc = null;
			try {
				
				
				
			   doc = builder.parse(new InputSource(reader));
				
				
			} catch (SAXException e) {
			
				System.err.println("XMLTools:  parse( Reader ) SAXException: " + e );
				
				LOGGER.error("parse(reader) SAXException: " + e, e);
			
			} catch (IOException e) {
		
				System.err.println("XMLTools:  parse( Reader ) IOException: " + e );
			
				LOGGER.error("parse(reader) IOException: " + e, e);
              
			}
        
		      LOGGER.debug("exiting parse(reader) returning document: " + doc.getDocumentElement().toString());
			  
			  return doc;
		  }
    
    
    
  
    
    
    
    /**
     * copies one node to another node (of a different dom document).
     */
    public static Node copyNode(Node source, Node dest) {
        
        //Debug.debugMethodBegin( "XMLTools", "copyNode" );
        
        if (source.getNodeType() == Node.TEXT_NODE) {
            Text tn = dest.getOwnerDocument().createTextNode( source.getNodeValue() );
            return tn;
        } else {
            
            NamedNodeMap attr = source.getAttributes();
            
            if (attr != null) {
                for (int i = 0; i < attr.getLength(); i++) {
                    ((Element)dest).setAttribute(attr.item(i).getNodeName(),(String)attr.item(i).getNodeValue());
                }
            }
            
            NodeList list = source.getChildNodes();
            
            for (int i = 0; i < list.getLength(); i++) {
                
                if ( !(list.item(i) instanceof Text) ) {
                    Element en = dest.getOwnerDocument().createElement( list.item(i).getNodeName() );
                    if (list.item(i).getNodeValue() != null) {
                        en.setNodeValue( list.item(i).getNodeValue() );
                    }
                    
                    Node n = copyNode(list.item(i), en);
                    dest.appendChild( n );
                } else {
                    Text tn = dest.getOwnerDocument().createTextNode( list.item(i).getNodeValue() );
                    dest.appendChild( tn );
                }
                
            }
        }
        
        //Debug.debugMethodEnd();
        return dest;
        
    }
    
    /**
     * inserts a node into a dom element (of a different dom document)
     */
    public static Node insertNodeInto(Node source, Node dest) {
       
        
        Document dDoc = null;
        Document sDoc = source.getOwnerDocument();
        if (dest instanceof Document) {
            dDoc = (Document)dest;
        } else {
            dDoc = dest.getOwnerDocument();
        }
        
        if ( dDoc.equals( sDoc ) ) {
            dest.appendChild( source );
        } else {
            
            Element element = dDoc.createElement( source.getNodeName() );
            dest.appendChild( element );
            
            copyNode( source, element );
            
        }
        
      
        return dest;
        
    }
    
    
    /**
     * returns the first child element of the submitted node
     */
    public static Element getFirstElement(Node node) {
        
        
        NodeList nl = node.getChildNodes();
        Element element = null;
        
        if (nl != null && nl.getLength() > 0) {
            
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i) instanceof Element) {
                    element = (Element)nl.item(i);
                    break;
                }
            }
            
        }
        
     
        return element;
    }
    
    /**
     * removes all direct child nodes of the submitted node
     * with the also submitted name
     */
    public static Node removeNamedChildNodes(Node node, String nodeName) {
       
        
        NodeList nl = node.getChildNodes();
        
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeName().equals( nodeName )) {
                    node.removeChild( nl.item(i) );
                }
            }
        }
        
      
        return node;
    }
    
    /**
     * returns the first child element of the submitted node
     */
    public static synchronized Element getNamedChild(Node node, String name) {
        //	Debug.debugMethodBegin( "XMLTools", "getNamedChild" );
        
        NodeList nl = node.getChildNodes();
        Element element = null;
        Element return_ = null;
        
        if (nl != null && nl.getLength() > 0) {
            
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i) instanceof Element) {
                    element = (Element)nl.item(i);
                    if ( element.getNodeName().equals( name ) ) {
                        return_ = element;
                        break;
                    }
                }
            }
        }
        
        //	Debug.debugMethodEnd();
        return return_;
    }
    
    /**
     * extracts the local name from a node name
     */
    public static String toLocalName(String nodeName)
    {
        int pos = nodeName.lastIndexOf(':');
        if ( pos > -1 ) {
            nodeName = nodeName.substring( pos+1, nodeName.length() );
        }
        return nodeName;
    }
    
    
    
	
    
    
}
