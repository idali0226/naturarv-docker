package se.nrm.dina.web.portal.util;

import java.util.Comparator; 
import javax.swing.SortOrder; 
import se.nrm.dina.web.portal.model.CollectionData;

/**
 * A utility class to sort java.util.List containing Specify entity beans
 * 
 * @author idali
 */
public class SortUtil implements Comparator {
     
    private final SortOrder sortOrder;

    
    public SortUtil(SortOrder sortOrder) { 
        this.sortOrder = sortOrder;
    } 
    
    @Override
    public int compare(Object o1, Object o2) {
        if(o1 instanceof CollectionData) {
            CollectionData cd1 = (CollectionData)o1;
            CollectionData cd2 = (CollectionData)o2;
            
            Integer int1 = cd1.getTotal();
            Integer int2 = cd2.getTotal();
            
            int value = int1.compareTo(int2);
            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        } 
        return 0;
    }
}
