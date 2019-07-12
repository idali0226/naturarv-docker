/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.web.portal.controllers;

import java.io.Serializable;
import java.util.Map;
import javax.annotation.PostConstruct; 
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession; 
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.web.portal.beans.StyleBean;
import se.nrm.dina.web.portal.model.SolrResult;
import se.nrm.dina.web.portal.services.solr.SolrSearchHelper;
import se.nrm.dina.web.portal.util.StringMap;

/**
 *
 * @author idali
 */
@Named("searchBean")
@SessionScoped
public class Search implements Serializable  {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    
    private static final String WILD_CHAR = "*";
    
    
    String searchText;
    private String input;
    private String defaultSearchText;
    
    private SolrResult solrResult; 
    
    
    private String locale = "sv";  
    
    
    
    private boolean hideadvance = true;
    private int tab;

    private HttpSession session; 
    private final String servername; 
    
    @Inject
    private StyleBean style;
    
    
    RequestContext requestContext; 
    FacesContext context;
     
    
    
    /** Constructor **/
    public Search() { 
        session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true); 
        servername = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getServerName();
        
        locale = (String) session.getAttribute("locale");
        if (locale == null) {
            locale = "sv";
        }
        defaultSearchText = StringMap.getInstance().getSearchDefaultText(locale);
        input = defaultSearchText;
        
        tab = 0;
    }
    
    /** 
     *  Initialize data after class constructed
     */
    @PostConstruct
    public void init() {
        
    }
    
    
    
    
    /**
     * Change topmenu tab
     * @param tab 
     */
    public void changeTab(int tab) {
        logger.info("changeTab : {}", tab);
         
        this.tab = tab;   
        style.setTabLink(tab);
    }
    
    /**
     * blur to set search text in simple search field
     */
    public void blur() {
        logger.info("blur");
        
        if(input == null || input.isEmpty()) {
            input = defaultSearchText;
        } 
    }
    
    
    
    
    /**
     * fullTextSearch - full text search
     * full text search is simple search starts when user start type in search field
     * 
     */
    public void fullTextSearch() {
//        logger.info("fullTextSearch : {} ", input); 
        
        if (!input.equals(defaultSearchText)) { 
            if(input != null && input.length() > 1) {
                searchText = SolrSearchHelper.getInstance().buildFullTextSearchText(input);
                logger.info("searchtext : {}", searchText); 
            }
        }
        
        
        
 
//        session.removeAttribute(SAVED_SEARCH_TEXT);                             // remove previous search text
//        preSearch();                                                            // clear data for new search
//        
//        String searchText = WILD_CHAR;  
//        if (!input.equals(defaultSearchText)) {                             
//            if (input != null && !StringUtils.isEmpty(input.trim())) { 
//                searchText = SolrSearchHelper.getInstance().buildFullTextSearchText(input);
//            }   
//        }  
//        session.setAttribute(SAVED_SEARCH_TYPE, false);
// 
//        updateResult = ":searchform:result welcomepagepanel"; 
//        try { 
//            Map<String, String> savedFilterMap = getSavedFilterMap();
//            solrResult = search(searchText, savedFilterMap, 0);                                 // search
//            session.setAttribute(SAVED_SEARCH_TEXT, searchText);
//            statistic.searchFilteredData(searchText, savedFilterMap, map, type, image);         // statistic data with new search query
// 
//            setResultViewWithNewFullTextSearch();  
//        } catch (EJBException e) {
//            if (e.getCause().getClass().getSimpleName().equals("SolrServerConnectionException")) {
//                addError("Server is not available", e.getLocalizedMessage());
//            }
//        }   
    } 
    
    
    
    private SolrResult search(String searchText, Map<String, String> filterMap, int start) {  
 
        return null;
    } 
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
//    private String getDefaultText() {
//
//        if (locale == null) {
//            locale = "sv";
//        }
//        defaultSearchText = StringMap.getInstance().getSearchDefaultText(locale);
//        return defaultSearchText;
//    }
    
    
    public String getDefaultSearchText() {
        return defaultSearchText;
    }
    
    
    public String getInput() { 
        logger.info("getInput : {} ", input );
        return (input == null || input.trim().length() < 1) ? defaultSearchText : input;
    }
    
    
    
    public int getTab() {
        return tab;
    }
     
    public boolean isHideadvance() {
        return hideadvance;
    }
}
