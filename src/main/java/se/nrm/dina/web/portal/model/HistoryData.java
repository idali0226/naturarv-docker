package se.nrm.dina.web.portal.model;

import java.io.Serializable;

/**
 *
 * @author idali
 */
public class HistoryData implements Serializable {
      
    private String key;
    private String name;
    private int totalCreated;
    private int totalModified;
    
    public HistoryData() {
        
    }
    
    public HistoryData(String key, String name, int totalCreated, int totalModified) {
        this.key = key;
        this.name = name;
        this.totalCreated = totalCreated;
        this.totalModified = totalModified;
    }

    public String getKey() {
        return key;
    }
    
    
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalCreated() {
        return totalCreated;
    }

    public void setTotalCreated(int totalCreated) {
        this.totalCreated = totalCreated;
    }

    public int getTotalModified() {
        return totalModified;
    }

    public void setTotalModified(int totalModified) {
        this.totalModified = totalModified;
    }
      
    @Override
    public String toString() {
        return HistoryData.class.getSimpleName();
    }
}
