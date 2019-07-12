package se.nrm.dina.web.portal.beans;
  
import java.io.Serializable;  
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped; 
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;    
import se.nrm.dina.web.portal.model.CollectionData;
import se.nrm.dina.web.portal.model.StatisticData;
import se.nrm.dina.web.portal.services.solr.SolrSearch; 

/**
 *
 * @author idali
 */ 
@Named("statistic")
@SessionScoped
public class StatisticBean implements Serializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
       
    private StatisticData statisticData; 
    private HttpSession session;  
     
    private int totalWithImage;
    private int totalWithMap;
    private int totalType;
    private int totalInSweden;
    private int totalDNA; 
    
     
//    private int defaultTotalWithImage; 
    
    @Inject
    private SolrSearch solrSearch; 
    
    public StatisticBean() { 
        logger.info("StatisticBean");
        session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);    
    }
    
    @PostConstruct
    public void init() {  
         initData();
    }
    
    /**
     * initData - To initialize statistic data when application starts. 
     */
    private void initData() {
        
        logger.info("initData");
        // search with wildcard, no filter, no coordinates, no image, no dna
        StatisticData data = searchData("text:*", null, false, false, false);           
        
        Map<String, Integer> statisticMap = data.getDataMap(); 
        
        session.setAttribute("defaultResultWithMap", statisticMap.get("map"));
        session.setAttribute("defaultTypeResult", statisticMap.get("type"));
        session.setAttribute("defaultImageData", statisticMap.get("image")); 
        session.setAttribute("defaultStatisticData", data);
        session.setAttribute("defaultDataInSweden", statisticMap.get("sweden")); 
        session.setAttribute("defaultDNAData", statisticMap.get("dna")); 
    }
    
    /**
     * 
     * @param text
     * @param filterQueries
     * @param map
     * @param type
     * @param image
     * @return 
     */
    private StatisticData searchData(String text, Map<String, String> filterQueries, boolean map, boolean type, boolean image) {
        String locale = (String) session.getAttribute("locale");
        if (locale == null) {
            locale = "sv";
        } 
        if(text == null) {
            text = "*";
        } 
        return solrSearch.getStatisticData(text, locale, filterQueries, type, map, image);  
    }
    
    
    
    
     
    private void resetDefaultData() { 
        initData();
    }
    
    
    public StatisticData getDefaultStatisticData() {

        if (session != null) {
            if (session.getAttribute("defaultStatisticData") == null) {
                resetDefaultData();
            }
        } else {
            session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            resetDefaultData();
        } 
        return (StatisticData) session.getAttribute("defaultStatisticData");
    }

    public int getDefaultTotalDNA() {
        if(session.getAttribute("defaultDNAData") == null) {
            resetDefaultData();
        }
        return (Integer) session.getAttribute("defaultDNAData");
    }

    public int getDefaultTotalInSweden() {
        if(session.getAttribute("defaultDataInSweden") == null) {
            resetDefaultData();
        }
        return (Integer) session.getAttribute("defaultDataInSweden");
    }

    public int getDefaultTotalType() {
        if(session.getAttribute("defaultTypeResult") == null) {
            resetDefaultData();
        }
        return (Integer) session.getAttribute("defaultTypeResult");
    }

    public int getDefaultTotalWithImage() {
        if(session.getAttribute("defaultImageData") == null) {
            resetDefaultData();
        }
        return (Integer) session.getAttribute("defaultImageData");
    }

    public int getDefaultTotalWithMap() {
        if(session.getAttribute("defaultResultWithMap") == null) {
            resetDefaultData();
        }
        return (Integer) session.getAttribute("defaultResultWithMap");
    }
     
    public List<CollectionData> getCollections() {
        
        StatisticData data = (StatisticData) session.getAttribute("defaultStatisticData"); 
        return data.getCollections();
    }
    
    

    public void searchFilteredData(String text, Map<String, String> filterQueries, boolean map, boolean type, boolean image) {
        
        logger.info("searchFilteredData : {}", text); 
        statisticData = searchData(text, filterQueries, map, type, image);
        
        Map<String, Integer> statisticMap = statisticData.getDataMap();
        if (statisticMap != null && !statisticMap.isEmpty()) {
            totalType = statisticMap.get("type");
            totalWithMap = statisticMap.get("map");
            totalInSweden = statisticMap.get("sweden");
            totalDNA = statisticMap.get("dna");
            totalWithImage = statisticMap.get("image");
        }
    }
    
    
    
     
    public StatisticData getStatisticData() {
        return statisticData;
    }

    public int getTotalDNA() {
        return totalDNA;
    }

    public int getTotalInSweden() {
        return totalInSweden;
    }

    public int getTotalType() {
        return totalType;
    }

    public int getTotalWithImage() {
        return totalWithImage;
    }

    public int getTotalWithMap() {
        return totalWithMap;
    }

    public void setTotalDNA(int totalDNA) {
        this.totalDNA = totalDNA;
    }

    public void setTotalInSweden(int totalInSweden) {
        this.totalInSweden = totalInSweden;
    }

    public void setTotalType(int totalType) {
        this.totalType = totalType;
    }

    public void setTotalWithImage(int totalWithImage) {
        this.totalWithImage = totalWithImage;
    }

    public void setTotalWithMap(int totalWithMap) {
        this.totalWithMap = totalWithMap;
    }

    public void setStatisticData(StatisticData statisticData) {
        this.statisticData = statisticData;
    } 
}
