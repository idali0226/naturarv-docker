package se.nrm.dina.web.portal.model;

import java.io.Serializable;
import java.util.ArrayList; 
import java.util.List; 

/**
 *
 * @author idali
 */
public class StatisticDataInstitution implements Serializable {
    
    private String institution;                                                     // name of the institution
    private String instCode;                                                        // institution code 
    private int numOfRecords;                                                       // total number of registied specimens in instituion collection
    private List<String> subCollectionList = new ArrayList<>();               // collections in institution
    
    public StatisticDataInstitution() {
        
    }
    
    public StatisticDataInstitution(String institution, String instCode, int numOfRecords, List<String> subCollectionList) {
        this.institution = institution;
        this.instCode = instCode;
        this.numOfRecords = numOfRecords;
        this.subCollectionList = subCollectionList;
    }

    
    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getInstCode() {
        return instCode;
    }

    public void setInstCode(String instCode) {
        this.instCode = instCode;
    }

    
    
    public int getNumOfRecords() {
        return numOfRecords;
    }

    public void setNumOfRecords(int numOfRecords) {
        this.numOfRecords = numOfRecords;
    }

    public List<String> getSubCollectionList() {
        return subCollectionList;
    }

    public void setSubCollectionList(List<String> subCollectionList) {
        this.subCollectionList = subCollectionList;
    } 
    
    @Override
    public String toString() {
        return institution + " [" + numOfRecords + "]";
    }
}
