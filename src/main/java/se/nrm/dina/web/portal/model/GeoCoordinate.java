package se.nrm.dina.web.portal.model;

import java.io.Serializable;

/**
 *
 * @author idali
 */
public class GeoCoordinate implements Serializable {
    
    private String name;
    private Double latitude; 
    private Double longitude;   
     
    
    public GeoCoordinate() {
        
    }
    
    public GeoCoordinate(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;  
    }

    public String getName() {
        return name;
    } 
 
    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
 
    
    @Override
    public String toString() {
        return "GeoCoordinate[ " + name + ": lat =" + latitude + " lon =" + longitude + " ]";
    }
}
