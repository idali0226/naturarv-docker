package se.nrm.dina.web.portal.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author idali
 */
public class PageDataWrapper implements Serializable {
    
    public List<SolrRecord> getPageResult(int pageNum, Map<Integer, List<SolrRecord>> map) { 
        return map.get(pageNum);
    }
    
}
