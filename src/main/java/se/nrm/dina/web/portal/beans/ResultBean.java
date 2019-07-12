package se.nrm.dina.web.portal.beans;

import java.io.Serializable;   
import java.util.List;
import javax.enterprise.context.SessionScoped;  
import javax.inject.Named; 
import se.nrm.dina.web.portal.model.SolrRecord; 

/**
 *
 * @author idali
 */
@SessionScoped
@Named
public class ResultBean implements Serializable {
      
    private SolrRecord[] selectedRecords; 
    private List<SolrRecord> resultList;  
    private int totalDisplay;
    
    public ResultBean() {
        
    }
 
     
    public List<SolrRecord> getResultList() {
        return resultList;
    }

    public void setResultList(List<SolrRecord> resultList) {
        this.resultList = resultList;
    }  

    public SolrRecord[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(SolrRecord[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public int getTotalDisplay() {
        return totalDisplay;
    }

    public void setTotalDisplay(int totalDisplay) {
        this.totalDisplay = totalDisplay;
    }
    
    
}
