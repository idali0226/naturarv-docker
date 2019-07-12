package se.nrm.dina.web.portal.model;

import java.io.Serializable;
//import se.nrm.dina.web.portal.util.StringMap;

/**
 *
 * @author idali
 */
public class SubCollection implements Serializable {
    
    private String subCollection;
    private int numOfRecords;
    
    public SubCollection() {
        
    }
    
    public SubCollection(String subCollection, int numOfRecords) {
        this.subCollection = subCollection;
        this.numOfRecords = numOfRecords;
    }

    public int getNumOfRecords() {
        return numOfRecords;
    }

    public String getSubCollection() {
        return subCollection;
    }
    
//    public String getSubCollectionName(String locale) {
//        return StringMap.getInstance().getString(subCollection, locale);
//    }
    
    @Override
    public String toString() {
        return subCollection + " [" + numOfRecords + "]";
    }
}
