package se.nrm.dina.web.portal.model;

import org.apache.solr.client.solrj.beans.Field;

/**
 *
 * @author idali
 */
public class SolrCollector {
    
    @Field("col")
    String collector;

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = collector;
    }
 
}
