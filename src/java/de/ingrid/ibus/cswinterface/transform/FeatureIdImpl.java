
package de.ingrid.ibus.cswinterface.transform;

import org.w3c.dom.*;

import de.ingrid.ibus.cswinterface.tools.XMLTools;


/**
*
* The <FeatureId> element is used to encode the unique identifier for 
* any feature instance. Within a filter expression, the <FeatureId> 
* is used as a reference to a particular feature instance.
*
*/
public class FeatureIdImpl implements FeatureId {
	
	private Element element = null;
	
	public FeatureIdImpl(Element element) {
		this.element = element;
	}
	
   /**
    * creates a new feature id tag
    */	
	public static FeatureId createFeatureId(Document doc, String featureId)
	{
		//Debug.debugMethodBegin( "FeatureIdImpl", "createFeatureId" );
		
		FeatureId fid = new FeatureIdImpl( doc.createElement("FeatureId") );
		fid.setFeatureId( featureId );
		
		//Debug.debugMethodEnd();
		return fid;
	}
	
	public Element getAsElement()
	{
		return element;
	}
	
   	
   /**
    * returns the feature id. A feature id is build from its feature type
    * name and its id seperated by a ".". e.g. Road.A565
    */	
	public String getFeatureId()
	{
		return XMLTools.getAttrValue( element, "fid" );
	}
	
   /**
    * @see #getFeatureId
    */	
	public void setFeatureId(String id)
	{
		element.setAttribute( "fid", id );
	}
	
	/**
    * returns the name of the feature type the feature belongs to.
    * if no feature type is defined <tt>null</tt> will be returned.
    */	
	public String extractFeatureTypeName()
	{
		//Debug.debugMethodBegin( this, "extractFeatureTypeName" );				
		
		String s = getFeatureId();
		
		if ( s.lastIndexOf(".") > -1 ) {			
			s = s.substring( 0, s.lastIndexOf(".") );
		}
		
		//Debug.debugMethodEnd();
		return s;
		
	}
	
	/**
    * returns the id of the feature. '*' is used as wild card. which
    * means each feature of the feature type should be processed.
    */	
	public String extractId()
	{
		//Debug.debugMethodBegin( this, "extractId" );
		
		String s = getFeatureId();
		
		if ( s.lastIndexOf(".") > -1 ) {
			s = s.substring( s.lastIndexOf(".") + 1 );
		}
		
		//Debug.debugMethodEnd();
		return s;
	}
	
}
