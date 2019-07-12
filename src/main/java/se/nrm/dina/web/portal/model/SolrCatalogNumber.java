package se.nrm.dina.web.portal.model;

import org.apache.solr.client.solrj.beans.Field;

/**
 *
 * @author idali
 */
public class SolrCatalogNumber {
    
    @Field("cn")
    String catalogNum;

    public String getCatalogNum() {
        return catalogNum;
    }

    public void setCatalogNum(String catalogNum) {
        this.catalogNum = catalogNum;
    } 
}
