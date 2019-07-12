/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.web.portal.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List; 
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.web.portal.model.MBImage;
import se.nrm.dina.web.portal.model.MorphBankImage;
import se.nrm.dina.web.portal.model.SolrRecord;
import se.nrm.dina.web.portal.services.solr.SolrSearch;

/**
 *
 * @author idali
 */
@Named("imageSwitcher")
@SessionScoped
public class ImageSwitcher implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private List<String> thumbs;
    private int mbid;
    private String catalogNumber;
    private String scientificName;
    
    @Inject
    private SolrSearch solrSearch;
      

    public ImageSwitcher() {
        thumbs = new ArrayList<>();
    }
    
          
    public void imageSwitch(SolrRecord record) {

        logger.info("imageSwitch : {}", record);

        thumbs = new ArrayList<>(); 
        this.scientificName = record.getFullname();
         
        MBImage image = solrSearch.getImagesByCatalogNumberAndCollectionCode(record.getCatalogNum(), record.getCollectionId());
        this.mbid = image.getMbId();
        this.thumbs.addAll(image.getThumbs());
    }
    
    public void imageSwitch(MorphBankImage image) {

        logger.info("imageSwitch : {}", image);

        thumbs = new ArrayList<>(); 
        MBImage mbImage = solrSearch.getImagesByCatalogNumberAndCollectionCode(image.getCatalogNumber(), image.getCollectionCode()); 
        this.mbid = mbImage.getMbId();
        this.thumbs.addAll(mbImage.getThumbs());
        this.scientificName = image.getName();
    }
 
    public int getMbid() {
        return mbid;
    }

    public List<String> getThumbs() {
        return thumbs;
    }
 
    public String getCatalogNumber() {
        return catalogNumber;
    }

    public String getScientificName() {
        return scientificName;
    }  
}
