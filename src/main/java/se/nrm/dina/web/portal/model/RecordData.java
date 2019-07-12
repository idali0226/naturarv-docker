package se.nrm.dina.web.portal.model;

import java.io.Serializable; 
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author idali
 */
@XmlRootElement(name="RecordSet") 
public class RecordData implements Serializable {
    
    
//    private SolrRecord[] records; 
    private List<SolrRecord> records;
    
    public RecordData() {
        
    }

    @XmlElements({
        @XmlElement(name = "Record", type = SolrRecord.class)
    })
    public List<SolrRecord> getRecords() {
        return records;
    }

    public void setRecords(List<SolrRecord> records) {
        this.records = records;
    }
}
