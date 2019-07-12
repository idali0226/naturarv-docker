package se.nrm.dina.web.portal.beans;

import java.io.Serializable; 
import javax.enterprise.context.SessionScoped; 
import javax.inject.Named;

/**
 *
 * @author idali
 */
@SessionScoped
@Named
public class SearchData implements Serializable {


    private boolean synonymer;
    
    private String province; 
    private String county;
    
    
    
    
    
    
    public SearchData() {
        
    }
 
    public boolean isSynonymer() {
        return synonymer;
    }

    public void setSynonymer(boolean synonymer) {
        this.synonymer = synonymer;
    }
 
    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
    
    
}
