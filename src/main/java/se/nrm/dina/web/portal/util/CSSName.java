package se.nrm.dina.web.portal.util;

/**
 *
 * @author idali
 */
public class CSSName {
    
    public final String DIV_VISIBLE = "visiblediv";
    public final String DIV_INVISIBLE = "invisiblediv";
     
    public final String ACTIVE_LINK = "activelink";
    public final String INACTIVE_LINK = "inactivelink"; 
    
    
    public final String ACTIVE_TAB_LINK = "activeTablink";
    public final String INACTIVE_TAB_LINK = "inactiveTablink"; 
    
    private static CSSName instance = null;
    
    public static synchronized CSSName getInstance() {
        if (instance == null) {
            instance = new CSSName();
        }
        return instance;
    }  
}
