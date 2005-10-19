/*
 * Created on 04.10.2005
 *
 */
package de.ingrid.ibus.cswinterface.analyse;

import java.util.ArrayList;

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
    
    
    public static final String GETCAPABILITIES = "GetCapabilities";
    
    public static final String GETRECORDS = "GetRecords";
    
    public static final String GETRECORDBYID = "GetRecordById";
    
    
    public static final String DESCRIBERECORD = "DescribeRecord";
    
    
    
    
   private boolean operationIsGetCap = false;
   
   private boolean operationIsGetRecs = false;
    
   private boolean operationIsGetRecById = false;
   
   
   private boolean operationIsDescRec = false;
    
    
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
     * Comment 
     */
    private boolean typeNameIsDataset = false;
    
    
    /**
     * Comment 
     */
    private boolean typeNameIsDatasetcollection = false;
    
    
    /**
     * Comment 
     */
    private boolean typeNameIsService = false;
    
    
    /**
     * Comment 
     */
    private boolean typeNameIsApplication = false;
    
    
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
     * stores the OGC Filter element
     */
    private SOAPElement soapElementFilter = null;
   
    
    
    
    
    
    //also needed for GetRecordById requests
    
    /**
     * TODO elementSetName default value for GetRecordById should be summary
     */
    private  String elementSetName = "full";
    
    
    
    /**
     * for the id string of GetRecById      
     * 
     */
    
    private String ids = null;
    
    
    
    /**
     * holds all ids from the id string of GetRecById
     */
    private ArrayList idsList = null;
    
    
    
    
    /**
     * needed??
     */
//    public  void clear() {
//        
//        maxRecords = MAXRECORDS;
//        resultType = "hits";
//        outputFormat = "text/xml";
//        outputSchema = "csw:ogccore";
//        typeNames = null; 
//        startPosition = STARTPOSITION;
//        sortBy = "metadata_element_name:A";
//        soapElementFilter = null;
//        elementSetName = "full";
//        ids = null;
//        //TODO typeNames ...
//    }
    
    
    
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
    /**
     * @return Returns the typeNameIsApplication.
     */
    public boolean isTypeNameIsApplication() {
        return typeNameIsApplication;
    }
    /**
     * @param typeNameIsAppl The typeNameIsApplication to set.
     */
    public void setTypeNameIsApplication(final boolean typeNameIsAppl) {
        this.typeNameIsApplication = typeNameIsAppl;
    }
    /**
     * @return Returns the typeNameIsDataset.
     */
    public boolean isTypeNameIsDataset() {
        return typeNameIsDataset;
    }
    /**
     * @param typeNameIsDatas The typeNameIsDataset to set.
     */
    public void setTypeNameIsDataset(final boolean typeNameIsDatas) {
        this.typeNameIsDataset = typeNameIsDatas;
    }
    /**
     * @return Returns the typeNameIsDatasetcollection.
     */
    public boolean isTypeNameIsDatasetcollection() {
        return typeNameIsDatasetcollection;
    }
    /**
     * @param typeNameIsDatasetcoll The typeNameIsDatasetcollection to set.
     */
    public void setTypeNameIsDatasetcollection(final boolean typeNameIsDatasetcoll) {
        this.typeNameIsDatasetcollection = typeNameIsDatasetcoll;
    }
    /**
     * @return Returns the typeNameIsService.
     */
    public boolean isTypeNameIsService() {
        return typeNameIsService;
    }
    /**
     * @param typeNameIsServ The typeNameIsService to set.
     */
    public void setTypeNameIsService(final boolean typeNameIsServ) {
        this.typeNameIsService = typeNameIsServ;
    }
    /**
     * @return Returns the idsList.
     */
    public ArrayList getIdsList() {
        return idsList;
    }
    /**
     * @param idsLst The idsList to set.
     */
    public void setIdsList(final ArrayList idsLst) {
        this.idsList = idsLst;
    }
/**
 * @return Returns the operationIsDescRec.
 */
public boolean isOperationIsDescRec() {
    return operationIsDescRec;
}
/**
 * @param opIsDescRec The operationIsDescRec to set.
 */
public void setOperationIsDescRec(final boolean opIsDescRec) {
    this.operationIsDescRec = opIsDescRec;
}
/**
 * @return Returns the operationIsGetCap.
 */
public boolean isOperationIsGetCap() {
    return operationIsGetCap;
}
/**
 * @param opIsGetCap The operationIsGetCap to set.
 */
public void setOperationIsGetCap(final boolean opIsGetCap) {
    this.operationIsGetCap = opIsGetCap;
}
/**
 * @return Returns the operationIsGetRecById.
 */
public boolean isOperationIsGetRecById() {
    return operationIsGetRecById;
}
/**
 * @param opIsGetRecById The operationIsGetRecById to set.
 */
public void setOperationIsGetRecById(final boolean opIsGetRecById) {
    this.operationIsGetRecById = opIsGetRecById;
}
/**
 * @return Returns the operationIsGetRecs.
 */
public boolean isOperationIsGetRecs() {
    return operationIsGetRecs;
}
/**
 * @param opIsGetRecs The operationIsGetRecs to set.
 */
public void setOperationIsGetRecs(final boolean opIsGetRecs) {
    this.operationIsGetRecs = opIsGetRecs;
}
}
