package se.nrm.dina.web.portal.model;
  
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement; 
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;  
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.beans.Field;
//import se.nrm.dina.common.util.morphbank.MorphBankImage;

/**
 *
 * @author idali
 */
@XmlRootElement  
@XmlType(name="",propOrder={"catalogNum","collectionName","startDate","catalogedDate","stationFieldNumber",
                            "fullname","commonName",
                            "highTaxon","sym","type","locality", "country","contient1","lat","lnt",
                            "collector", "auth", "determiner", "remarks"})
public class SolrRecord extends MapRecord {
      
     
    @Field("cm")
    String[] commonNames;
     
    @Field("ht")
    String highTaxon;
     
    @Field("tsn")
    String type;
     
    @Field("cln") 
    String collectionId;
    
    @Field("clnm") 
    String collectionName;
  
    @Field("ctd")
    Date catalogedDate;
 
    @Field("sym")
    String[] synomy;
    
    String sym;
     
    @Field("rm")
    String[] remarks;
    
    @Field("asr")
    String[] accessionRemarks;
    
    @Field("auth")
    String[] author;
    
    @Field("col")
    String[] collectors;
      
    @Field("cy")
    String country;
    
    @Field("ct") 
    String continent; 
     
    @Field("dn")
    String determiner;
    
    @Field("mbid")
    String mbid;
    
    @Field("img")
    String[] imgMbids;
     
    @Field("sfn")
    String stationFieldNumber;
    
    MBImage mbImage;
    boolean imageExist;
    boolean displayMap;
    boolean displayImage;
     
    
    String institution = "Naturhistoriska riksmuseet"; 
    String contient1;
    boolean selected = false;
    
    @XmlElement(name="type") 
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
 

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    @XmlElement(name="catalogdate") 
    public Date getCatalogedDate() {
        return catalogedDate;
    }

    public void setCatalogedDate(Date catalogedDate) {
        this.catalogedDate = catalogedDate;
    }
  
    @XmlElement(name="catalognumber") 
    @Override
    public String getCatalogNum() {
        return catalogNum;
    }
 
    @XmlElement(name="hightaxon") 
    public String getHighTaxon() {
        return highTaxon;
    }

    public void setHighTaxon(String highTaxon) {
        this.highTaxon = highTaxon;
    }

    @XmlElement(name="locality") 
    @Override
    public String getLocality() {
        return locality;
    }
    
    
    
 
    
    public String[] getCommonNames() {
        return commonNames;
    }

    public void setCommonNames(String[] commonNames) {
        this.commonNames = commonNames;
    }
    
    @XmlElement(name="commonname") 
    public String getName() {
        return commonNames ==  null ? fullname : fullname + " [" + StringUtils.join(commonNames, ", ")  + "] ";
    }
 

    @XmlElement(name="statedate") 
    @Override
    public Date getStartDate() {
        return startDate;
    }
 
    @XmlElement(name="stationFieldNumber") 
    public String getStationFieldNumber() {
        return stationFieldNumber;
    }

    public void setStationFieldNumber(String stationFieldNumber) {
        this.stationFieldNumber = stationFieldNumber;
    }

    @XmlElement(name="determiner") 
    public String getDeterminer() {
        return determiner;
    }

    public void setDeterminer(String determiner) {
        this.determiner = determiner;
    }
 
    @XmlElement(name="taxon") 
    @Override
    public String getFullname() {
        return fullname;
    }
  
    @XmlTransient
    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    @XmlTransient
    public String[] getImgMbids() {
        return imgMbids;
    }

    public void setImgMbids(String[] imgMbids) {
        this.imgMbids = imgMbids;
    }
 
    @XmlTransient
    public String getCollectionId() {
        return collectionId;
    }
    
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    @XmlElement(name="synomy")
    public String getSym() {  
        if(synomy != null) {
            return String.join(", ", synomy);
//            return StringUtils.join(synomy, ", ");  
        }
        return null; 
    }

    public void setSym(String sym) {
        this.sym = sym;
    }

    @XmlTransient
    public String[] getSynomy() { 
        return synomy;
    }

    public void setSynomy(String[] synomy) {
        this.synomy = synomy;
    }
     
 
     
    @XmlTransient
    public String getDisciplineIconId() {
        return collectionId;
    }



