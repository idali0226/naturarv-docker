package se.nrm.dina.web.portal.mailhandler;
 
import java.util.List; 

class Email {

     /**
     * Recipients in the to-field.
     */
    private final List<String> mailRecipients;
    
    /**
     * Subject of the e-mail.
     */
    private final String subject; 

    /**
     * Creates a new email object.
     * @param to recipient of the e-mail
     * @param subject subject
     * @param body the content of the e-mail
     * @param from e-mail address of the sender
     * @param fromDescription description of the e-mail address of the sender
     */
    public Email(List<String> mailRecipients, String subject) {
        this.mailRecipients = mailRecipients;
        this.subject = subject;  
    }
 

    /**
     * Gets the recipients.
     * @return the email addresses of the recipients.
     */
    public List<String> getMailRecipients() {
        return this.mailRecipients;
    }

    /**
     * Gets the subject line of the e-mail.
     * @return the subject.
     */
    public String getSubject() {
        return this.subject;
    }  
}
