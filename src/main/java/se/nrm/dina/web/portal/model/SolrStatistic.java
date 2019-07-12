package se.nrm.dina.web.portal.model;

import java.io.Serializable; 
import java.util.List; 

/**
 *
 * @author idali
 */
public class SolrStatistic  implements Serializable  {
    
    private int totalNumber;
    private List<StatisticDataInstitution> statisticDataList; 
     
    public SolrStatistic() {
        
    }

    public List<StatisticDataInstitution> getStatisticDataList() {
        return statisticDataList;
    }

    public void setStatisticDataList(List<StatisticDataInstitution> statisticDataList) {
        this.statisticDataList = statisticDataList;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    } 
}
