package se.nrm.dina.web.portal.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties; 

/**
 *
 * @author idali
 */
public class PropertyHandler {
    
    private static final Properties properties = new Properties();
    private static PropertyHandler instance = null;

    public static synchronized PropertyHandler getInstance() {
        if (instance == null) {
            instance = new PropertyHandler();
        }
        return instance;
    }

    public PropertyHandler() {
        InputStream inputStream = PropertyHandler.class.getResourceAsStream("/ApplicationResources.properties");
         
        try {
            properties.load(inputStream);
        } catch (IOException ex) {
            
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                
            }
        } 
    }

    /**
     * Get the value of the property with key.
     *
     * @param key may not be null
     * @return the value of the property 
     */
    public String getProperty(String key) { 
        if (key == null) {
            return null;
        } 
        String value = properties.getProperty(key);
        if (value == null) {
            return null;
        }
        return value;
    }
    
}
