package se.nrm.dina.web.portal.controllers;
  
import java.io.Serializable;
import java.util.ArrayList;  
import java.util.Date;
import java.util.HashMap;  
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;  
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;  
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named; 
//import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;   
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  
import se.nrm.dina.common.util.HelpClass; 
import se.nrm.dina.web.portal.beans.ChartBean; 
import se.nrm.dina.web.portal.beans.ErrorReportBean;
import se.nrm.dina.web.portal.beans.GalleriaBean; 
import se.nrm.dina.web.portal.beans.GeoMap;
//import se.nrm.dina.web.portal.beans.MBImageWrapper; 
import se.nrm.dina.web.portal.beans.PagingNavigation; 
import se.nrm.dina.web.portal.beans.StatisticBean;
import se.nrm.dina.web.portal.beans.StyleBean;
import se.nrm.dina.web.portal.mailhandler.MailHandler;
import se.nrm.dina.web.portal.model.CollectionData; 
import se.nrm.dina.web.portal.model.MapRecord; 
import se.nrm.dina.web.portal.model.QueryBean;
import se.nrm.dina.web.portal.model.SolrRecord; 
import se.nrm.dina.web.portal.model.SolrResult; 
import se.nrm.dina.web.portal.model.StatisticDataInstitution;
import se.nrm.dina.web.portal.services.solr.SolrSearch;
import se.nrm.dina.web.portal.services.solr.SolrSearchHelper; 
import se.nrm.dina.web.portal.util.StringMap; 

/**
 *
 * @author idali
 */
@Named("search")
@SessionScoped
//@ApplicationScoped
public class SearchBean implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static final String RESULT_SET = "resultSet"; 
    private static final String SAVED_QUERY = "savedQuery"; 
    private static final String SAVED_SEARCH_TEXT = "searchText";
    private static final String SAVED_SELECTED_RECORDS = "savedSelectedRecords";
    private static final String SAVED_SEARCH_TYPE = "savedSearchType";
    private static final String WILD_CHAR = "*";
    
    private  Map<String, String> paramMap;
    
    // Search method.  Set default search to simple search
    private boolean advance = false;
    
    private SolrResult solrResult; 
    
    
    private boolean hideadvance = true;
    
    private HttpSession session; 
    
    
    @Inject
    private StatisticBean statistic;
    
    /** Constructor **/
    public SearchBean() {
        
        session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true); 
