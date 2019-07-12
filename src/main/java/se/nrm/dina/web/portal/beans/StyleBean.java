package se.nrm.dina.web.portal.beans;

import java.io.Serializable; 
import javax.enterprise.context.SessionScoped; 
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.web.portal.util.CSSName;

/**
 *
 * @author idali
 */
@SessionScoped
@Named
public class StyleBean implements Serializable {
     
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    
    private String svLink; 
    private String enLink;
    private String esLink;
    
    private static final String SWEDISH = "sv";
    private static final String ENGLISH = "en";
    private static final String SPANISH = "es"; 
      
    private String tabStart;
    private String tabCollection;
    private String tabPartner;
    private String tabFaq;
    private String tabAbout;
    
    
    
    private final String activeLink = CSSName.getInstance().ACTIVE_LINK; 
    private final String inactiveLink = CSSName.getInstance().INACTIVE_LINK;
    
    private final String activeTabLink = CSSName.getInstance().ACTIVE_TAB_LINK;
    private final String inactiveTabLink = CSSName.getInstance().INACTIVE_TAB_LINK;
       
    public StyleBean() {
        svLink = activeLink;
        enLink = inactiveLink;
        esLink = inactiveLink;
        
        tabStart = activeTabLink;
        tabCollection = inactiveTabLink;
        tabPartner = inactiveTabLink;
        tabFaq = inactiveTabLink;
        tabAbout = inactiveTabLink;
    }
  
    public String getEnLink() {
        return enLink;
    }

    public void setEnLink(boolean isActive) {
        this.enLink = isActive ? activeLink : inactiveLink;
    }

    public String getEsLink() {
        return esLink;
    }

    public void setEsLink(boolean isActive) {
        this.esLink = isActive ? activeLink : inactiveLink;
    }

    public String getSvLink() {
        logger.info("getSvLink : {}", svLink);
        return svLink;
    }

    public void setSvLink(boolean isActive) {
        this.svLink = isActive ? activeLink : inactiveLink;
    } 

    public String getTabStart() {
        return tabStart;
    }

    public String getTabCollection() {
        return tabCollection;
    }

    public String getTabPartner() {
        return tabPartner;
    }

    public String getTabFaq() {
        return tabFaq;
    }

    public String getTabAbout() {
        return tabAbout;
    }

    public void setTabStart(boolean isActive) {
        this.tabStart = isActive ? activeTabLink : inactiveTabLink;
    }

    public void setTabCollection(boolean isActive) {
        this.tabCollection = isActive ? activeTabLink : inactiveTabLink;
    }

    public void setTabPartner(boolean isActive) {
        this.tabPartner = isActive ? activeTabLink : inactiveTabLink;
    }

    public void setTabFaq(boolean isActive) {
        this.tabFaq = isActive ? activeTabLink : inactiveTabLink;
    }

    public void setTabAbout(boolean isActive) {
        this.tabAbout = isActive ? activeTabLink : inactiveTabLink;
    }
    
    
    public void setTabLink(int tab) {
        
        logger.info("setTabLink : {}", tab);
        
        setTabAbout(false);
        setTabCollection(false);
        setTabFaq(false);
        setTabPartner(false);
        setTabStart(false);
        
        switch (tab) {
            case 0:
                setTabStart(true);
                break;
            case 1:
                setTabCollection(true);
                break;
            case 2:
                setTabPartner(true);
                break;
            case 3:
                setTabFaq(true);
                break;
            case 4:
                setTabAbout(true);
                break;
        }
    }
   
    public void setLanguageLink(String locale) {
        
        logger.info("setLanguageLink : {}", locale);
         
        setSvLink(false);
        setEnLink(false);
        setEsLink(false);

        switch (locale) {
            case SWEDISH:
                setSvLink(true);
                break;
            case ENGLISH:
                setEnLink(true);
                break;
            case SPANISH:
                setEsLink(true);
                break;
        }
    }    
}
