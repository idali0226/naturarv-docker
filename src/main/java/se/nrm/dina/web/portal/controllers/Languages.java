package se.nrm.dina.web.portal.controllers;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped; 
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named; 
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;    
import se.nrm.dina.web.portal.beans.StyleBean;  
import se.nrm.dina.web.portal.util.DinaPortalHelper;

/**
 *
 * @author idali
 */
@SessionScoped
@Named("languages")
public class Languages implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
 
    @Inject
    private StyleBean style;
    private String locale = "sv";

    boolean swedish = true;
    
    public Languages() {
        locale = DinaPortalHelper.getDefaultLanguage(); 
    }

    @PostConstruct
    public void init() {   
        style.setLanguageLink(locale);
        setLocaleInSession();
    }

    
    public void changelanguage(String locale) { 
        logger.info("changelanguage - locale: {}", locale);
         
        setLocale(locale);
    }
    
    
    
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale; 
    }

    public boolean isSwedish() {
        return locale.equals("sv");
    }

    public void setSwedish(boolean swedish) {
        this.swedish = swedish;
    } 
    
    private void setLocaleInSession() { 
        
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);    
        session.setAttribute("locale", locale);
    }
}
