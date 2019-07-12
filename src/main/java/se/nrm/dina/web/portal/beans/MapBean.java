package se.nrm.dina.web.portal.beans;

import java.io.Serializable;  
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;  
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.PointSelectEvent;   
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  
import se.nrm.dina.web.portal.model.SolrRecord; 

/**
 *
 * @author idali
 */ 
@ManagedBean
@SessionScoped 
public class MapBean implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
 
    private MapModel advancedModel;   
      
    private Marker marker; 
    private String info; 

    public MapBean() {  
        advancedModel = new DefaultMapModel();   
    }
     
  
    public MapModel getAdvancedModel(SolrRecord record) {
        
        logger.info("getAdvancedModel: {}", record);
        
        advancedModel = new DefaultMapModel();   
        if(record != null) {
            LatLng coord = new LatLng(record.getLat1(), record.getLnt1());
            //Icons and Data  
            advancedModel.addOverlay(new Marker(coord, record.getLocality(), null, "http://maps.google.com/mapfiles/ms/micons/red-dot.png"));
        } else {
            LatLng coord = new LatLng(0, 0);
            advancedModel.addOverlay(new Marker(coord, "", null, "http://maps.google.com/mapfiles/ms/micons/red-dot.png")); 
        }  
        return advancedModel;
    }

    public void onMarkerSelect(OverlaySelectEvent event) {
        marker = (Marker) event.getOverlay(); 
    }

    public Marker getMarker() {
        return marker;
    }
   

    public String getInfo() {
        return info;
    }
  
    public void onPointSelect(PointSelectEvent event) {
        LatLng latlng = event.getLatLng(); 
        addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Point Selected", "Lat:" + latlng.getLat() + ", Lng:" + latlng.getLng()));
    }
 
    public void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    } 
}