//        servername = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getServerName();
        
        paramMap = new HashMap<>();
 
        QueryBean qb = new QueryBean("", "contains", "text", "");
        queryBeans = new ArrayList<>();
        queryBeans.add(qb);
        
        locale = (String) session.getAttribute("locale");
        if (locale == null) {
            locale = "sv";
        }
        
        defaultSearchText = StringMap.getInstance().getSearchDefaultText(locale);
        input = defaultSearchText;
        
        exportDataSet = new ArrayList<>();
        resultList = new ArrayList<>();  
    }
    
    
    
    
    
    public boolean isHideadvance() {
        return hideadvance;
    }
    
    
    /**
     * fullTextSearch - full text search
     * full text search is simple search starts when user start type in search field
     * 
     */
    public void fullTextSearch() {
//        logger.info("fullTextSearch : {} ", input); 
           
        advance = false;
        session.removeAttribute(SAVED_SEARCH_TEXT);                             // remove previous search text
        preSearch();                                                            // clear data for new search
        
        String searchText = WILD_CHAR;  
        if (!input.equals(defaultSearchText)) {                             
            if (input != null && input.length() >=2) { 
                searchText = SolrSearchHelper.getInstance().buildFullTextSearchText(input);
            }   
        }  
        session.setAttribute(SAVED_SEARCH_TYPE, false);
 
//        updateResult = ":searchform:result welcomepagepanel";                                   
        try { 
            Map<String, String> savedFilterMap = getSavedFilterMap();
            solrResult = search(searchText, savedFilterMap, 0);                                 // search
            session.setAttribute(SAVED_SEARCH_TEXT, searchText);
            statistic.searchFilteredData(searchText, savedFilterMap, map, type, image);         // statistic data with new search query
 
            setResultViewWithNewFullTextSearch();  
        } catch (EJBException e) {
            if (e.getCause().getClass().getSimpleName().equals("SolrServerConnectionException")) {
                addError("Server is not available", e.getLocalizedMessage());
            }
        }   
    } 
    
        
    /**
     * init data before search
     */
    private void preSearch() {
        solrResult = null;
        displayOneDetail = false; 
         
        session.removeAttribute("mapList");
                
        resultList = new ArrayList<>();                               // reset result list
        navigationBean.setCurrentPage(1);                                       // reset current page to 1
        overMaxium = false;  
        exportDataSet = new ArrayList<>();                            // reset exportDataSet
        session.removeAttribute(RESULT_SET);                                    // remove saved search result
        navigationBean.resetdata();                                             // reset navigation bean
        pageDataMap = new LinkedHashMap<>();                 // clear page data
         
        
        resultsExist = false; 
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
     
    /** --------------------------------------- beginning start up value ----------------------------------- **/
 
    private int resultview = 0;                                     // Default result view, simple list
     
    private int welcomeContent = 0;                                 // Default top menu index   
    
    
    private String numDisplay = "10";                               // default number of hits per page
    private String sortby = "score";                                // solr sort score   
    private String locale = "sv";                                   // default locale is swedish
    
    private boolean image = false;
    private boolean map = false;
    private boolean type = false;
    
    
    
    
    private boolean expandsAdvance = true;
    // Search method.  Set default search to simple search
    
    /** --------------------------------------- end start up value ----------------------------------- **/
     
    
    private boolean welcomePage = true;
    private boolean resultPage = false;
    private boolean overMaxium = false;
    private boolean searchStart = false; 
      

    private boolean resultsExist = false; 
    
    private String updateResult = ":searchform:result welcomepagepanel";
     
    private List<SolrRecord> selectedRecords = new ArrayList<>(); 
    private List<String> checkedRecordsCatlogNumList = new ArrayList<>();
 
    private Map<Integer, SolrResult> pageDataMap = new LinkedHashMap<>();
    
    
    private List<SolrRecord> resultList;
    
    private boolean displayOneDetail = false;
    
    private boolean selectall = false;
     
    private Map<String, String> queries = new HashMap<>();
    
     
     
    private String querytext;
    private String input;
    private String defaultSearchText;
     
    
    
    
    private SolrRecord record; 
    
      
    
    @Inject
    private ChartBean chart;
    @Inject
    private GeoMap gmap;
    @Inject
    private GalleriaBean galleriaBean;
       
    @Inject
    private StyleBean style;
    @Inject
    private SolrSearch solrSearch;
//    @Inject
//    private MBImageWrapper imgWrapper;
    @Inject
    private ErrorReportBean errorbean;
    @Inject
    private MailHandler mail;
    @Inject
    private Languages languages;

    @Inject
    private PagingNavigation navigationBean;
    
    private List<SolrRecord> exportDataSet;
     
    private List<QueryBean> queryBeans;
    
    private String searchQuery = ""; 
    private String param = "";
    
    private List<String> allImagesPaths;
    
//    private final String servername; 
//    private boolean isLocal;
     
    RequestContext requestContext; 
    FacesContext context;
    

    
 
    private boolean isNewCall() { 
        return paramMap.containsKey("taxonname") || paramMap.containsKey("param");
    }
    
    
    
    /** 
     *  Initialize data after class constructed
     */
    @PostConstruct
    public void init() {
//        logger.info("init : {}", searchQuery);
        context = FacesContext.getCurrentInstance();  
        boolean isPostBack = context.isPostback();        // false if page is start up or reloaded by browser  
             
        
        requestContext = RequestContext.getCurrentInstance();   
        if(!isPostBack) {     
            
            session = (HttpSession)context.getExternalContext().getSession(true);                       // renew session
            paramMap = context.getExternalContext().getRequestParameterMap();
            
//            boolean isNew = false;
//            // check if page called from another application
//            if(paramMap.containsKey("taxonname") || paramMap.containsKey("param")) {
//                isNew = true;
//            }
            
            if(isNewCall()) {
                Map<String, String> filterMap = new HashMap<>();
                
                
                cleardata();  
                if(paramMap.containsKey("taxonname")) {  
                    searchQuery = paramMap.get("taxonname");  
                } else if (paramMap.containsKey("param")) {                                                  // called from dnakey or smtp
                    param = paramMap.get("param");
                    if (param.equals("dnakey")) {                                                            // called from dnakey
                        searchQuery = paramMap.get("catalogNumber"); 
                    } else if (param.equals("inventory")) {                                                  // called from smtp
                        searchQuery = paramMap.get("key"); 
                        filterMap.put("cln:", String.valueOf(StringMap.getInstance().getCollectionCodeByName("SMTP_INV")));
                        queries.put("Collection", "Sweden Malaise Tra...");
                        session.setAttribute(SAVED_QUERY, filterMap);
                    }
                }  
                input = searchQuery;   
                fullTextSearch();  
            } else {
                changeTab(0);
                resultview = 0;
                input = getDefaultText();
            }
            requestContext.update("searchform:simpleSearchPanel"); 
        } else { 
            String savedText = getSavedSearchText();
            if(!savedText.equals(WILD_CHAR)) {
                searchQuery = savedText;
            }
        }
    }
    
    private String getDefaultText() {

        if (locale == null) {
            locale = "sv";
        }
        defaultSearchText = StringMap.getInstance().getSearchDefaultText(locale);
        return defaultSearchText;
    }
    
//    private boolean getSavedSearchType() { 
//           
//        if(session.getAttribute(SAVED_SEARCH_TYPE) == null) {
//            return false;
//        } else {
//            return (Boolean)session.getAttribute(SAVED_SEARCH_TYPE);
//        } 
//    }
    
    public void closePage() {
        logger.info("closePage");
//        FacesContext.getCurrentInstance().getExternalContext().invalidateSession(); 
    }
    
    /**
     * alldata - simple search to retrieve all records
     */
    public void alldata() {
        logger.info("alldata" );
    
        cleardata();
        
        solrResult = search(WILD_CHAR, new HashMap<>(), 0); 
        statistic.searchFilteredData(WILD_CHAR, new HashMap<>(), false, false, false);
        setResultView(); 
    }
    
    /**
     * searchwithtype - retrieve all document in Sweden
     */
    public void searchinsweden() {
        logger.info("searchinsweden : {}", welcomePage); 

        if(welcomePage) {
            cleardata();
        } else {
            preSearch(); 
        }
         
        Map<String, String> savedFilterMap = getSavedFilterMap();                       // get saved filter query
        savedFilterMap.put("cy:", "*sweden*");                                          // add filter into query
        session.setAttribute(SAVED_QUERY, savedFilterMap);                              // save new query in session
        
        queries.put("Sweden", "");                                                      // add filter in query filter in ui
        String searchText = getSavedSearchText();                                       // build search text
        solrResult = search(searchText, savedFilterMap, 0);                             // search
 
        statistic.searchFilteredData(searchText, savedFilterMap, map, type, image);         // research for statistic data
        setResultView();                                                                // set ui
    }
    
    
    /**
     * searchwithmap - simple search filter with map
     */
    public void searchwithmap() {
        logger.info("searchwithmap"); 
        if(welcomePage) {
            cleardata();
        } else {
            preSearch(); 
        }
        Map<String, String> savedFilterMap = getSavedFilterMap(); 
        map = true; 
        String searchText = getSavedSearchText(); 
        solrResult = search(searchText, savedFilterMap, 0);
        queries.put("Map", "");  
        
        statistic.searchFilteredData(searchText, savedFilterMap, map, type, image);
        setResultView(); 
    }

    /**
     * searchwithtype - simple search filter with type
     */
    public void searchwithtype() {
        logger.info("searchwithtype : {}", welcomePage); 
        if(welcomePage) {
            cleardata();
        } else {
            preSearch(); 
        }
        Map<String, String> savedFilterMap = getSavedFilterMap();  
        String searchText = getSavedSearchText(); 
         
        type = true; 
        queries.put("Type", "true"); 
        
        solrResult = search(searchText, savedFilterMap, 0);
        statistic.searchFilteredData(searchText, savedFilterMap, map, type, image);
        setResultView(); 
    }
    
    
    public void searchwithimages() {
        logger.info("searchwithimages : {}", welcomePage); 
        if(welcomePage) {
            cleardata();
        } else {
            preSearch(); 
        }
        Map<String, String> savedFilterMap = getSavedFilterMap();  
        String searchText = getSavedSearchText();
         
        image = true; 
        queries.put("Image", "true"); 
        
        solrResult = search(searchText, savedFilterMap, 0);
        statistic.searchFilteredData(searchText, savedFilterMap, map, type, image);
        setResultView(); 
    }
  
    public void searchdna() {
        logger.info("searchdna");
 
        if(welcomePage) {
            cleardata();
        } else {
            preSearch(); 
        }
        Map<String, String> savedFilterMap = getSavedFilterMap(); 
        savedFilterMap.put("dna:", "*"); 
        queries.put("DNA", "");  
        
        String searchText = getSavedSearchText();
        session.setAttribute(SAVED_QUERY, savedFilterMap);
        solrResult = search(searchText, savedFilterMap, 0);
 
        statistic.searchFilteredData(searchText, savedFilterMap, map, type, image);
        setResultView(); 
        pageDataMap.put(1, solrResult);
    }
    


    /**
     * searchCollection - search filter with collection code
     * @param collection
     */
    public void searchCollection(CollectionData collection) {
        logger.info("searchCollection : {}", collection.getCode());
  
        if(welcomePage) {
            cleardata();
        } else {
            preSearch(); 
        }
        
        Map<String, String> savedFilterMap = getSavedFilterMap();  
        savedFilterMap.put("cln:", collection.getCode());  
        session.setAttribute(SAVED_QUERY, savedFilterMap);
        
        queries.put("Collection", collection.getShortName());
        
        String searchText = getSavedSearchText();
        solrResult = search(searchText, savedFilterMap, 0);
 
        statistic.searchFilteredData(searchText, savedFilterMap, map, type, image);
        setResultView();  
    }

    /**
     * institutiondata -- add institution code into search queries and search
     * 
     * @param institution
     */
    public void institutiondata(StatisticDataInstitution institution) {
        logger.info("institutiondata : {}", institution.getInstCode()); 
        
        if(welcomePage) {
            cleardata();
        } else {
            preSearch(); 
        }
        Map<String, String> savedFilterMap = getSavedFilterMap();  
        savedFilterMap.put("institution", institution.getInstCode()); 
        session.setAttribute(SAVED_QUERY, savedFilterMap);  
        queries.put("Institution", institution.getInstitution());
        
        String searchText = getSavedSearchText();
        solrResult = search(searchText, savedFilterMap, 0);
 
        statistic.searchFilteredData(searchText, savedFilterMap, map, type, image);
        setResultView();  
    } 
    
    
    private void setResultViewWithNewFullTextSearch() {
        logger.info("setResultViewWithNewFullTextSearch : {} - {}", solrResult, resultview );
        
        int numFound;
        welcomePage = false; 
        if (solrResult != null) {
            numFound = (int) solrResult.getNumFound();
            if(numFound > 10000) {
                overMaxium = true;
            }
            session.setAttribute(RESULT_SET, solrResult);
            int currentPage = navigationBean.resetPage(solrResult, getNumPerPage(), numFound);
            pageDataMap.put(currentPage, solrResult);  
            resultList = solrResult.getRecords();  
            resultsExist = true; 
        } else {
            resultList = new ArrayList<>(); 
            resultsExist = false;
        }  
        
        if(resultview > 1) {
            resultview = 0;
        }
        searchStart = true;
        resultPage = true;   
    }
    
    
    private SolrResult search(String searchText, Map<String, String> filterMap, int start) {  
        logger.info("search : {}", type + "--  " + map + image);
         
        int numPerPage = getNumPerPage();    
         
        try {
            return solrSearch.searchWithQueryFilter(searchText, filterMap, start, numPerPage, type, map, image, sortby); 
        } catch (EJBException e) { 
            if (e.getCause().getClass().getSimpleName().equals("SolrServerConnectionException")) {
                logger.error("Solr is not available");
                addError("Solr is not available", e.getLocalizedMessage());
            }
        }  
        return null;
    } 
    
        

    
    
    
    
    
    
    
    
 
    
    
    
    
    public List<String> querycomplete(QueryBean bean) {  
        
        logger.info("querycomplete : {} -- {}", bean.getValue(), bean.getField());
        String fieldName = bean.getField();
        String value = bean.getValue();
        String content = bean.getContent();
          
        switch (fieldName) {
            case "text":
                return solrSearch.autoCompleteSearchAllField(value, content);
            case "ftx":
            case "eftx":
                return solrSearch.autoCompleteTaxon(value, content);
            case "asn":
                return solrSearch.autoCompleteUseFacet(value, content, "asn");
            case "cn":
                return solrSearch.autoCompleteUseFacet(value, content, "cn");
            default:
                return solrSearch.autoCompleteSearch(value, fieldName, content);
//            return solrSearch.autoComplete(value, fieldName, content);
        } 
    }
    
    
    
    
    
    
    
    
    
    
        
    /** ########################## page navigation ####################### **/
    /**
     * nextPage
     */
    public void nextPage() {
        logger.info("nextPage");

        int currentPage = navigationBean.getCurrentPage();                              // current page
        int totalPages = navigationBean.getTotalPages();                                // total pages 
           
        int numPerPage = getNumPerPage();                                               // number result display in page
        if (currentPage < totalPages) {
            navigationBean.setNextPage(numPerPage);                                     // if current page is not last page, set current page to next page
            setPageData(numPerPage);
        } 
    }
    
    
    /**
     * previousPage
     */
    public void previousPage() {
        logger.info("previousPage");

        int currentPage = navigationBean.getCurrentPage(); 
        int numPerPage = getNumPerPage();
        
        if (currentPage > 1) {                                                                      // if current page is not first page, set current page to previous page
            navigationBean.setPreviousPage(numPerPage);  
            setPageData(numPerPage);
        }
    }
    
    
    /**
     * firstPage
     */
    public void firstPage() {
        logger.info("firstPage"); 
        navigationBean.setFirstPage();                                     
        setPageData(getNumPerPage()); 
    }
    
    public void lastPage() {
        logger.info("firstPage"); 
             
        int numPerPage = getNumPerPage();
        navigationBean.setLastPage(numPerPage);                               
        setPageData(getNumPerPage()); 
    }
    

    
    
    /**
     * changePage
     * @param pageNum
     */
    public void changePage(int pageNum) {
        logger.info("changePage : {}", pageNum);

        int numPerPage = getNumPerPage();
        int start = (pageNum - 1) * numPerPage;

        navigationBean.setStart(start);
        navigationBean.setCurrentPage(pageNum);

        setPageData(numPerPage);
    }
    
    
    
    private void setPageData(int numPerPage) {  
        solrResult = pageDataMap.get(navigationBean.getCurrentPage());                          // get saved page result    
        if(solrResult != null) {                                                                // if page result exist, reset ui    
            navigationBean.resetPage(solrResult, numPerPage, navigationBean.getTotalFound());  
        } else {                                                                                // if result not exist, search again
            Map<String, String> filterMap = getSavedFilterMap();   
            String searchText = getSavedSearchText();
            solrResult = search(searchText, filterMap, navigationBean.getStart());  
        }
        setResultView(); 
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private void setResultView() {
        logger.info("setResultView : {} - {}", solrResult, resultview);
         
        overMaxium = false;
        welcomePage = false; 
        int numFound;
        if (solrResult != null) {
            numFound = (int) solrResult.getNumFound();
            if (!selectedRecords.isEmpty() && !checkedRecordsCatlogNumList.isEmpty()) {
                for (SolrRecord sRecord : solrResult.getRecords()) {
                    if (checkedRecordsCatlogNumList.contains(sRecord.getCatalogNum())) {
                        sRecord.setSelected(true);
                    }
                }
                if(selectedRecords.size() > 10000) {
                    overMaxium = true;
                } 
            } else { 
                if (numFound > 10000) {
                    overMaxium = true;
                }
            }
            session.setAttribute(RESULT_SET, solrResult);
              
            int currentPage = navigationBean.resetPage(solrResult, getNumPerPage(), numFound);
            pageDataMap.put(currentPage, solrResult); 
            resultList = solrResult.getRecords();  
            resultsExist = true;
            
        } else {
            resultList = new ArrayList<>(); 
            resultsExist = false;
        }  
        
        if(resultview == 3) {
            showallmap();
        } else if(resultview == 4) {
            if(statistic.getTotalWithImage() > 0) {
                showallimages();
            } else {
                resultview = 0;
            }
                
        }  
        searchStart = true;
        resultPage = true;  
    }
        

    

    
    
    
    
    /** ############################# advance search ################## **/
    public void advanceClear() { 
        
        logger.info("advanceClear");
         
        cleardata();
        appendQuery(); 
        
        advance = true;
        hideadvance = false; 
        resultsExist = false; 
        
        statistic.searchFilteredData("*", null, false, false, false);
    }
    
    public void hideadavancesearch() {
        logger.info("hideadavancesearch"); 
        expandsAdvance = false;
    }

    public void expandadavancesearch() {
        logger.info("expandadavancesearch"); 
        expandsAdvance = true; 
    }
        
    /**
     * closeadvancesearch
     */
    public void closeadvancesearch() {
        logger.info("closeadvancesearch");
 
        hideadvance = true;     
        advance = false;  
        input = getDefaultText();
        session.setAttribute(SAVED_SEARCH_TYPE, false);
    }
    


    /**
     * openAdvanceSearch
     */
    public void openAdvanceSearch() {
        logger.info("openAdvanceSearch : {}", welcomePage);
         
        
        if(welcomePage) {
            cleardata();
        } else {
            String searchText;
            QueryBean qb = new QueryBean("", "contains", "text", "");
            if(!input.equals(defaultSearchText) && !StringUtils.isEmpty(input)) {
                qb.setValue(input);  
                queryBeans = new ArrayList<>();
                queryBeans.add(qb);
                querytext = "";
                searchText = buildSearchText(WILD_CHAR);
                session.setAttribute(SAVED_SEARCH_TEXT, searchText);
                appendQuery();  
            }    
        }  
        advance = true; 
        hideadvance = false;
        expandsAdvance = true;  
        resultPage = true;
        welcomePage = false;
        session.setAttribute(SAVED_SEARCH_TYPE, true);
    }
     
    
    /**
     * advanceSearch
     */
    public void advanceSearch() {
        logger.info("advenceSearch : {}", advance); 
        
        updateResult = ":searchform:result welcomepagepanel";  
        preSearch(); 
        advance = true; 
        session.setAttribute(SAVED_SEARCH_TYPE, true);
        buildAdvanceSearch();  
    }
    
    private void buildAdvanceSearch() {
        logger.info("buildAdvanceSearch");
//        if(start == 0) {
//            navigationBean.setCurrentPage(1);
//        } 
        navigationBean.setCurrentPage(1);
        
//        selectedRecords = new ArrayList<SolrRecord>();
        String searchText; 
        Map<String, String> filterMap = getSavedFilterMap();
        
//        boolean doSearch = false;
        if (queryBeans != null && !queryBeans.isEmpty()) {  
            if (queryBeans.size() == 1) {     
                QueryBean bean = queryBeans.get(0);
                searchText = buildQueryBeanString(bean); 
            } else {
                searchText = buildQueryString();   
            }    
            
            solrResult = search(searchText, filterMap, 0);
            session.setAttribute(SAVED_SEARCH_TEXT, searchText);  
            statistic.searchFilteredData(searchText, filterMap, map, type, image); 
            input = getDefaultSearchText();
            setResultView();
//            if(resultview == 3) {
//                showallmap();
//            } else if(resultview == 4) {
//                showallimages();
//            }
        }   
        
    }
    
    
    public void removeall() {
        logger.info("removeall : {}", selectall);
        if(!selectall) {
            selectedRecords = new ArrayList<>();
            checkedRecordsCatlogNumList = new ArrayList<>();
            
            
            pageDataMap.entrySet()
                    .stream()
                    .map((entry) -> (SolrResult)entry.getValue())
                    .map((result) -> result.getRecords())
                    .forEach((records) -> {
                records.stream().forEach((r) -> {
                    r.setSelected(false);
                });
            });

//            for(Map.Entry<Integer, SolrResult> entry : pageDataMap.entrySet()) {
//                SolrResult result = (SolrResult)entry.getValue();
//                List<SolrRecord> records = result.getRecords();
//                for(SolrRecord r : records) {
//                    r.setSelected(false);
//                }
//            } 
        }
        if(solrResult.getNumFound() > 10000) {
            overMaxium = true;
        }
        resultview = 0;
    }
    
    public void removeone(SolrRecord record) { 
        for(Map.Entry<Integer, SolrResult> entry : pageDataMap.entrySet()) {
            SolrResult result = (SolrResult)entry.getValue();
            List<SolrRecord> records = result.getRecords();
            for(SolrRecord r : records) {
                if(r.getCatalogNum().equals(record.getCatalogNum())) {
                    r.setSelected(false);
                }
            }
            selectedRecords.remove(record);
            checkedRecordsCatlogNumList.remove(record.getCatalogNum());
        } 
        if(selectedRecords.isEmpty()) { 
            if(solrResult.getNumFound() > 10000) {
                overMaxium = true; 
            }
            resultview = 0;
        } else {
            if(selectedRecords.size() > 10000) {
                overMaxium = true;
            }
        }
    }
    
    /**
     * selectone - check box
     * 
     * @param record 
     */
    public void selectone(SolrRecord record) {
        
        logger.info("selectone");
        if (record.isSelected()) {
            selectedRecords.add(record);
            checkedRecordsCatlogNumList.add(record.getCatalogNum()); 
        } else {
            selectedRecords.remove(record); 
            checkedRecordsCatlogNumList.remove(record.getCatalogNum()); 
        } 
         
        if(selectedRecords.isEmpty()) { 
            int totalRecords = (int)solrResult.getNumFound();
            if(totalRecords > 10000) {
                overMaxium = true;
            }
            selectall = false;
        } else {
            selectall = true;
            overMaxium = selectedRecords.size() > 10000; 
        } 
    }
    
    /**
     * Display search results in images view.  One image per specimen
     */
    public void showallimages() {
        
        logger.info("showallimages"); 
         
        galleriaBean.setSearchCondition(getSavedSearchText(), getSavedFilterMap(), type, map);
        
        resultview = 4;   
    }
         
    
    /**
     * showallmap -- display whole map
     */
    public void showallmap() {
        logger.info("showallmap " );
  
//        updateResult = "welcomepagepanel";   
        resultview = 3;
        Map<String, String> filterMap = getSavedFilterMap();
        String searchText = getSearchText(); 
           
        
        if(filterMap.containsKey("geopoint:")) {
            SolrResult result = solrSearch.searchWithQueryFilter(searchText, filterMap, 0, 100, type, true, image, sortby); 
            gmap.setMapDataList(result, searchText, filterMap, type, image);
        } else {
            int total = navigationBean.getTotalFound();    
            SolrResult result;
            int numPerPage = getNumPerPage(); 
            int totalMap = statistic.getTotalWithMap(); 
            if(totalMap > 0) {
                if(totalMap <= 500) {                                   // if total amount is less than 500, display all with individual markers in map
                    if(total > numPerPage) {                            // if total amount is larger than number result per page, retrieve all data
                        result = solrSearch.searchWithQueryFilter(searchText, filterMap, 0, total, type, true, image, sortby);  
                    } else {            // else get result from saved in session
                        result = solrResult; 
                    }   
                    gmap.setMapData(result, searchText, filterMap, type, image);
                } else  {           // if total amount is large than 500, clustering data in geobox
                    gmap.setMapData(searchText, filterMap, type, image); 
                } 
            } else {
                gmap.setMapData(null, searchText, filterMap, type, image);
            }  
        } 
//        displaySingleList = false;
//        session.removeAttribute("mapList");
    }
    
    
    

    
    
    /**
     * changeNumDisplay
     */
    public void changeNumDisplay() {
        logger.info("changeNumDisplay : {}", numDisplay);
  
        navigationBean.setCurrentPage(1);                                       // reset to first page
        navigationBean.setStart(0);                                             // search start from 0
        
        int numPerPage = getNumPerPage();
        
        pageDataMap = new LinkedHashMap<>();                 // remove saved page data
        setPageData(numPerPage);  
    }
     
    
    /** ########################## end page navigation ####################### **/
    
    /**
     * sortresult
     */
    public void sortresult() {
        logger.info("sortresult : {} -- {}", sortby, input); 
        
        navigationBean.setCurrentPage(1);                                       // reset to first page
        navigationBean.setStart(0);                                             // search start from 0
        
        int numPerPage = getNumPerPage(); 
        pageDataMap = new LinkedHashMap<>();                 // remove saved page data
        setPageData(numPerPage);  
    }
    
    
    public void simpleview() {
        logger.info("simpleview"); 
        if(displayOneDetail && resultview == 2) {
            selectedRecords = (List<SolrRecord>)session.getAttribute(SAVED_SELECTED_RECORDS);
        }
        resultview = 0; 
    }
    
    public void detailview() {
        logger.info("detialview");
        if(displayOneDetail && resultview == 2) {
            selectedRecords = (List<SolrRecord>)session.getAttribute(SAVED_SELECTED_RECORDS);
        }
        resultview = 1; 
    }
    
    public void selectedview() {
        logger.info("selectedview");
        resultview = 2;
    }
    
    
    
    /** #######################  filter #####################  **/
    /**
     * removeFilter
     * @param key
     * @param value
     */
    public void removeFilter(String key, String value) { 
        logger.info("removeFilter : {} -- {}", key, value);
  
        Map<String, String> filterMap = getSavedFilterMap(); 
        switch (key) {
            case "Map":
                map = false;
                break;
            case "Type":
                type = false;
                break;
            case "Image":
                image = false;
                break;
            case "Collection":
                filterMap.remove("cln:");
                break;
            case "Sweden":
                filterMap.remove("cy:");
                break;
            case "DNA":
                filterMap.remove("dna:");
                break;
            case "Institution":
                filterMap.remove("institution");
                break;
            case "geopoint":
                filterMap.remove("geopoint:");
                break;
        }
     
        if (queries.isEmpty()) {
            queries = new HashMap<>();
        } else {
            queries.remove(key);
        }
        session.setAttribute(SAVED_QUERY, filterMap);                                   // save new query in session
          
        pageDataMap = new LinkedHashMap<>();   
        String searchText = getSearchText();                                            // build search text
        solrResult = search(searchText, filterMap, 0);                                  // search
 
        statistic.searchFilteredData(searchText, filterMap, map, type, image);          // research for statistic data
        setResultView();                                                                // set ui 
    }
    
    
        
    /**
     * removeAllQueries -- remove all query filters
     */
    public void removeAllQueries() {
        logger.info("removelAllQueries");
 
        Map<String, String> filterMap = getSavedFilterMap();

        type = false;
        image = false;
        map = false; 
        
        preSearch();
        for(Map.Entry<String, String> entry : queries.entrySet()) {
            String key = entry.getKey();
            if(entry.getKey().equals("Collection")) {
                key = "cln:";
            } else if(key.equals("Institution")) {
                key = "institution";
            } else if(key.equals("DNA")) {
                key = "dna:";
            } else if(key.equals("Sweden")) {
                key = "cy:";
            } else if(key.equals("geopoint")) {
                key = "geopoint:";
            }
            filterMap.remove(key);
        } 
 
        session.setAttribute(SAVED_QUERY, filterMap);
        pageDataMap = new LinkedHashMap<>();   
        queries = new HashMap();
        String searchText = getSearchText();                                            // build search text
        solrResult = search(searchText, filterMap, 0);                                  // search
 
        statistic.searchFilteredData(searchText, filterMap, map, type, image);          // research for statistic data
        setResultView();   
    }

    
    
    
    
    
     
    




    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
     
    
    /**
     * operationchange -- when operation changes in ui from advance search
     * @param qb
     * @param index 
     */
    public void operationchange(QueryBean qb, int index) {
        logger.info("operationchange : {} -- {}", qb.getOperattion(), index);
        appendQuery();
    
        if(index == 1) {
            QueryBean bean = queryBeans.get(0);
            if(qb.getOperattion().equals("and")){ 
                bean.setOperattion("and");
            } else {
                bean.setOperattion("");
            }
        } 
    }
    
    
    
    public void contentchange(QueryBean qb, int index) {
        logger.info("contentchange : {} -- {}", qb.getContent(), index);
  
    }
    
    
    
    
    
    /**
     * appandQuery
     */
    private void appendQuery() {
//        logger.info("appandQuery : {}", queryBeans.size());

        StringBuilder sb = new StringBuilder();
        for (QueryBean bean : queryBeans) {
            if (queryBeans.indexOf(bean) > 0) {
                sb.append(" ");
                sb.append(bean.getOperattion());
                sb.append(" ");
            }
            sb.append(buildQueryString(bean));
        }
        querytext = sb.toString();
    }
    
    
    private String buildQueryString(QueryBean bean) {
        StringBuilder sb = new StringBuilder();
        switch (bean.getField()) {
            case "date":
                Date startDate = bean.getFromDate();
                Date endDate = bean.getToDate();
                sb.append("[From date] ");
                if(startDate == null) {
                    sb.append("*");
                } else {
                    sb.append(HelpClass.dateToString(startDate));
                }   sb.append(" [To date] ");
                if(endDate == null) {
                    sb.append("*");
                } else {
                    sb.append(HelpClass.dateToString(bean.getToDate()));
            }   break;
            case "season":
                //            int startMonth = bean.getStartMonth();
//            int endMonth = bean.getEndMonth();
            
//            if(startMonth == 0) {
//                startMonth = 1;
//            } 
//
//            if (endMonth == 0) {
//                endMonth = 12;
//            }
                
                sb.append(" [Form] ");
                sb.append(StringMap.getInstance().getMonth(bean.getStartMonth(), locale, true));
                sb.append(" ");
                sb.append(bean.getStartDay());
                sb.append(" [To] ");
                sb.append(StringMap.getInstance().getMonth(bean.getEndMonth(), locale, false));
                sb.append(" ");
                sb.append(bean.getEndDay());
                break;
            default:
                if (bean.getValue() != null && bean.getValue().trim().length() > 0) {
                    sb.append(bean.getValue());
                    if (!bean.getField().equals("text")) {
                        String field = StringMap.getInstance().getFieldName(bean.getField() + "_" + locale);
                        sb.append(" [");
                        sb.append(field);
                        sb.append("] ");
                }
            }   break;
        }
        return sb.toString();
    }
    
     

    
    
    /**
     * Set page to default data.   
     */
    private void cleardata() {  
        
        type = false;
        image = false;
        map = false;  
        resultview = 0;
        advance = false;
        hideadvance = true;
        expandsAdvance = true;
//        searchQuery = "";
        
        sortby = "score";                                                       // reset sorting to sore -- default sort
        numDisplay = "10";  
      
        pageDataMap = new LinkedHashMap<>();                               // remove selected results
        selectedRecords = new ArrayList<>();
        checkedRecordsCatlogNumList = new ArrayList<>(); 
        
        selectall = false;  
        searchStart = false;
         
        // Remove session data
        session.removeAttribute(SAVED_QUERY);           
        session.removeAttribute(SAVED_SEARCH_TEXT);  
        // End of remove session data

        input = getDefaultText();                                         // reset input field to default text
        querytext = "";
                                                            // default display number of page is 10     -- default value

        queries = new HashMap<>(); 
         
        QueryBean qb = new QueryBean("", "contains", "text", "");
        queryBeans = new ArrayList<>();
        queryBeans.add(qb);
        querytext = ""; 
        
        preSearch();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
     

    /**
     * 
     * Result for data export
     * This function only support for page data
     * 
     * @return List<>
     */
    public List<SolrRecord> getExportRecords() {
        
        logger.info("getExportRecords");
   
        if(exportDataSet.isEmpty()) {
            prepareExportData();
        } 
        
        return exportDataSet;
    }
     
 
    public void prepareExportData() {
        logger.info("prepareExportData");
        
        exportDataSet = new ArrayList<>();
         
        // If user selected result for export, return selected result list, otherwise return whole result set
        if (selectedRecords != null && !selectedRecords.isEmpty()) {
            exportDataSet = selectedRecords;
        } else { 
            String searchText = getSearchText(); 
            Map<String, String> filterMap = getSavedFilterMap();

            SolrResult savedResult = (SolrResult) session.getAttribute(RESULT_SET);
            int totalRecords = 0;
            if(savedResult != null) {
                totalRecords = (int) savedResult.getNumFound();
            }
             
            int downloadNum = totalRecords;
            if(totalRecords > 10000) { 
                downloadNum = 10000;
            }
            SolrResult result = solrSearch.searchWithQueryFilter(searchText, filterMap, 0, downloadNum, type, map, image, sortby); 
            exportDataSet = result.getRecords();
        } 
    }
    

 

    
    private void setWelcomePageData() {
        welcomePage = true; 
        resultPage = false;
        hideadvance = true;  
        overMaxium = false;
 
        input = getDefaultText(); 
        
        updateResult = ":searchform:result :searchform:welcomepagepanel";  
    }
    
    
    public void changeTab(int tab) {
        logger.info("changeTab : {}", tab);
        
        setWelcomePageData();
        welcomeContent = tab;  
        
        style.setTabLink(tab);
    }

//    /**
//     * startpage - return to start page
//     */
//    public void startpage() { 
//        logger.info("startpage");
//         
//        setWelcomePageData();
//        welcomeContent = 0;   
//    }
//
    /**
     * Display list of collections
     */
    public void listCollections() {
        logger.info("listCollections");
        
        setWelcomePageData();
        welcomeContent = 1;   
    }
//
    /**
     * Display list of partners
     */
    public void listPartners() {
        logger.info("listPartners");
         
        setWelcomePageData();
        welcomeContent = 2;    
    }
//
//    /**
//     * fqa - show frequent ask questions
//     */
//    public void fqa() {
//        logger.info("fqa");
//        
//        setWelcomePageData();                                                    
//        welcomeContent = 3;    
//    }
//
//    public void aboutSite() {
//        logger.info("aboutSite");
//         
//        setWelcomePageData();
//        welcomeContent = 4;    
//    }

    public void contactus() {
        logger.info("contactus");
       
        setWelcomePageData();
        welcomeContent = 5;    
    }

    public void blur() {
        logger.info("blur");
        if (input == null) {
            input = getDefaultText();
        } else if (input.length() < 1) {
            input = getDefaultText();
        }
    }

    private String buildQueryBeanString(QueryBean bean) {
        
        logger.info("buildQueryBeanString : bean : {}", bean); 
        
        String field = bean.getField();
        String searchText;
        switch (field) {
            case "date":
                searchText = SolrSearchHelper.getInstance().buildDate(bean);
                break;
            case "season":
                searchText = SolrSearchHelper.getInstance().buildSeason(bean);
                break;
            case "ftx":
                searchText = SolrSearchHelper.getInstance().buildClassificationSearch(bean);
                break;
            case "eftx":
                searchText = SolrSearchHelper.getInstance().buildDeterminationSearch(bean);
                break;
            case "text":
                searchText = SolrSearchHelper.getInstance().buildFullTextSearchText(bean); 
                break;
            case "cm":
                searchText = SolrSearchHelper.getInstance().buildCommonNameSearchText(bean);
                break;
            default:
                searchText = SolrSearchHelper.getInstance().buildAdvanceSearchText(bean);
                break;
        }
        return searchText;
    }
    
    private String addBean(String text, QueryBean bean) {
        StringBuilder sb = new StringBuilder();
        
        String op = bean.getOperattion();
        if (op.equals("and")) {
            sb.append("+");
        }  
        sb.append("("); 
        sb.append(text); 
        sb.append(") ");
        sb.append(buildQueryBeanString(bean));
        return sb.toString().trim();
    }
    
    private String buildTwoBeans(QueryBean bean1, QueryBean bean2) {
        StringBuilder sb = new StringBuilder();
  
        if(bean2.getOperattion().equals("and")) {
            sb.append("+");
        } 
        sb.append("(");
        sb.append(buildQueryBeanString(bean1));

        sb.append(") ");
        sb.append(buildQueryBeanString(bean2));
        return sb.toString().trim();
    }
    
    private String buildQueryString() {
        
        QueryBean b1 = queryBeans.get(0);
        QueryBean b2 = queryBeans.get(1);
        String text = buildTwoBeans(b1, b2);
         
        StringBuilder sb = new StringBuilder(); 
        if(queryBeans.size() == 2) {  
            return text;
        } else {    
            for(int i = 2; i < queryBeans.size(); i++) {
                text = addBean(text, queryBeans.get(i)); 
            }
            sb.append(text); 
        } 
         
        return text;
    }
     



    /**
     * showOneDetail -- open one record in detail view
     * 
     * @param record 
     */
    public void showOneDetail(SolrRecord record) {
        logger.info("showOneDetail : {}", record.getCatalogNum());
 
//        List<SolrRecord> list = new ArrayList<>();
//        list.add(record);
        
        displayOneDetail = true;
        
        record.setSelected(true);
        selectall = true;
        
        selectedRecords.add(record);
        session.setAttribute(SAVED_SELECTED_RECORDS, selectedRecords); 
        selectedRecords = new ArrayList<>();
        selectedRecords.add(record);
     
        resultview = 2;
    }
     
    private int getNumPerPage() {
        try {
            return numDisplay == null ? 10 : Integer.parseInt(numDisplay);
        } catch(NumberFormatException ex) {
            logger.error(ex.getMessage()); 
        }
        return 10;
    }
    
    
    public void backtoresult() {
        logger.info("backtoresult"); 
         
        welcomePage = false; 
        resultPage = true;                                                    // check to open download box
        
        if(advance) {
            hideadvance = false;
        } 
    }
    
     
    
    /**
     * getSavedFilterMap
     * 
     * @return Map<String, String>
     */
    private Map<String, String> getSavedFilterMap() {
        Map<String, String> filterMap = (Map<String, String>) session.getAttribute(SAVED_QUERY); 
        return filterMap == null ? new HashMap<>() : filterMap;
    }
 
    private String getSearchText() { 
        if(advance) {
            return getSavedSearchText();
        } else {
            return buildSearchText(WILD_CHAR); 
        }
    }
    
    
    private String getSavedSearchText() {
        String searchText = (String) session.getAttribute(SAVED_SEARCH_TEXT);
        return searchText == null || searchText.isEmpty() ? WILD_CHAR : searchText;
    }
    
    
    

    
    
    
    


    public String getUpdateResult() {
        return updateResult;
    }
    
     

     
      


    /**
     * keyup -- event from auto complete field in advance search
     */
    public void keyup() { 
        appendQuery();
    }
    
    /**
     * itemSelect -- when auto complete item is selected.
     * 
     * @param event 
     */
    public void itemSelect(SelectEvent event) {
//        logger.info("itemSelect");
         
        appendQuery();
    }
    





    /**
     * addqueryline -- add query into query list from advance search
     * @param qb
     * @param index 
     */
    public void addqueryline(QueryBean qb, int index) {
//        logger.info("addqueryline : {} -- {}", qb, index);

        if (index == queryBeans.size() - 1) {
            QueryBean bean = queryBeans.get(queryBeans.size() - 1);                     // get last query bean
            bean.setField(qb.getField());
            bean.setValue(qb.getValue());
            bean.setOperattion(qb.getOperattion());
            bean.setStMon(qb.getStMon());
            bean.setEndMon(qb.getEndMon());
            bean.setFromDate(qb.getFromDate());
            bean.setToDate(qb.getToDate());
            
            if(index == 1 && qb.getOperattion().equals("and")) {
                QueryBean querybean = queryBeans.get(0);
                querybean.setOperattion("and");
            }

            QueryBean newBean = new QueryBean("and", "contains", "text", "");
            queryBeans.add(newBean); 
        } 
    }
    
    

    
    /**
     * removequeryline -- remove query from advance search
     * 
     * @param qb
     * @param index 
     */
    public void removequeryline(QueryBean qb, int index) {
        logger.info("removequeryline : {} -- {}", qb, index);
 
        queryBeans.remove(index);
        appendQuery(); 
    }
    
 
    
    
    /**
     * handleDateSelect -- when date selected in ui, append query into query field.
     * 
     * @param event 
     */
    public void handleDateSelect(SelectEvent event) {
        logger.info("handleDateSelect : {}", event);
        appendQuery();
    }
 
    public List getMonthList() {

        List dropMonthList = new ArrayList();
        locale = (String) session.getAttribute("locale"); 
        List<String> months = StringMap.getInstance().getMonths(locale);
        try {
            int numMonth = 0;
            for (String mon : months) {
                dropMonthList.add(new SelectItem(String.valueOf(numMonth), mon));
                numMonth++;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return dropMonthList;
    }
    
    

    
     
    
    
    /**
     * 
     * @param text
     * @return String
     */
    private String buildSearchText(String text) { 
        logger.info("buildSearchText : {} -- {}" , text , input);
         
        StringBuilder sb = new StringBuilder();
        if (!input.equals(defaultSearchText)) {                      
            if (input.length() > 0) {
                String s = input.replaceAll("[(),]", ""); 
                s = s.replaceAll("[/,]", " "); 
                if(input.contains("-")) {
                    String[] strInputs = StringUtils.split(s, "-");
                    for(String string : strInputs) {
                        sb.append("+text:*");
                        sb.append(string.trim().toLowerCase());
                        sb.append("* ");
                    } 
                } else if(input.contains(" ")) {
                    
                    String string = s.replace(" ", "* +text:*"); 
                    sb.append("+text:*");
                    sb.append(string);
                    sb.append("*");
                } else {
                    sb.append("text:*"); 
                    sb.append(input.trim().toLowerCase()); 
                    sb.append(WILD_CHAR);  
                } 
            }
        } else {
            return text;
        }
        return sb.toString().trim();
    }
    
    
    
    
    
    
    

    public boolean isSearchStart() {
        return searchStart;
    }
    
     
     

    public void setDefaultSearchText(String defaultSearchText) {
        this.defaultSearchText = defaultSearchText;
    }
 

    public boolean isExpandsAdvance() {
        return expandsAdvance;
    }

    public void setExpandsAdvance(boolean expandsAdvance) {
        this.expandsAdvance = expandsAdvance;
    }
    
    public List<SolrRecord> getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(List<SolrRecord> selectedRecords) {
        this.selectedRecords = selectedRecords;
    }  
 

    public String getNumDisplay() {
        return numDisplay;
    }

    public void setNumDisplay(String numDisplay) {
        this.numDisplay = numDisplay;
    }

    public String getSortby() {
        return sortby;
    }

    public void setSortby(String sortby) {
        this.sortby = sortby;
    }

    public boolean isResultsExist() {
        return resultsExist;
    } 

    public boolean isAdvance() {
        return advance;
    } 


 
    public Map<String, String> getQueries() {
        return queries;
    }
  
    public boolean isWelcomePage() {
        return welcomePage;
    }
 
    private int getNumberOfDays(int month) {
        switch (month) {
            case 1:
                return 31;
            case 2:
                return 29;
            case 3:
                return 31;
            case 4:
                return 30;
            case 5:
                return 31;
            case 6:
                return 30;
            case 7:
                return 31;
            case 8:
                return 31;
            case 9:
                return 30;
            case 10:
                return 31;
            case 11:
                return 30;
            case 12:
                return 31;
            default:
                return 31;
        }
    }

    public List getStartDayList() { 
        logger.info("getStartDayList"); 
        
        int startMonth = getMonth("start");
        return getDayList(startMonth); 
    }

    public List getEndDayList() { 
        logger.info("getEndDayList");
        
        int endMonth = getMonth("end");
        return getDayList(endMonth); 
    }
    
    private int getMonth(String month) {
        
        for (QueryBean bean : queryBeans) {
            if (bean.getField().equals("season")) {
                return month.equals("start") ? bean.getStartMonth() : bean.getEndMonth();
            }
        }
        return 0; 
    }
    
    private List getDayList(int month) { 
        int dayCount = getNumberOfDays(month);
        List dropDayList = new ArrayList();
        try {
            for (int i = 1; i <= dayCount; i++) {
                dropDayList.add(new SelectItem(String.valueOf(i), String.valueOf(i)));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return dropDayList;
    }

    public void changeStartMonth() {
        logger.info("changeStartMonth");
        appendQuery();
    }

    public void changeEndMonth() {
        logger.info("changeEndMonth");
        appendQuery();
    }

    public void changeStartDay() {
        logger.info("changeStartDay");
        appendQuery();
    }

    public void changeEndDay() {
        logger.info("changeEndDay");
        appendQuery();
    }

 
 
    /**
     * showimages -- display images.  Images are retrieved from morphybank
     * 
     * @param record 
     */
    public void displayImages(SolrRecord record) {
        logger.info("displayImages : {}", record.getImgMbids(), record.getMbid());
         
        String[] mbids = record.getImgMbids();
        if(mbids != null) {  
            record.setImageExist(true);
            record.setDisplayImage(true); 
        }     
    }
     
    /**
     * showmap -- display individual map
     * @param record 
     */
    public void showmap(SolrRecord record) {
        logger.info("showmap");
        record.setDisplayMap(true);
    }
    
    
    public void detail(MapRecord mapData) {
        record = solrSearch.searchByCatalogNumber(mapData.getCatalogNum());
    }
    
    public void listMapData(List<MapRecord> mapRecords) {
        
        logger.info("listMapData");
        MapRecord mapRecord = mapRecords.get(0);
        String lat = mapRecord.getLat();
        String lng = mapRecord.getLnt();
        
        String geopoint = mapRecord.getGeopoint();
         
        String searchText = getSearchText();
        Map<String, String> filterMap = getSavedFilterMap();
        filterMap.put("geopoint:", geopoint);
        session.setAttribute(SAVED_QUERY, filterMap);
        
        queries.put("geopoint", lat + " " + lng); 
        
        solrResult = search(searchText, filterMap, 0); 
        overMaxium = false;
        if (selectedRecords.isEmpty()) {
            if (solrResult.getNumFound() > 10000) {
                overMaxium = true;
            }
        } else {
            if(selectedRecords.size() > 10000) {
                overMaxium = true;
            }
        }   
        statistic.searchFilteredData(searchText, filterMap, map, type, image);
        resultview = 0;
        setResultView();  
    }
     
 
    public void imageDetail(String mbid) {
        logger.info("imageDetail : {}", mbid);
        
        showOneDetail(solrSearch.searchByMBid(mbid));
    }
    

    /**
     * senderrorreport -- send error report via email
     */
    public void senderrorreport() {

        logger.info("senderrorreport : {} -- {}", locale, errorbean.isLocality());

        if(errorbean != null && record != null) { 
            mail.sendMail(record, errorbean);
        } 
        
        welcomePage = false; 
        resultPage = true;                                                    // check to open download box
        
        if(advance) {
            hideadvance = false;
        } 

//        locale = (String) session.getAttribute("locale");
//        FacesMessage fm = new FacesMessage(StringMap.getInstance().getString("sendmail", locale));
//        FacesContext.getCurrentInstance().addMessage("", fm);
         
        addInfo("", StringMap.getInstance().getString("sendmail", locale)); 
    }
    
    public void errorreportFromMap(MapRecord mapData) {
        logger.info("errorreportFromMap: {}", mapData);
        
        
        record = solrSearch.searchByCatalogNumber(mapData.getCatalogNum());
         
        errorbean.setCatalogNumber(false);
        errorbean.setCollector(false);
        errorbean.setContinent(false);
        errorbean.setCoordinate(false);
        errorbean.setCountry(false);
        errorbean.setDate(false);
        errorbean.setDeterminater(false);
        errorbean.setErrorDesc("");
        errorbean.setFamily(false);
        errorbean.setLocality(false);
        errorbean.setOtherinfo(false);
        errorbean.setSpecimenName(false);
        errorbean.setTypeinfo(false);
        
        welcomeContent = 100;
//        resultview = 100;
         
        setWelcomePageData();
    }

    /**
     * errorreport -- set errorreport data
     * @param data 
     */
    public void errorreport(SolrRecord data) {

        logger.info("errorreport: {}", data);
        record = data;
        errorbean.setCatalogNumber(false);
        errorbean.setCollector(false);
        errorbean.setContinent(false);
        errorbean.setCoordinate(false);
        errorbean.setCountry(false);
        errorbean.setDate(false);
        errorbean.setDeterminater(false);
        errorbean.setErrorDesc("");
        errorbean.setFamily(false);
        errorbean.setLocality(false);
        errorbean.setOtherinfo(false);
        errorbean.setSpecimenName(false);
        errorbean.setTypeinfo(false);
        
        welcomeContent = 100;
//        resultview = 100;
         
        setWelcomePageData();
    }
    
    public void validateEmail() {
        logger.info("validateEmail: {}", errorbean.getErrorDesc());
    }
 
    public void checkedLocality() {
        logger.info("checked");
        errorbean.setLocality(true);
    }


    /**
     * closeMap -- close individual map
     * @param record 
     */
    public void closeMap(SolrRecord record) {
        record.setDisplayMap(false); 
    }
    
    public void closeImage(SolrRecord record) {
        record.setDisplayImage(false);
    }
    
    public void closeImageView() {
        logger.info("closeImageView");
        resultview = 0;
        
        updateResult = ":searchform:result welcomepagepanel";
    }

    public void closeWholeMap() {
        
        logger.info("closeWholeMap"); 
        resultview = 0;
        
        updateResult = ":searchform:result welcomepagepanel";
    }

    public void closeimages(SolrRecord record) {
        record.setImageExist(false);
    }
 
 
    
    public void export() {
        logger.info("export");
        exportDataSet = new ArrayList<>(); 
    }

    /**
     * changelanguage - change ui language
     * @param locale 
     */
    public void changelanguage(String locale) {

        logger.info("changelanguage - locale: {}", locale);
 
        if(!this.locale.equals(locale)) {
            this.locale = locale;
            languages.setLocale(locale);
            style.setLanguageLink(locale);

            if(input.equals(defaultSearchText)) {
                defaultSearchText = StringMap.getInstance().getSearchDefaultText(locale); 
                input = defaultSearchText;
            } 
            chart.changeLanguage(locale); 
            session.setAttribute("locale", locale); 
        } 
    }

    public List<String> getAllImagesPaths() {
        return allImagesPaths;
    }


 
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    public boolean isOverMaxium() {
        return overMaxium;
    }

    public int getResultview() {
        return resultview;
    }

    public boolean isSelectall() {
        return selectall;
    }

    public void setSelectall(boolean selectall) {
        this.selectall = selectall;
    }
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
     
    public int getWelcomeContent() {
        return welcomeContent;
    }
     
    public String getQuerytext() {
        return querytext;
    }
 
    public List<QueryBean> getQueryBeans() {
        return queryBeans;
    }
 
 
    public SolrRecord getRecord() {
        return record;
    } 
 
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    public List<SolrRecord> getResultList() {
        return resultList;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getDefaultSearchText() {
        return defaultSearchText;
    }

 
    
    
    
    
    
    
    
    
    
    public String getLocale() {
        return locale;
    }
    
     

    public String getInput() {
        
        logger.info(" get input : {} ", input );
        return (input == null || input.trim().length() < 1) ? getDefaultSearchText() : input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public boolean isImage() {
        return image;
    } 
    
    public boolean isMap() {
        return map;
    }
 
    
 
    public boolean isType() {
        return type;
    }

    public String getMaxDate() {
        return HelpClass.getTodaysDate();
    }
    
    
    
    
    public boolean isResultPage() {
        return resultPage;
    }

    public void setResultPage(boolean resultPage) {
        this.resultPage = resultPage;
    }
 
    private void addError(String errorTitle, String errorMsg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errorTitle, errorMsg));
    }

    private void addInfo(String msgTitle, String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msgTitle, msg));
    }  
    
    private void addWarning(String warningTitle, String warningMsg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, warningTitle, warningMsg));
    } 
}
