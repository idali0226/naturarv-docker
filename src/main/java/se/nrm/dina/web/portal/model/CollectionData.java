package se.nrm.dina.web.portal.model;

import java.io.Serializable;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author idali
 */
public class CollectionData implements Serializable {
    
    private String code;
    private String name;
    private int total;
    
    public CollectionData() {
        
    }
    
    public CollectionData(String code, String name, int total) {
        this.code = code;
        this.name = name;
        this.total = total; 
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getTotal() {
        return total;
    }
    
    public String getShortName() {
        return name.length() > 20 ? StringUtils.substring(name, 0, 19) + "..." : name;
    }
    
    @Override
    public String toString() {
        return code + " : " + name + " : " + total;
    }
    
}
