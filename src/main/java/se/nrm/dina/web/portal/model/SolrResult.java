package se.nrm.dina.web.portal.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List; 
import java.util.Map;

/**
 *
 * @author idali
 */ 
public class SolrResult implements Serializable {
    
    private long numFound;
    private int start; 
    
    private List<SolrRecord> records;
    private Map<String, Integer> map = new HashMap<>();
     
    public SolrResult() {
        
    }
    
    public SolrResult(long numFound, int start, List<SolrRecord> records) {
        this.numFound = numFound;
        this.start = start;
        this.records = records;
    }
    
    public SolrResult(long numFound, Map<String, Integer> map, List<SolrRecord> records) {
        this.numFound = numFound;
        this.map = map;
        this.records = records;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }
    
    

    public long getNumFound() {
        return numFound;
    }

    public List<SolrRecord> getRecords() {
        return records;
    }

    public int getStart() {
        return start;
    }  
}
