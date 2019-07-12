package se.nrm.dina.web.portal.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.solr.client.solrj.beans.Field;

/**
 *
 * @author idali
 */
public class MapRecord implements Serializable {
    
     
    @Field("id")
    String id;
    
    @Field("geo") 
    String geo;
    
    @Field("geopoint")
    String geopoint;
     
    @Field("cn")
    String catalogNum;
       
    @Field("ftx")
    String fullname; 
           
    @Field("sd")
    Date startDate;
    
    @Field("lc") 
    String locality;
     
    @Field("lat")
    String lat;
     
    @Field("lnt")
    String lnt;
     
    @Field("lat1")
    Double lat1;
     
    @Field("lnt1")
    Double lnt1;

    public String getCatalogNum() {
        return catalogNum;
    }

    public void setCatalogNum(String catalogNum) {
        this.catalogNum = catalogNum;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public Double getLat1() {
        return lat1;
    }

    public void setLat1(Double lat1) {
        this.lat1 = lat1;
    }

    public String getLnt() {
        return lnt;
    }

    public void setLnt(String lnt) {
        this.lnt = lnt;
    }

    public Double getLnt1() {
        return lnt1;
    }

    public void setLnt1(Double lnt1) {
        this.lnt1 = lnt1;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getGeopoint() {
        return geopoint;
    }

    public void setGeopoint(String geopoint) {
        this.geopoint = geopoint;
    }
    
    
}