    @XmlTransient
    public String[] getRemarks() {
        return remarks;
    }

    public void setRemarks(String[] remarks) {
        this.remarks = remarks;
    }

    @XmlElement(name="remarks") 
    public String getRemarkList() {
        return StringUtils.join(remarks, ". ");  
    }
  

    @XmlElement(name="latitude") 
    @Override
    public String getLat() {
        return lat;
    }

//    @Override
//    public void setLat(String lat) {
//        this.lat = lat;
//    }

    @XmlElement(name="longitude") 
    @Override
    public String getLnt() {
        return lnt;
    }

    public String[] getAccessionRemarks() {
        return accessionRemarks;
    }

    public void setAccessionRemarks(String[] accessionRemarks) {
        this.accessionRemarks = accessionRemarks;
    }
    
    
     

    
    
    @XmlElement(name="Author")
    public String getAuth() { 
        return StringUtils.join(author, ", "); 
    }
       
    @XmlTransient
    public String[] getAuthor() {
        return author;
    }

    public void setAuthor(String[] author) {
        this.author = author;
    }
   
    @XmlElement(name="Continent") 
    public String getContinent() {
        return continent;
    }
    
    
    @XmlElement(name="collector") 
    public String getCollector() {   
        return StringUtils.join(collectors, ", ");  
    }
    
    @XmlTransient 
    public String[] getCollectors() {
        return collectors;
    }

    public void setCollectors(String[] collectors) {
        this.collectors = collectors;
    }
    

    public void setContinent(String continent) {
        this.continent = continent;
    }
    
 

    @XmlElement(name="Country") 
    public String getCountry() {
        return country;
    } 
    
    public void setCountry(String country) {
        this.country = country;
    }

    @XmlTransient
    public MBImage getMbImage() {
        
        List<Integer> imgIds = new ArrayList();
        for(String imgMbid : imgMbids) {
            imgIds.add(Integer.parseInt(imgMbid));
        } 
        return new MBImage(Integer.parseInt(mbid), imgIds); 
    }

    public void setMbImage(MBImage mbImage) {
        this.mbImage = mbImage;
    }
    
    
    
    

//    @XmlTransient
//    public MBImage getImages() {
//        
//        images = new ArrayList<>();
//        MorphBankImage img;
//        for(String imgMBid : imgMbids) {  
//            img = new MorphBankImage(Integer.parseInt(imgMBid), "http://morphbank.nrm.se/?id=" + imgMBid,
//                                        catalogNum, fullname, "", 
//                                        "http://images.morphbank.nrm.se/?id=" + imgMBid + "&imgType=thumb");
//            
//            
//            images.add(img);
//        } 
//        return images;
//    }
//
//    public void setImages(List<MBImage> images) {
//        this.images = images;
//    } 
    
    @XmlTransient
    public List<String> getThumbs() {
        List<String> thumbs = new ArrayList<>();
        
        for(String imgMBid : imgMbids) { 
            thumbs.add("http://images.morphbank.nrm.se/?id=" + imgMBid + "&imgType=thumb");
        }
        return thumbs;  
    } 

    @XmlTransient
    public boolean isImageExist() {
        return mbid != null;
    }

    public void setImageExist(boolean imageExist) {
        this.imageExist = imageExist;
    }

    @XmlTransient
    public boolean isDisplayMap() {
        return displayMap;
    }

    public void setDisplayMap(boolean displayMap) { 
        this.displayMap = displayMap;
    }

    @XmlTransient
    public boolean isDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(boolean displayImage) {
        this.displayImage = displayImage;
    }
    
    
    
    
    
    

    @XmlTransient
    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public boolean isSelected() {
        return selected;
    }

    @XmlTransient
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

 
    
    
 
    public int getCoordinate() { 
        
        if(lat != null && lat.length() > 0) {
            if(lnt != null && lnt.length() > 0) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if(lnt != null && lnt.length() > 0) {
                return 2;
            }
            return 3;
        }
    }
    
 
    
    
    
    public String getNewLine() {
        return "\n";
    }
    
    public String getNewTab() {
        return "\t";
    }
 
    @Override
    public String toString() {
        return "Detail : [ " + catalogNum + " : family : [ " + highTaxon + " ] : "  + locality + " ] : Collection : " + collectionId;
    }
}
