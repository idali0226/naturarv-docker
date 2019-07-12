/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.web.portal.beans;
  
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author idali
 */
@ManagedBean 
@SessionScoped
public class CaptchaView {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public void check() {
        
        logger.info("check"); 
        
        
    }
}
