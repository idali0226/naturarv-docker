package se.nrm.dina.web.portal.model;

import org.apache.solr.client.solrj.beans.Field;

/**
 *
 * @author idali
 */
public class SolrFamily {
    
    @Field("ht")
    String family;

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    } 
}
