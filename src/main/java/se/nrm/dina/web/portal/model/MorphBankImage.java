package se.nrm.dina.web.portal.model;

import java.util.List;
import org.apache.solr.common.SolrDocument;

/**
 *
 * @author idali
 */
public class MorphBankImage {
    
    private int morphbankImageId;
    private String morphbankImageLink;
    private String collectionCode;
    private String name;
    private String mbview;
    private String catalogNumber;
    private String thumb;

    MorphBankImage() {
    }
     
    public MorphBankImage(int morphbankImageId, String morphbankImageLink, String collectionCode, 
                          String catalogNumber, String name, String mbview, String thumb) {
        this.morphbankImageId = morphbankImageId;
        this.morphbankImageLink = morphbankImageLink;
        this.collectionCode = collectionCode;
        this.name = name;
        this.mbview = mbview;
        this.catalogNumber = catalogNumber;
        this.thumb = thumb;
    }

    public int getMorphbankImageId() {
        return morphbankImageId;
    }

    public String getMorphbankImageLink() {
        return morphbankImageLink;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public String getCollectionCode() {
        return collectionCode;
    }
     
    public String getName() {
        return name;
    }

    public String getThumb() {
        return thumb; 
    }

    public String getMbview() {
        return mbview;
    }
    
   
    
    @Override
    public String toString() {
        return "MorphBankImage : [" + morphbankImageLink + " - " + thumb;
    }
}
