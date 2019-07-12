package se.nrm.dina.web.portal.model;

import com.spatial4j.core.shape.Rectangle;
import java.io.Serializable;   
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author idali
 */
public final class GeoData implements Serializable {
    
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final Double maxLat;
    private final Double minLat;
    private final Double maxLng;
    private final Double minLng;
    
    private double rangeLng; 
    private double rangeLat;
     
    
    private int maxCount;
    private int minCount;
    
    private boolean fixLng;
    private boolean fixLat;
            
    private List<SolrRecord> records;
    private List<MapRecord> mapRecords;
    private Map<BoxData, Integer> map;
    
    private Map<Rectangle, Integer> geoMap; 
    private int zoom;
    
    private int totalFound;
    
    public GeoData(final double maxLat, final double minLat, final double maxLng, final double minLng) {
        this.maxLat = maxLat;
        this.minLat = minLat;
        this.maxLng = maxLng;
        this.minLng = minLng;
    }
 
 
    public double getMaxLat() {
        return maxLat;
    }

    public double getMaxLng() {
        return maxLng;
    }

    public double getMinLat() {
        return minLat;
    }

    public double getMinLng() {
        return minLng;
    }

    public int getTotalFound() {
        return totalFound;
    }

    public void setTotalFound(int totalFound) {
        this.totalFound = totalFound;
    }

    public boolean isFixLat() {
        return fixLat;
    }
 
    public boolean isFixLng() {
        return fixLng;
    }
 

    public int getZoom() {
        return zoom;
    }
    
    
    public double getRadius(int zoom) {
        switch (zoom) {
            case 1:  return 3;  
            case 2:  return 2; 
            case 3:  return 1.5; 
            case 4:  return 1.0; 
            case 5:  return 0.5; 
            case 6:  return 0.2; 
            case 7:  return 0.09; 
            case 8:  return 0.06; 
            case 9:  return 0.03; 
            case 10: return 0.02; 
            case 11: return 0.009; 
            case 12: return 0.006; 
            case 13: return 0.9; 
            case 14: return 0.6; 
            case 15: return 0.3; 
            case 16: return 0.1;  
            default: return 0;  
        } 
    }
    
    

    public double getRangeLat(int zoom) {
        switch (zoom) {
            case 1:  return 9;  
            case 2:  return 6.15234375; 
            case 3:  return 3.076171875; 
            case 4:  return 1.5380859375; 
            case 5:  return 0.76904296875; 
            case 6:  return 0.384521484375; 
            case 7:  return 0.1922607421875; 
            case 8:  return 0.09613037109375; 
            case 9:  return 0.048065185546875; 
            case 10: return 0.0240325927734375; 
            case 11: return 0.01201629638672; 
            case 12: return 0.0060081482; 
            case 13: return 0.0030040741; 
            case 14: return 0.00150203704834; 
            case 15: return 0.00075102; 
            case 16: return 0.0003755092621; 
            case 17: return 0.000187754631; 
            default: return 9;  
        } 
    }

    public double getRangeLng(int zoom) {
        switch (zoom) {
            case 1:  return 18;  
            case 2:  return 12.3046875; 
            case 3:  return 6.15234375; 
            case 4:  return 3.076171875; 
            case 5:  return 1.5380859375; 
            case 6:  return 0.76904296875; 
            case 7:  return 0.384521484375; 
            case 8:  return 0.1922607421875; 
            case 9:  return 0.09613037109375; 
            case 10: return 0.048065185546875; 
            case 11: return 0.0240325927734375; 
            case 12: return 0.01201629638672; 
            case 13: return 0.0060081482; 
            case 14: return 0.0030040741; 
            case 15: return 0.00150203704834; 
            case 16: return 0.00075102; 
            case 17: return 0.0003755092621; 
            default: return 18;  
        }
    }
 
    
   
    public String buildSearchBox() {
        StringBuilder sb = new StringBuilder();
        sb.append("geo:[");
        sb.append(minLat);
        sb.append(","); 
        sb.append(minLng);
        sb.append(" TO ");
        sb.append(maxLat);
        sb.append(",");
        sb.append(maxLng);
        sb.append("]");
        return sb.toString(); 
    }
    
    public BoxData buildBox(double lat, double lng) { 
        
        double r = rangeLng; 
        double rLat = rangeLat;
        double toLat = lat + rLat;
//        double toLat = lat + r;
        double startLng = lng - r;
        
        return new BoxData(lat, startLng, toLat, lng, null); 
    }
    
    public BoxData buildBoxData(double lat, double lng) {
        double r = rangeLng; 
        double rLat = rangeLat;
        double toLat = lat + rLat;
//        double toLat = lat + r;
        double toLng = lng + r;
        
        return new BoxData(lat, lng, toLat, toLng, null); 
    }
    
     

    public Map<BoxData, Integer> getMap() {
        return map;
    }

    public void setMap(Map<BoxData, Integer> map) {
        this.map = map;
    }

    public List<SolrRecord> getRecords() {
        return records;
    }

    public void setRecords(List<SolrRecord> records) {
        this.records = records;
    }
 

    public List<MapRecord> getMapRecords() {
        return mapRecords;
    }

    public void setMapRecords(List<MapRecord> mapRecords) {
        this.mapRecords = mapRecords;
    }

    public Map<Rectangle, Integer> getGeoMap() {
        return geoMap;
    }

    public void setGeoMap(Map<Rectangle, Integer> geoMap) {
        this.geoMap = geoMap;
    }

   

    
    
    
    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getMinCount() {
        return minCount;
    }

    public void setMinCount(int minCount) {
        this.minCount = minCount;
    } 
}
