package se.nrm.dina.web.portal.beans;

import java.io.Serializable;  
import java.util.ArrayList; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner; 
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.enterprise.context.SessionScoped;  
import javax.inject.Inject;
import javax.inject.Named; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  
import se.nrm.dina.web.portal.model.MorphBankImage;
import se.nrm.dina.web.portal.services.solr.SolrSearch;

/**
 *
 * @author idali
 */
@Named("galleriaBean")
@SessionScoped
public class GalleriaBean implements Serializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass()); 
    
    private final int PARTS_COUNT = 17;
    private final int VIEW_COUNT = 6;
    private final static String ALL = "all";
       
    private boolean type;
    private boolean map;
    private Map<String, String> filterMap; 
    private Map<String, String> filters;
    private String value;
    
    private String view = "all";
    private String sex = "all";
    private String stage = "all";
    private String part = "all";
     
    private boolean allView = false;
    private boolean allParts = false;
    private boolean allSexes = false;
    private boolean allStages = false;
    private boolean defaultView = true;
    
    
    private boolean labelView = false;
    private boolean dorsalView = false;
    private boolean ventralView = false;
    private boolean lateralView = false;
    private boolean frontalView = false;
    private boolean caudalView = false;
    
    
    private List<String> viewList;
    private List<String> partsList;
    private List<String> sexList;
    private List<String> stageList;
    
    private StringJoiner sj;
    
    private String mbid;
    
    @Inject
    private SolrSearch solrSearch;
      
    private List<List<MorphBankImage>> images; 
    private List<List<MorphBankImage>> results;  
    
    private static final String MORPHBANK_BROWSER_URL = "http://morphbank.nrm.se/Browse/ByImage/?specimenId=";

    
    public GalleriaBean() { 
        viewList = new ArrayList<>(); 
        partsList = new ArrayList<>();
        sexList = new ArrayList<>();
        stageList = new ArrayList<>();
        
        images = new ArrayList<>(); 
        filterMap = new HashMap<>();
        filters = new HashMap<>(); 
    }
    
    Predicate<String> pAll = v -> v.equals(ALL);
    Predicate<String> pNotAll = v -> !v.equals(ALL);
     
    public void selectParts() {
        logger.info("selectParts");
        
        boolean isMatch = partsList.parallelStream().anyMatch(pAll); 
        if(allParts) {
            allParts = false;   
            if(isMatch) {
                partsList.remove(ALL);
            } else {
                partsList = new ArrayList<>();
            } 
        } else {
            if(isMatch) {
                allParts = true;
            } else {
                if(partsList.stream().count() == PARTS_COUNT) {
                    allParts = true;
                }
            }
        } 
         
        if (allParts) {
            partsList = new ArrayList<>();
            partsList.add("all");
            partsList.add("abdomen");
            partsList.add("face");
            partsList.add("genitalia");
            partsList.add("wings");
            partsList.add("head");
            partsList.add("legs"); 
            partsList.add("lobe");
            partsList.add("pronotum");
            partsList.add("vari");
            partsList.add("mesonotum");
            partsList.add("posterior");
            partsList.add("palps");
            partsList.add("tarsi"); 
            partsList.add("labrum");
            partsList.add("/notum/");
            partsList.add("mouth");
            partsList.add("chelicerae"); 
        }  

        viewOptionChanged();
    }

    public void selectViews() {
        logger.info("selectViews");

        boolean isMatch = viewList.parallelStream().anyMatch(pAll);
        if (allView) {
            allView = false;
            if (isMatch) {
                viewList.remove(ALL);
            } else {
                viewList = new ArrayList<>();
            }
        } else {
            if (isMatch) {
                allView = true;
            } else {
                if (viewList.stream().count() == VIEW_COUNT) {
                    allView = true;
                }
            }
        }  
        
        if (allView) {
            viewList = new ArrayList<>();
            viewList.add("all");
            viewList.add("label");
            viewList.add("dorsal");
            viewList.add("ventral");
            viewList.add("lateral");
            viewList.add("frontal");
            viewList.add("caudal"); 
        }  
        viewOptionChanged();
    }
    
    
    public void selectSexes() {
        logger.info("selectSexes");

        boolean isMatch = sexList.parallelStream().anyMatch(pAll);
        if (allSexes) {
            allSexes = false;
            if (isMatch) {
                sexList.remove(ALL);
            } else {
                sexList = new ArrayList<>();
            }
        } else {
            if (isMatch) {
                allSexes = true;
            } else {
                if (sexList.stream().count() == 2) {
                    allSexes = true;
                }
            }
        }
  
        if (allSexes) {
            sexList = new ArrayList<>();
            sexList.add("all");
            sexList.add("/male/");
            sexList.add("female"); 
        } 
         
        viewOptionChanged();
    }
    
    public void selectStages() {
        logger.info("selectStages"); 

        boolean isMatch = stageList.parallelStream().anyMatch(pAll);
        if (allStages) {
            allStages = false;
            if (isMatch) {
                stageList.remove(ALL);
            } else {
                stageList = new ArrayList<>();
            }
        } else {
            if (isMatch) {
                allStages = true;
            } else {
                if (stageList.stream().count() == 2) {
                    allStages = true;
                }
            }
        }
         
        if (allStages) {
            stageList = new ArrayList<>();
            stageList.add("all");
            stageList.add("larva");
            stageList.add("adult"); 
        } 
         
        viewOptionChanged();
    }
    
    public void setSearchCondition(String value, Map<String, String> filterMap, boolean type, boolean map) { 
        
        logger.info("setSearchCondition");
        images = new ArrayList<>();
        results = new ArrayList<>();
        
        allView = false;
        allParts = false;
        allSexes = false;
        allStages = false;
        defaultView = true;
        viewList = new ArrayList<>();
        partsList = new ArrayList<>();
        sexList = new ArrayList<>();
        stageList = new ArrayList<>();
         
        results = solrSearch.getMBImages(value, filterMap, 0, type, map); 
        
        images.addAll(results); 
         
        this.type = type;
        this.map = map; 
        this.filterMap = filterMap;
        this.value = value;
    }
        
    public List<List<MorphBankImage>> getMbImages() {    
        return images; 
    }   
    
    
    
    private void viewOptionChanged() { 
        logger.info("changeViewEvent " );
         
        defaultView = (viewList.isEmpty() && partsList.isEmpty() && sexList.isEmpty() && stageList.isEmpty());
        if(defaultView) {
            images = solrSearch.getMBImages(value, filterMap, 0, type, map);   
        } else {
            filters = new HashMap<>(); 
            filters.putAll(filterMap);
 
            StringBuilder sb = new StringBuilder();
            sb.append("+(");
            sb.append(value);
            sb.append(") ");
            
            
            buildString(sb, viewList);
            buildString(sb, partsList);
            buildString(sb, sexList);
            buildString(sb, stageList);
              
            List<String> joinlist = Stream.of(viewList, partsList, sexList, stageList)
                                            .flatMap(x -> x.stream())
                                            .filter(x -> !x.equals(ALL))
                                            .collect(toList());

            images = solrSearch.getMBImagesWithOption(sb.toString(), filters, joinlist, type, map);   
        } 
    } 
    
    private void buildString(StringBuilder sb, List<String> selectedList) {
        if (!selectedList.isEmpty()) {
            sb.append(" +(mbview: (*");
            sj = new StringJoiner("* *");
            selectedList.stream()
                    .filter(pNotAll)
                    .forEach(v -> {
                        sj.add(v);
                    });
            sb.append(sj.toString());
            sb.append("*)) ");
        }
    }
    
    public boolean isAllView() {
        return allView;
    }

    public void setAllView(boolean allView) {
        this.allView = allView;
    }

    public boolean isCaudalView() {
        return caudalView;
    }

    public void setCaudalView(boolean caudalView) {
        this.caudalView = caudalView;
    }

    public boolean isDorsalView() {
        return dorsalView;
    }

    public void setDorsalView(boolean dorsalView) {
        this.dorsalView = dorsalView;
    }

    public boolean isFrontalView() {
        return frontalView;
    }

    public void setFrontalView(boolean frontalView) {
        this.frontalView = frontalView;
    }

    public boolean isLabelView() {
        return labelView;
    }

    public void setLabelView(boolean labelView) {
        this.labelView = labelView;
    }

    public boolean isVentralView() {
        return ventralView;
    }

    public void setVentralView(boolean ventralView) {
        this.ventralView = ventralView;
    }

    public boolean isLateralView() {
        return lateralView;
    }

    public void setLateralView(boolean lateralView) {
        this.lateralView = lateralView;
    }
 
    
 
    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getMbid() {
        return mbid;
    }

    public List<String> getViewList() {
        return viewList;
    }

    public void setViewList(List<String> viewList) {
        this.viewList = viewList;
    }

    public List<String> getPartsList() {
        return partsList;
    }

    public void setPartsList(List<String> partsList) {
        this.partsList = partsList;
    }

    public List<String> getSexList() {
        return sexList;
    }

    public void setSexList(List<String> sexList) {
        this.sexList = sexList;
    }

    public List<String> getStageList() {
        return stageList;
    }

    public void setStageList(List<String> stageList) {
        this.stageList = stageList;
    }
    
    public String getMorphBankId() {
        return MORPHBANK_BROWSER_URL + mbid;
    } 
}
