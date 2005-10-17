/*
 * Created on 04.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface.analyse;

import javax.xml.soap.SOAPElement;

/**
 * @author rschaefer
 */
public final class SessionParameters {
    
    
 
    
    /**
     * Comment 
     */
    public  static final int MAXRECORDS = 10;
    
    
    /**
     * Comment 
     */
    public  static final int STARTPOSITION = 1;
    
    
    
    
    /**
     * Comment for <code>maxRecords</code>
     */
    private  int maxRecords = MAXRECORDS;
    
    
    
    /**
     * Comment for <code>resultType</code>
     */
    private  String resultType = "hits";
    
    
    /**
     * Comment for <code>outputFormat</code>
     */
    private  String outputFormat = "text/xml"; 
    
    
    /**
     * Comment
     */
    private  String outputSchema = "csw:ogccore"; 
    
    
    /**
     * Comment
     */
    private  String typeNames = null; 
    
    
    /**
     * Comment for <code>startPosition</code>
     */
    private  int startPosition = STARTPOSITION;
    
    
   
    
    /**
     * the sorting: default is by 
     * metadata element name (ascending)
     */
    private  String sortBy = "metadata_element_name:A";
    

    
    /**
     * soapElementFilter
     */
    private SOAPElement soapElementFilter = null;
   
    
    
    
    
    
    //also needed for GetRecordById requests
    
    /**
     * TODO elementSetName default value for GetRecordById should be summary
     */
    private  String elementSetName = "full";
    
    
    
    /**
     * Comment for ids of GetRecById      
     * 
     */
    
    private String ids = null;
    
    
    
    
    /**
     * needed??
     */
    public  void clear() {
        
        maxRecords = MAXRECORDS;
        resultType = "hits";
        outputFormat = "text/xml";
        outputSchema = "csw:ogccore";
        typeNames = null; 
        startPosition = STARTPOSITION;
        sortBy = "metadata_element_name:A";
        soapElementFilter = null;
        elementSetName = "full";
        ids = null;
    }
    
    
    
    /**
     * @return Returns the elementSetName.
     */
    public  String getElementSetName() {
        return elementSetName;
    }
    /**
     * @param elemSetName The elementSetName to set.
     */
    public  void setElementSetName(final String elemSetName) {
        this.elementSetName = elemSetName;
    }
    /**
     * @return Returns the maxRecords.
     */
    public  int getMaxRecords() {
        return maxRecords;
    }
    /**
     * @param maxRecs The maxRecords to set.
     */
    public  void setMaxRecords(final int maxRecs) {
        this.maxRecords = maxRecs;
    }
    /**
     * @return Returns the startPosition.
     */
    public  int getStartPosition() {
        return startPosition;
    }
    /**
     * @param startPos The startPosition to set.
     */
    public  void setStartPosition(final int startPos) {
        this.startPosition = startPos;
    }
    
    
  
    /**
     * @return Returns the outputFormat.
     */
    public  String getOutputFormat() {
        return outputFormat;
    }
    /**
     * @param outputForm The outputFormat to set.
     */
    public  void setOutputFormat(final String outputForm) {
        this.outputFormat = outputForm;
    }
    /**
     * @return Returns the outputSchema.
     */
    public  String getOutputSchema() {
        return outputSchema;
    }
    /**
     * @param outputSch The outputSchema to set.
     */
    public  void setOutputSchema(final String outputSch) {
        this.outputSchema = outputSch;
    }
    /**
     * @return Returns the resultType.
     */
    public  String getResultType() {
        return resultType;
    }
    /**
     * @param resultT  The resultType to set.
     */
    public  void setResultType(final String resultT) {
        this.resultType = resultT;
    }
    /**
     * @return Returns the sortBy.
     */
    public  String getSortBy() {
        return sortBy;
    }
    /**
     * @param sort The sortBy to set.
     */
    public  void setSortBy(final String sort) {
        this.sortBy = sort;
    }
    /**
     * @return Returns the typeNames.
     */
    public  String getTypeNames() {
        return typeNames;
    }
    /**
     * @param typeN The typeNames to set.
     */
    public  void setTypeNames(final String typeN) {
        this.typeNames = typeN;
    }
    /**
     * @return Returns the soapElementFilter.
     */
    public SOAPElement getSoapElementFilter() {
        return soapElementFilter;
    }
    /**
     * @param soapElemFilter The soapElementFilter to set.
     */
    public void setSoapElementFilter(final SOAPElement soapElemFilter) {
        this.soapElementFilter = soapElemFilter;
    }
    /**
     * @return Returns the ids.
     */
    public String getIds() {
        return ids;
    }
    /**
     * @param string The ids to set.
     */
    public void setIds(final String string) {
        this.ids = string;
    }
}
