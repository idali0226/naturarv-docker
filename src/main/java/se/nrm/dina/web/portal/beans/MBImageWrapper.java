package se.nrm.dina.web.portal.beans;

import java.io.Serializable;
import java.util.ArrayList; 
import java.util.List; 
import javax.enterprise.context.SessionScoped; 
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.common.util.morphbank.MorphBankImage; 
import se.nrm.dina.common.util.HelpClass; 

/**
 *
 * @author idali
 */
@SessionScoped
@Named
public class MBImageWrapper implements Serializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
//    private Map<String, MorphybankImageMetadata> mpImageMap = new HashMap<String, MorphybankImageMetadata>();
    
//    @Inject
//    private MorphybankClient morphybankService;
    
    public MBImageWrapper() {  
        
    } 
    
    public String getImageById(String mbid) {
        return HelpClass.getMorphbankThumbURLById(mbid);
    }
     
 
    public List<MorphBankImage> getImages(String[] imgMBIDs) {
        
        
        // TODO : for home development
//        imgMBIDs = new String[5];
//        imgMBIDs[0] = "833628";
//        imgMBIDs[1] = "833627";
//        imgMBIDs[2] = "833626";
//        imgMBIDs[3] = "833625";
//        imgMBIDs[4] = "833624";
        
        
        List<MorphBankImage> images = new ArrayList<>();
        for(String imageId : imgMBIDs) { 
            String morphbankImageUrl = HelpClass.getMorphbankImageURLById(imageId);
            String morphBankThumbUrl = HelpClass.getMorphbankThumbURLById(imageId);
            images.add(new MorphBankImage(Integer.parseInt(imageId), morphbankImageUrl, "", "", "", morphBankThumbUrl));
        } 
        return images;
    } 
}
