package se.nrm.dina.web.portal.model;
 
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author idali
 */
public class StatisticData implements Serializable {
    
    private int total;                                                          // total number of registed specimens in all collections
    private List<CollectionData> collections;                                   // list of collections in all institutions
    private List<StatisticDataInstitution> institutionDataList;                 // list of institution data
    
    private Map<String, Integer> dataMap = new HashMap<>();
    
    public StatisticData() { 
    }
    
    public StatisticData(int total, List<CollectionData> collections) {
        this.total = total;
        this.collections = collections;
    }
    
    public StatisticData(int total, List<CollectionData> collections, List<StatisticDataInstitution> institutionDataList) {
        this.total = total;
        this.collections = collections; 
        this.institutionDataList = institutionDataList;
    }

    
    public StatisticData(int total, List<CollectionData> collections, List<StatisticDataInstitution> institutionDataList, Map<String, Integer> dataMap) {
        this.total = total;
        this.collections = collections; 
        this.institutionDataList = institutionDataList;
        this.dataMap = dataMap;
    }

    public Map<String, Integer> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Integer> dataMap) {
        this.dataMap = dataMap;
    }
     
    public List<StatisticDataInstitution> getInstitutionDataList() {
        return institutionDataList;
    } 
    
    public List<CollectionData> getCollections() {
        return collections;
    }
     
    public void setCollections(List<CollectionData> collections) {
        this.collections = collections;
    }
 
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    } 
       
    @Override
    public String toString() {
        return total + " : " + collections;
    }
}
