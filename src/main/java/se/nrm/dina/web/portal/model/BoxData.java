package se.nrm.dina.web.portal.model;
 
import com.spatial4j.core.shape.Point;
import java.io.Serializable;
import org.primefaces.model.map.LatLng;

/**
 *
 * @author idali
 */
public final class BoxData implements Serializable {
    
    private int totalCount;
    private double lat;
    private double lng; 
    private String box; 
     
    private double south;
    private double north;
    private double west;
    private double east;
    private Point point;
    private String geoHash;
    private MapRecord mapRecord;
      
    public BoxData() {
        
    }
     
    public BoxData(double south, double west, double north, double east, Point point) {
        this.south = south;
        this.north = north;
        this.west = west;
        this.east = east;
        this.point = point;
    }
    
    public BoxData(double south, double west, double north, double east, Point point, int totalCount) {
        this.south = south;
        this.north = north;
        this.west = west;
        this.east = east;
        this.point = point;
        this.totalCount = totalCount;
    }
    
    public BoxData(double south, double west, double north, double east, String geoHash, MapRecord record, int totalCount) {
        this.south = south;
        this.north = north;
        this.west = west;
        this.east = east; 
        this.geoHash = geoHash;
        this.mapRecord = record;
        this.totalCount = totalCount;
    }
    
    
    public BoxData(double south, double west, double north, double east, Point point, String geoHash, int totalCount) {
        this.south = south;
        this.north = north;
        this.west = west;
        this.east = east;
        this.point = point;
        this.geoHash = geoHash;
        this.totalCount = totalCount;
    }
    
    public BoxData(int totalCount, double lat, double lng, MapRecord mapRecord) {
        this.totalCount = totalCount;
        this.lat = lat;
        this.lng = lng;  
        this.mapRecord = mapRecord;
    }
    
    public BoxData(int totalCount, double lat, double lng, String box) {
        this.totalCount = totalCount;
        this.lat = lat;
        this.lng = lng;
        this.box = box;  
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public String getBox() {
        return box;
    }
    
    public String getCoordinate() {
        return "Lat: " + lat + " -- Lan: " + lng;
    }

    public LatLng getCoodEnd() { 
        return new LatLng(north, east); 
    }

    public LatLng getCoordStart() {
        
        return new LatLng(south, west);
    }

    public double getEast() {
        return east;
    }

    public double getNorth() {
        return north;
    }

    public double getSouth() {
        return south;
    }

    public double getWest() {
        return west;
    }

    public Point getPoint() {
        return point;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public MapRecord getMapRecord() {
        return mapRecord;
    }

    public void setMapRecord(MapRecord mapRecord) {
        this.mapRecord = mapRecord;
    }

    
    
     
    public String getGeoSearchRange() {
         
        
        StringBuilder sb = new StringBuilder(); 
        if (north > 90) {
            north = 90;
        } 
        if (east > 180) {
            east = 180;
        }
        
        if(south < -90) {
            south = -90;
        }
        
        if(west < -180) {
            west = -180;
        }
        
        sb.append("geo:[");
        sb.append(south);
        sb.append(",");
        sb.append(west);
        sb.append(" TO ");
        sb.append(north);
        sb.append(",");
        sb.append(east);
        sb.append("]");
        return sb.toString(); 
    }
}
