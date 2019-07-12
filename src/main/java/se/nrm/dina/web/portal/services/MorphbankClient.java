package se.nrm.dina.web.portal.services;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.Serializable;
import java.net.URI;
import javax.ejb.Stateless;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import se.nrm.dina.common.util.HelpClass;

/**
 *
 * @author idali
 */
@Stateless
public class MorphbankClient implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String MORPHBANK_SERVICE_URL = HelpClass.getMorphbankServiceURL();
    private final WebResource service;

    
    
    
    public MorphbankClient() { 
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        service = client.resource(MORPHBANK_SERVICE_URL);
    }
     
    public String getMorphbankImageIdByTaxonName(String taxonName) {   
          
        logger.info("getMorphbankImageIdByTaxonName: {}", taxonName);
         
        MultivaluedMapImpl queryParams = new MultivaluedMapImpl();
        queryParams.add("method", "search");
        queryParams.add("objecttype", "Image"); 
        queryParams.add("keywords", taxonName);
        queryParams.add("limit", 8);
        queryParams.add("firstResult", 0);
        queryParams.add("format", "id");          
                 
        try {
            return service.path("request").queryParams(queryParams).type(MediaType.APPLICATION_XML).get(String.class); 
        } catch(UniformInterfaceException e) {
            logger.error(e.getMessage());
        }
        return null;
    } 

    private static URI getBaseURI() { 
        return UriBuilder.fromUri(MORPHBANK_SERVICE_URL).build(); 
    }
}
