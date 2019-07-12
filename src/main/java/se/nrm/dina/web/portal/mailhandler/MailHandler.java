package se.nrm.dina.web.portal.mailhandler;
 
import java.io.Serializable;  
import java.util.Properties;  
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
//import javax.mail.BodyPart;
import javax.mail.Message;

import javax.mail.MessagingException;  
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage; 
//import javax.mail.internet.MimeMultipart; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  
import se.nrm.dina.web.portal.beans.ErrorReportBean;
import se.nrm.dina.web.portal.model.SolrRecord;

/**
 * Send email
 */
@SessionScoped
@Named
public class MailHandler implements Serializable {
    
    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Production server *
     */
    private final static String SMTP_HOST_NAME = "mail.smtp.host";
//    private final static String HOST = "mailserver.nrm.se";
      
    private final static String HOST = "mail.nrm.se";
     

    private final static String EMAIL_ADDRESS = "team@mail.dina-web.net";
//    private final static String EMAIL_ADDRESS = "ida.li@nrm.se";

    /**
     * Production server *
     */

     /** LOCALHOST **/
//    private static final String SMTP_HOST_NAME = "smtp.gmail.com";
//    private static final int SMTP_HOST_PORT = 465;
//    private static final String SMTP_AUTH_USER = "idali0226@gmail.com"; 
//    private static final String SMTP_AUTH_PWD  = "friday18";
 
    
//    @Resource(name = "java:/mail/app")
//    private Session session;
     
    public MailHandler() {
    }
 
    public void sendMail(SolrRecord data, ErrorReportBean errorbean)  {
        ErropReportEmail report = new ErropReportEmail();
        send(report.createErrorReport(errorbean.getErrorDesc(), data, errorbean)); 
    }
     
    
    private void send(String strBody)  {
        try {
            Properties props = new Properties();
             
            props.put(SMTP_HOST_NAME, HOST);
            Session session = Session.getInstance(props, null);
            session.setDebug(true);
            
            Message message = new MimeMessage(session);
 
            
            InternetAddress emailAddress = new InternetAddress(EMAIL_ADDRESS);
            message.setSubject("Error report (naturarv)"); 
            message.addRecipient(Message.RecipientType.TO, emailAddress);
              
            message.setContent(strBody, "text/html; charset=ISO-8859-1"); 
            Transport.send(message);   
        } catch (MessagingException ex) {
            logger.error(ex.getMessage());
        } 
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    /** Localhost **/
    
//    private void send(String strBody)  {
//        
//        
//        String emailAddress = "ida.li@nrm.se";
//        Message message = new MimeMessage(session);
//        session.setDebug(true); 
//        try {
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress)); 
//            message.setSubject(MimeUtility.encodeText("Loan admin new account", "utf-8", "B"));
//
//            message.setContent(strBody, "text/html; charset=ISO-8859-1"); 
//            Transport.send(message);
//        } catch (MessagingException | UnsupportedEncodingException ex) {
//            logger.warn(ex.getMessage());
//        } 
//             
//    }
     
    public static void main(String[] args) throws MessagingException {
        MailHandler handler = new MailHandler();
//        handler.sendMail("test", "mytest");
    }

    
}
