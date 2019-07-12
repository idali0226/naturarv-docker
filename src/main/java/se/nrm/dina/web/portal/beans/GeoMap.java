package se.nrm.dina.web.portal.beans;
   
import java.io.Serializable; 
import java.util.ArrayList;
import java.util.Collection; 
import java.util.HashMap;
import java.util.List;
import java.util.Map; 
import java.util.TreeSet;
import javax.enterprise.context.SessionScoped; 
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;  
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;  
import org.primefaces.context.RequestContext;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.LatLngBounds;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Overlay;
import org.primefaces.model.map.Polyline;
import org.primefaces.model.map.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  
import se.nrm.dina.web.portal.model.BoxData;
import se.nrm.dina.web.portal.model.GeoData;
import se.nrm.dina.web.portal.model.MapRecord;
import se.nrm.dina.web.portal.model.SolrRecord;
import se.nrm.dina.web.portal.model.SolrResult;
import se.nrm.dina.web.portal.services.solr.SolrSearch;
import se.nrm.dina.web.portal.util.StringMap;

/**
 *
 * @author idali
 */
@Named("geoMap")
@SessionScoped
public class GeoMap implements Serializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass()); 
     
    private MapModel model;
    private int zoom;  
    private Double centerLat;
    private Double centerLng;
    
    private boolean changeZoom = false;
     
    private final String imagePath;
    private final String imagePath_1;
    private final String imagePath_yellowMarker;
    private final String imagePath_blackPlus;
    
    private double minlat;
    private double minlng;
    private double maxlat;
    private double maxlng;
    
    private double dAngle = 0;
    private double angInc = 30;
     
    private final int MAX_ZOOM = 12;
    
    private int min;
    private int max;
    
    private TreeSet<Integer> colorSet = new TreeSet<>();
    private int colorIndex = 0;
    private int setSize = 0;
    private int divid = 0;
    
    private boolean isType; 
    private boolean isImage;
    private String searchText;
    private Map<String, String> mapDataFilter;
    
    private List newColors;
    
    private MapRecord selectedRecord;
    private List<MapRecord> records;
    private String locality = "";
    private String coordinate = "";
    private String taxon = ""; 
    private StringBuilder mapInfoSb = new StringBuilder();
    private StringBuilder boxSB = new StringBuilder();
       
    private int infoLink;
    
    private List<String> colors; 
    private String color = "#790022"; 
    private String thisColor;
    
    
    private Map<String, List<Marker>> openedMarkerMap;
    private Map<String, List<Polyline>> polylineMap;  
     
    private String locale;
    private final HttpSession session;
    
    private final static String relativeWebPath = "/resources/images/icons/red10.png";
    private final static String relativeWebPath_1 = "/resources/images/icons/marker_red_plus_19.png";
    private final static String relativeWebPath_2 = "/resources/images/icons/marker_red_minus_19.png";
    private final static String relativeWebPath_yellow = "/resources/images/icons/pink_10.png";
    private final ServletContext ctx; 
    private final String servername;
    private final int serverport;
    private final String basePath; 
    private final String path; 
    
    @Inject
    private SolrSearch solrSearch; 
    
    
    
    public GeoMap() {
        
        logger.info("GeoMap");
                
        ctx= (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        servername = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getServerName();
        serverport  = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getServerPort();
        logger.info("srvername : {}", servername);
        
        if(servername.contains("dina-web") || servername.contains("as.nrm.se")) {
            basePath = "https://www.dina-web.net";
        } else {
            basePath = "http://" + servername + ":" + serverport;
        }
        path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        logger.info("context path : {}", path);
        
        imagePath = basePath + path + relativeWebPath;
        logger.info("image path : {}", imagePath);
        
        imagePath_1 = basePath + path + relativeWebPath_1;
        imagePath_yellowMarker = basePath + path + relativeWebPath_yellow;
        imagePath_blackPlus = basePath + path + relativeWebPath_2;
        
        
        openedMarkerMap = new HashMap<>();
        polylineMap = new HashMap<>();
        mapInfoSb = new StringBuilder();
        
        colors = new ArrayList<>();
        newColors = new ArrayList();
        
        initMap(); 
        
        session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true); 
        locale = (String) session.getAttribute("locale");
        if (locale == null) {
            locale = "sv";
        }  
    }
    
    private void initMap() {
        
        model = new DefaultMapModel();
        zoom = 1;  
        centerLat = 30.0;
        centerLng = 31.0;
        infoLink = 0;
        
        changeZoom = false;
        
        min = 0;
        max = 0; 
        
        minlat = 90;
        minlng = 180;
        maxlat = -90;
        maxlng = -180;
    } 
     
    private void addMarkersIntoModel(List<? extends MapRecord> records, boolean rezoom) { 
         
        Map<String, List<MapRecord>> geoLatLnt = new HashMap<>();
        String geo;
        boolean isAdd = true;
        
        List<MapRecord> list; 
        for (MapRecord record : records) {
            geo = record.getGeo();
            if(geo != null) {
                if(geoLatLnt.containsKey(geo)) { 
                    list = geoLatLnt.get(geo); 
                    for (MapRecord mr : list) {
                        if (mr.getCatalogNum().equals(record.getCatalogNum())) {
                            isAdd = false;
                        }
                    }
                    if(isAdd) {
                        list.add(record);
                    }
                    isAdd = true;
                } else {
                    list = new ArrayList<>();
                    list.add(record);
                    geoLatLnt.put(geo, list);
                } 
            } 
        }   
        
        double lat;
        double lng;
        List<MapRecord> value;
        MapRecord record;
        LatLng coord;
        String s; 
        for (Map.Entry<String, List<MapRecord>> entry : geoLatLnt.entrySet()) { 
            value = entry.getValue();
            record = value.get(0);
            lat = record.getLat1();
            lng = record.getLnt1();
            coord = new LatLng(lat, lng); 

            if(rezoom) {
                if(minlat > lat) {
                    minlat = lat;
                } 
                if(maxlat < lat) {
                    maxlat = lat;
                } 
                if(minlng > lng) {
                    minlng = lng;
                }
                if(maxlng < lng) {
                    maxlng = lng;
                }
            } 
            if (value.size() > 1) {  
                s = buildString(value.size(), record, value);  
                model.addOverlay(new Marker(coord, s, value, imagePath_1, imagePath_1)); 
            } else { 
                addMarker(record, coord); 
            }
        } 
    }
    
    private void addMarker(MapRecord record, LatLng coord) {
          
        if(record != null) {
            mapInfoSb = new StringBuilder();
             
            mapInfoSb.append(record.getFullname());
            mapInfoSb.append("\n");
            mapInfoSb.append(record.getLocality());
            mapInfoSb.append("\n");
            mapInfoSb.append(record.getLat());
            mapInfoSb.append(" -- ");
            mapInfoSb.append(record.getLnt()); 
            model.addOverlay(new Marker(coord, mapInfoSb.toString().trim(), record, imagePath, imagePath)); 
        } 
    }
    
    private String buildString(int count, MapRecord record, List<MapRecord> records) { 
        mapInfoSb = new StringBuilder();
        mapInfoSb.append("Total count: ");
        mapInfoSb.append(count);
        mapInfoSb.append("\n");
        mapInfoSb.append(record.getLocality());
        mapInfoSb.append("\n");
        mapInfoSb.append(record.getLat());
        mapInfoSb.append(" -- ");
        mapInfoSb.append(record.getLnt());
        mapInfoSb.append("\n\n"); 
        
        if(count > 10) {
            count = 10;
        }
        MapRecord mapRecord;
        for (int i = 0; i < count; i++) {
            mapRecord = records.get(i);
            mapInfoSb.append(mapRecord.getCatalogNum());
            mapInfoSb.append("\t");
            mapInfoSb.append(mapRecord.getFullname());
            mapInfoSb.append("\n");
        }
        return mapInfoSb.toString();
    }

    /**
     * Calculate density colors
     * 
     * @param count - number of records
     * 
     * @return String - color code
     */ 
    private String getColorCode(int count) {
        color = "#790022";  
        if(count == colorSet.first()) {
            color = "#F7C7C7";
        } else if(count == colorSet.last()) {
            color = "#790022";
        } 
        
        colorIndex = colorSet.headSet(count).size(); 
         
        setSize = colorSet.size();
        divid = setSize / 4; 
        
        if(colorIndex > 0 && colorIndex <= divid) {
            color = "#E98990";
        } else if(colorIndex > divid && colorIndex <= divid * 2) {
            color = "#DA323D";
        } else if(colorIndex > divid * 2 && colorIndex <= divid * 3) {
            color = "#A2002E";
        } else if(colorIndex > divid * 3 && colorIndex < colorSet.size() - 1) {
            color = "#8E0028";
        }
         
        if(!colors.contains(color)) {
            colors.add(color); 
        } 
        return color; 
    }
    
 
    
    private void addRectanglerToModel(BoxData box, int count, Rectangle rect) {
        thisColor = getColorCode(count);                    // color to fill the rectangle
        
        rect.setStrokeColor(thisColor);
        rect.setStrokeOpacity(0.8);
        rect.setStrokeWeight(0);
        
        rect.setFillColor(thisColor);
        rect.setFillOpacity(0.8); 
          
        rect.setData(box); 
        model.addOverlay(rect);
    }
     
    /**
     * Add searched result into gmap
     * 
     * @param result -- SolrResout
     * @param searchText
     * @param filterMap
     * @param type
     * @param image
     * 
     */
    public void setMapData(SolrResult result, String searchText, Map<String, String> filterMap, boolean type, boolean image) {

        logger.info("setMapData : {}", result); 
        
        
        openedMarkerMap = new HashMap<>();
        polylineMap = new HashMap<>();
        
        colors = new ArrayList<>();
        mapDataFilter = new HashMap<>();
        mapDataFilter.putAll(filterMap);
        isType = type;
        isImage = image;
        this.searchText = searchText;
         
        initMap();
        if(result != null) {     
            List<SolrRecord> solrRecords = result.getRecords();
            
            if(solrRecords != null) {
                addMarkersIntoModel(solrRecords, true);  
            }  
            setZoom(); 
        }  
    }
    
    
    public void setMapDataList(SolrResult result, String searchText, Map<String, String> filterMap, boolean type, boolean image) {

        logger.info("setMapDataList"); 
         
        openedMarkerMap = new HashMap<>();
        polylineMap = new HashMap<>();
        
        colors = new ArrayList<>();
        mapDataFilter = new HashMap<>();
        mapDataFilter.putAll(filterMap);
        isType = type;
        isImage = image;
        this.searchText = searchText;
         
        initMap();
        if(result != null) {     
            List<SolrRecord> solrRecords = result.getRecords();
            
            if(solrRecords != null) {
                SolrRecord record = solrRecords.get(0); 
                centerLat = record.getLat1();
                centerLng = record.getLnt1();
                
                if(!changeZoom) {
                    zoom = 10;
                } 
                model.addOverlay(new Marker(new LatLng(centerLat, centerLng), buildString((int)result.getNumFound(), record, records), records, imagePath_1, imagePath_1));
            }    
        }   
    }
    
    public void setMapData(String searchText, Map<String, String> filterMap, boolean type, boolean image) {
        
        logger.info("setMap");
          
        initMap();
        colors = new ArrayList<>();
        
        openedMarkerMap = new HashMap<>();
        polylineMap = new HashMap<>();
         
        mapDataFilter = new HashMap<>();
        mapDataFilter.putAll(filterMap);
        
        min = 1000000;
        max = 0;
        
        isType = type; 
        isImage = image;
        this.searchText = searchText; 
        
        GeoData geoData = new GeoData(90, -90, 180, -180); 
        
        
        Map<String, Object> map = solrSearch.geoSpatialGroupSearch(searchText, filterMap, type, image, geoData); 
        if(map != null) {
            setMapData(map, geoData); 
        } 
    }
    
    private int calculateBoxLng(double lng, double lngRange) { 
        return (int) Math.ceil((180 + lng) / lngRange); 
    }
    
    private int calculateBoxLat(double lat, double latRange) { 
        return (int) Math.ceil((90 + lat) / latRange); 
    }
    
    private String boxXY(int x, int y) {
        boxSB = new StringBuilder();
        boxSB.append("y");
        boxSB.append(y);
        boxSB.append("x");
        boxSB.append(x);
        return boxSB.toString().trim();
    }
 
    
    private BoxData buildBoxData(MapRecord record) {
        return null;
    }
    private void setMapData(Map<String, Object> map, GeoData geoData) {
        
        min = 1000000;
        max = 0;
        
        String resultType = (String)map.get("resultType"); 
         
        if(resultType.equals("multiGroup")) {
            
            minlat = (Double)map.get("minlat");
            minlng = (Double)map.get("minlng");
            maxlat = (Double)map.get("maxlat");
            maxlng = (Double)map.get("maxlng");
        
            List<BoxData> boxDataList = (List<BoxData>) map.get("resultList"); 
            if(!changeZoom) { 
                setZoom(); 
            }  
            
            String xyBox;
            Map<String, List<BoxData>> countMap = new HashMap<>();
            MapRecord record;
            double lat;
            double lng;
            int count;
            int boxlat;
            int boxlng;
            double latRange = geoData.getRangeLat(zoom);
            double lngRange = geoData.getRangeLng(zoom); 
             
            BoxData box; 
            for(BoxData boxData : boxDataList) {
                record = boxData.getMapRecord(); 
                lat = record.getLat1();
                lng = record.getLnt1();
                count = boxData.getTotalCount(); 
                
                boxlat = calculateBoxLat(lat, latRange);
                boxlng = calculateBoxLng(lng, lngRange);  
                xyBox = boxXY(boxlng, boxlat);
 
                box = new BoxData((boxlat - 1) * latRange - 90,
                                    (boxlng - 1) * lngRange - 180, 
                                    boxlat * latRange - 90, 
                                    boxlng * lngRange - 180, 
                                    record.getGeopoint(), record, count); 
                if(countMap.containsKey(xyBox)) {
                    List<BoxData> boxList = countMap.get(xyBox);
                    boxList.add(box); 
                } else {
                    List<BoxData> boxList = new ArrayList<>();
                    boxList.add(box);
                    countMap.put(xyBox, boxList);
                } 
            } 
            
            colorSet = new TreeSet<>();
            LatLng coord;
            MapRecord mapRecord;
            List<BoxData> boxList;
            BoxData boxData;
            Map<BoxData, Integer> boxMap = new HashMap<>();
            for(Map.Entry<String, List<BoxData>> entry : countMap.entrySet()) {
                boxList = entry.getValue();
                boxData = boxList.get(0);  

                if(boxList.size() == 1 && boxData.getTotalCount() == 1) {  
                    mapRecord = boxData.getMapRecord();
                    coord = new LatLng(mapRecord.getLat1(), mapRecord.getLnt1());
                    addMarker(mapRecord, coord);    
                } else {
                    int total = 0;
                    for(BoxData b : boxList) {
                        total += b.getTotalCount(); 
                    } 
                    
                    if(total > 1 && total < min) {
                        min = total;
                    }
                    if(total > max) {
                        max = total;
                    }
                    colorSet.add(total);    
                    boxMap.put(boxData, total);
                } 
            }
             
            boxMap.entrySet().stream()
                    .forEach(b -> {
                        LatLngBounds latLng = new LatLngBounds(b.getKey().getCoordStart(), b.getKey().getCoodEnd()); 
                        Rectangle rect = new Rectangle(latLng);  
                        addRectanglerToModel(b.getKey(), b.getValue(), rect);   
                    });
            
            
            
//            for(Map.Entry<BoxData, Integer> entry : boxMap.entrySet()) {
//                bx = entry.getKey();
//                latLng = new LatLngBounds(bx.getCoordStart(), bx.getCoodEnd()); 
//                rect = new Rectangle(latLng); 
//                addRectanglerToModel(bx, entry.getValue(), rect);  
//            }
        } else {
            List<MapRecord> mapRecords = (List<MapRecord>)map.get("resultList"); 
            if(mapRecords != null) {  
                if(resultType.equals("singleGroup")) { 
                    String imgPath = imagePath;         // Single marker image
                    if(mapRecords.size() > 1) {         // if records is more than 1, get multiple marker image
                        imgPath = imagePath_1;
                    }
                    MapRecord record = mapRecords.get(0);
                    String s = buildString(mapRecords.size(), record, mapRecords);
                      
                    LatLng coord = new LatLng(record.getLat1(), record.getLnt1());
                    model.addOverlay(new Marker(coord, s, mapRecords, imgPath, imgPath));

                    // set center, and set zoom level to 10
                    centerLat = record.getLat1();       
                    centerLng = record.getLnt1();
                    
                    if(!changeZoom) {
                        zoom = 10;
                    }
                } else {
                    boolean rezoom = true;
                    if(changeZoom) {
                        rezoom = false;
                    }
                    addMarkersIntoModel(mapRecords, rezoom); 
                }
            }
        } 
    }
        
    public void onStateChange(StateChangeEvent event) {
        logger.info("onStateChange");
          
        openedMarkerMap = new HashMap();
        polylineMap = new HashMap();
        colors = new ArrayList(); 
        changeZoom = false;
        
        if(zoom != event.getZoomLevel()) {
            zoom = event.getZoomLevel();  
            changeZoom = true;
        }
        centerLat = event.getCenter().getLat();
        centerLng = event.getCenter().getLng();
         
        if(zoom >= MAX_ZOOM) {
            zoom = MAX_ZOOM;
        } 
        model = new DefaultMapModel(); 
        
        Map<String, String> tempMap = new HashMap();
        tempMap.putAll(mapDataFilter);
        
        LatLngBounds bound = event.getBounds();
           
        GeoData geoData = new GeoData(bound.getNorthEast().getLat(), bound.getSouthWest().getLat(), bound.getNorthEast().getLng(), bound.getSouthWest().getLng()); 
      
        if(zoom >= MAX_ZOOM) {
            geoData = solrSearch.spatialSearchAllData(searchText, tempMap, isType, isImage, geoData); 
            addMarkersIntoModel(geoData.getMapRecords(), false);   
        } else { 
            Map<String, Object> map = solrSearch.geoSpatialGroupSearch(searchText, tempMap, isType, isImage, geoData); 
            if(map != null) {
                setMapData(map, geoData); 
            } 
        } 
    }
    
    private LatLng getLatLng(LatLng coordOrg, int index, int size) {
        double d = 1; 
        
        
          
        if(zoom == 1) {
            d = 18;  
        } else if(zoom == 2) {
            d = 9; 
        } else if(zoom == 3) {
            d = 5; 
        } else if(zoom == 4) {
            d = 3; 
        } else if (zoom == 5) {
            d = 2; 
        } else if (zoom == 6) {
            d = 1; 
        } else if(zoom == 7) {
            d = 0.5; 
        } else if(zoom == 8) {
            d = 0.3; 
        } else if(zoom == 9) {
            d = 0.1; 
        } else if(zoom == 10) {
            d = 0.07; 
        } else if(zoom == 11) {
            d = 0.04;  
        } else if(zoom == 12) {
            d = 0.02; 
        } else if(zoom == 13) {
            d = 0.008;  
        } else if(zoom == 14) {
            d = 0.006;
        } else if(zoom == 15) {
            d = 0.005;
        }
          
        
        dAngle = 0;
        angInc = 30;
        if(size == 2) {
            angInc = 180;  
        } else if(size == 3) {
            angInc = 120;
        } else if(size == 4) {
            angInc = 90;
        } else if(size == 5) {
            angInc = 72;
        } else if(size == 6) {
            angInc = 60;
        } else if(size == 7) {
            angInc = 51;
        } else if(size == 8) {
            angInc = 45;
        } else if(size == 9) {
            angInc = 40;
        } else if(size == 10) {
            angInc = 36;
        } else if(size == 11) {
            angInc = 32.7;
        } 
         
        dAngle = index * angInc * Math.PI / 180;  
        
//        double x = d * Math.cos(dAngle);
//        double y = d * Math.sin(dAngle); 
        
        return new LatLng(coordOrg.getLat() + d * Math.sin(dAngle), coordOrg.getLng() + d * Math.cos(dAngle));  
    }
     
    public void onMarkerSelect(OverlaySelectEvent event) {
        
        logger.info("onMarkerSelect "); 
        
        Overlay overlay = event.getOverlay(); 
        if (overlay instanceof Marker) {                            // if it is a marker
             
            Marker marker = (Marker) overlay; 
            if (marker.getData() instanceof MapRecord) {
                selectedRecord = (MapRecord) marker.getData();
                locality = selectedRecord.getLocality();
                coordinate = selectedRecord.getLat() + " -- " + selectedRecord.getLnt();
                taxon = selectedRecord.getFullname();  
                infoLink = 0;
            } else if(marker.getData() instanceof List) {
                infoLink = 1;
                records = (List<MapRecord>)marker.getData();
                if(records != null && !records.isEmpty()) { 
                    MapRecord mapRecord = records.get(0);
                    String geopoint = mapRecord.getGeopoint(); 
                    if(records.size() <= 12) {  
                        if(openedMarkerMap.containsKey(geopoint)) {                         // close icon
                            marker.setIcon(imagePath_1);
                            List<Marker> markers = openedMarkerMap.get(geopoint);
                            
                            List<Marker> modelMarkers = model.getMarkers();
                            List<Polyline> polylines = model.getPolylines();
                            
                            markers.stream().forEach((m) -> {
                                modelMarkers.remove(m);
                            });
                            
                            List<Polyline> polylineList = polylineMap.get(geopoint);
                            polylineList.stream().forEach((polyline) -> {
                                polylines.remove(polyline);
                            });
                            polylineMap.remove(geopoint);
                            openedMarkerMap.remove(geopoint);
                        } else {
                            infoLink = 2; 
                            List<Marker> markers = new ArrayList<>();
                            List<Polyline> polylinelist = new ArrayList<>();
                            int count = 0;
                             
                            double lat = mapRecord.getLat1();
                            double lng = mapRecord.getLnt1();
                            LatLng coordOrg = new LatLng(lat, lng);  
//                            LatLng coordOrg = buildCenterCoord(mapRecord);
                            
                            int size = records.size();
                            Polyline polyline;
                            Marker m;
                            LatLng coord;
                            StringBuilder sb;
                            for(MapRecord record : records) {  
                                sb = new StringBuilder();
                                sb.append(record.getFullname());
                                sb.append("\n");
                                sb.append(record.getLocality());
                                sb.append("\n");
                                sb.append(record.getLat());
                                sb.append(" -- ");
                                sb.append(record.getLnt());
                                 
                                coord = getLatLng(coordOrg, count, size);
                                  
                                m = new Marker(coord, sb.toString(), record, imagePath_yellowMarker, imagePath_yellowMarker);
                                markers.add(m);
                                model.addOverlay(m); 
                                count++;
                                
                                
                                //Polyline  
                                polyline = new Polyline();  
                                polyline.getPaths().add(coordOrg);  
                                polyline.getPaths().add(coord);  
                                 

                                polyline.setStrokeWeight(1);  
                                polyline.setStrokeColor("#750202");  
                                polyline.setStrokeOpacity(1);  
                                polylinelist.add(polyline);
                                  
                                model.addOverlay(polyline);  
                            }
                            marker.setIcon(imagePath_blackPlus);
                              
                            openedMarkerMap.put(geopoint, markers);
                            polylineMap.put(geopoint, polylinelist);
                        } 
                        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("searchform:largeMap");
                        //update panel  
                        RequestContext context = RequestContext.getCurrentInstance();   
                        context.update("searchform:largeMap"); 
 
                    } else {  
                        taxon = StringMap.getInstance().getString("total", locale) + records.size();

                        locality = mapRecord.getLocality();
                        coordinate = mapRecord.getLat() + " -- " + mapRecord.getLnt(); 
                    }   
                } 
            } 
        } else if(overlay instanceof Rectangle) {  
            RequestContext context = RequestContext.getCurrentInstance();  
            
     
            infoLink = 2;
            colors = new ArrayList<>(); 
            model = new DefaultMapModel();
            
            Rectangle rectangle = (Rectangle)overlay;
                 
            taxon = ""; 
            locality = ""; 
            coordinate = "";
//            LatLngBounds bound = new LatLngBounds(new LatLng(-90, -180), new LatLng(90, 180));
//            if(rectangle != null) {
//                bound = rectangle.getBounds(); 
//            }
            
            LatLngBounds bound = rectangle.getBounds(); 
             
            double north = bound.getNorthEast().getLat();
            double south = bound.getSouthWest().getLat();
            double east = bound.getNorthEast().getLng();
            double west = bound.getSouthWest().getLng();
            minlat = north;
            maxlat = south;
            minlng = east;
            maxlng = west;  
             
            setZoom();
            
            GeoData geoData = new GeoData(south, north,  west, east); 
          
            Map<String, String> tempMap = new HashMap<>();
            tempMap.putAll(mapDataFilter);  
            
            
        
            if (zoom == MAX_ZOOM) {
                geoData = solrSearch.spatialSearchAllData(searchText, tempMap, isType, isImage, geoData);
                List<MapRecord> mapRecords = geoData.getMapRecords();
                addMarkersIntoModel(mapRecords, false);
            } else {
                Map<String, Object> map = solrSearch.geoSpatialGroupSearch(searchText, tempMap, isType, isImage, geoData); 
                if(map != null) {
                    setMapData(map, geoData); 
                } 
            }
            FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("searchform:largeMap");
            //update panel  
            
            Collection<String> updateIds = new ArrayList<>();
            updateIds.add("searchform:largeMap");
            updateIds.add("searchform:colorpanel");
            
            context.update(updateIds);
//            context.update("searchform:largeMap");  
        } 
    } 
    

    

    private void setZoom() {
         
        int oldZoom = zoom;
          
        double d;
        if(maxlng < minlng) {
            d = 360 + maxlng - minlng;  
        } else {
            d = maxlng - minlng;
        }
 
        int zoomlevelLng = 1;
        if(d >= 240) {
            zoomlevelLng = 1;
        } else if(d < 240 && d >= 120) {
            zoomlevelLng = 2;
        } else if(d < 120 && d >= 62) {
            zoomlevelLng = 3;
        } else if(d < 62 && d >= 25) {
            zoomlevelLng = 4;
        } else if (d < 25 && d >= 10) {
            zoomlevelLng = 5;
        } else if (d < 10 && d >= 7) {
            zoomlevelLng = 6;
        } else if(d < 7) {
            if(zoom > 7) {
                zoomlevelLng = zoom;
            } else {
                zoomlevelLng = 7;
            } 
        }

        double latd = maxlat - minlat;  
        
        int zoomlevelLat = 1;  
        if(latd >= 100) {
            zoomlevelLat = 1;
        } else if(latd < 100 && latd >= 60) {
            zoomlevelLat = 2;
        } else if (latd < 60 && latd >= 32) {
            zoomlevelLat = 3;
        } else if (latd < 32 && latd >= 12) {
            zoomlevelLat = 4;
        } else if(latd < 12 && latd >= 6) {
            zoomlevelLat = 5;
        } else if(latd < 6 && latd >= 3) {
            zoomlevelLat = 6;
        } else if(latd < 3) {
            if(zoom > 7) {
                zoomlevelLat = zoom;
            } else {
                zoomlevelLat = 7;
            } 
        }

        if(zoomlevelLng > zoomlevelLat) {
            zoom = zoomlevelLat;
        } else {
            zoom = zoomlevelLng;
        }  
        if(oldZoom != zoom) {
            centerLat = minlat + latd / 2;
            centerLng = minlng + d / 2;
        } 
    }
    
    
    
      
    public String getCoordinate() {
        return coordinate;
    }

    public String getLocality() {
        return locality;
    }

    public String getSearchText() {
        return searchText;
    }

    public MapRecord getSelectedRecord() {
        return selectedRecord;
    }

    public String getTaxon() {
        return taxon;
    }
    
    
     

    public List<String> getColors() { 
        
        newColors = new ArrayList();
        if(colors.contains("#F7C7C7")) {
            newColors.add("#F7C7C7");
        }
        if(colors.contains("#E98990")) {
            newColors.add("#E98990");
        }
        if(colors.contains("#DA323D")) {
            newColors.add("#DA323D");
        }
        if(colors.contains("#A2002E")) {
            newColors.add("#A2002E");
        }
        if(colors.contains("#8E0028")) {
            newColors.add("#8E0028");
        }
        if(colors.contains("#790022")) {
            newColors.add("#790022");
        } 
        return newColors;
    }
      
    public Double getCenterLat() {
        return centerLat;
    }

    public Double getCenterLng() {
        return centerLng;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public MapModel getModel() {
        return model;
    }

    public int getZoom() {
        return zoom;
    }
    
    
    public int getInfoLink() {
        return infoLink;
    }  
    
    
    public List<MapRecord> getRecords() {
        return records;
    } 
}
